package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PlayerName {
  DESMOND("Desmond"),
  BLACK_VISION("BlackVision"),
  GLOXINIA("Gloxinia"),
  B4ONE("B4one"),
  NEKIT("221w33"),
  KOPFIRE("Kopfire"),
  WOLF_SMXL("Wolf_SMXL");

  String name;

  public static PlayerName fromName(String name) {
    for (PlayerName playerName : PlayerName.values()) {
      if (playerName.name.equals(name)) {
        return playerName;
      }
    }
    return null;
  }
}
