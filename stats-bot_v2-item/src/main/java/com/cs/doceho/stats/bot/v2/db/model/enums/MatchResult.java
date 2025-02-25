package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MatchResult implements NameIdentifiable<MatchResult> {
  WIN("Победа"),
  LOSE("Поражение"),
  DRAW("Ничья");

  String name;

  public static MatchResult fromName(String name) {
    return NameIdentifiable.fromName(MatchResult.class, name);
  }

}
