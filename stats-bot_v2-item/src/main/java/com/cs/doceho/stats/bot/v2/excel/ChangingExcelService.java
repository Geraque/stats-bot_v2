package com.cs.doceho.stats.bot.v2.excel;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
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

  public void addMatches(List<MatchItem> matchList) throws IOException {
    XSSFWorkbook workbook = excelWriter.readWorkbook(FILE_PATH);
    for (MatchItem matchItem : matchList) {
      matchItem.setMap(MapType.ANCIENT);
    }

    // Группировка матчей
    Map<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> grouped = matchGroupingService.groupMatches(
        matchList);

    // Запись данных в Excel
    for (Map.Entry<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> sheetEntry : grouped.entrySet()) {
      String sheetName = sheetEntry.getKey();
      Map<String, Map<String, Map<PlayerName, MatchItem>>> dayGroups = sheetEntry.getValue();
      excelWriter.processSheet(sheetName, workbook, dayGroups);
    }
//    grouped.forEach((sheetName, dayGroups) -> {
//      // Обновление статистики по картам для каждого матча
//      dayGroups.values().forEach(matchDayGroup ->
//          matchDayGroup.values().forEach(matchGroup ->
//              excelWriter.updateMapStatistics(workbook, matchGroup.values().iterator().next())
//          )
//      );
//      // Обработка листа Excel
//      excelWriter.processSheet(sheetName, workbook, dayGroups);
//    });


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
}


