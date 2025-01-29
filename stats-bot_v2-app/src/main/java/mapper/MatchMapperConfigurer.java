package mapper;

import db.model.MatchItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import model.Match;
import model.enums.MatchType;
import model.enums.PlayerName;
import org.springframework.stereotype.Component;
import ru.ds.orika.MapperFactoryConfigurer;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class MatchMapperConfigurer implements MapperFactoryConfigurer {

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
  }
}
