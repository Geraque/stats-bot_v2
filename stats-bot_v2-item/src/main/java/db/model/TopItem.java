package db.model;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import db.model.enums.PlayerName;
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
@Table(name = "tops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
    @TypeDef(
        name = "pgsql_enum_player_name",
        typeClass = PostgreSQLEnumType.class,
        defaultForType = PlayerName.class
    )
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "uuid")
  @Type(type = "org.hibernate.type.PostgresUUIDType")
  UUID id;

  @Enumerated(EnumType.STRING)
  @Type(type = "pgsql_enum_player_name")
  @Column(name = "name", columnDefinition = "player_names")
  PlayerName name;

  @Column(name = "year")
  Integer year;

  @Column(name = "rating")
  Double rating;

  @Column(name = "place")
  Integer place;

}
