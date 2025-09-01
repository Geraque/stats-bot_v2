package com.cs.doceho.stats.bot.v2.service.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;

@UtilityClass
@Slf4j
public class DateService {

    public LocalDateTime parseDate(String dateStr) {
        try {
            return OffsetDateTime.parse(dateStr).toLocalDateTime();
        } catch (Exception e) {
            log.error("Ошибка парсинга даты: {}", dateStr);
            return null;
        }
    }

    public static LocalDateTime parseDate(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneOffset.ofHours(4))
            .toLocalDateTime();
    }
}
