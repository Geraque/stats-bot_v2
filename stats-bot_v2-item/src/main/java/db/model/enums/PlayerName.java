package db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PlayerName {
  DESMOND("Desmond"),
  BLACK_VISION("BlackVision"),
  GLOXINIA("Gloxinia"),
  B4ONE("B4one"),
  MAXIM("221w33"),
  KOPFIRE("Kopfire"),
  WOLF_SMXL("Wolf_SMXL");

  String name;
}
