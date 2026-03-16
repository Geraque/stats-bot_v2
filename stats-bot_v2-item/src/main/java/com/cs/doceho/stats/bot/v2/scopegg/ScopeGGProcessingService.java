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

    static Integer LIMIT = 1;
    static List<String> COOKIES = List.of("locale=ru; mix-panel-uuid=ef8434d8-feb6-4ad1-bda7-88da67afd39c; _ga=GA1.2.1807339426.1769785219; _gcl_au=1.1.1971894622.1769785219; _scid=m-SwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9l; _ga=GA1.3.1807339426.1769785219; _hjSessionUser_2157523=eyJpZCI6ImJhZmI3NjgxLWRmZmMtNWJjOC1hZjVmLTQwYjg1YjhkOGQzMCIsImNyZWF0ZWQiOjE3NzAwMjY4NzExNDQsImV4aXN0aW5nIjp0cnVlfQ==; locale-saved=true; _ScCbts=%5B%5D; _sctr=1%7C1771790400000; scope_session_id=ac838815-105f-4919-bb76-b720d4559dd6; userId=152932932; _gid=GA1.2.2037905513.1771829721; matches-3785366331805663810=152932932; _ym_uid=1771846451515342707; _ym_d=1771846451; matches-3805006755541287071=152932932; matches-3805009031873954012=152932932; matches-3805004960244957548=152932932; matches-3805013481460072923=152932932; matches-3805011535839887827=152932932; matches-3805015570961662478=152932932; matches-3805017095675052272=152932932; mix-first-action=24.02.2026; mix-initial-first-action-sended=true; _hjSession_2157523=eyJpZCI6IjQyOWE1MDI4LTdmYzktNGNmMy1iNjk1LTU1ZDczNzRhNjUzYyIsImMiOjE3NzE5Mjk5ODMxODQsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; backend_sticky_session=http://10.44.25.86:80; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; _rdt_uuid=1769785218948.aa52d8d4-85cb-4f33-8c51-7bc3f4420e42; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=qWSwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9lQxf9Bw; amp_22bf3d_scope.gg=l7X_XBWtcOPTFR2OcfGOGQ.MTUyOTMyOTMy..1ji7k3m4h.1ji7l76s4.24.k.2o; _ga_K2ZZ7R3N94=GS2.2.s1771929982$o6$g1$t1771931159$j44$l0$h0",
        "mix-panel-uuid=e2bb5ed1-d93e-46d1-8c1e-6ac5fc599239; device-id=yBZgkB-WK8AqwR2tnydjeR; _ga=GA1.2.1930624276.1754850795; _scid=g6cIBXyGmFfp4kbgTWQsrpK8xIUeBgue; _ym_uid=1754850796618917766; _hjSessionUser_2157523=eyJpZCI6IjBhZWVkNzhmLWI2YTMtNTlkYS1hNmNlLWU2MTBjZWZmNzAzYiIsImNyZWF0ZWQiOjE3NTQ4NTA3OTY0OTQsImV4aXN0aW5nIjp0cnVlfQ==; intercom-id-jw52e3zr=0bf1a3cb-3b8d-4fc8-96d7-ac459796a05f; intercom-device-id-jw52e3zr=c840807d-3767-4fd3-ac41-944d0fb10983; locale=ru; locale-saved=true; clip-remove-modal-flag=true; _ga=GA1.3.1930624276.1754850795; _scid_r=micIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw5GA; _gcl_au=1.1.1319109683.1764156648; scope_session_id=1d742319-dbee-4b5c-b903-90748db3e261; _ym_d=1771859556; _ym_isad=2; _ScCbts=%5B%5D; _sctr=1%7C1771790400000; userId=132758037; _gid=GA1.2.983048540.1771863880; matches-3805039431652475201=132758037; matches-3805049557037875822=132758037; mix-first-action=24.02.2026; mix-initial-first-action-sended=true; backend_sticky_session=http://10.44.25.72:80; _hjSession_2157523=eyJpZCI6Ijk5Mjc5NTQyLTcwZmUtNDAzNy05NmUxLWRlNzgzYTk4OGRlYSIsImMiOjE3NzE5Mjk5OTkyNjUsInMiOjEsInIiOjEsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MX0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; matches-3805033850342474301=132758037; _scid_r=kicIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw6Lg; _rdt_uuid=1754850796159.c33ffe49-4746-4c88-abe2-f670dbd257bd; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; amp_22bf3d_scope.gg=yBZgkB-WK8AqwR2tnydjeR.MTMyNzU4MDM3..1ji7k45fu.1ji7l6coj.o7.b7.13e; _ga_K2ZZ7R3N94=GS2.2.s1771929999$o97$g1$t1771931133$j39$l0$h0",
        "mix-panel-uuid=c9c5332a-c883-494d-8b46-b8a18f4a6a1b; device-id=MEcRzNW3kweBwHm3e6zmqI; _ga=GA1.2.788400622.1755537239; _scid=G-Ogx87jBenf1aM4i0RQNB74bEeJTdnr; _hjSessionUser_2157523=eyJpZCI6ImEzZjk5MWM4LTU0ZmEtNTg2Ni04ZGJiLTU4MjRmZDBiNjQ3ZiIsImNyZWF0ZWQiOjE3NTU1MzcyMzg3NzIsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; _ym_uid=1755898433920643818; clip-remove-modal-flag=true; _ga=GA1.3.788400622.1755537239; _gcl_au=1.1.574723095.1765641788; _ym_d=1771931538; _ym_isad=2; _ScCbts=%5B%5D; _hjSession_2157523=eyJpZCI6Ijg2YmNmZTMzLTI1ZjctNDVmOS1hMGZmLTJmMzIwYjQxYWNjNyIsImMiOjE3NzE5MzE1MzgyODksInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=142; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://app.scope.gg/ru/matches/3796662568240546180/855190916; mix-panel-browser-referring-domain=app.scope.gg; mix-first-action=24.02.2026; redirectUrl=/ru/matches; _sctr=1%7C1771876800000; backend_sticky_session=http://10.44.25.94:80; scope_session_id=61087865-08ae-413c-9ed0-96b5a9f5906f; userId=855190916; matches-3792972697412043066=855190916; matches-3792969074607128720=855190916; _rdt_uuid=1755537235627.13256546-1922-4364-b376-9e9c3152d8be; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _scid_r=JmOgx87jBenf1aM4i0RQNB74bEeJTdnrRjxtSA; amp_22bf3d_scope.gg=MEcRzNW3kweBwHm3e6zmqI.ODU1MTkwOTE2..1ji7lj50m.1ji7lkoup.h0.7h.oh; _ga_K2ZZ7R3N94=GS2.2.s1771931538$o79$g1$t1771931608$j55$l0$h0");


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
