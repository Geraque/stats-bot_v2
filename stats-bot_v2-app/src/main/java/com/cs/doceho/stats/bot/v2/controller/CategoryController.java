package com.cs.doceho.stats.bot.v2.controller;


import com.cs.doceho.stats.bot.v2.api.CategoryApi;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.service.TopCategoryService;
import io.vavr.control.Try;
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
public class CategoryController implements CategoryApi {

  TopCategoryService topCategoryService;

  @Override
  public ResponseEntity<Player> getRating() {
    return Try.of(topCategoryService::getTopRating)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getOpenKill() {
    return Try.of(topCategoryService::getTopByOpenKill)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getFlash() {
    return Try.of(topCategoryService::getTopByFlash)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getTrade() {
    return Try.of(topCategoryService::getTopTrade)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getWallBang() {
    return Try.of(topCategoryService::getTopWallBang)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getThreeKill() {
    return Try.of(topCategoryService::getTopThreeKill)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getFourKill() {
    return Try.of(topCategoryService::getTopFourKill)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getAce() {
    return Try.of(topCategoryService::getTopAce)
        .map(ResponseEntity::ok)
        .get();
  }

  @Override
  public ResponseEntity<Player> getClutches() {
    return Try.of(topCategoryService::getTopClutches)
        .map(ResponseEntity::ok)
        .get();
  }
}
