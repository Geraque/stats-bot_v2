package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.model.Player;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(value = "Топ по категориям", tags = {"category"})
@RequestMapping("/category")
public interface CategoryApi {

    @ApiOperation(value = "Получение топ 1 по рейтингу",
        nickname = "getRating", tags = {"category"})
    @GetMapping("/rating")
    ResponseEntity<Player> getRating();

    @ApiOperation(value = "Получение топ 1 по опен киллам",
        nickname = "getOpenKill", tags = {"category"})
    @GetMapping("/open-kill")
    ResponseEntity<Player> getOpenKill();

    @ApiOperation(value = "Получение топ 1 по флешкам",
        nickname = "getFlash", tags = {"category"})
    @GetMapping("/flash")
    ResponseEntity<Player> getFlash();

    @ApiOperation(value = "Получение топ 1 по размену",
        nickname = "getTrade", tags = {"category"})
    @GetMapping("/trade")
    ResponseEntity<Player> getTrade();

    @ApiOperation(value = "Получение топ 1 по прострелам",
        nickname = "getWallBang", tags = {"category"})
    @GetMapping("/wall-bang")
    ResponseEntity<Player> getWallBang();

    @ApiOperation(value = "Получение топ 1 по трипл киллам",
        nickname = "getThreeKill", tags = {"category"})
    @GetMapping("/three-kill")
    ResponseEntity<Player> getThreeKill();

    @ApiOperation(value = "Получение топ 1 по квадро киллам",
        nickname = "getFourKill", tags = {"category"})
    @GetMapping("/four-kill")
    ResponseEntity<Player> getFourKill();

    @ApiOperation(value = "Получение топ 1 по эйсам",
        nickname = "getAce", tags = {"category"})
    @GetMapping("/ace")
    ResponseEntity<Player> getAce();

    @ApiOperation(value = "Получение топ 1 по клатчам",
        nickname = "getClutches", tags = {"category"})
    @GetMapping("/clutches")
    ResponseEntity<Player> getClutches();

}
