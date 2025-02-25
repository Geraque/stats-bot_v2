package com.cs.doceho.stats.bot.v2.excel;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangingExcelService {

  UpdateLastRowService updateAverageFormula;
  ExcelWriter excelWriter;
  MatchGroupingService matchGroupingService;

  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/Statistics.xlsx";

  // Map of sheet names to map cell references for each map
  private static final Map<String, Map<String, String>> MAP_CELL_REFERENCES = Map.ofEntries(
      new SimpleEntry<>("2025 mm", Map.ofEntries(
          new SimpleEntry<>("DUST_II", "BV1"),
          new SimpleEntry<>("ANCIENT", "B4"),
          new SimpleEntry<>("MIRAGE", "CA1"),
          new SimpleEntry<>("OFFICE", "CA4"),
          new SimpleEntry<>("INFERNO", "CF1"),
          new SimpleEntry<>("VERTIGO", "CF4"),
          new SimpleEntry<>("TRAIN", "CK1"),
          new SimpleEntry<>("ANUBIS", "CK4"),
          new SimpleEntry<>("NUKE", "CP1"),
          new SimpleEntry<>("ITALY", "CP4"),
          new SimpleEntry<>("EDIN", "CU1"),
          new SimpleEntry<>("OVERPASS", "CU4"),
          new SimpleEntry<>("BASALT", "CZ1")
      )),
      new SimpleEntry<>("Premier 2025", Map.ofEntries(
          new SimpleEntry<>("DUST_II", "BV1"),
          new SimpleEntry<>("ANCIENT", "B4"),
          new SimpleEntry<>("MIRAGE", "CA1"),
          new SimpleEntry<>("OFFICE", "CA4"),
          new SimpleEntry<>("INFERNO", "CF1"),
          new SimpleEntry<>("VERTIGO", "CF4"),
          new SimpleEntry<>("TRAIN", "CK1"),
          new SimpleEntry<>("ANUBIS", "CK4"),
          new SimpleEntry<>("NUKE", "CP1"),
          new SimpleEntry<>("ITALY", "CP4"),
          new SimpleEntry<>("EDIN", "CU1"),
          new SimpleEntry<>("OVERPASS", "CU4"),
          new SimpleEntry<>("BASALT", "CZ1")
      )),
      new SimpleEntry<>("Faceit 2025", Map.ofEntries(
          new SimpleEntry<>("INFERNO", "H3"),
          new SimpleEntry<>("NUKE", "L3"),
          new SimpleEntry<>("WHISTLE", "P3"),
          new SimpleEntry<>("PALAIS", "T4"),
          new SimpleEntry<>("OVERPASS", "X3"),
          new SimpleEntry<>("VERTIGO", "AB3")
      ))
  );

  static Map<MatchType, String> SHEET_NAME_MAP = Map.of(MatchType.WINGMAN, "2х2 2025",
      MatchType.MATCH_MAKING, "2025 mm",
      MatchType.PREMIER, "Premier 2025",
      MatchType.FACEIT, "Faceit 2025");


  public void addMatches(List<MatchItem> matchList) throws IOException {
    XSSFWorkbook workbook = excelWriter.readWorkbook(FILE_PATH);

    // Группировка матчей
    Map<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> grouped = matchGroupingService.groupMatches(
        matchList);

    // Обновление статистики по картам
    updateMapStatistics(workbook, matchList);

    // Запись данных в Excel
    for (Map.Entry<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> sheetEntry : grouped.entrySet()) {
      String sheetName = sheetEntry.getKey();
      Map<String, Map<String, Map<PlayerName, MatchItem>>> dayGroups = sheetEntry.getValue();
      excelWriter.processSheet(sheetName, workbook, dayGroups);
    }

    // Обновление формул
    for (String sheetName : grouped.keySet()) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet != null) {
        updateAverageFormula.apply(sheet);
      }
    }

    // Сохранение изменений в файл
    log.info("Данные записаны в excel");
    excelWriter.saveWorkbook(FILE_PATH, workbook);
  }

  /**
   * Обновляет статистику по картам в Excel файле
   * @param workbook Excel файл
   * @param matchList список матчей для обновления статистики
   */
  private void updateMapStatistics(XSSFWorkbook workbook, List<MatchItem> matchList) {
    for (MatchItem match : matchList) {
      String sheetName = SHEET_NAME_MAP.get(match.getType());
      String mapName = match.getMap().name();
      
      // Проверяем, есть ли такой лист и карта в нашем маппинге
      if (!MAP_CELL_REFERENCES.containsKey(sheetName) || 
          !MAP_CELL_REFERENCES.get(sheetName).containsKey(mapName)) {
        log.warn("Не найдена ячейка для карты {} в листе {}", mapName, sheetName);
        continue;
      }
      
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        log.warn("Лист {} не найден в Excel файле", sheetName);
        continue;
      }
      
      String cellReference = MAP_CELL_REFERENCES.get(sheetName).get(mapName);
      CellReference cellRef = new CellReference(cellReference);
      Row row = sheet.getRow(cellRef.getRow());
      if (row == null) {
        row = sheet.createRow(cellRef.getRow());
      }
      
      Cell cell = row.getCell(cellRef.getCol());
      if (cell == null) {
        cell = row.createCell(cellRef.getCol());
        // Если ячейка пустая, создаем новую статистику
        cell.setCellValue(mapName + " (0/0/0)");
      }
      
      String cellValue = cell.getStringCellValue();
      updateCellStatistics(cell, cellValue, match.getResult());
    }
  }
  
  /**
   * Обновляет статистику в ячейке на основе результата матча
   * @param cell ячейка для обновления
   * @param cellValue текущее значение ячейки
   * @param result результат матча
   */
  private void updateCellStatistics(Cell cell, String cellValue, MatchResult result) {
    // Извлекаем статистику из строки вида "MAP_NAME (wins/losses/draws)"
    Pattern pattern = Pattern.compile("(.*?)\\s*\\((\\d+)/(\\d+)/(\\d+)\\)");
    Matcher matcher = pattern.matcher(cellValue);
    
    if (matcher.find()) {
      String mapName = matcher.group(1).trim();
      int wins = Integer.parseInt(matcher.group(2));
      int losses = Integer.parseInt(matcher.group(3));
      int draws = Integer.parseInt(matcher.group(4));
      
      // Обновляем статистику в зависимости от результата
      switch (result) {
        case WIN:
          wins++;
          break;
        case LOSE:
          losses++;
          break;
        case DRAW:
          draws++;
          break;
        default:
          log.warn("Неизвестный результат матча: {}", result);
          return;
      }
      
      // Обновляем значение ячейки
      cell.setCellValue(mapName + " (" + wins + "/" + losses + "/" + draws + ")");
    } else {
      // Если формат не соответствует ожидаемому, создаем новую статистику
      String mapName = cellValue.replaceAll("\\s*\\(.*\\)\\s*", "").trim();
      int wins = result == MatchResult.WIN ? 1 : 0;
      int losses = result == MatchResult.LOSE ? 1 : 0;
      int draws = result == MatchResult.DRAW ? 1 : 0;
      
      cell.setCellValue(mapName + " (" + wins + "/" + losses + "/" + draws + ")");
    }
  }
}


