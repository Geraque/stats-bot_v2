package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = "Match endpoints", tags = {"match"})
@RequestMapping("/match")
public interface MatchApi {

  @ApiOperation(value = "Get all matches", nickname = "getAllMatches", tags = {"match"})
  @GetMapping
  List<Match> getAllMatches() throws Exception;

  @ApiOperation(value = "Get match by id", nickname = "getMatchById", tags = {"match"})
  @GetMapping("/{id}")
  ResponseEntity<Match> getMatchById(@PathVariable(value = "id") UUID matchId);

  @ApiOperation(value = "Get matches by player", nickname = "getMatchByName", tags = {"match"})
  @GetMapping("/player/{name}")
  ResponseEntity<List<Match>> getMatchByName(@PathVariable(value = "name") String playerName);

  @ApiOperation(value = "Get player stats", nickname = "getPlayerStats", tags = {"match"})
  @GetMapping("/player-stats/{name}")
  ResponseEntity<Player> getPlayerStats(@PathVariable(value = "name") String playerName);

  @ApiOperation(value = "Get all player stats", nickname = "getAllStats", tags = {"match"})
  @GetMapping("/all-stats")
  ResponseEntity<List<Player>> getAllStats();

  @ApiOperation(value = "Export latest matches as SQL", nickname = "exportMatchesSql", tags = {"match"})
  @GetMapping(value = "/export/sql", produces = {"text/plain", "application/sql"})
  ResponseEntity<String> exportMatchesSql(
      @RequestParam(value = "limit", defaultValue = "10") int limit,
      @RequestParam(value = "onConflictDoNothing", defaultValue = "false") boolean onConflictDoNothing);

  @ApiOperation(value = "Create match", nickname = "createMatch", tags = {"match"})
  @PostMapping
  ResponseEntity<Match> create(@Valid @RequestBody Match match);

  @ApiOperation(value = "Update match", nickname = "updateMatch", tags = {"match"})
  @PutMapping("/{id}")
  ResponseEntity<Match> update(@PathVariable(value = "id") UUID id,
      @Valid @RequestBody Match matchDetails);

  @ApiOperation(value = "Delete match", nickname = "deleteMatch", tags = {"match"})
  @DeleteMapping("/{id}")
  ResponseEntity<?> delete(@PathVariable(value = "id") UUID id);
}
