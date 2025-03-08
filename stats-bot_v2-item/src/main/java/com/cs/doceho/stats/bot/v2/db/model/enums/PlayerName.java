package com.cs.doceho.stats.bot.v2.db.model.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PlayerName {
  DESMOND("Desmond\uD83C\uDFB4", Arrays.asList("76561198113198660", "76561198815456644", "76561198093023765")),
  GLOXINIA("Gloxinia", Collections.singletonList("76561198182310891")),
  BLACK_VISION("BlackVision", Collections.singletonList("76561198342639103")),
  KOPFIRE("Kopfire", Collections.singletonList("76561198154347514")),
  B4ONE("B4one",Collections.singletonList("76561198158794811")),
  NEKIT("221w33",Collections.singletonList("76561198096869637")),
  MVFOREVER01("MVforever01",Collections.singletonList("notFound")),
  WOLF_SMXL("Wolf_SMXL", Collections.singletonList("76561198341666571")),
  WESDIA("Wesdia", Collections.singletonList("76561198330586281"));

  String name;
  List<String> ids;

  public static PlayerName fromName(String name) {
    for (PlayerName playerName : PlayerName.values()) {
      if (playerName.name.equals(name)) {
        return playerName;
      }
    }
    return null;
  }

  public static PlayerName fromId(String id) {
    if (id == null) return null;
    for (PlayerName player : values()) {
      if (player.getIds().contains(id)) {
        return player;
      }
    }
    return null;
  }
}
