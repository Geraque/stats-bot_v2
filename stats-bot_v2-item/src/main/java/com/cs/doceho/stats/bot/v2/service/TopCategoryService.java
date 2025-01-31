package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.model.Player;
import java.util.Comparator;
import java.util.List;
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
      int clutchOne = sumInt(playerMatches, MatchItem::getClutchOne);
      int clutchTwo = sumInt(playerMatches, MatchItem::getClutchTwo);
      int clutchThree = sumInt(playerMatches, MatchItem::getClutchThree);
      int clutchFour = sumInt(playerMatches, MatchItem::getClutchFour);
      int clutchFive = sumInt(playerMatches, MatchItem::getClutchFive);

      int totalClutches = clutchOne + clutchTwo + clutchThree + clutchFour + clutchFive;

      return Player.builder()
          .name(playerMatches.get(0).getPlayerName().getName())
          .matches(playerMatches.size())
          .clutchOne(clutchOne)
          .clutchTwo(clutchTwo)
          .clutchThree(clutchThree)
          .clutchFour(clutchFour)
          .clutchFive(clutchFive)
          .rating((double) totalClutches)
          .build();
    });
  }

  private Player getTopPlayerByMetric(ToDoubleFunction<MatchItem> metricFunction) {
    return getTopPlayerByAggregator(playerMatches -> {
      double totalMetricValue = playerMatches.stream()
          .mapToDouble(metricFunction)
          .sum();

      return Player.builder()
          .name(playerMatches.get(0).getPlayerName().getName())
          .matches(playerMatches.size())
          .rating(totalMetricValue)
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
