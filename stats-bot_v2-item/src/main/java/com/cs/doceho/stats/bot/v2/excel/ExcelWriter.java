package com.cs.doceho.stats.bot.v2.excel;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelWriter {

  FinderUtils finderUtils;
  UpdateMapAndPlayerStatistics updateMapAndPlayerStatistics;
  CellStyleService createCellStyle;

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
      PlayerName.WESDIA, 5,
      PlayerName.CHELIKOPUKICH, 6);
  static Map<PlayerName, Integer> NON_WINGMAN_STATS_START_COLUMN_MAP = Map.of(PlayerName.DESMOND, 7,
      PlayerName.BLACK_VISION, 20,
      PlayerName.GLOXINIA, 33,
      PlayerName.WOLF_SMXL, 46,
      PlayerName.WESDIA, 59,
      PlayerName.CHELIKOPUKICH, 72);


  public XSSFWorkbook readWorkbook(String filePath) throws IOException {
    try (FileInputStream fis = new FileInputStream(filePath)) {
      return new XSSFWorkbook(fis);
    }
  }

  public void saveWorkbook(String filePath, XSSFWorkbook workbook) throws IOException {
    try (workbook; FileOutputStream fos = new FileOutputStream(filePath)) {
      workbook.write(fos);
    }
  }

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

      processMatchRows(sheet, dayEntry.getValue(), dateRowIndex, globalCounter);
    }
  }

  private int createDateRowIfAbsent(Sheet sheet, int dateRowIndex, String dayKey) {
    if (dateRowIndex == -1) {
      int insertRowIndex = finderUtils.getInsertRowIndex(sheet);
      sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
      Row dateRow = sheet.createRow(insertRowIndex);
      dateRow.createCell(0).setCellValue(dayKey);
      createCellStyle.applyColorStyle(sheet.getWorkbook(), dateRow);
      log.info("Добавлена строка с датой: {} в строке {}", dayKey, insertRowIndex);
      dateRowIndex = insertRowIndex;
    }
    return dateRowIndex;
  }

  private void processMatchRows(Sheet sheet,
      Map<String, Map<PlayerName, MatchItem>> matchGroups,
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
      insertMatchData(sheet.getWorkbook(), matchRow, nextMatchNumber, matchData);

      // Выбор вызова обновления статистики в зависимости от типа матча
      MatchItem sampleMatch = matchData.values().iterator().next();
      if (sampleMatch.getType() == MatchType.WINGMAN) {
        updateMapAndPlayerStatistics.wingman(sheet.getWorkbook(), sampleMatch, insertRowIndex);
      } else {
        updateMapAndPlayerStatistics.matchmaking(sheet.getWorkbook(), sampleMatch, insertRowIndex);
      }
    }
  }

  private void insertMatchData(Workbook workbook, Row matchRow, int nextMatchNumber,
      Map<PlayerName, MatchItem> matchData) {
    MatchItem sampleMatch = matchData.values().iterator().next();
    String mapName = sampleMatch.getMap().getName();
    String matchIdentifier = nextMatchNumber + " map (" + mapName + ")";

    XSSFColor xssfColor = new XSSFColor(
        COLOR_MATHC_RESULT_MAP.getOrDefault(sampleMatch.getResult(),
            new byte[]{(byte) 255, (byte) 255, (byte) 255}), null);
    CellStyle mapCellStyle = createCellStyle.createCellStyle(workbook, xssfColor);

    Cell mapCell = matchRow.createCell(0);
    mapCell.setCellValue(matchIdentifier);
    mapCell.setCellStyle(mapCellStyle);

    fillPlayerData(workbook, matchRow, sampleMatch.getType(), matchData);
  }

  private void fillPlayerData(Workbook workbook, Row matchRow, MatchType matchType,
      Map<PlayerName, MatchItem> matchData) {
    if (matchType == MatchType.WINGMAN) {
      matchData.forEach((playerName, matchItem) -> {
        int targetCol = WINGMAN_RATING_COLUMN_MAP.get(playerName);
        setCellValueIfNotNull(matchRow, targetCol, matchItem.getRating());
      });
    } else {
      matchData.forEach((playerName, matchItem) -> {
        int ratingCol = NON_WINGMAN_RATING_COLUMN_MAP.get(playerName);
        setCellValueIfNotNull(matchRow, ratingCol, matchItem.getRating());

        int statsStartCol = NON_WINGMAN_STATS_START_COLUMN_MAP.get(playerName);
        if (statsStartCol != -1) {
          int[] stats = new int[] {
              getStatValue(matchItem.getSmokeKill()),
              getStatValue(matchItem.getOpenKill()),
              getStatValue(matchItem.getThreeKill()),
              getStatValue(matchItem.getFourKill()),
              getStatValue(matchItem.getAce()),
              getStatValue(matchItem.getFlash()),
              getStatValue(matchItem.getTrade()),
              getStatValue(matchItem.getWallBang()),
              getStatValue(matchItem.getClutchOne()),
              getStatValue(matchItem.getClutchTwo()),
              getStatValue(matchItem.getClutchThree()),
              getStatValue(matchItem.getClutchFour()),
              getStatValue(matchItem.getClutchFive())
          };
          for (int i = 0; i < stats.length; i++) {
            Cell cell = matchRow.createCell(statsStartCol + i);
            cell.setCellValue(stats[i]);
          }
        }
      });
      createCellStyle.applyColorStyle(workbook, matchRow);
    }
  }

  // Метод для установки значения ячейки, если значение не null и индекс допустим
  private void setCellValueIfNotNull(Row row, int colIndex, Number value) {
    if (colIndex != -1 && value != null) {
      Cell cell = row.createCell(colIndex);
      cell.setCellValue(value.doubleValue());
    }
  }

  // Метод для получения значения статистики с заменой null на 0
  private int getStatValue(Integer stat) {
    return stat != null ? stat : 0;
  }

}
