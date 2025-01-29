package mapper;

import db.model.TopItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import model.Top;
import model.enums.PlayerName;
import org.springframework.stereotype.Component;
import ru.ds.orika.MapperFactoryConfigurer;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class TopMapperConfigurer implements MapperFactoryConfigurer {

  @Override
  public void configure(MapperFactory factory) {
    factory.classMap(TopItem.class, Top.class)
        .field("id", "id")
        .field("year", "year")
        .field("rating", "rating")
        .field("place", "place")
        .customize(new CustomMapper<>() {
          @Override
          public void mapAtoB(TopItem topItem, Top top,
              MappingContext context) {
            top.setName(PlayerName.valueOf(topItem.getName().name()));
          }
        }).register();
  }
}
