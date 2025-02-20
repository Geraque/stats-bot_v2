package com.cs.doceho.stats.bot.v2.leetify;


import com.cs.doceho.stats.bot.v2.config.LeetifyProperties;
import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.leetify.dto.ClutchData;
import com.cs.doceho.stats.bot.v2.leetify.dto.GameDetail;
import com.cs.doceho.stats.bot.v2.leetify.dto.GameHistoryResponse.GameIdWrapper;
import com.cs.doceho.stats.bot.v2.leetify.dto.MatchKey;
import com.cs.doceho.stats.bot.v2.leetify.dto.OpeningDuel;
import com.cs.doceho.stats.bot.v2.leetify.dto.PlayerStat;
import com.cs.doceho.stats.bot.v2.service.ChangingExcelService;
import com.cs.doceho.stats.bot.v2.service.utils.DateService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeetifyProcessingService {

  MatchRepository matchRepository;
  ChangingExcelService changingExcelService;
  DateService dateService;
  LeetifyApiClient apiClient;
  LeetifyProperties leetifyProperties;
  static Integer LIMIT = 3;

  public void processMatches() throws IOException {
    List<String> tokens = apiClient.getAllTokens(leetifyProperties.getAccounts());
    List<MatchItem> addedMatches = new ArrayList<>();
    // Шаг 1. Проверка на дубликаты
    List<MatchItem> existingMatches = matchRepository.findTop20ByOrderByDateDesc();
    Set<MatchKey> existingMatchKeys = new HashSet<>();
    for (MatchItem match : existingMatches) {
      existingMatchKeys.add(
          new MatchKey(match.getDate(), match.getPlayerName(), match.getRating()));
    }

    // Шаг 2. Получение истории игр и формирование списка gameId

    List<GameIdWrapper> gameHistories = tokens.stream()
        .flatMap(token -> apiClient.getGameHistory(token).stream())
        .sorted((o1, o2) -> {
          OffsetDateTime d1 = OffsetDateTime.parse(o1.getFinishedAt());
          OffsetDateTime d2 = OffsetDateTime.parse(o2.getFinishedAt());
          return d2.compareTo(d1);
        })
        .collect(Collectors.toList());
    if (gameHistories.isEmpty()) {
      log.error("История игр пуста или не получена.");
      return;
    }
    List<String> gameIds = gameHistories.stream().map(GameIdWrapper::getId).limit(LIMIT)
        .collect(Collectors.toList());

    // Шаг 3. Обработка каждой игры
    for (String gameId : gameIds) {
      GameDetail gameDetail = apiClient.getGameDetail(gameId);
      if (gameDetail == null || gameDetail.getPlayerStats() == null) {
        continue;
      }
      List<ClutchData> clutches = apiClient.getClutches(gameId);
      List<OpeningDuel> openingDuels = apiClient.getOpeningDuels(gameId);

      Map<String, Integer> openKillCounts = new HashMap<>();
      if (openingDuels != null) {
        for (OpeningDuel duel : openingDuels) {
          openKillCounts.put(duel.getAttackerName(),
              openKillCounts.getOrDefault(duel.getAttackerName(), 0) + 1);
        }
      }
      for (PlayerStat stat : gameDetail.getPlayerStats()) {
        PlayerName playerName = PlayerName.fromId(stat.getSteam64Id());
        if (playerName == null) {
          continue;
        }
        LocalDateTime finishedAt = dateService.parseDate(gameDetail.getFinishedAt());
        Double rating = stat.getHltvRating();
        MatchKey key = new MatchKey(finishedAt, playerName, rating);
        if (existingMatchKeys.contains(key)) {
          continue;
        }
        MatchType matchType = MatchType.fromLeetifyName(gameDetail.getDataSource());
        MatchItem matchItem = MatchItem.builder()
            .playerName(playerName)
            .date(finishedAt)
            .rating(rating)
            .threeKill(stat.getMulti3k())
            .fourKill(stat.getMulti4k())
            .ace(stat.getMulti5k())
            .flash(stat.getFlashAssist())
            .trade(stat.getTradeKillsSucceeded())
            .smokeKill(0)
            .wallBang(0)
            .openKill(openKillCounts.getOrDefault(stat.getName(), 0))
            .map(MapType.fromLeetifyName(gameDetail.getMapName()))
            .result(calculateMapResult(gameDetail.getPlayerStats(), gameDetail.getTeamScores(),
                matchType))
            .build();

        setType(matchItem, matchType, gameDetail.getMatchmakingGameStats().get(0).getRank()); //Можно использовать любой ранг
        matchItem = setClutch(matchItem, clutches, stat.getSteam64Id());
        log.info("Сохранение матча: {}", matchItem);
        addedMatches.add(matchItem);
        matchRepository.save(matchItem);
        existingMatchKeys.add(key);
      }
    }
    changingExcelService.addMatches(addedMatches);
  }

  private MatchResult calculateMapResult(List<PlayerStat> playerStats, List<Integer> teamScores,
      MatchType type) {
    int index = IntStream.range(0, playerStats.size())
        .filter(i -> PlayerName.DESMOND.getIds().contains(playerStats.get(i).getSteam64Id()))
        .findFirst()
        .orElse(-1);
    int teamId = 0;
    switch (type) {
      case WINGMAN:
        teamId = index > 2 ? 1 : 0;
        break;
      case FACEIT:
      case PREMIER:
      case MATCH_MAKING:
        teamId = index > 4 ? 1 : 0;
        break;
    }
    int winRounds = teamScores.get(teamId);
    int loseRounds = teamScores.get(teamId == 0 ? 1 : 0);
    return winRounds > loseRounds ? MatchResult.WIN :
        winRounds == loseRounds ? MatchResult.DRAW : MatchResult.LOSE;
  }

  private MatchItem setClutch(MatchItem matchItem, List<ClutchData> clutches, String steamId) {
    // Подсчёт clutch-данных
    int clutchOne = 0, clutchTwo = 0, clutchThree = 0, clutchFour = 0, clutchFive = 0;
    if (clutches != null) {
      for (ClutchData clutch : clutches) {
        if (clutch.getClutchesWon() == 1 && steamId.equals(clutch.getSteam64Id())) {
          switch (clutch.getHandicap()) {
            case 0:
              clutchOne++;
              break;
            case -1:
              clutchTwo++;
              break;
            case -2:
              clutchThree++;
              break;
            case -3:
              clutchFour++;
              break;
            case -4:
              clutchFive++;
              break;
            default:
              break;
          }
        }
      }
    }
    return matchItem.toBuilder()
        .clutchOne(clutchOne)
        .clutchTwo(clutchTwo)
        .clutchThree(clutchThree)
        .clutchFour(clutchFour)
        .clutchFive(clutchFive)
        .build();
  }

  private void setType(MatchItem matchItem, MatchType matchType, Integer randomRank) {
    matchItem.setType(matchType);
    if (MatchType.MATCH_MAKING.equals(matchItem.getType())
        && randomRank > 100) {
      matchItem.setType(MatchType.PREMIER);
    }
  }

}
