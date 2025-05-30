package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MapType {
  DUST_II("dust", "de_dust2", "Dust"),
  MIRAGE("mir", "de_mirage", "Mirage"),
  INFERNO("inf", "de_inferno", "Inferno"),
  ANCIENT("anc", "de_ancient", "Ancient"),
  OFFICE("off", "cs_office", "Office"),
  VERTIGO("vert", "de_vertigo", "Vertigo"),
  TRAIN("train", "de_train", "Train"),
  ANUBIS("anu", "de_anubis", "Anubis"),
  NUKE("nuke", "de_nuke", "Nuke"),
  ITALY("italy", "cs_italy", "Italy"),
  EDIN("edin", "de_edin", "Edin"),
  WHISTLE("whi", "de_whistle", "Whistle"),
  PALAIS("pal", "de_palais", "Palais"),
  BASALT("bas", "de_basalt", "Basalt"),
  OVERPASS("over", "de_overpass", "Overpass");

  String name;
  String leetifyName;
  String fullName;

  public static MapType fromName(String name) {
    for (MapType matchName : MapType.values()) {
      if (matchName.name.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

  public static MapType fromLeetifyName(String name) {
    for (MapType matchName : MapType.values()) {
      if (matchName.leetifyName.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

}
