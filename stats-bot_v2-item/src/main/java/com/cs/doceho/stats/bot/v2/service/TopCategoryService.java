package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.service.utils.CalculationService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopCategoryService {

  MatchService matchService;
  CalculationService calculationService;

  public Player getTopRating() {
    return getTopPlayerByMetricWithAverage(MatchItem::getRating);
  }

  public Player getTopByOpenKill() {
    return getTopPlayerByMetric(MatchItem::getOpenKill);
  }

  public Player getTopByFlash() {
    return getTopPlayerByMetric(MatchItem::getFlash);
  }

  public Player getTopTrade() {
    return getTopPlayerByMetric(MatchItem::getTrade);
  }

  public Player getTopWallBang() {
    return getTopPlayerByMetric(MatchItem::getWallBang);
  }

  public Player getTopThreeKill() {
    return getTopPlayerByMetric(MatchItem::getThreeKill);
  }

  public Player getTopFourKill() {
    return getTopPlayerByMetric(MatchItem::getFourKill);
  }

  public Player getTopAce() {
    return getTopPlayerByMetric(MatchItem::getAce);
  }

  public Player getTopClutches() {
    return getTopPlayerByAggregator(playerMatches -> {
      String playerName = playerMatches.stream()
          .filter(match -> match != null
              && match.getPlayerName() != null
              && match.getPlayerName().getName() != null)
          .findFirst()
          .map(match -> match.getPlayerName().getName())
          .orElse("Unknown");

      int clutchOne = sumInt(playerMatches,
          match -> calculationService.safeInt(match.getClutchOne()));
      int clutchTwo = sumInt(playerMatches,
          match -> calculationService.safeInt(match.getClutchTwo()));
      int clutchThree = sumInt(playerMatches,
          match -> calculationService.safeInt(match.getClutchThree()));
      int clutchFour = sumInt(playerMatches,
          match -> calculationService.safeInt(match.getClutchFour()));
      int clutchFive = sumInt(playerMatches,
          match -> calculationService.safeInt(match.getClutchFive()));

      int totalClutches = clutchOne + clutchTwo + clutchThree + clutchFour + clutchFive;

      return Player.builder()
          .name(playerName)
          .matches(playerMatches.size())
          .clutchOne(clutchOne)
          .clutchTwo(clutchTwo)
          .clutchThree(clutchThree)
          .clutchFour(clutchFour)
          .clutchFive(clutchFive)
          .rating((double) totalClutches) // TODO: для каждого показателя своё поле заполнять
          .build();
    });
  }

  private Player getTopPlayerByMetric(ToDoubleFunction<MatchItem> metricFunction) {
    return getTopPlayerByAggregator(playerMatches -> {

      String playerName = playerMatches.stream()
          .filter(match -> match != null
              && match.getPlayerName() != null
              && match.getPlayerName().getName() != null)
          .findFirst()
          .map(match -> match.getPlayerName().getName())
          .orElse("Unknown");

      double totalMetricValue = playerMatches.stream()
          .filter(Objects::nonNull)
          .mapToDouble(match -> {
            try {
              return metricFunction.applyAsDouble(match);
            } catch (NullPointerException e) {
              return 0.0;
            }
          })
          .sum();

      return Player.builder()
          .name(playerName)
          .matches(playerMatches.size())
          .rating(totalMetricValue) //TODO: для каждого показателя своё поле заполнять
          .build();
    });
  }

  private Player getTopPlayerByMetricWithAverage(ToDoubleFunction<MatchItem> metricFunction) {
    return getTopPlayerByAggregator(playerMatches -> {
      double totalMetricValue = playerMatches.stream()
          .mapToDouble(metricFunction)
          .sum();

      return Player.builder()
          .name(playerMatches.get(0).getPlayerName().getName())
          .matches(playerMatches.size())
          .rating(totalMetricValue / playerMatches.size())
          .build();
    });
  }

  private Player getTopPlayerByAggregator(Function<List<MatchItem>, Player> aggregatorFunction) {
    List<MatchItem> matches = matchService.getAll();

    return matches.stream()
        .collect(Collectors.groupingBy(MatchItem::getPlayerName))
        .values().stream()
        .map(aggregatorFunction)
        .max(Comparator.comparing(Player::getRating))
        .orElse(null);
  }

  private int sumInt(List<MatchItem> playerMatches, ToIntFunction<MatchItem> mapper) {
    return playerMatches.stream()
        .mapToInt(mapper)
        .sum();
  }

}
