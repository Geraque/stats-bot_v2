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
    ScopeGGApi scopeGGApi;

    static Integer LIMIT = 9;
    static List<String> COOKIES = List.of("locale=ru; mix-panel-uuid=ef8434d8-feb6-4ad1-bda7-88da67afd39c; _ga=GA1.2.1807339426.1769785219; _gcl_au=1.1.1971894622.1769785219; _scid=m-SwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9l; _ga=GA1.3.1807339426.1769785219; _hjSessionUser_2157523=eyJpZCI6ImJhZmI3NjgxLWRmZmMtNWJjOC1hZjVmLTQwYjg1YjhkOGQzMCIsImNyZWF0ZWQiOjE3NzAwMjY4NzExNDQsImV4aXN0aW5nIjp0cnVlfQ==; locale-saved=true; _ym_uid=1771846451515342707; _ym_d=1771846451; _hjDonePolls=1848450; clip-remove-modal-flag=true; _sctr=1%7C1776542400000; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=144; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; mix-first-action=19.04.2026; backend_sticky_session=http://10.44.34.8:80; _gid=GA1.3.942727985.1776599008; scope_session_id=c21d2a15-8d85-43ef-8505-d264ad7e91f8; _rdt_uuid=1769785218948.aa52d8d4-85cb-4f33-8c51-7bc3f4420e42; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=uGSwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9lQxf9jg; userId=152932932; _ga_K2ZZ7R3N94=GS2.2.s1776598979$o55$g1$t1776599038$j1$l0$h0; amp_22bf3d_scope.gg=l7X_XBWtcOPTFR2OcfGOGQ.MTUyOTMyOTMy..1jmioqvja.1jmioruua.an.4a.f1",
        "mix-panel-uuid=e2bb5ed1-d93e-46d1-8c1e-6ac5fc599239; device-id=yBZgkB-WK8AqwR2tnydjeR; _ga=GA1.2.1930624276.1754850795; _scid=g6cIBXyGmFfp4kbgTWQsrpK8xIUeBgue; _ym_uid=1754850796618917766; _hjSessionUser_2157523=eyJpZCI6IjBhZWVkNzhmLWI2YTMtNTlkYS1hNmNlLWU2MTBjZWZmNzAzYiIsImNyZWF0ZWQiOjE3NTQ4NTA3OTY0OTQsImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=0bf1a3cb-3b8d-4fc8-96d7-ac459796a05f; intercom-device-id-jw52e3zr=c840807d-3767-4fd3-ac41-944d0fb10983; locale=ru; locale-saved=true; clip-remove-modal-flag=true; _ga=GA1.3.1930624276.1754850795; _scid_r=micIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw5GA; _ym_d=1771859556; _gcl_au=1.1.814993102.1772176187; _hjDonePolls=1848450; _sctr=1%7C1776542400000; _gid=GA1.3.1350936373.1776599091; scope_session_id=94a6a959-eb90-4333-bb61-a97edde40344; userId=132758037; _gid=GA1.2.1350936373.1776599091; mix-first-action=19.04.2026; mix-initial-first-action-sended=true; _rdt_uuid=1754850796159.c33ffe49-4746-4c88-abe2-f670dbd257bd; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=mScIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw6ng; _ga_K2ZZ7R3N94=GS2.2.s1776603261$o128$g0$t1776603261$j60$l0$h0; _hjSession_2157523=eyJpZCI6ImZjZDhkMDRiLWJjMzYtNGFjOS05MWQ3LWMxNzc3YWNkMjEwMyIsImMiOjE3NzY2MDMyNjEzODksInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; backend_sticky_session=http://10.44.34.12:80; amp_22bf3d_scope.gg=yBZgkB-WK8AqwR2tnydjeR.MTMyNzU4MDM3..1jmissq69.1jmissr77.105.f3.1f8; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=144; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com",
        "mix-panel-uuid=c9c5332a-c883-494d-8b46-b8a18f4a6a1b; device-id=MEcRzNW3kweBwHm3e6zmqI; _ga=GA1.2.788400622.1755537239; _scid=G-Ogx87jBenf1aM4i0RQNB74bEeJTdnr; _hjSessionUser_2157523=eyJpZCI6ImEzZjk5MWM4LTU0ZmEtNTg2Ni04ZGJiLTU4MjRmZDBiNjQ3ZiIsImNyZWF0ZWQiOjE3NTU1MzcyMzg3NzIsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; _ym_uid=1755898433920643818; clip-remove-modal-flag=true; _ga=GA1.3.788400622.1755537239; _ym_d=1771931538; _gcl_au=1.1.1716721413.1773473956; scope_session_id=a3a40719-6bea-48d2-adac-5b18cfe3d993; userId=855190916; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=144; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; backend_sticky_session=http://10.44.34.9:80; mix-first-action=19.04.2026; mix-initial-first-action-sended=true; _sctr=1%7C1776542400000; _scid_r=LWOgx87jBenf1aM4i0RQNB74bEeJTdnrRjxuig; _rdt_uuid=1755537235627.13256546-1922-4364-b376-9e9c3152d8be; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _ga_K2ZZ7R3N94=GS2.2.s1776599094$o108$g1$t1776599126$j28$l0$h0; amp_22bf3d_scope.gg=MEcRzNW3kweBwHm3e6zmqI.ODU1MTkwOTE2..1jmispui1.1jmispui1.kt.ah.ve; _gid=GA1.2.1862414795.1776603168; _gat_UA-148508910-1=1; _hjSession_2157523=eyJpZCI6IjdhM2U3ZmZjLTdhMzctNDBmZS1iYjEyLTliZWQ3OWVhMjA3ZiIsImMiOjE3NzY2MDMxNzQ5NzUsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; matches-3813774718675189957=855190916");


    @Transactional
    public void addMatch(String id) throws IOException {
        List<MatchItem> addedMatches = processMatches(id);
        matchRepository.saveAll(addedMatches);
        changingExcelService.addMatches(addedMatches);
    }

    @Transactional
    public void addLastMatches() throws IOException {
        List<MatchItem> addedMatches = new ArrayList<>();
        List<ScopeGGMatch> gameHistories = COOKIES.stream()
            .flatMap(token -> scopeGGApi.getHistory(token).stream())
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
        for (ScopeGGMatch game : gameHistories) {
            List<MatchItem> matchItems = processMatches(game.getId());
            matchRepository.saveAll(matchItems);
            addedMatches.addAll(matchItems);
        }
        changingExcelService.addMatches(addedMatches);
    }


    public List<MatchItem> processMatches(String id) {
        //Проверка на дубликаты
        List<MatchItem> existingMatches = matchRepository.findTop20ByOrderByDateDesc();
        Set<MatchKey> existingMatchKeys = new HashSet<>();
        for (MatchItem match : existingMatches) {
            existingMatchKeys.add(
                new MatchKey(match.getDate(), match.getPlayerName(), match.getMap()));
        }

        List<MatchItem> addedMatches = new ArrayList<>();
        GameInfo gameDetail = scopeGGApi.getGameInfo(id);
        if (gameDetail == null || gameDetail.getPlayerStats() == null) {
            return null;
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
                .result(gameDetail.getResult())
                .clutchOne(stat.getClutchOne())
                .clutchTwo(stat.getClutchTwo())
                .clutchThree(stat.getClutchThree())
                .clutchFour(stat.getClutchFour())
                .clutchFive(stat.getClutchFive())
                .type(matchType)
                .build();

            log.info("Сохранение матча: {}", matchItem);
            addedMatches.add(matchItem);
        }
        return addedMatches;
    }

}
