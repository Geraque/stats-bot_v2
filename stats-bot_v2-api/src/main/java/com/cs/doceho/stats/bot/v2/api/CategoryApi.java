package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.model.Player;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(value = "Запросы к основной таблице со всеми матчами", tags = {"match"})
@RequestMapping("/category")
public interface CategoryApi {

  @ApiOperation(value = "Получение топ 1 по рейтингу",
      nickname = "getRating", tags = {"match"})
  @GetMapping("/rating")
  ResponseEntity<Player> getRating();

  @ApiOperation(value = "Получение топ 1 по опен киллам",
      nickname = "getOpenKill", tags = {"match"})
  @GetMapping("/open-kill")
  ResponseEntity<Player> getOpenKill();

  @ApiOperation(value = "Получение топ 1 по флешкам",
      nickname = "getFlash", tags = {"match"})
  @GetMapping("/flash")
  ResponseEntity<Player> getFlash();

  @ApiOperation(value = "Получение топ 1 по размену",
      nickname = "getTrade", tags = {"match"})
  @GetMapping("/trade")
  ResponseEntity<Player> getTrade();

  @ApiOperation(value = "Получение топ 1 по прострелам",
      nickname = "getWallBang", tags = {"match"})
  @GetMapping("/wall-bang")
  ResponseEntity<Player> getWallBang();

  @ApiOperation(value = "Получение топ 1 по трипл киллам",
      nickname = "getThreeKill", tags = {"match"})
  @GetMapping("/three-kill")
  ResponseEntity<Player> getThreeKill();

  @ApiOperation(value = "Получение топ 1 по квадро киллам",
      nickname = "getFourKill", tags = {"match"})
  @GetMapping("/four-kill")
  ResponseEntity<Player> getFourKill();

  @ApiOperation(value = "Получение топ 1 по эйсам",
      nickname = "getAce", tags = {"match"})
  @GetMapping("/ace")
  ResponseEntity<Player> getAce();

  @ApiOperation(value = "Получение топ 1 по клатчам",
      nickname = "getClutches", tags = {"match"})
  @GetMapping("/clutches")
  ResponseEntity<Player> getClutches();

}
