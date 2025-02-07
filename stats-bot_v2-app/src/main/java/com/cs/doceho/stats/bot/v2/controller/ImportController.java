package com.cs.doceho.stats.bot.v2.controller;

import com.cs.doceho.stats.bot.v2.api.ImportApi;
import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.service.ChangingExcelService;
import com.cs.doceho.stats.bot.v2.service.LeetifyService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportController implements ImportApi {

  LeetifyService leetifyService;
  ChangingExcelService changingExcelService;

  @Override
  public ResponseEntity<?> getFromLeetify() throws IOException {
//    leetifyService.processMatches();
    LocalDateTime now = LocalDateTime.now();
    MatchItem matchItem = MatchItem.builder()
        .playerName(PlayerName.fromName(PlayerName.DESMOND.getName()))
        .date(now)
        .rating(1.29)
        .smokeKill(null)
        .openKill(7)
        .threeKill(0)
        .fourKill(0)
        .ace(0)
        .flash(0)
        .trade(4)
        .wallBang(null)
        .clutchOne(2)
        .clutchTwo(2)
        .clutchThree(0)
        .clutchFour(0)
        .clutchFive(0)
        .type(MatchType.WINGMAN)
        .map(MapType.VERTIGO)
        .build();
    MatchItem matchItem2 = MatchItem.builder()
        .playerName(PlayerName.fromName(PlayerName.BLACK_VISION.getName()))
        .date(now)
        .rating(0.75)
        .smokeKill(null)
        .openKill(4)
        .threeKill(0)
        .fourKill(0)
        .ace(0)
        .flash(0)
        .trade(2)
        .wallBang(null)
        .clutchOne(2)
        .clutchTwo(0)
        .clutchThree(0)
        .clutchFour(0)
        .clutchFive(0)
        .type(MatchType.WINGMAN)
        .map(MapType.VERTIGO)
        .build();
    changingExcelService.addMatches(List.of(matchItem, matchItem2));
//    changingExcelService.addMatches(List.of(matchItem));
    return ResponseEntity.ok().build();
  }
}
