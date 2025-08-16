package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MatchType {
  MATCH_MAKING("ММ", "matchmaking_competitive"),
  WINGMAN("Напарники", "matchmaking_wingman"),
  PREMIER("Премьер", "matchmaking"),
  FACEIT("Faceit", "faceit");

  String name;
  String leetifyName;

  public static MatchType fromName(String name) {
    for (MatchType matchName : MatchType.values()) {
      if (matchName.name.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

  public static MatchType fromLeetifyName(String name) {
    for (MatchType matchName : MatchType.values()) {
      if (matchName.leetifyName.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

}
