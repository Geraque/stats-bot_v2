package com.cs.doceho.stats.bot.v2.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Player {

  String name;
  Integer matches;
  Double rating;
  Integer smokeKill;
  Integer openKill;
  Integer threeKill;
  Integer fourKill;
  Integer ace;
  Integer flash;
  Integer trade;
  Integer wallBang;
  Integer clutchOne;
  Integer clutchTwo;
  Integer clutchThree;
  Integer clutchFour;
  Integer clutchFive;
}
