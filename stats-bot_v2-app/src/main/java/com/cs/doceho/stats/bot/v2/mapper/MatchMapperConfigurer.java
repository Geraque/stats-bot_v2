package com.cs.doceho.stats.bot.v2.mapper;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.model.Top;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.model.enums.PlayerName;
import org.springframework.stereotype.Component;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
@Slf4j
@AllArgsConstructor
public class MatchMapperConfigurer extends ConfigurableMapper {

  @Override
  public void configure(MapperFactory factory) {
    factory.classMap(MatchItem.class, Match.class)
        .field("id", "id")
        .field("data", "data")
        .field("rating", "rating")
        .field("smokeKill", "smokeKill")
        .field("openKill", "openKill")
        .field("threeKill", "threeKill")
        .field("fourKill", "fourKill")
        .field("ace", "ace")
        .field("flash", "flash")
        .field("trade", "trade")
        .field("wallBang", "wallBang")
        .field("clutchOne", "clutchOne")
        .field("clutchTwo", "clutchTwo")
        .field("clutchThree", "clutchThree")
        .field("clutchFour", "clutchFour")
        .field("clutchFive", "clutchFive")
        .customize(new CustomMapper<>() {
          @Override
          public void mapAtoB(MatchItem matchItem, Match match,
              MappingContext context) {
            match.setName(PlayerName.valueOf(matchItem.getName().name()));
            match.setType(MatchType.valueOf(matchItem.getType().name()));

          }
        }).register();

    factory.classMap(TopItem.class, Top.class)
        .field("id", "id")
        .field("year", "year")
        .field("rating", "rating")
        .field("place", "place")
        .customize(new CustomMapper<>() {
          @Override
          public void mapAtoB(TopItem topItem, Top top,
              MappingContext context) {
            top.setName(topItem.getName().getName());
          }
        }).register();
  }
}
