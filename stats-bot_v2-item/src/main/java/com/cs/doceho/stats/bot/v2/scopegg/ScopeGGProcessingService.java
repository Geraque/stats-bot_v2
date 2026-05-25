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

    static Integer LIMIT = 45;
    static List<String> COOKIES = List.of(
        "locale=ru; mix-panel-uuid=ef8434d8-feb6-4ad1-bda7-88da67afd39c; _ga=GA1.2.1807339426.1769785219; _scid=m-SwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9l; _ga=GA1.3.1807339426.1769785219; _hjSessionUser_2157523=eyJpZCI6ImJhZmI3NjgxLWRmZmMtNWJjOC1hZjVmLTQwYjg1YjhkOGQzMCIsImNyZWF0ZWQiOjE3NzAwMjY4NzExNDQsImV4aXN0aW5nIjp0cnVlfQ==; locale-saved=true; _ym_uid=1771846451515342707; _ym_d=1771846451; _hjDonePolls=1848450; clip-remove-modal-flag=true; _rdt_uuid=1769785218948.aa52d8d4-85cb-4f33-8c51-7bc3f4420e42; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _gcl_au=1.1.1134478136.1777920593; scope_session_id=3e1a8b51-eff9-4378-83ca-af754547fb79; userId=152932932; mix-first-action=25.05.2026; mix-initial-first-action-sended=true; _ga_K2ZZ7R3N94=GS2.2.s1779729806$o61$g0$t1779729806$j60$l0$h0; backend_sticky_session=d2ff4d360265fcf2; _scid_r=oWSwI2jdFQcsJ0kAiC_gtKr5fKeC7Q9lQxf9lg; _hjSession_2157523=eyJpZCI6IjgxOGJhY2EwLTEzYmMtNDYxNi04NzUyLTM5OTliNWU1YTY0MCIsImMiOjE3Nzk3Mjk4MDY2MjEsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=146; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=N/A; mix-panel-browser-referring-domain=N/A; _sctr=1%7C1779652800000; _gid=GA1.2.1194372383.1779729864; amp_22bf3d_scope.gg=l7X_XBWtcOPTFR2OcfGOGQ.MTUyOTMyOTMy..1jpg2jch6.1jpg2toh7.bg.4t.gd; _gat_UA-148508910-1=1",
        "mix-panel-uuid=e2bb5ed1-d93e-46d1-8c1e-6ac5fc599239; device-id=yBZgkB-WK8AqwR2tnydjeR; _ga=GA1.2.1930624276.1754850795; _scid=g6cIBXyGmFfp4kbgTWQsrpK8xIUeBgue; _ym_uid=1754850796618917766; _hjSessionUser_2157523=eyJpZCI6IjBhZWVkNzhmLWI2YTMtNTlkYS1hNmNlLWU2MTBjZWZmNzAzYiIsImNyZWF0ZWQiOjE3NTQ4NTA3OTY0OTQsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; clip-remove-modal-flag=true; _ga=GA1.3.1930624276.1754850795; _scid_r=micIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw5GA; _ym_d=1771859556; _gcl_au=1.1.814993102.1772176187; _hjDonePolls=1848450; _rdt_uuid=1754850796159.c33ffe49-4746-4c88-abe2-f670dbd257bd; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; scope_session_id=c267f375-737d-4c67-ab61-51e1a3b1248c; userId=132758037; _sctr=1%7C1779480000000; matches-3821538552815026570=132758037; _hjSession_2157523=eyJpZCI6ImQzMGZlZTY4LTExZjItNDA3OC04ZWFiLWNiNjhkMjgwMTU3MCIsImMiOjE3Nzk3Mjk2MTM3MDAsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-first-action=25.05.2026; mix-initial-first-action-sended=true; backend_sticky_session=99082961825cc702; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=146; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com; _gid=GA1.2.420544998.1779729617; matches-3821712509727932547=132758037; matches-3821714397366059139=132758037; matches-3821716147565232628=132758037; matches-3821717983663751754=132758037; _scid_r=pScIBXyGmFfp4kbgTWQsrpK8xIUeBgueCFw6mg; matches-3821719918546518298=132758037; _ga_K2ZZ7R3N94=GS2.2.s1779729613$o131$g1$t1779729730$j16$l0$h0; amp_22bf3d_scope.gg=yBZgkB-WK8AqwR2tnydjeR.MTMyNzU4MDM3..1jpg2dh0q.1jpg2uh3t.11d.fn.1h4",
        "mix-panel-uuid=c9c5332a-c883-494d-8b46-b8a18f4a6a1b; device-id=MEcRzNW3kweBwHm3e6zmqI; _ga=GA1.2.788400622.1755537239; _scid=G-Ogx87jBenf1aM4i0RQNB74bEeJTdnr; _hjSessionUser_2157523=eyJpZCI6ImEzZjk5MWM4LTU0ZmEtNTg2Ni04ZGJiLTU4MjRmZDBiNjQ3ZiIsImNyZWF0ZWQiOjE3NTU1MzcyMzg3NzIsImV4aXN0aW5nIjp0cnVlfQ==; locale=ru; locale-saved=true; _ym_uid=1755898433920643818; clip-remove-modal-flag=true; _ga=GA1.3.788400622.1755537239; _ym_d=1771931538; _gcl_au=1.1.1716721413.1773473956; _rdt_uuid=1755537235627.13256546-1922-4364-b376-9e9c3152d8be; _rdt_em=0000000000000000000000000000000000000000000000000000000000000001; _hjSession_2157523=eyJpZCI6IjEyOWRkYjc1LTQxYjUtNGNkMi1iMjM1LThlMGYzNTc2YmY3ZiIsImMiOjE3Nzk3Mjk4OTYyMTQsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; mix-panel-os=Windows; mix-panel-device=PC; mix-panel-browser=Chromium; mix-panel-browser-version=146; mix-panel-browser-screen-width=1920; mix-panel-browser-screen-height=1080; mix-panel-browser-referrer=https://steamcommunity.com/; mix-panel-browser-referring-domain=steamcommunity.com; mix-first-action=25.05.2026; redirectUrl=/ru/matches; _gid=GA1.3.1671212415.1779729898; backend_sticky_session=c18c18c4f568c7b6; _sctr=1%7C1779652800000; scope_session_id=7e6ca849-3552-4559-8348-2d88681ed9d8; userId=855190916; _gid=GA1.2.1671212415.1779729898; matches-3820419700949516770=855190916; matches-3820423184167993463=855190916; matches-3820427502757610084=855190916; matches-3820431696793174436=855190916; _scid_r=F2Ogx87jBenf1aM4i0RQNB74bEeJTdnrRjxukw; matches-3820434174989304017=855190916; _ga_K2ZZ7R3N94=GS2.2.s1779729896$o111$g1$t1779730036$j32$l0$h0; amp_22bf3d_scope.gg=MEcRzNW3kweBwHm3e6zmqI.ODU1MTkwOTE2..1jpg2m529.1jpg2v8uh.lp.b5.10u; _gat_UA-148508910-1=1");


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
