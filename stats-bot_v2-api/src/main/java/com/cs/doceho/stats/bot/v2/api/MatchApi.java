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

@Api(value = "Запросы к основной таблице со всеми матчами", tags = {"match"})
@RequestMapping("/match")
public interface MatchApi {

  @ApiOperation(value = "Получение всех матчей",
      nickname = "getAllMatches", tags = {"match"})
  @GetMapping
  List<Match> getAllMatches() throws Exception;

  @ApiOperation(value = "Получение матчей по id",
      nickname = "getMatchById", tags = {"match"})
  @GetMapping("/{id}")
  ResponseEntity<Match> getMatchById(@PathVariable(value = "id") UUID matchId);

  @ApiOperation(value = "Получение матчей по игроку",
      nickname = "getMatchByName", tags = {"match"})
  @GetMapping("/player/{name}")
  ResponseEntity<List<Match>> getMatchByName(@PathVariable(value = "name") String playerName);

  @ApiOperation(value = "Получение всей статистики игрока",
      nickname = "getPlayerStats", tags = {"match"})
  @GetMapping("/player-stats/{name}")
  ResponseEntity<Player> getPlayerStats(@PathVariable(value = "name") String playerName);

  @ApiOperation(value = "Получение всей статистики всех игроков",
      nickname = "getAllStats", tags = {"match"})
  @GetMapping("/all-stats")
  ResponseEntity<List<Player>> getAllStats();

  @ApiOperation(value = "Создание матча",
      nickname = "createMatch", tags = {"match"})
  @PostMapping
  ResponseEntity<Match> create(@Valid @RequestBody Match match);

  @ApiOperation(value = "Изменение матча по id",
      nickname = "updateMatch", tags = {"match"})
  @PutMapping("/{id}")
  ResponseEntity<Match> update(@PathVariable(value = "id") UUID Id,
      @Valid @RequestBody Match MatchDetails);

  @ApiOperation(value = "Удаление матча по id",
      nickname = "deleteMatch", tags = {"match"})
  @DeleteMapping("/{id}")
  ResponseEntity<?> delete(@PathVariable(value = "id") UUID Id);

}
