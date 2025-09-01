package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PlayerName {
    DESMOND("Desmond\uD83C\uDFB4", List.of("76561198113198660", "76561198815456644", "76561198093023765"), List.of("152932932", "132758037", "855190916")),
    GLOXINIA("Gloxinia", List.of("76561198182310891"), List.of("222045163")),
    BLACK_VISION("BlackVision", List.of("76561198342639103"), List.of("382373375")),
    KOPFIRE("Kopfire", List.of("76561198154347514"), List.of("заменить")),
    B4ONE("B4one", List.of("76561198158794811"), List.of("заменить")),
    NEKIT("221w33", List.of("76561198096869637"), List.of("заменить")),
    MVFOREVER01("MVforever01", List.of("notFound"), List.of("заменить")),
    WOLF_SMXL("Wolf_SMXL", List.of("76561198341666571"), List.of("заменить")),
    CHELIKOPUKICH("Chelicopukich", List.of("76561198110403185"), List.of("заменить")),
    WESDIA("Wesdia", List.of("76561198330586281"), List.of("заменить"));

    String name;
    List<String> ids;
    List<String> scopeggIds;

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

    public static PlayerName fromScopeggId(String id) {
        if (id == null) return null;
        for (PlayerName player : values()) {
            if (player.getScopeggIds().contains(id)) {
                return player;
            }
        }
        return null;
    }
}
