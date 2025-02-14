package com.cs.doceho.stats.bot.v2.service.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DateService {

  public LocalDateTime parseDate(String dateStr) {
    try {
      return OffsetDateTime.parse(dateStr).toLocalDateTime();
    } catch (Exception e) {
      log.error("Ошибка парсинга даты: {}", dateStr);
      return null;
    }
  }
}
