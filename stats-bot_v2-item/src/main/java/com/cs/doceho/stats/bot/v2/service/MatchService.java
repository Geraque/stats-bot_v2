package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.service.utils.CalculationService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchService {

  MatchRepository matchRepository;
  CalculationService calculationService;

  public List<MatchItem> getAll() {
    return matchRepository.findAll();
  }

  public MatchItem get(UUID matchId) {
    return matchRepository.findById(matchId)
        .orElseThrow(
            () -> new RuntimeException("Match not found for this id : " + matchId));
  }

  public List<MatchItem> getByName(String playerName) {
    return getAll()
        .stream()
        .filter(m -> m.getPlayerName().getName().equals(playerName))
        .sorted(Comparator.comparing(MatchItem::getDate).reversed())
        .collect(Collectors.toList());
  }

  @Transactional
  public MatchItem create(Match match) {
    MatchItem matchItem = MatchItem.builder()
        .playerName(PlayerName.fromName(match.getPlayerName()))
        .date(match.getDate())
        .rating(match.getRating())
        .smokeKill(match.getSmokeKill())
        .openKill(match.getOpenKill())
        .threeKill(match.getThreeKill())
        .fourKill(match.getFourKill())
        .ace(match.getAce())
        .flash(match.getFlash())
        .trade(match.getTrade())
        .wallBang(match.getWallBang())
        .clutchOne(match.getClutchOne())
        .clutchTwo(match.getClutchTwo())
        .clutchThree(match.getClutchThree())
        .clutchFour(match.getClutchFour())
        .clutchFive(match.getClutchFive())
        .type(MatchType.fromName(match.getType()))
        .build();

    return matchRepository.save(matchItem);
  }

  @Transactional
  public MatchItem update(UUID matchId,
      Match matchDetails) {
    MatchItem matchItem = get(matchId);

    matchItem.setPlayerName(PlayerName.fromName(matchDetails.getPlayerName()));
    matchItem.setDate(matchDetails.getDate());
    matchItem.setRating(matchDetails.getRating());
    matchItem.setSmokeKill(matchDetails.getSmokeKill());
    matchItem.setOpenKill(matchDetails.getOpenKill());
    matchItem.setThreeKill(matchDetails.getThreeKill());
    matchItem.setFourKill(matchDetails.getFourKill());
    matchItem.setAce(matchDetails.getAce());
    matchItem.setFlash(matchDetails.getFlash());
    matchItem.setTrade(matchDetails.getTrade());
    matchItem.setWallBang(matchDetails.getWallBang());
    matchItem.setClutchOne(matchDetails.getClutchOne());
    matchItem.setClutchTwo(matchDetails.getClutchTwo());
    matchItem.setClutchThree(matchDetails.getClutchThree());
    matchItem.setClutchFour(matchDetails.getClutchFour());
    matchItem.setClutchFive(matchDetails.getClutchFive());
    matchItem.setType(MatchType.fromName(matchDetails.getType()));

    return matchRepository.save(matchItem);
  }

  public void delete(UUID id) {
    matchRepository.deleteById(id);
  }

  public Player getPlayerStats(String playerName) {
    // Создание объекта player с начальными значениями статистики
    Player player = Player.builder()
        .name(playerName)
        .matches(0)
        .rating(0.0)
        .smokeKill(0)
        .openKill(0)
        .threeKill(0)
        .fourKill(0)
        .ace(0)
        .flash(0)
        .trade(0)
        .wallBang(0)
        .clutchFive(0)
        .clutchFour(0)
        .clutchOne(0)
        .clutchThree(0)
        .clutchTwo(0)
        .build();

    // Фильтрация матчей по имени игрока с дополнительными проверками на null
    getAll().stream()
        .filter(match -> match != null
            && match.getPlayerName() != null
            && match.getPlayerName().getName() != null
            && match.getPlayerName().getName().equals(playerName))
        .forEach(match -> {
          // Инкремент количества матчей
          player.setMatches(player.getMatches() + 1);

          // Суммирование статистики с использованием методов safeDouble/calculationService.safeInt
          player.setRating(player.getRating() +  calculationService.safeDouble(match.getRating()));
          player.setSmokeKill(player.getSmokeKill() + calculationService.safeInt(match.getSmokeKill()));
          player.setOpenKill(player.getOpenKill() + calculationService.safeInt(match.getOpenKill()));
          player.setThreeKill(player.getThreeKill() + calculationService.safeInt(match.getThreeKill()));
          player.setFourKill(player.getFourKill() + calculationService.safeInt(match.getFourKill()));
          player.setAce(player.getAce() + calculationService.safeInt(match.getAce()));
          player.setFlash(player.getFlash() + calculationService.safeInt(match.getFlash()));
          player.setTrade(player.getTrade() + calculationService.safeInt(match.getTrade()));
          player.setWallBang(player.getWallBang() + calculationService.safeInt(match.getWallBang()));
          player.setClutchOne(player.getClutchOne() + calculationService.safeInt(match.getClutchOne()));
          player.setClutchTwo(player.getClutchTwo() + calculationService.safeInt(match.getClutchTwo()));
          player.setClutchThree(player.getClutchThree() + calculationService.safeInt(match.getClutchThree()));
          player.setClutchFour(player.getClutchFour() + calculationService.safeInt(match.getClutchFour()));
          player.setClutchFive(player.getClutchFive() + calculationService.safeInt(match.getClutchFive()));
        });

    // Вычисление среднего значения рейтинга, если найдены матчи
    if (player.getMatches() > 0) {
      player.setRating(player.getRating() / player.getMatches());
    }

    return player;
  }

  public List<Player> getAllStats() {
    // Группируем статистику по имени игрока
    return getAll().stream()
        .collect(Collectors.groupingBy(
            matchItem -> matchItem.getPlayerName().getName(),
            Collectors.toList()))
        .entrySet()
        .stream()
        .map(entry -> {
          String playerName = entry.getKey();
          List<MatchItem> matches = entry.getValue();

          Player player = new Player();
          player.setName(playerName);
          player.setMatches(matches.size());

          player.setRating(
              matches.stream().mapToDouble(MatchItem::getRating).sum() / matches.size());
          player.setSmokeKill(
              matches.stream().mapToInt(MatchItem::getSmokeKill).sum() / matches.size());
          player.setOpenKill(
              matches.stream().mapToInt(MatchItem::getOpenKill).sum() / matches.size());
          player.setThreeKill(
              matches.stream().mapToInt(MatchItem::getThreeKill).sum() / matches.size());
          player.setFourKill(
              matches.stream().mapToInt(MatchItem::getFourKill).sum() / matches.size());
          player.setAce(matches.stream().mapToInt(MatchItem::getAce).sum() / matches.size());
          player.setFlash(matches.stream().mapToInt(MatchItem::getFlash).sum() / matches.size());
          player.setTrade(matches.stream().mapToInt(MatchItem::getTrade).sum() / matches.size());
          player.setWallBang(
              matches.stream().mapToInt(MatchItem::getWallBang).sum() / matches.size());
          player.setClutchOne(
              matches.stream().mapToInt(MatchItem::getClutchOne).sum() / matches.size());
          player.setClutchTwo(
              matches.stream().mapToInt(MatchItem::getClutchTwo).sum() / matches.size());
          player.setClutchThree(
              matches.stream().mapToInt(MatchItem::getClutchThree).sum() / matches.size());
          player.setClutchFour(
              matches.stream().mapToInt(MatchItem::getClutchFour).sum() / matches.size());
          player.setClutchFive(
              matches.stream().mapToInt(MatchItem::getClutchFive).sum() / matches.size());

          return player;
        })
        .collect(Collectors.toList());
  }

  private Player getTopPlayerByMetric(ToDoubleFunction<MatchItem> metricFunction) {
    return getTopPlayerByAggregator(playerMatches -> {
      double totalMetricValue = playerMatches.stream()
          .mapToDouble(metricFunction)
          .sum();

      return Player.builder()
          .name(playerMatches.get(0).getPlayerName().getName())
          .matches(playerMatches.size())
          .rating(totalMetricValue)
          .build();
    });
  }

  private Player getTopPlayerByMetricWithAverage(ToDoubleFunction<MatchItem> metricFunction) {
    return getTopPlayerByAggregator(playerMatches -> {
      double totalMetricValue = playerMatches.stream()
          .mapToDouble(metricFunction)
          .sum();

      return Player.builder()
          .name(playerMatches.get(0).getPlayerName().getName())
          .matches(playerMatches.size())
          .rating(totalMetricValue / playerMatches.size())
          .build();
    });
  }

  private Player getTopPlayerByAggregator(Function<List<MatchItem>, Player> aggregatorFunction) {
    List<MatchItem> matches = getAll();

    return matches.stream()
        .collect(Collectors.groupingBy(MatchItem::getPlayerName))
        .values().stream()
        .map(aggregatorFunction)
        .max(Comparator.comparing(Player::getRating))
        .orElse(null);
  }

  private int sumInt(List<MatchItem> playerMatches, ToIntFunction<MatchItem> mapper) {
    return playerMatches.stream()
        .mapToInt(mapper)
        .sum();
  }

}
