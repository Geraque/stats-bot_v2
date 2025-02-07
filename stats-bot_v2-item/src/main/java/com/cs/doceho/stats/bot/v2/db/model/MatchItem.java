package com.cs.doceho.stats.bot.v2.db.model;

import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;


@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
    @TypeDef(
        name = "pgsql_enum_player_name",
        typeClass = PostgreSQLEnumType.class,
        defaultForType = PlayerName.class
    ),
    @TypeDef(
        name = "pgsql_enum_match_type",
        typeClass = PostgreSQLEnumType.class,
        defaultForType = MatchType.class
    ),
    @TypeDef(
        name = "pgsql_enum_map_type",
        typeClass = PostgreSQLEnumType.class,
        defaultForType = MatchType.class
    )
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "uuid")
  @Type(type = "org.hibernate.type.PostgresUUIDType")
  UUID id;

  @Enumerated(EnumType.STRING)
  @Type(type = "pgsql_enum_player_name")
  @Column(name = "player_name", columnDefinition = "player_names")
  PlayerName playerName;

  @Column(name = "date")
  LocalDateTime date;

  @Column(name = "rating")
  Double rating;

  @Column(name = "smoke_kill")
  Integer smokeKill;

  @Column(name = "open_kill")
  Integer openKill;

  @Column(name = "three_kill")
  Integer threeKill;

  @Column(name = "four_kill")
  Integer fourKill;

  @Column(name = "ace")
  Integer ace;

  @Column(name = "flash")
  Integer flash;

  @Column(name = "trade")
  Integer trade;

  @Column(name = "wall_bang")
  Integer wallBang;

  @Column(name = "clutch_one")
  Integer clutchOne;

  @Column(name = "clutch_two")
  Integer clutchTwo;

  @Column(name = "clutch_three")
  Integer clutchThree;

  @Column(name = "clutch_four")
  Integer clutchFour;

  @Column(name = "clutch_five")
  Integer clutchFive;

  @Enumerated(EnumType.STRING)
  @Type(type = "pgsql_enum_match_type")
  @Column(name = "type", columnDefinition = "match_types")
  MatchType type;

  @Enumerated(EnumType.STRING)
  @Type(type = "pgsql_enum_map_type")
  @Column(name = "map", columnDefinition = "map_types")
  MapType map;

}
