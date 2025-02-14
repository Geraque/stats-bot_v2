package com.cs.doceho.stats.bot.v2.leetify.dto;

import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
/**
 * Вспомогательный класс для проверки дубликатов матчей. Сравнение происходит по date, playerName
 * и rating.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchKey {

  LocalDateTime date;
  PlayerName playerName;
  Double rating;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchKey that = (MatchKey) o;
    return Objects.equals(date, that.date) &&
        playerName == that.playerName &&
        Objects.equals(rating, that.rating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, playerName, rating);
  }
}
