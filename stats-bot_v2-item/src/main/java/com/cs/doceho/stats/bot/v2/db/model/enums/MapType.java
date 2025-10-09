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
  BREWERY("bre", "de_brewery", "Brewery"),
  GRAIL("gra", "de_grail", "Grail"),
  AGENCY("age", "cs_agency", "Agency"),
  DOGTOWN("dog", "de_dogtown", "Dogtown"),
  ROOFTOP("roof", "de_rooftop", "Rooftop"),
  GOLDEN("gold", "de_golden", "Golden"),
  PALACIO("pala", "de_palacio", "Palacio"),
  OVERPASS("over", "de_overpass", "Overpass");

  String name;
  String csName;
  String fullName;

  public static MapType fromName(String name) {
    for (MapType matchName : MapType.values()) {
      if (matchName.name.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

  public static MapType fromCSName(String name) {
    for (MapType matchName : MapType.values()) {
      if (matchName.csName.equals(name)) {
        return matchName;
      }
    }
    return null;
  }

}
