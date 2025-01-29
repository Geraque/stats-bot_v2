package model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import model.enums.MatchType;
import model.enums.PlayerName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Match {

  UUID id;

  PlayerName name;

  LocalDate data;

  Double rating;

  Integer smokeKill;

  Integer openKill;

  Integer threeKill;

  Integer fourKill;

  Integer ace;

  Integer flash;

  Integer trade;

  Integer wallBang;

  Integer clutchOne;

  Integer clutchTwo;

  Integer clutchThree;

  Integer clutchFour;

  Integer clutchFive;

  MatchType type;

}
