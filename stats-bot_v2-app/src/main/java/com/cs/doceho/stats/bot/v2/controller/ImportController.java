package com.cs.doceho.stats.bot.v2.controller;

import com.cs.doceho.stats.bot.v2.api.ImportApi;
import com.cs.doceho.stats.bot.v2.leetify.LeetifyProcessingService;
import java.io.IOException;
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

  LeetifyProcessingService leetifyService;

  @Override
  public ResponseEntity<?> getFromLeetify() throws IOException {
    leetifyService.processMatches();
    return ResponseEntity.ok().build();
  }
}
