package com.cs.doceho.stats.bot.v2.scopegg;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import com.cs.doceho.stats.bot.v2.excel.ChangingExcelService;
import com.cs.doceho.stats.bot.v2.leetify.dto.MatchKey;
import com.cs.doceho.stats.bot.v2.scopegg.dto.GameInfo;
import com.cs.doceho.stats.bot.v2.scopegg.dto.PlayerStat;
import com.cs.doceho.stats.bot.v2.scopegg.dto.ScopeGGMatch;
import com.cs.doceho.stats.bot.v2.service.utils.DateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
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
    ScopeGGApi apiClient;

    static Integer LIMIT = 27;
    static List<String> COOKIES = List.of("locale=ru; mix-panel-uuid=14f1a0c4-db14-4d8a-b7b5-2476596b8cf6; _ym_uid=1735587504868434686; _hjSessionUser_2157523=eyJpZCI6IjA0MGI4YzgyLTI5NDItNTI3Zi04MjhjLWUzODkzNThjNDlhOSIsImNyZWF0ZWQiOjE3MzU1ODc1MDQ5MjksImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=f3db038d-fdd8-4542-85c2-8d84cf9bb4d1; intercom-device-id-jw52e3zr=4544d653-4b3c-4427-80bf-2e17ac2387c6; _ga=GA1.3.23116477.1735587504; _scid=MU-0xXP7ZFdha-SMrsRja0pUcEGZTQSB; locale-saved=true; _scid_r=W8-0xXP7ZFdha-SMrsRja0pUcEGZTQSBabxwUA; _hjMinimizedPolls=1570091%2C1575057; _hjDonePolls=1570091%2C1575057%2C1593377; _ga=GA1.2.23116477.1735587504; _ym_d=1753530609; scope_session_id=b33a3567-d49b-407a-8c69-ecff4354eaff; userId=152932932; clip-remove-modal-flag=true; _sctr=1%7C1755892800000; matches-3770725243560132668=152932932; matches-3770723632947396928=152932932; matches-3770750772845740495=382373375; matches-3770872979697696864=152932932; matches-3770877867370479772=152932932; matches-3770882351316336724=152932932; matches-3770886899686703200=152932932; matches-3770890741534949882=152932932; matches-3770895562635739255=152932932; matches-3770900946377244867=152932932; matches-3770906181942378636=152932932; _ScCbts=%5B%5D; matches-3771054616012128657=152932932; matches-3771055880879997123=152932932; matches-3771060158667424270=152932932; matches-3771065789369549074=152932932; matches-3771073352806957752=152932932; backend_sticky_session=http://10.44.0.210:80; mix-first-action=28.08.2025; mix-initial-first-action-sended=true; _hjSession_2157523=eyJpZCI6IjA2ZTc4MTE2LTA3YjYtNDVjMC04YzgyLWMzMWViMjIyOTJkNSIsImMiOjE3NTYzODE4MDIzMzQsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=138; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; _ga_K2ZZ7R3N94=GS2.2.s1756381802$o149$g1$t1756383351$j59$l0$h0; _scid_r=W8-0xXP7ZFdha-SMrsRja0pUcEGZTQSBabxyEg; _rdt_uuid=1737652790712.77713a27-3f55-4efc-b9dd-4a5047035018; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; amp_22bf3d_scope.gg=dYX848Mx6MnwtWxX5BQBS6.MTUyOTMyOTMy..1j3o86rpa.1j3o9m57n.277.e3.2la; _gid=GA1.2.1083646231.1756383450; _gat_UA-148508910-1=1",
        "mix-panel-uuid=e2bb5ed1-d93e-46d1-8c1e-6ac5fc599239; device-id=yBZgkB-WK8AqwR2tnydjeR; _ga=GA1.2.1930624276.1754850795; _scid=g6cIBXyGmFfp4kbgTWQsrpK8xIUeBgue; _ym_uid=1754850796618917766; _ym_d=1754850796; _hjSessionUser_2157523=eyJpZCI6IjBhZWVkNzhmLWI2YTMtNTlkYS1hNmNlLWU2MTBjZWZmNzAzYiIsImNyZWF0ZWQiOjE3NTQ4NTA3OTY0OTQsImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=0bf1a3cb-3b8d-4fc8-96d7-ac459796a05f; intercom-device-id-jw52e3zr=c840807d-3767-4fd3-ac41-944d0fb10983; locale=ru; locale-saved=true; clip-remove-modal-flag=true; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=138; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com; backend_sticky_session=http://10.44.2.18:80; mix-first-action=01.09.2025; _hjSession_2157523=eyJpZCI6IjhhNGQ4ZmE2LWUyMmUtNDg1Ni1hNWM5LTAxNTQ0YTczZjkxZCIsImMiOjE3NTY3MjQ0Njk3NDQsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MX0=; _ym_isad=2; _ScCbts=%5B%5D; _sctr=1%7C1756670400000; redirectUrl=/ru/clips/132758037; _ga=GA1.3.1930624276.1754850795; _gid=GA1.3.1000258148.1756724477; _gat_UA-148508910-1=1; mix-initial-first-action-sended=true; scope_session_id=087f3932-5262-4f1f-8890-22b09e6e2e6c; userId=132758037; _rdt_uuid=1754850796159.c33ffe49-4746-4c88-abe2-f670dbd257bd; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=mScIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw56g; _ga_K2ZZ7R3N94=GS2.2.s1756724469$o6$g1$t1756724493$j36$l0$h0; amp_22bf3d_scope.gg=yBZgkB-WK8AqwR2tnydjeR.MTMyNzU4MDM3..1j42f07t2.1j42f0une.1q.l.2f",
        "mix-panel-uuid=c9c5332a-c883-494d-8b46-b8a18f4a6a1b; device-id=MEcRzNW3kweBwHm3e6zmqI; _ga=GA1.2.788400622.1755537239; _scid=G-Ogx87jBenf1aM4i0RQNB74bEeJTdnr; _hjSessionUser_2157523=eyJpZCI6ImEzZjk5MWM4LTU0ZmEtNTg2Ni04ZGJiLTU4MjRmZDBiNjQ3ZiIsImNyZWF0ZWQiOjE3NTU1MzcyMzg3NzIsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; _ym_uid=1755898433920643818; _ym_d=1755898433; clip-remove-modal-flag=true; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=138; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://app.scope.gg/ru/matches; mix-panel-browser-referring-domain=app.scope.gg; backend_sticky_session=http://10.44.2.46:80; mix-first-action=01.09.2025; _hjSession_2157523=eyJpZCI6IjAyMTkzOTk2LWFlYTQtNDkxMy1iNjY5LWRiNTkyY2UwMzhhMyIsImMiOjE3NTY3MjQ0NzE5NzMsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; _ym_isad=2; _ScCbts=%5B%5D; _sctr=1%7C1756670400000; redirectUrl=/ru/clips/855190916; _ga=GA1.3.788400622.1755537239; _gid=GA1.3.1887353635.1756724501; _gat_UA-148508910-1=1; mix-initial-first-action-sended=true; scope_session_id=166ca270-8fff-4262-b504-77c13af04205; userId=855190916; _scid_r=KmOgx87jBenf1aM4i0RQNB74bEeJTdnrRjxteQ; _rdt_uuid=1755537235627.13256546-1922-4364-b376-9e9c3152d8be; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _ga_K2ZZ7R3N94=GS2.2.s1756724471$o3$g1$t1756724514$j17$l0$h0; amp_22bf3d_scope.gg=MEcRzNW3kweBwHm3e6zmqI.ODU1MTkwOTE2..1j42f097a.1j42f1k8d.u.d.1b");

    @Transactional
    public void processMatches() throws IOException {
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
            .limit(LIMIT)
            .collect(Collectors.toList());

        if (gameHistories.isEmpty()) {
            log.error("История игр пуста или не получена.");
            return;
        }

        // Шаг 3. Обработка каждой игры
        for (ScopeGGMatch game : gameHistories) {
            GameInfo gameDetail = apiClient.getGameInfo(game.getId());
            if (gameDetail == null || gameDetail.getPlayerStats() == null) {
                continue;
            }
            for (PlayerStat stat : gameDetail.getPlayerStats()) {
                PlayerName playerName = PlayerName.fromScopeggId(stat.getId());
                if (playerName == null) {
                    continue;
                }
                LocalDateTime finishedAt = DateService.parseDate(gameDetail.getFinishedAt());
                MatchKey key = new MatchKey(finishedAt, playerName, gameDetail.getMapName());
                if (existingMatchKeys.contains(key)) {
                    continue;
                }
                MatchType matchType = MatchType.fromScopegg(gameDetail.getDataSource());
                MatchItem matchItem = MatchItem.builder()
                    .playerName(playerName)
                    .date(finishedAt)
                    .rating(Math.round(stat.getHltvRating() * 100.0) / 100.0)
                    .threeKill(stat.getMulti3k())
                    .fourKill(stat.getMulti4k())
                    .ace(stat.getMulti5k())
                    .flash(stat.getFlashAssist())
                    .trade(stat.getTradeKillsSucceeded())
                    .smokeKill(stat.getSmokeKill())
                    .wallBang(stat.getWallBang())
                    .openKill(stat.getOpenKill())
                    .map(gameDetail.getMapName())
                    .result(game.getResult())
                    .clutchOne(stat.getClutchOne())
                    .clutchTwo(stat.getClutchTwo())
                    .clutchThree(stat.getClutchThree())
                    .clutchFour(stat.getClutchFour())
                    .clutchFive(stat.getClutchFive())
                    .type(matchType)
                    .build();


                log.info("Сохранение матча: {}", matchItem);
                addedMatches.add(matchItem);
                matchRepository.save(matchItem);
                existingMatchKeys.add(key);
            }
        }
        changingExcelService.addMatches(addedMatches);
    }

}
