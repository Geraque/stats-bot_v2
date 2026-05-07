package com.cs.doceho.stats.bot.v2.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchSqlFormatterTest {

  private final MatchSqlFormatter formatter = new MatchSqlFormatter();

  // Recommended usage for Flyway data refresh:
  // create src/main/resources/db/migration/R__fill_matches.sql
  // and paste the result of:
  // curl "http://localhost:8098/match/export/sql?limit=1000&onConflictDoNothing=true"
  @Test
  void shouldFormatMatchesAsInsertSql() {
    MatchItem first = match(
        "6c96b73b-5a91-4420-b740-bfe0c9a55496",
        PlayerName.BLACK_VISION,
        LocalDateTime.of(2026, 4, 11, 20, 9, 1),
        1.68
    );
    MatchItem second = match(
        "5ab1c554-a53d-4df6-9dac-6fb6b3966ed7",
        PlayerName.GLOXINIA,
        LocalDateTime.of(2026, 4, 11, 20, 9, 1),
        1.21
    );

    String sql = formatter.format(List.of(first, second), false);

    assertThat(sql).startsWith(
        "INSERT INTO public.matches (id,player_name,\"date\",rating,smoke_kill,open_kill,three_kill,"
    );
    assertThat(sql).contains("'6c96b73b-5a91-4420-b740-bfe0c9a55496'::uuid");
    assertThat(sql).contains("'BLACK_VISION'::public.\"player_names\"");
    assertThat(sql).contains("'2026-04-11 20:09:01.000'");
    assertThat(sql).contains("'PREMIER'::public.\"match_types\"");
    assertThat(sql).contains("'NUKE'::public.\"map_types\"");
    assertThat(sql).contains("'WIN'::public.\"match_results\"");
    assertThat(sql).contains(")," + System.lineSeparator() + "     ('5ab1c554-a53d-4df6-9dac-6fb6b3966ed7'::uuid");
    assertThat(sql).endsWith(";");
    assertThat(sql).doesNotEndWith(",");
  }

  @Test
  void shouldAppendOnConflictDoNothingWhenEnabled() {
    MatchItem first = match(
        "6c96b73b-5a91-4420-b740-bfe0c9a55496",
        PlayerName.BLACK_VISION,
        LocalDateTime.of(2026, 4, 11, 20, 9, 1),
        1.68
    );
    MatchItem second = match(
        "5ab1c554-a53d-4df6-9dac-6fb6b3966ed7",
        PlayerName.GLOXINIA,
        LocalDateTime.of(2026, 4, 11, 20, 9, 1),
        1.21
    );

    String sql = formatter.format(List.of(first, second), true);

    assertThat(sql).contains(")," + System.lineSeparator() + "     ('5ab1c554-a53d-4df6-9dac-6fb6b3966ed7'::uuid");
    assertThat(sql).endsWith(System.lineSeparator() + "ON CONFLICT (id) DO NOTHING;");
    assertThat(sql).doesNotEndWith(",;");
  }

  @Test
  void shouldReturnCommentWhenNoMatchesFound() {
    assertThat(formatter.format(List.of(), false)).isEqualTo("-- no matches found");
    assertThat(formatter.format(List.of(), true)).isEqualTo("-- no matches found");
  }

  private MatchItem match(String id, PlayerName playerName, LocalDateTime date, double rating) {
    return MatchItem.builder()
        .id(UUID.fromString(id))
        .playerName(playerName)
        .date(date)
        .rating(rating)
        .smokeKill(2)
        .openKill(1)
        .threeKill(1)
        .fourKill(0)
        .ace(1)
        .flash(0)
        .trade(6)
        .wallBang(2)
        .clutchOne(0)
        .clutchTwo(0)
        .clutchThree(0)
        .clutchFour(0)
        .clutchFive(1)
        .type(MatchType.PREMIER)
        .map(MapType.NUKE)
        .result(MatchResult.WIN)
        .build();
  }
}
