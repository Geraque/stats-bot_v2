package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.cs.doceho.stats.bot.v2.model.Match;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping()
public interface MatchApi {

  @GetMapping("/matches")
  List<Match> getAllMatches();

  //Получение матчей по id
  @GetMapping("/matches/{id}")
  ResponseEntity<Match> getMatchById(@PathVariable(value = "id") Long matchId)
      throws ResourceNotFoundException;

  //Получение матчей по игроку
  @GetMapping("/getbyname/{name}")
  ResponseEntity<List<Match>> getMatchByName(@PathVariable(value = "name") String matchName)
      throws ResourceNotFoundException;

  //Получение всей статистики игрока
  @GetMapping("/getplayerstats/{name}")
  ResponseEntity<double[]> getPlayerStats(@PathVariable(value = "name") String matchName)
      throws ResourceNotFoundException;


  //Получение всей статистики всех игроков
  @GetMapping("/getallstats")
  ResponseEntity<double[]> getAllStats()
      throws ResourceNotFoundException;

  //Получение топ 1 по рейтингу
  @GetMapping("/getrating")
  ResponseEntity<String[]> getRating()
      throws ResourceNotFoundException;

  //Получение топ 1 по опен киллам
  @GetMapping("/getopenkill")
  ResponseEntity<String[]> getOpenKill()
      throws ResourceNotFoundException;

  //Получение топ 1 по флешкам
  @GetMapping("/getflash")
  ResponseEntity<String[]> getFlash()
      throws ResourceNotFoundException;

  //Получение топ 1 по размену
  @GetMapping("/gettrade")
  ResponseEntity<String[]> getTrade()
      throws ResourceNotFoundException;

  //Получение топ 1 по прострелам
  @GetMapping("/getwallbang")
  ResponseEntity<String[]> getWallBang()
      throws ResourceNotFoundException;

  //Получение топ 1 по трипл киллам
  @GetMapping("/getthreekill")
  ResponseEntity<String[]> getThreeKill()
      throws ResourceNotFoundException;

  //Получение топ 1 по квадро киллам
  @GetMapping("/getfourkill")
  ResponseEntity<String[]> getFourKill()
      throws ResourceNotFoundException;

  //Получение топ 1 по эйсам
  @GetMapping("/getace")
  ResponseEntity<String[]> getAce()
      throws ResourceNotFoundException;

  //Получение топ 1 по клатчам
  @GetMapping("/getclutches")
  ResponseEntity<String[]> getClutches()
      throws ResourceNotFoundException;

  //Создание матча
  @PostMapping("/matches")
  Match createMatch(@Valid @RequestBody Match Match);


  //Изменение матча по id
  @PutMapping("/matches/{id}")
  ResponseEntity<Match> updateMatch(@PathVariable(value = "id") Long desmondId,
      @Valid @RequestBody Match MatchDetails) throws ResourceNotFoundException;

  //Удаление матча по id
  @DeleteMapping("/matches/{id}")
  Map<String, Boolean> deleteMatch(@PathVariable(value = "id") Long desmondId)
      throws ResourceNotFoundException;

}
