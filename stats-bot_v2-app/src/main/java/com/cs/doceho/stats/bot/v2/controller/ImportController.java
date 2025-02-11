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

  @Override
  public ResponseEntity<?> getFromLeetify() throws IOException {
    leetifyService.processMatches();
    return ResponseEntity.ok().build();
  }
}
