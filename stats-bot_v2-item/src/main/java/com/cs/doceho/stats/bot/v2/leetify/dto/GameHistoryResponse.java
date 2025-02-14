package com.cs.doceho.stats.bot.v2.leetify.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO для ответа /history. Извлекается поле games – список объектов, каждый из которых содержит
 * только id игры.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameHistoryResponse {

  List<GameIdWrapper> games;

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GameIdWrapper {

    String id;
    String finishedAt;
  }
}


