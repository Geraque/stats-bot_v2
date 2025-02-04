package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.MatchApi;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.service.LeetifyService;
import com.cs.doceho.stats.bot.v2.service.MatchService;
import io.vavr.control.Try;
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
  MapperFacade mapper;

  @Override
  public List<Match> getAllMatches() {
    leetifyService.processMatches();
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
