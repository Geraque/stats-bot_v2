package com.cs.doceho.stats.bot.v2.excel;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelWriter {

  FinderUtils finderUtils;

  static Map<MatchResult, byte[]> COLOR_MATHC_RESULT_MAP = Map.of(MatchResult.WIN,
      new byte[]{(byte) 198, (byte) 239, (byte) 206},
      MatchResult.LOSE, new byte[]{(byte) 255, (byte) 199, (byte) 206},
      MatchResult.DRAW, new byte[]{(byte) 255, (byte) 235, (byte) 156});

  static Map<PlayerName, Integer> WINGMAN_RATING_COLUMN_MAP = Map.of(PlayerName.DESMOND, 1,
      PlayerName.BLACK_VISION, 2,
      PlayerName.GLOXINIA, 3,
      PlayerName.WOLF_SMXL, 4);
  static Map<PlayerName, Integer> NON_WINGMAN_RATING_COLUMN_MAP = Map.of(PlayerName.DESMOND, 1,
      PlayerName.BLACK_VISION, 2,
      PlayerName.GLOXINIA, 3,
      PlayerName.WOLF_SMXL, 4,
      PlayerName.GLEB, 5);

  static Map<PlayerName, Integer> NON_WINGMAN_STATS_START_COLUMN_MAP = Map.of(PlayerName.DESMOND, 6,
      PlayerName.BLACK_VISION, 19,
      PlayerName.GLOXINIA, 32,
      PlayerName.WOLF_SMXL, 45,
      PlayerName.GLEB, 58);

  // Метод для чтения существующего файла
  public XSSFWorkbook readWorkbook(String filePath) throws IOException {
    try (FileInputStream fis = new FileInputStream(filePath)) {
      return new XSSFWorkbook(fis);
    }
  }

  // Метод для записи в файл
  public void saveWorkbook(String filePath, XSSFWorkbook workbook) throws IOException {
    try (workbook; FileOutputStream fos = new FileOutputStream(filePath)) {
      workbook.write(fos);
    }
  }

  // Обработка листа Excel (вставка строк, ячеек)
  public void processSheet(String sheetName, XSSFWorkbook workbook,
      Map<String, Map<String, Map<PlayerName, MatchItem>>> dayGroups) {
    Sheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      sheet = workbook.createSheet(sheetName);
    }

    int globalCounter = finderUtils.getGlobalNextMatchNumber(sheet);

    for (Map.Entry<String, Map<String, Map<PlayerName, MatchItem>>> dayEntry : dayGroups.entrySet()) {
      String dayKey = dayEntry.getKey();
      int dateRowIndex = finderUtils.findDateRowIndex(sheet, dayKey);
      dateRowIndex = createDateRowIfAbsent(sheet, dateRowIndex, dayKey);

      processMatchRows(workbook, sheet, dayEntry.getValue(), dateRowIndex, globalCounter);
    }
  }

  private int createDateRowIfAbsent(Sheet sheet, int dateRowIndex, String dayKey) {
    if (dateRowIndex == -1) {
      int insertRowIndex = finderUtils.getInsertRowIndex(sheet);
      sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
      Row dateRow = sheet.createRow(insertRowIndex);
      dateRow.createCell(0).setCellValue(dayKey);
      log.info("Добавлена строка с датой: {} в строке {}", dayKey, insertRowIndex);
      dateRowIndex = insertRowIndex;
    }
    return dateRowIndex;
  }

  private void processMatchRows(XSSFWorkbook workbook, Sheet sheet, Map<String, Map<PlayerName, MatchItem>> matchGroups,
      int dateRowIndex, int globalCounter) {
    for (Map.Entry<String, Map<PlayerName, MatchItem>> matchEntry : matchGroups.entrySet()) {
      Map<PlayerName, MatchItem> matchData = matchEntry.getValue();
      int nextMatchNumber = ++globalCounter;
      int insertRowIndex = finderUtils.getInsertRowIndex(sheet);

      if (insertRowIndex <= dateRowIndex) {
        insertRowIndex = dateRowIndex + 1;
      }

      sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
      Row matchRow = sheet.createRow(insertRowIndex);
      insertMatchData(workbook, matchRow, nextMatchNumber, matchData);
    }
  }

  private void insertMatchData(XSSFWorkbook workbook,Row matchRow, int nextMatchNumber,
      Map<PlayerName, MatchItem> matchData) {
    MatchItem sampleMatch = matchData.values().iterator().next();
    String mapName = sampleMatch.getMap().getName();
    String matchIdentifier = nextMatchNumber + " map (" + mapName + ")";

    XSSFColor xssfColor = new XSSFColor(
        COLOR_MATHC_RESULT_MAP.getOrDefault(sampleMatch.getResult(),
            new byte[]{(byte) 255, (byte) 255, (byte) 255}), null);
    CellStyle mapCellStyle = createCellStyle(workbook, xssfColor);

    Cell mapCell = matchRow.createCell(0);
    mapCell.setCellValue(matchIdentifier);
    mapCell.setCellStyle(mapCellStyle);

    fillPlayerData(matchRow, sampleMatch.getType(), matchData);
  }

  private CellStyle createCellStyle(XSSFWorkbook workbook, XSSFColor color) {
    CellStyle mapCellStyle = workbook.createCellStyle();
    mapCellStyle.setFillForegroundColor(color);
    mapCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return mapCellStyle;
  }


  private void fillPlayerData(Row matchRow, MatchType matchType,
      Map<PlayerName, MatchItem> matchData) {
    // Заполнение строки данными для каждого игрока
    if (matchType == MatchType.WINGMAN) {
      // Для листа "2х2 2025": столбцы B, C, D, E – рейтинги для DESMOND, BLACK_VISION, GLOXINIA, WOLF_SMXL
      for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
        int targetCol = WINGMAN_RATING_COLUMN_MAP.get(entry.getKey());
        if (targetCol != -1 && entry.getValue().getRating() != null) {
          Cell cell = matchRow.createCell(targetCol);
          cell.setCellValue(entry.getValue().getRating());
        }
      }
    } else {
      // Для листов "2025 mm", "Premier 2025", "Faceit 2025":
      // Рейтинг – колонки B, C, D; статистика – диапазоны для каждого игрока.
      for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
        int ratingCol = NON_WINGMAN_RATING_COLUMN_MAP.get(entry.getKey());
        if (ratingCol != -1 && entry.getValue().getRating() != null) {
          Cell cell = matchRow.createCell(ratingCol);
          cell.setCellValue(entry.getValue().getRating());
        }
        int statsStartCol = NON_WINGMAN_STATS_START_COLUMN_MAP.get(entry.getKey());
        if (statsStartCol != -1) {
          MatchItem mi = entry.getValue();
          int[] stats = new int[13];
          stats[0] = (mi.getSmokeKill() != null) ? mi.getSmokeKill() : 0;
          stats[1] = (mi.getOpenKill() != null) ? mi.getOpenKill() : 0;
          stats[2] = (mi.getThreeKill() != null) ? mi.getThreeKill() : 0;
          stats[3] = (mi.getFourKill() != null) ? mi.getFourKill() : 0;
          stats[4] = (mi.getAce() != null) ? mi.getAce() : 0;
          stats[5] = (mi.getFlash() != null) ? mi.getFlash() : 0;
          stats[6] = (mi.getTrade() != null) ? mi.getTrade() : 0;
          stats[7] = (mi.getWallBang() != null) ? mi.getWallBang() : 0;
          stats[8] = (mi.getClutchOne() != null) ? mi.getClutchOne() : 0;
          stats[9] = (mi.getClutchTwo() != null) ? mi.getClutchTwo() : 0;
          stats[10] = (mi.getClutchThree() != null) ? mi.getClutchThree() : 0;
          stats[11] = (mi.getClutchFour() != null) ? mi.getClutchFour() : 0;
          stats[12] = (mi.getClutchFive() != null) ? mi.getClutchFive() : 0;
          for (int i = 0; i < stats.length; i++) {
            Cell cell = matchRow.createCell(statsStartCol + i);
            cell.setCellValue(stats[i]);
          }
        }
      }
    }
  }
}
