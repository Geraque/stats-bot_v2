package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MatchResult {
  WIN("Победа"), //TODO Неправильно подсчитывается, если кто-то сдаётся при одинаковом счёте
  LOSE("Поражение"),
  DRAW("Ничья");

  String name;

  public static MapType fromName(String name) {
    for (MapType matchName : MapType.values()) {
      if (matchName.getName().equals(name)) {
        return matchName;
      }
    }
    return null;
  }

}
