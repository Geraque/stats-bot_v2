package com.cs.doceho.stats.bot.v2.leetify.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClutchData {

  String steam64Id;
  int clutchesWon;
  int handicap;
}
