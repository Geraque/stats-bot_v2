package com.cs.doceho.stats.bot.v2.scopegg;

import com.cs.doceho.stats.bot.v2.config.LeetifyProperties;
import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.excel.ChangingExcelService;
import com.cs.doceho.stats.bot.v2.leetify.dto.MatchKey;
import com.cs.doceho.stats.bot.v2.service.utils.DateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScopeGGProcessingService {

    MatchRepository matchRepository;
    ChangingExcelService changingExcelService;
    DateService dateService;
    ScopeGGApi apiClient;
    LeetifyProperties leetifyProperties;
    static Integer LIMIT = 5;
    static List<String> COOKIES = List.of("locale=ru; mix-panel-uuid=14f1a0c4-db14-4d8a-b7b5-2476596b8cf6; _ym_uid=1735587504868434686; _hjSessionUser_2157523=eyJpZCI6IjA0MGI4YzgyLTI5NDItNTI3Zi04MjhjLWUzODkzNThjNDlhOSIsImNyZWF0ZWQiOjE3MzU1ODc1MDQ5MjksImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=f3db038d-fdd8-4542-85c2-8d84cf9bb4d1; intercom-device-id-jw52e3zr=4544d653-4b3c-4427-80bf-2e17ac2387c6; _ga=GA1.3.23116477.1735587504; _scid=MU-0xXP7ZFdha-SMrsRja0pUcEGZTQSB; locale-saved=true; _scid_r=W8-0xXP7ZFdha-SMrsRja0pUcEGZTQSBabxwUA; _hjMinimizedPolls=1570091%2C1575057; _hjDonePolls=1570091%2C1575057%2C1593377; _ga=GA1.2.23116477.1735587504; _ym_d=1753530609; scope_session_id=b33a3567-d49b-407a-8c69-ecff4354eaff; userId=152932932; clip-remove-modal-flag=true; _sctr=1%7C1755892800000; matches-3770725243560132668=152932932; matches-3770723632947396928=152932932; matches-3770750772845740495=382373375; matches-3770872979697696864=152932932; matches-3770877867370479772=152932932; matches-3770882351316336724=152932932; matches-3770886899686703200=152932932; matches-3770890741534949882=152932932; matches-3770895562635739255=152932932; matches-3770900946377244867=152932932; matches-3770906181942378636=152932932; _ScCbts=%5B%5D; matches-3771054616012128657=152932932; matches-3771055880879997123=152932932; matches-3771060158667424270=152932932; matches-3771065789369549074=152932932; matches-3771073352806957752=152932932; backend_sticky_session=http://10.44.0.210:80; mix-first-action=28.08.2025; mix-initial-first-action-sended=true; _hjSession_2157523=eyJpZCI6IjA2ZTc4MTE2LTA3YjYtNDVjMC04YzgyLWMzMWViMjIyOTJkNSIsImMiOjE3NTYzODE4MDIzMzQsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=138; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; _ga_K2ZZ7R3N94=GS2.2.s1756381802$o149$g1$t1756383351$j59$l0$h0; _scid_r=W8-0xXP7ZFdha-SMrsRja0pUcEGZTQSBabxyEg; _rdt_uuid=1737652790712.77713a27-3f55-4efc-b9dd-4a5047035018; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; amp_22bf3d_scope.gg=dYX848Mx6MnwtWxX5BQBS6.MTUyOTMyOTMy..1j3o86rpa.1j3o9m57n.277.e3.2la; _gid=GA1.2.1083646231.1756383450; _gat_UA-148508910-1=1");

    @Transactional
    public void processMatches() {
        List<MatchItem> addedMatches = new ArrayList<>();
        // Шаг 1. Проверка на дубликаты
        List<MatchItem> existingMatches = matchRepository.findTop20ByOrderByDateDesc();
        Set<MatchKey> existingMatchKeys = new HashSet<>();
        for (MatchItem match : existingMatches) {
            existingMatchKeys.add(
                new MatchKey(match.getDate(), match.getPlayerName(), match.getMap()));
        }

        // Шаг 2. Получение истории игр и формирование списка gameId

        List<ScopeGGMatch> gameHistories = COOKIES.stream()
            .flatMap(token -> apiClient.getHistory(token).stream())
            .sorted((ScopeGGMatch o1, ScopeGGMatch o2) -> {
                Instant d1 = Instant.ofEpochMilli(Long.parseLong(o1.getFinishedAt()));
                Instant d2 = Instant.ofEpochMilli(Long.parseLong(o2.getFinishedAt()));
                return d2.compareTo(d1);
            })
            .collect(Collectors.toList());


        if (gameHistories.isEmpty()) {
            log.error("История игр пуста или не получена.");
            return;
        }
        List<String> gameIds = gameHistories.stream().map(ScopeGGMatch::getId).limit(LIMIT)
            .collect(Collectors.toList());

        log.info("gameIds123: {}", gameIds);

//        // Шаг 3. Обработка каждой игры
//        for (String gameId : gameIds) {
//            GameDetail gameDetail = apiClient.getGameDetail(gameId);
//            if (gameDetail == null || gameDetail.getPlayerStats() == null) {
//                continue;
//            }
//            List<ClutchData> clutches = apiClient.getClutches(gameId);
//            List<OpeningDuel> openingDuels = apiClient.getOpeningDuels(gameId);
//
//            Map<String, Integer> openKillCounts = new HashMap<>();
//            if (openingDuels != null) {
//                for (OpeningDuel duel : openingDuels) {
//                    openKillCounts.put(duel.getAttackerName(),
//                        openKillCounts.getOrDefault(duel.getAttackerName(), 0) + 1);
//                }
//            }
//            for (PlayerStat stat : gameDetail.getPlayerStats()) {
//                PlayerName playerName = PlayerName.fromId(stat.getSteam64Id());
//                if (playerName == null) {
//                    continue;
//                }
//                LocalDateTime finishedAt = dateService.parseDate(gameDetail.getFinishedAt());
//                MapType mapType = MapType.fromLeetifyName(gameDetail.getMapName());
//                MatchKey key = new MatchKey(finishedAt, playerName, mapType);
//                if (existingMatchKeys.contains(key)) {
//                    continue;
//                }
//                MatchType matchType = MatchType.fromLeetifyName(gameDetail.getDataSource());
//                MatchItem matchItem = MatchItem.builder()
//                    .playerName(playerName)
//                    .date(finishedAt)
//                    .rating(stat.getHltvRating())
//                    .threeKill(stat.getMulti3k())
//                    .fourKill(stat.getMulti4k())
//                    .ace(stat.getMulti5k())
//                    .flash(stat.getFlashAssist())
//                    .trade(stat.getTradeKillsSucceeded())
//                    .smokeKill(0)
//                    .wallBang(0)
//                    .openKill(openKillCounts.getOrDefault(stat.getName(), 0))
//                    .map(MapType.fromLeetifyName(gameDetail.getMapName()))
//                    .result(calculateMapResult(gameDetail.getPlayerStats(), gameDetail.getTeamScores()))
//                    .build();
//
//                setType(
//                    matchItem,
//                    matchType,
//                    gameDetail.getMatchmakingGameStats()
//                        .stream()
//                        .map(GameDetail.PlayerRank::getRank)
//                        .max(Comparator.naturalOrder())
//                        .get() //TODO может не правильно работать, если ни у кого нет ранга
//                );
//                matchItem = setClutch(matchItem, clutches, stat.getSteam64Id());
//                log.info("Сохранение матча: {}", matchItem);
//                addedMatches.add(matchItem);
//                matchRepository.save(matchItem);
//                existingMatchKeys.add(key);
//            }
//        }
//        changingExcelService.addMatches(addedMatches);
    }

}
