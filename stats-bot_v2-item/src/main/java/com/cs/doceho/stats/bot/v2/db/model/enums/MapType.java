package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MapType implements NameIdentifiable<MapType>, LeetifyNameIdentifiable<MapType> {
  DUST_II("dust", "de_dust2"),
  MIRAGE("mir", "de_mirage"),
  INFERNO("inf", "de_inferno"),
  ANCIENT("anc", "de_ancient"),
  OFFICE("off", "cs_office"),
  VERTIGO("vert", "de_vertigo"),
  TRAIN("train", "de_train"),
  ANUBIS("anu", "de_anubis"),
  NUKE("nuke", "de_nuke"),
  ITALY("italy", "cs_italy"),
  EDIN("edin", "de_edin"),
  WHISTLE("whi", "de_whistle"),
  PALAIS("pal", "de_palais"),
  BASALT("bas", "de_basalt"),
  OVERPASS("over", "de_overpass");

  String name;
  String leetifyName;

  public static MapType fromName(String name) {
    return NameIdentifiable.fromName(MapType.class, name);
  }

  public static MapType fromLeetifyName(String name) {
    return LeetifyNameIdentifiable.fromLeetifyName(MapType.class, name);
  }
}
