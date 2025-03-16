package com.cs.doceho.stats.bot.v2.excel;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

  static Map<String, CellCoordinate> MAP_CELL_COORDINATE = Map.ofEntries(
      Map.entry("DUST", new CellCoordinate(0, 87)),       // ячейка CJ1 (row 0, col 87)
      Map.entry("ANCIENT", new CellCoordinate(3, 87)),      // ячейка CJ4 (row 3, col 87)
      Map.entry("MIRAGE", new CellCoordinate(0, 93)),       // ячейка CP1 (row 0, col 93)
      Map.entry("OFFICE", new CellCoordinate(3, 93)),       // ячейка CP4 (row 3, col 93)
      Map.entry("INFERNO", new CellCoordinate(0, 99)),      // ячейка CV1 (row 0, col 99)
      Map.entry("VERTIGE", new CellCoordinate(3, 99)),      // ячейка CV4 (row 3, col 99)
      Map.entry("TRAIN", new CellCoordinate(0, 105)),       // ячейка DB1 (row 0, col 105)
      Map.entry("ANUBIS", new CellCoordinate(3, 105)),      // ячейка DB4 (row 3, col 105)
      Map.entry("NUKE", new CellCoordinate(0, 111)),        // ячейка DH1 (row 0, col 111)
      Map.entry("ITALY", new CellCoordinate(3, 111)),       // ячейка DH4 (row 3, col 111)
      Map.entry("EDIN", new CellCoordinate(0, 117)),        // ячейка DN1 (row 0, col 117)
      Map.entry("OVERPASS", new CellCoordinate(3, 117)),    // ячейка DN4 (row 3, col 117)
      Map.entry("BASALT", new CellCoordinate(0, 123))       // ячейка DT1 (row 0, col 123)
  );

  static Map<PlayerName, Integer> MAP_SUMMARY_COLUMN_ORDER = Map.of(
      PlayerName.DESMOND, 87,         // CJ
      PlayerName.BLACK_VISION, 88,      // CK
      PlayerName.GLOXINIA, 89,          // CL
      PlayerName.WOLF_SMXL, 90,         // CM
      PlayerName.WESDIA, 91,            // CN
      PlayerName.CHELIKOPUKICH, 92      // CO
  );


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

  private void processMatchRows(XSSFWorkbook workbook, Sheet sheet,
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
      insertMatchData(workbook, matchRow, nextMatchNumber, matchData);

      // Вызов обновления общей статистики карты и статистики каждого игрока.
      MatchItem sampleMatch = matchData.values().iterator().next();
      updateMapAndPlayerStatistics(workbook, sampleMatch, insertRowIndex);
    }
  }


  private void insertMatchData(XSSFWorkbook workbook, Row matchRow, int nextMatchNumber,
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
    if (matchType == MatchType.WINGMAN) {
      for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
        int targetCol = WINGMAN_RATING_COLUMN_MAP.get(entry.getKey());
        if (targetCol != -1 && entry.getValue().getRating() != null) {
          Cell cell = matchRow.createCell(targetCol);
          cell.setCellValue(entry.getValue().getRating());
        }
      }
    } else {
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

  public void updateMapAndPlayerStatistics(XSSFWorkbook workbook, MatchItem match, int matchRowIndex) {
    // Определение листа по типу матча (обновление для MATCH_MAKING и PREMIER)
    String sheetName = null;
    if (match.getType() == MatchType.MATCH_MAKING) {
      sheetName = "2025 mm";
    } else if (match.getType() == MatchType.PREMIER) {
      sheetName = "Premier 2025";
    }
    if (sheetName == null) {
      return;
    }
    Sheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      return;
    }

    // Получение названия карты (приведение к верхнему регистру)
    String mapName = match.getMap().getName().toUpperCase();
    CellCoordinate summaryCoord = MAP_CELL_COORDINATE.get(mapName);
    if (summaryCoord == null) {
      return;
    }

    // Обновление общего результата карты (ячейка в summaryCoord.row, summaryCoord.column)
    Row overallRow = sheet.getRow(summaryCoord.row);
    if (overallRow == null) {
      overallRow = sheet.createRow(summaryCoord.row);
    }
    Cell overallCell = overallRow.getCell(summaryCoord.column);
    if (overallCell == null) {
      overallCell = overallRow.createCell(summaryCoord.column);
      overallCell.setCellValue(mapName + " (0/0/0)");
    }
    String currentText = overallCell.getStringCellValue();
    int wins = 0, losses = 0, draws = 0;
    Pattern pattern = Pattern.compile("\\((\\d+)/(\\d+)/(\\d+)\\)");
    Matcher matcher = pattern.matcher(currentText);
    if (matcher.find()) {
      wins = Integer.parseInt(matcher.group(1));
      losses = Integer.parseInt(matcher.group(2));
      draws = Integer.parseInt(matcher.group(3));
    }
    switch (match.getResult()) {
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
        break;
    }
    overallCell.setCellValue(mapName + " (" + wins + "/" + losses + "/" + draws + ")");

    // Обновление сводных формул для каждого игрока.
    // Предполагается, что:
    // - Строка summaryCoord.row + 1 содержит формулы индивидуальной статистики (СРЗНАЧ)
    // - Строка summaryCoord.row + 2 содержит итоговую сумму (СУММ)
    int summaryPlayerRowIndex = summaryCoord.row + 1;
    int summaryTotalRowIndex = summaryCoord.row + 2;
    Row summaryPlayerRow = sheet.getRow(summaryPlayerRowIndex);
    if (summaryPlayerRow == null) {
      summaryPlayerRow = sheet.createRow(summaryPlayerRowIndex);
    }
    Row summaryTotalRow = sheet.getRow(summaryTotalRowIndex);
    if (summaryTotalRow == null) {
      summaryTotalRow = sheet.createRow(summaryTotalRowIndex);
    }

    // Выбор карты столбцов для рейтинговых ячеек в матче (уже существующие мапы)
    Map<PlayerName, Integer> ratingColumnMap = (match.getType() == MatchType.WINGMAN)
        ? WINGMAN_RATING_COLUMN_MAP
        : NON_WINGMAN_RATING_COLUMN_MAP;

    // Для каждого игрока обновление формул.
    for (Map.Entry<PlayerName, Integer> entry : MAP_SUMMARY_COLUMN_ORDER.entrySet()) {
      PlayerName player = entry.getKey();
      int summaryCol = entry.getValue();

      // Получение рейтинговой ячейки нового матча для игрока.
      int matchRatingCol = ratingColumnMap.getOrDefault(player, -1);
      if (matchRatingCol == -1) continue;
      Row matchRow = sheet.getRow(matchRowIndex);
      if (matchRow == null) continue;
      Cell matchRatingCell = matchRow.getCell(matchRatingCol);
      if (matchRatingCell == null) continue;
      // Вычисление адреса ячейки нового результата (например, "B4")
      String newCellRef = new CellReference(matchRowIndex, matchRatingCol).formatAsString();

      // Обновление формулы ячейки индивидуальной статистики игрока (СРЗНАЧ)
      Cell playerSummaryCell = summaryPlayerRow.getCell(summaryCol);
      if (playerSummaryCell == null) {
        playerSummaryCell = summaryPlayerRow.createCell(summaryCol);
        playerSummaryCell.setCellFormula("СРЗНАЧ(" + newCellRef + ")");
      } else {
        String formula = playerSummaryCell.getCellFormula();
        // Если формула заканчивается на ")", удалить его и добавить новый адрес
        if (formula.endsWith(")")) {
          formula = formula.substring(0, formula.length() - 1) + "," + newCellRef + ")";
        } else {
          formula = "СРЗНАЧ(" + newCellRef + ")";
        }
        log.info("formula123: {}", formula);
        playerSummaryCell.setCellFormula(formula);
      }
    }
  }


  @AllArgsConstructor
  private static class CellCoordinate {
    int row;
    int column;
  }


}
