package com.cs.doceho.stats.bot.v2.db.model.enums;

/**
 * Интерфейс для перечислений, которые могут быть идентифицированы по имени
 */
public interface NameIdentifiable<T extends Enum<T>> {

  String getName();

  /**
   * Находит элемент перечисления по имени
   *
   * @param name имя для поиска
   * @return найденный элемент или null
   */
  static <E extends Enum<E> & NameIdentifiable<E>> E fromName(Class<E> enumClass, String name) {
    for (E value : enumClass.getEnumConstants()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return null;
  }
}