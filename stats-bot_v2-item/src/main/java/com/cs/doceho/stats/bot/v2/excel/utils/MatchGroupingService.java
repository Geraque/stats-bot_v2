package com.cs.doceho.stats.bot.v2.excel.utils;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchGroupingService {

  static DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  static DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss");

  static Map<MatchType, String> SHEET_NAME_MAP = Map.of(MatchType.WINGMAN, "2х2 2025",
      MatchType.MATCH_MAKING, "2025 mm",
      MatchType.PREMIER, "Premier 2025",
      MatchType.FACEIT, "Faceit 2025");

  public Map<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> groupMatches(
      List<MatchItem> matchList) {
    Map<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> grouped = new HashMap<>();

    for (MatchItem match : matchList) {
      String sheetName = SHEET_NAME_MAP.get(match.getType());
      if (sheetName == null) {
        log.warn("Тип матча не распознан, пропуск: {}", match);
        continue;
      }
      String dayKey = match.getDate().format(dayFormatter);
      String matchKey = match.getDate().format(fullFormatter) + "|" + match.getMap().getName();
      grouped.computeIfAbsent(sheetName, k -> new TreeMap<>(
              Comparator.comparing((String key) -> LocalDate.parse(key, dayFormatter))))
          .computeIfAbsent(dayKey, k -> new HashMap<>())
          .computeIfAbsent(matchKey, k -> new HashMap<>())
          .put(match.getPlayerName(), match);
    }
    return grouped;
  }
}
