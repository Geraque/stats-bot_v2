package com.cs.doceho.stats.bot.v2.mapper;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.TopItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Top;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class MatchMapperConfigurer extends ConfigurableMapper {

  @Override
  public void configure(MapperFactory factory) {
    factory.classMap(MatchItem.class, Match.class)
        .field("id", "id")
        .field("date", "date")
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
            match.setPlayerName(matchItem.getPlayerName().getName());
            match.setType(matchItem.getType().getName());
            match.setMap(Optional.ofNullable(matchItem)
                .map(MatchItem::getMap)
                .map(MapType::getName)
                .orElse(null));

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
            top.setPlayerName(topItem.getPlayerName().getName());
          }
        }).register();
  }
}
