package com.cs.doceho.stats.bot.v2.scopegg.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStat {
    String id;                 // ключ в scoreboard_values
    Double hltvRating;         // Rating2
    Integer smokeKill;         // highlights.KillsThroughSmoke.Value
    Integer openKill;          // Basic.OpenKills
    Integer multi3k;           // highlights.TripleKills.Value
    Integer multi4k;           // highlights.QuadrupleKills.Value
    Integer multi5k;           // highlights.PentaKills.Value
    Integer clutchOne;         // счит. из clutches.*.*.Won[].EnemiesCount == 1
    Integer clutchTwo;         // == 2
    Integer clutchThree;       // == 3
    Integer clutchFour;        // == 4
    Integer clutchFive;        // == 5
    Integer flashAssist;       // highlights.FlashAssistsReal.Value
    Integer tradeKillsSucceeded; // Basic.TradeKills
    Integer WallBang;          // highlights.WallbangKills.Value
}
