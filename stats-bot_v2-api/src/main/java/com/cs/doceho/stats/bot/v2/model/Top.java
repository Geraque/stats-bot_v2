package com.cs.doceho.stats.bot.v2.model;


import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.cs.doceho.stats.bot.v2.model.enums.PlayerName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Top {

  UUID id;

  PlayerName name;

  Integer year;

  Double rating;

  Integer place;

}
