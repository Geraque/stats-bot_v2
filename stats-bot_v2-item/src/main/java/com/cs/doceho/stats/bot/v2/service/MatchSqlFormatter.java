package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MatchSqlFormatter {

  private static final String HEADER =
      "INSERT INTO public.matches (id,player_name,\"date\",rating,smoke_kill,open_kill,three_kill,"
          + "four_kill,ace,flash,trade,wall_bang,clutch_one,clutch_two,clutch_three,clutch_four,"
          + "clutch_five,\"type\",\"map\",\"result\") VALUES";
  private static final String ON_CONFLICT_DO_NOTHING = "ON CONFLICT (id) DO NOTHING;";
  private static final String NO_MATCHES = "-- no matches found";
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  public String format(List<MatchItem> matches, boolean onConflictDoNothing) {
    if (matches == null || matches.isEmpty()) {
      return NO_MATCHES;
    }

    return HEADER + System.lineSeparator() + matches.stream()
        .map(this::formatRow)
        .collect(Collectors.joining("," + System.lineSeparator(), "", formatSuffix(onConflictDoNothing)));
  }

  private String formatSuffix(boolean onConflictDoNothing) {
    return onConflictDoNothing
        ? System.lineSeparator() + ON_CONFLICT_DO_NOTHING
        : ";";
  }

  private String formatRow(MatchItem match) {
    return "     (" + castUuid(match.getId())
        + "," + castEnum(match.getPlayerName(), "player_names")
        + "," + quoteDate(match.getDate())
        + "," + formatDecimal(match.getRating())
        + "," + formatInteger(match.getSmokeKill())
        + "," + formatInteger(match.getOpenKill())
        + "," + formatInteger(match.getThreeKill())
        + "," + formatInteger(match.getFourKill())
        + "," + formatInteger(match.getAce())
        + "," + formatInteger(match.getFlash())
        + "," + formatInteger(match.getTrade())
        + "," + formatInteger(match.getWallBang())
        + "," + formatInteger(match.getClutchOne())
        + "," + formatInteger(match.getClutchTwo())
        + "," + formatInteger(match.getClutchThree())
        + "," + formatInteger(match.getClutchFour())
        + "," + formatInteger(match.getClutchFive())
        + "," + castEnum(match.getType(), "match_types")
        + "," + castEnum(match.getMap(), "map_types")
        + "," + castEnum(match.getResult(), "match_results")
        + ")";
  }

  private String castUuid(Object value) {
    if (value == null) {
      return "NULL";
    }
    return "'" + escapeSql(value.toString()) + "'::uuid";
  }

  private String castEnum(Enum<?> value, String typeName) {
    if (value == null) {
      return "NULL";
    }
    return "'" + escapeSql(value.name()) + "'::public.\"" + typeName + "\"";
  }

  private String quoteDate(LocalDateTime value) {
    if (value == null) {
      return "NULL";
    }
    return "'" + DATE_FORMATTER.format(value) + "'";
  }

  private String formatDecimal(Double value) {
    if (value == null) {
      return "NULL";
    }
    return BigDecimal.valueOf(value).toPlainString();
  }

  private String formatInteger(Integer value) {
    return value == null ? "NULL" : value.toString();
  }

  private String escapeSql(String value) {
    return value.replace("'", "''");
  }
}
