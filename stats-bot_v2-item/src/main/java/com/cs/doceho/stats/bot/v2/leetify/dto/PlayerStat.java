package com.cs.doceho.stats.bot.v2.leetify.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStat {

  String name;
  String steam64Id;
  Double hltvRating;
  Integer multi3k;
  Integer multi4k;
  Integer multi5k;
  Integer flashAssist;
  Integer tradeKillsSucceeded;
}
