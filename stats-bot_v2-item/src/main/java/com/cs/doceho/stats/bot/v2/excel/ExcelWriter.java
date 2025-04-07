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

  static Map<String, MapCellBlock> MAP_CELL_BLOCK = Map.ofEntries(
      Map.entry("DUST",     new MapCellBlock(0, 1, 87)),   // Общий: CJ1, Индив.: CJ2, Итог: CJ3 (столбцы CJ-CO)
      Map.entry("ANCIENT",  new MapCellBlock(3, 4, 87)),   // Общий: CJ4, Индив.: CJ5, Итог: CJ6
      Map.entry("MIRAGE",   new MapCellBlock(0, 1, 93)),   // Общий: CP1, Индив.: CP2, Итог: CP3 (столбцы CP-CU)
      Map.entry("OFFICE",   new MapCellBlock(3, 4, 93)),   // Общий: CP4, Индив.: CP5, Итог: CP6
      Map.entry("INFERNO",  new MapCellBlock(0, 1, 99)),   // Общий: CV1, Индив.: CV2, Итог: CV3 (столбцы CV-DA)
      Map.entry("VERTIGO",  new MapCellBlock(3, 4, 99)),   // Общий: CV4, Индив.: CV5, Итог: CV6
      Map.entry("TRAIN",    new MapCellBlock(0, 1, 105)),  // Общий: DB1, Индив.: DB2, Итог: DB3 (столбцы DB-DG)
      Map.entry("ANUBIS",   new MapCellBlock(3, 4, 105)),  // Общий: DB4, Индив.: DB5, Итог: DB6
      Map.entry("NUKE",     new MapCellBlock(0, 1, 111)),  // Общий: DH1, Индив.: DH2, Итог: DH3 (столбцы DH-DM)
      Map.entry("ITALY",    new MapCellBlock(3, 4, 111)),  // Общий: DH4, Индив.: DH5, Итог: DH6
      Map.entry("EDIN",     new MapCellBlock(0, 1, 117)),  // Общий: DN1, Индив.: DN2, Итог: DN3 (столбцы DN-DS)
      Map.entry("OVERPASS", new MapCellBlock(3, 4, 117)),  // Общий: DN4, Индив.: DN5, Итог: DN6
      Map.entry("BASALT",   new MapCellBlock(0, 1, 123))   // Общий: DT1, Индив.: DT2, Итог: DT3 (столбцы DT-DY)
  );

  static Map<String, WingmanMapCellBlock> WINGMAN_MAP_CELL_BLOCK = Map.of(
      "INFERNO", new WingmanMapCellBlock(2, 3, 7),   // H3-K3, H4-K4
      "NUKE",    new WingmanMapCellBlock(2, 3, 11),  // L3-O3, L4-O4
      "WHISTLE", new WingmanMapCellBlock(2, 3, 15),  // P3-S3, P4-S4
      "PALAIS",  new WingmanMapCellBlock(2, 3, 19),  // T3-V3, T4-W4
      "OVERPASS",new WingmanMapCellBlock(2, 3, 23),  // X3-Z3, X4-AA4
      "VERTIGO", new WingmanMapCellBlock(2, 3, 27)   // AB3-AE3, AB4-AE4
  );


  static Map<PlayerName, Integer> PLAYER_OFFSET = Map.of(
      PlayerName.DESMOND, 0,          // первый столбец блока
      PlayerName.BLACK_VISION, 1,
      PlayerName.GLOXINIA, 2,
      PlayerName.WOLF_SMXL, 3,
      PlayerName.WESDIA, 4,
      PlayerName.CHELIKOPUKICH, 5
  );

  static Map<PlayerName, Integer> PLAYER_OFFSET_WINGMAN = Map.of(
      PlayerName.DESMOND, 0,
      PlayerName.BLACK_VISION, 1,
      PlayerName.GLOXINIA, 2,
      PlayerName.WOLF_SMXL, 3
  );

  static List<byte[]> MATCH_ROW_COLORS = List.of(
      new byte[]{(byte) 198, (byte) 239, (byte) 206}, // H
      new byte[]{(byte) 255, (byte) 255, (byte) 204}, // P
      new byte[]{(byte) 255, (byte) 235, (byte) 156}, // U
      new byte[]{(byte) 255, (byte) 255, (byte) 204}, // AC
      new byte[]{(byte) 68,  (byte) 114, (byte) 196},  // AH
      new byte[]{(byte) 255, (byte) 255, (byte) 204}, // AP
      new byte[]{(byte) 255, (byte) 199, (byte) 206}, // AU
      new byte[]{(byte) 255, (byte) 255, (byte) 204}, // BC
      new byte[]{(byte) 226, (byte) 239, (byte) 217}, // BH
      new byte[]{(byte) 255, (byte) 255, (byte) 204}, // BP
      new byte[]{(byte) 217, (byte) 225, (byte) 242}, // BU
      new byte[]{(byte) 255, (byte) 255, (byte) 204}  // CC
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
      dateRowIndex = createDateRowIfAbsent(workbook, sheet, dateRowIndex, dayKey);

      processMatchRows(workbook, sheet, dayEntry.getValue(), dateRowIndex, globalCounter);
    }
  }

  private int createDateRowIfAbsent(XSSFWorkbook workbook, Sheet sheet, int dateRowIndex, String dayKey) {
    if (dateRowIndex == -1) {
      int insertRowIndex = finderUtils.getInsertRowIndex(sheet);
      sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
      Row dateRow = sheet.createRow(insertRowIndex);
      dateRow.createCell(0).setCellValue(dayKey);

      int[] styleColumns = new int[] {7, 15, 20, 28, 33, 41, 46, 54, 59, 67, 72, 80};
      for (int i = 0; i < MATCH_ROW_COLORS.size(); i++) {
        applyColorStyle(workbook, dateRow, MATCH_ROW_COLORS.get(i), styleColumns[i]);
      }

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

      // Выбор вызова обновления статистики в зависимости от типа матча
      MatchItem sampleMatch = matchData.values().iterator().next();
      if (sampleMatch.getType() == MatchType.WINGMAN) {
        updateWingmanPlayerStatistics(workbook, sampleMatch, insertRowIndex);
      } else {
        updateMapAndPlayerStatistics(workbook, sampleMatch, insertRowIndex);
      }
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

    fillPlayerData(workbook, matchRow, sampleMatch.getType(), matchData);
  }

  private CellStyle createCellStyle(XSSFWorkbook workbook, XSSFColor color) {
    CellStyle mapCellStyle = workbook.createCellStyle();
    mapCellStyle.setFillForegroundColor(color);
    mapCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return mapCellStyle;
  }


  private void fillPlayerData(XSSFWorkbook workbook, Row matchRow, MatchType matchType,
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

      // Массив с информацией о столбцах для установки стилей
      int[] styleColumns = new int[] {7, 15, 20, 28, 33, 41, 46, 54, 59, 67, 72, 80};
      for (int i = 0; i < MATCH_ROW_COLORS.size(); i++) {
        applyColorStyle(workbook, matchRow, MATCH_ROW_COLORS.get(i), styleColumns[i]);
      }
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

  // Метод для применения стиля с заданным цветом для ячейки в указанном столбце
  private void applyColorStyle(XSSFWorkbook workbook, Row row, byte[] color, int colIndex) {
    XSSFColor xssfColor = new XSSFColor(color, null);
    CellStyle style = createCellStyle(workbook, xssfColor);
    applyBordersIfNecessary(style, color);
    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    cell.setCellStyle(style);
  }

  /**
   * Добавляет стандартную тонкую черную рамку к стилю ячейки,
   * если цвет ячейки соответствует предопределенному целевому цвету.
   *
   * @param style Стиль ячейки (CellStyle), к которому нужно применить рамку.
   * @param color Цвет ячейки в виде массива байт для проверки.
   */
  private void applyBordersIfNecessary(CellStyle style, byte[] color) {
    // Целевой цвет для добавления рамки
    byte[] targetColor = new byte[]{(byte) 255, (byte) 255, (byte) 204};

    // Проверяем, совпадает ли текущий цвет с целевым цветом для рамки
    if (java.util.Arrays.equals(color, targetColor)) {
      // Добавляем тонкую рамку со всех сторон
      style.setBorderTop(BorderStyle.THIN);
      style.setBorderBottom(BorderStyle.THIN);
      style.setBorderLeft(BorderStyle.THIN);
      style.setBorderRight(BorderStyle.THIN);

      // Устанавливаем цвет рамки (светло-серый, аналог стиля "Примечание")
      short borderColor = IndexedColors.GREY_25_PERCENT.getIndex();
      style.setTopBorderColor(borderColor);
      style.setBottomBorderColor(borderColor);
      style.setLeftBorderColor(borderColor);
      style.setRightBorderColor(borderColor);
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

    // Приведение названия карты к верхнему регистру
    String mapName = match.getMap().getFullName().toUpperCase();
    MapCellBlock block = MAP_CELL_BLOCK.get(mapName);
    if (block == null) {
      // Если карта не найдена – выход
      return;
    }

    // --- Обновление общего результата карты ---
    Row overallRow = sheet.getRow(block.overallRow);
    if (overallRow == null) {
      overallRow = sheet.createRow(block.overallRow);
    }
    // Ячейка с общим результатом берётся по начальному столбцу блока
    Cell overallCell = overallRow.getCell(block.startColumn);
    if (overallCell == null) {
      overallCell = overallRow.createCell(block.startColumn);
      overallCell.setCellValue(mapName + " (0/0/0)");
    }
    // Чтение текущего результата в формате "MAP (wins/loses/draws)"
    String currentText = overallCell.getStringCellValue();
    int wins = 0, losses = 0, draws = 0;
    Pattern pattern = Pattern.compile("\\((\\d+)/(\\d+)/(\\d+)\\)");
    Matcher matcher = pattern.matcher(currentText);
    if (matcher.find()) {
      wins = Integer.parseInt(matcher.group(1));
      losses = Integer.parseInt(matcher.group(2));
      draws = Integer.parseInt(matcher.group(3));
    }
    // Обновление в зависимости от результата матча
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

    // --- Обновление статистики игроков ---
    // Получение рейтинговой ячейки нового матча для игрока осуществляется через уже существующую мапу:
    Map<PlayerName, Integer> ratingColumnMap = (match.getType() == MatchType.WINGMAN)
        ? WINGMAN_RATING_COLUMN_MAP
        : NON_WINGMAN_RATING_COLUMN_MAP;

    // Итерация по игрокам, для которых рассчитываются формулы
    for (Map.Entry<PlayerName, Integer> entry : PLAYER_OFFSET.entrySet()) {
      PlayerName player = entry.getKey();
      int offset = entry.getValue();

      // Вычисление номера столбца в блоке для данного игрока
      int summaryCol = block.startColumn + offset;

      // Получение рейтинговой ячейки нового матча
      int matchRatingCol = ratingColumnMap.getOrDefault(player, -1);
      if (matchRatingCol == -1) continue;
      Row matchRow = sheet.getRow(matchRowIndex);
      if (matchRow == null) continue;
      Cell matchRatingCell = matchRow.getCell(matchRatingCol);
      if (matchRatingCell == null) continue;
      // Получение адреса ячейки (например, "CV2" или "DB5") для нового результата
      String newCellRef = new CellReference(matchRowIndex, matchRatingCol).formatAsString();

      // --- Обновление формулы для индивидуальной статистики (СРЗНАЧ) ---
      // TODO Сделать так, что если в стате 0, а не AVG, то не ломалось бы
      Row individualRow = sheet.getRow(block.individualRow);
      if (individualRow == null) {
        individualRow = sheet.createRow(block.individualRow);
      }
      Cell playerSummaryCell = individualRow.getCell(summaryCol);
      if (playerSummaryCell == null) {
        playerSummaryCell = individualRow.createCell(summaryCol);
        playerSummaryCell.setCellFormula("СРЗНАЧ(" + newCellRef + ")");
      } else {
        String formula = playerSummaryCell.getCellFormula();
        if (formula.endsWith(")")) {
          formula = formula.substring(0, formula.length() - 1) + "," + newCellRef + ")";
        } else {
          formula = "СРЗНАЧ(" + newCellRef + ")";
        }
        playerSummaryCell.setCellFormula(formula);
      }
    }
  }


  public void updateWingmanPlayerStatistics(XSSFWorkbook workbook, MatchItem match, int matchRowIndex) {
    // Определение листа для WINGMAN
    String sheetName = "2х2 2025";
    Sheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      return;
    }

    // Приведение названия карты к верхнему регистру
    String mapName = match.getMap().getFullName().toUpperCase();
    WingmanMapCellBlock block = WINGMAN_MAP_CELL_BLOCK.get(mapName);
    if (block == null) {
      return;
    }

    // --- Обновление общей ячейки блока ---
    // Общая ячейка будет содержать название карты и количество сыгранных игр, например "DUST (20)"
    Row overallRow = sheet.getRow(block.overallRow);
    if (overallRow == null) {
      overallRow = sheet.createRow(block.overallRow);
    }
    Cell overallCell = overallRow.getCell(block.startColumn);
    if (overallCell == null) {
      overallCell = overallRow.createCell(block.startColumn);
      overallCell.setCellValue(mapName + " (1)");
    } else {
      String cellText = overallCell.getStringCellValue();
      Pattern pattern = Pattern.compile("(.+)\\s*\\((\\d+)\\)");
      Matcher matcher = pattern.matcher(cellText);
      if (matcher.find()) {
        String cellMapName = matcher.group(1).trim();
        int count = Integer.parseInt(matcher.group(2));
        count++;
        overallCell.setCellValue(cellMapName + " (" + count + ")");
      } else {
        overallCell.setCellValue(mapName + " (1)");
      }
    }

    // --- Обновление формул для индивидуальной статистики игроков ---
    Map<PlayerName, Integer> ratingColumnMap = WINGMAN_RATING_COLUMN_MAP;
    for (Map.Entry<PlayerName, Integer> entry : PLAYER_OFFSET_WINGMAN.entrySet()) {
      PlayerName player = entry.getKey();
      int offset = entry.getValue();
      int summaryCol = block.startColumn + offset;

      int matchRatingCol = ratingColumnMap.getOrDefault(player, -1);
      if (matchRatingCol == -1) continue;
      Row matchRow = sheet.getRow(matchRowIndex);
      if (matchRow == null) continue;
      Cell matchRatingCell = matchRow.getCell(matchRatingCol);
      if (matchRatingCell == null) continue;
      String newCellRef = new CellReference(matchRowIndex, matchRatingCol).formatAsString();

      Row playerRow = sheet.getRow(block.playerRow);
      if (playerRow == null) {
        playerRow = sheet.createRow(block.playerRow);
      }
      Cell playerSummaryCell = playerRow.getCell(summaryCol);
      if (playerSummaryCell == null) {
        playerSummaryCell = playerRow.createCell(summaryCol);
        playerSummaryCell.setCellFormula("СРЗНАЧ(" + newCellRef + ")");
      } else {
        String formula = playerSummaryCell.getCellFormula();
        if (formula.endsWith(")")) {
          formula = formula.substring(0, formula.length() - 1) + "," + newCellRef + ")";
        } else {
          formula = "СРЗНАЧ(" + newCellRef + ")";
        }
        playerSummaryCell.setCellFormula(formula);
      }
    }
  }




  @AllArgsConstructor
  private static class MapCellBlock {
    int overallRow;     // Индекс для общего результата карты (0-индексирован)
    int individualRow;  // Индекс для ячеек индивидуальной статистики игроков
    int startColumn;    // Номер столбца для первого игрока в блоке
  }

  @AllArgsConstructor
  private static class WingmanMapCellBlock {
    int overallRow;   // Строка, где записано название карты (без формул)
    int playerRow;    // Строка для ячеек со сводной статистикой игроков
    int startColumn;  // Начальный столбец блока
  }


}
