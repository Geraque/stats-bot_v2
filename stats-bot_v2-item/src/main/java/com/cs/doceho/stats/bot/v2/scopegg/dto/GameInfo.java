package com.cs.doceho.stats.bot.v2.scopegg.dto;

import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameInfo {
    MapType mapName;
    long finishedAt;
    String dataSource;
    List<PlayerStat> playerStats;
}


