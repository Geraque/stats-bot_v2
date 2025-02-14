package com.cs.doceho.stats.bot.v2.leetify.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameDetail {

  String finishedAt;
  String dataSource;
  String mapName;
  List<PlayerStat> playerStats;
  List<PlayerRank> matchmakingGameStats;
  List<Integer> teamScores;

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class PlayerRank {

    Integer rank;
  }
}
