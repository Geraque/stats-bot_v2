package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.MatchApi;
import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.service.ChangingExcelService;
import com.cs.doceho.stats.bot.v2.service.LeetifyService;
import com.cs.doceho.stats.bot.v2.service.MatchService;
import io.vavr.control.Try;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchController implements MatchApi {

  MatchService matchService;
  LeetifyService leetifyService;
  ChangingExcelService changingExcelService;
  MapperFacade mapper;

  @Override
  public List<Match> getAllMatches() throws IOException {
//    leetifyService.processMatches();
    MatchItem matchItem = MatchItem.builder()
        .playerName(PlayerName.fromName(PlayerName.DESMOND.getName()))
        .date(LocalDateTime.now())
        .rating(5.0)
        .smokeKill(6)
        .openKill(7)
        .threeKill(8)
        .fourKill(9)
        .ace(9)
        .flash(8)
        .trade(7)
        .wallBang(6)
        .clutchOne(5)
        .clutchTwo(4)
        .clutchThree(3)
        .clutchFour(2)
        .clutchFive(1)
        .type(MatchType.MATCH_MAKING)
        .build();
    changingExcelService.addMatches(List.of(matchItem));
    return matchService.getAll().stream()
        .map(it -> mapper.map(it, Match.class))
        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<Match> getMatchById(UUID matchId) {
    return Try.of(() -> matchService.get(matchId))
        .map(reference -> mapper.map(reference, Match.class))
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<List<Match>> getMatchByName(String playerName) {
    return Try.of(() -> matchService.getByName(playerName))
        .map(reference -> reference.stream()
            .map(it -> mapper.map(it, Match.class))
            .collect(Collectors.toList()))
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getPlayerStats(String playerName) {
    return Try.of(() -> matchService.getPlayerStats(playerName))
        .map(ResponseEntity::ok)
        .get();
  }


  @Override
  public ResponseEntity<List<Player>> getAllStats() {
    return Try.of(matchService::getAllStats)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Match> create(Match match) {
    return Try.of(() -> matchService.create(match))
        .map(reference -> mapper.map(reference, Match.class))
        .map(ResponseEntity::ok)
        .recover(NoSuchElementException.class, ResponseEntity.notFound().build())
        .get();
  }

  @Override
  public ResponseEntity<Match> update(UUID matchId,
      Match matchDetails) {
    return Try.of(() -> matchService.update(matchId, matchDetails))
        .map(reference -> mapper.map(reference, Match.class))
        .map(ResponseEntity::ok)
        .recover(NoSuchElementException.class, ResponseEntity.notFound().build())
        .get();
  }

  @Override
  public ResponseEntity<?> delete(UUID id) {
    matchService.delete(id);
    return ResponseEntity.ok().build();
  }
}
