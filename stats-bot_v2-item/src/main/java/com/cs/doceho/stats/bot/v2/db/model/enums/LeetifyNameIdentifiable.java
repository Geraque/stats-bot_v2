package com.cs.doceho.stats.bot.v2.db.model.enums;

/**
 * Интерфейс для перечислений, которые могут быть идентифицированы по имени Leetify
 */
public interface LeetifyNameIdentifiable<T extends Enum<T>> {

  String getLeetifyName();

  /**
   * Находит элемент перечисления по имени Leetify
   *
   * @param name имя Leetify для поиска
   * @return найденный элемент или null
   */
  static <E extends Enum<E> & LeetifyNameIdentifiable<E>> E fromLeetifyName(Class<E> enumClass,
      String name) {
    for (E value : enumClass.getEnumConstants()) {
      if (value.getLeetifyName().equals(name)) {
        return value;
      }
    }
    return null;
  }
} 