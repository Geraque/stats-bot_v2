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

    static Integer LIMIT = 4;
    static List<String> COOKIES = List.of("locale=ru; mix-panel-uuid=117b12a0-332c-4a40-b4c9-0beb4ea59b1c; _ym_uid=1766665656302288153; _ym_d=1766665656; _ga=GA1.2.526984837.1766665656; _scid=loM3qpdFY0_t4TXHdSVMhbGY8y35_U2O; _ga=GA1.3.526984837.1766665656; scope_session_id=1f956f33-3640-4eb1-84e3-c47450677835; userId=152932932; _hjSessionUser_2157523=eyJpZCI6IjFlYzg5MDJmLTQ0NDAtNWMwOS05Njk4LWNlMTIzYjg0ZTUwNiIsImNyZWF0ZWQiOjE3NjY2NjU2NTY1MTYsImV4aXN0aW5nIjp0cnVlfQ==; _sctr=1%7C1766606400000; locale-saved=true; _gcl_au=1.1.1458407594.1766736945; clip-remove-modal-flag=true; matches-3794248850307285250=152932932; matches-3794244944034529425=152932932; mix-first-action=02.01.2026; mix-initial-first-action-sended=true; _hjSession_2157523=eyJpZCI6ImQzM2Q0MDMyLTAzZjQtNDZhNS1hMjhiLWNhY2RkY2ZmMDMwOCIsImMiOjE3NjczNzM4NjQzMjUsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; backend_sticky_session=http://10.44.1.233:80; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; _gid=GA1.2.439162106.1767373867; matches-3795379487563055711=152932932; matches-3795392720357294335=152932932; matches-3795383866282213629=152932932; matches-3795388159102026556=152932932; _rdt_uuid=1766665656252.62061ba7-044b-4c79-8bc2-696e0a44a249; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=qAM3qpdFY0_t4TXHdSVMhbGY8y35_U2OMB1Z3A; _ga_K2ZZ7R3N94=GS2.2.s1767373863$o4$g1$t1767375411$j60$l0$h0; amp_22bf3d_scope.gg=baCi9oz7ZZOYWlUBD2eoeq.MTUyOTMyOTMy..1jdvr1v71.1jdvt5ih2.1c.n.23",
        "mix-panel-uuid=e2bb5ed1-d93e-46d1-8c1e-6ac5fc599239; device-id=yBZgkB-WK8AqwR2tnydjeR; _ga=GA1.2.1930624276.1754850795; _scid=g6cIBXyGmFfp4kbgTWQsrpK8xIUeBgue; _ym_uid=1754850796618917766; _ym_d=1754850796; _hjSessionUser_2157523=eyJpZCI6IjBhZWVkNzhmLWI2YTMtNTlkYS1hNmNlLWU2MTBjZWZmNzAzYiIsImNyZWF0ZWQiOjE3NTQ4NTA3OTY0OTQsImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=0bf1a3cb-3b8d-4fc8-96d7-ac459796a05f; intercom-device-id-jw52e3zr=c840807d-3767-4fd3-ac41-944d0fb10983; locale=ru; locale-saved=true; clip-remove-modal-flag=true; _ga=GA1.3.1930624276.1754850795; _scid_r=micIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw5GA; _gcl_au=1.1.1319109683.1764156648; _sctr=1%7C1765310400000; _ym_isad=2; _hjSession_2157523=eyJpZCI6ImNiN2UyM2VlLTcxNTgtNDUwNy05OWY5LWZlZWM5YWUwNWM0YiIsImMiOjE3NjczNzU0Mzg1MjgsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; matches-3791102627849175344=132758037; _gid=GA1.2.899676371.1767375441; _gid=GA1.3.899676371.1767375441; scope_session_id=4be81768-9f2a-4829-aa8c-a754d6770dc5; userId=132758037; matches-3794252720072818877=132758037; mix-first-action=02.01.2026; mix-initial-first-action-sended=true; _scid_r=kScIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw68A; _rdt_uuid=1754850796159.c33ffe49-4746-4c88-abe2-f670dbd257bd; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; backend_sticky_session=http://10.44.1.227:80; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com; _gat_UA-148508910-1=1; _ga_K2ZZ7R3N94=GS2.2.s1767375438$o85$g1$t1767376218$j52$l0$h0; amp_22bf3d_scope.gg=yBZgkB-WK8AqwR2tnydjeR.MTMyNzU4MDM3..1jdvsi1hr.1jdvt9qqd.ku.9n.ul",
        "mix-panel-uuid=c9c5332a-c883-494d-8b46-b8a18f4a6a1b; device-id=MEcRzNW3kweBwHm3e6zmqI; _ga=GA1.2.788400622.1755537239; _scid=G-Ogx87jBenf1aM4i0RQNB74bEeJTdnr; _hjSessionUser_2157523=eyJpZCI6ImEzZjk5MWM4LTU0ZmEtNTg2Ni04ZGJiLTU4MjRmZDBiNjQ3ZiIsImNyZWF0ZWQiOjE3NTU1MzcyMzg3NzIsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; _ym_uid=1755898433920643818; _ym_d=1755898433; clip-remove-modal-flag=true; _ga=GA1.3.788400622.1755537239; _sctr=1%7C1765569600000; _gcl_au=1.1.574723095.1765641788; _hjSession_2157523=eyJpZCI6ImVjMzFhNDQzLWJiNzAtNGU3MS04N2YxLTNjZjZlYjk1MTU4YiIsImMiOjE3NjczNzU0OTQxMDEsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; matches-3792972697412043066=855190916; _gid=GA1.2.499786855.1767375496; _gid=GA1.3.499786855.1767375496; scope_session_id=3de0f3d2-a50b-4d7b-96f8-f7d030544790; userId=855190916; mix-first-action=02.01.2026; mix-initial-first-action-sended=true; backend_sticky_session=http://10.44.1.248:80; _rdt_uuid=1755537235627.13256546-1922-4364-b376-9e9c3152d8be; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=PWOgx87jBenf1aM4i0RQNB74bEeJTdnrRjxtIQ; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com; _ga_K2ZZ7R3N94=GS2.2.s1767375494$o71$g1$t1767375859$j53$l0$h0; amp_22bf3d_scope.gg=MEcRzNW3kweBwHm3e6zmqI.ODU1MTkwOTE2..1jdvsjo0b.1jdvsus5e.ff.6k.m3; _gat_UA-148508910-1=1");

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
