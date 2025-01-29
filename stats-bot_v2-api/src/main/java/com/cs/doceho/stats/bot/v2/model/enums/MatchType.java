package com.cs.doceho.stats.bot.v2.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MatchType {
  MATCH_MAKING("ММ"),
  WINGMAN("Напарники"),
  PREMIER("Премьер");

  String name;
}
