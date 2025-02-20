package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Сервис для изменения Excel-файла statistics.xlsx. Если несколько MatchItem за один день
 * объединяются в один блок (с заголовком-дата), то на уровне матча выполняется дополнительная
 * группировка: если совпадают полный временной штамп (dd.MM.yyyy'T'HH:mm:ss), карта и тип матча, то
 * данные разных игроков записываются в одну строку (один матч). При этом номер матча определяется
 * глобально по всему листу.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangingExcelService {

  UpdateLastRowService updateAverageFormula;
  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/Statistics.xlsx";

  static Map<MatchType, String> SHEET_NAME_MAP = Map.of(MatchType.WINGMAN, "2х2 2025",
      MatchType.MATCH_MAKING, "2025 mm",
      MatchType.PREMIER, "Premier 2025",
      MatchType.FACEIT, "Faceit 2025");
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


  /**
   * Добавление матчей из списка в Excel-файл. Группировка выполняется по листу, затем по дате
   * (dd.MM.yyyy) и далее по полному ключу матча, который формируется из полного временного штампа
   * (dd.MM.yyyy'T'HH:mm:ss) и названия карты. Если у объектов совпадают все параметры, то данные
   * разных игроков объединяются в одну строку. Нумерация матчей определяется глобально (например,
   * если последний матч в предыдущий день имел номер 40, то первый матч нового дня получит номер
   * 41).
   *
   * @param matchList список объектов MatchItem для записи в файл
   * @throws IOException при ошибках работы с файлом
   */
  public void addMatches(List<MatchItem> matchList) throws IOException {
    FileInputStream fis = new FileInputStream(FILE_PATH);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss");

    /*
     * Группировка по ключам:
     *  - Внешний Map: ключ – имя листа (определяется по типу матча).
     *  - Второй уровень: ключ – день (формат dd.MM.yyyy) для размещения заголовка.
     *  - Третий уровень: ключ – полный ключ матча, сформированный как
     *      fullTimestamp + "|" + mapName,
     *    где fullTimestamp – строка формата dd.MM.yyyy'T'HH:mm:ss.
     *  - Четвёртый уровень: Map, сопоставляющая PlayerName с соответствующим MatchItem.
     */
    Map<String, Map<String, Map<String, Map<PlayerName, MatchItem>>>> grouped = new HashMap<>();
    for (MatchItem match : matchList) {
      String sheetName = SHEET_NAME_MAP.get(match.getType());
      if (sheetName == null) {
        log.warn("Тип матча не распознан, пропуск: {}", match);
        continue;
      }
      String dayKey = match.getDate().format(dayFormatter);
      String matchKey = match.getDate().format(fullFormatter) + "|" + match.getMap().getName();
      grouped.computeIfAbsent(sheetName, k ->
              new TreeMap<>(Comparator.comparing((String key) -> LocalDate.parse(key, dayFormatter))))
          .computeIfAbsent(dayKey, k -> new HashMap<>())
          .computeIfAbsent(matchKey, k -> new HashMap<>())
          .put(match.getPlayerName(), match);
    }
    log.info("Сформированная группировка: {}", grouped);

    // Обработка групп по листу, дню и матчу
    for (String sheetName : grouped.keySet()) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        sheet = workbook.createSheet(sheetName);
        // При необходимости можно добавить заголовочную строку
      }
      // Определение глобального номера для нового матча по всему листу
      int globalCounter = getGlobalNextMatchNumber(sheet);
      Map<String, Map<String, Map<PlayerName, MatchItem>>> dayGroups = grouped.get(sheetName);
      for (String dayKey : dayGroups.keySet()) {
        // Найти или создать строку с датой (заголовок дня)
        int dateRowIndex = findDateRowIndex(sheet, dayKey);
        if (dateRowIndex == -1) {
          int insertRowIndex = getInsertRowIndex(sheet);
          sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
          Row dateRow = sheet.createRow(insertRowIndex);
          dateRow.createCell(0).setCellValue(dayKey);
          log.info("Добавлена строка с датой: {} в строке {}", dayKey, insertRowIndex);
          dateRowIndex = insertRowIndex;
        }
        // Для каждого уникального матча в этом дне
        Map<String, Map<PlayerName, MatchItem>> matchGroups = dayGroups.get(dayKey);
        for (String matchKey : matchGroups.keySet()) {
          Map<PlayerName, MatchItem> matchData = matchGroups.get(matchKey);
          int nextMatchNumber = ++globalCounter;
          int insertRowIndex = getInsertRowIndex(sheet);
          if (insertRowIndex <= dateRowIndex) {
            insertRowIndex = dateRowIndex + 1;
          }
          sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
          Row matchRow = sheet.createRow(insertRowIndex);
          // Из любого объекта группы извлекается название карты (предполагается, что для группы карта одинакова)
          MatchItem sampleMatch = matchData.values().iterator().next();
          String mapName = sampleMatch.getMap().getName();
          String matchIdentifier = nextMatchNumber + " map (" + mapName + ")";

          XSSFColor xssfColor = new XSSFColor(
              COLOR_MATHC_RESULT_MAP.getOrDefault(sampleMatch.getResult(),
                  new byte[]{(byte) 255, (byte) 255, (byte) 255}), null);
          CellStyle mapCellStyle = workbook.createCellStyle();
          mapCellStyle.setFillForegroundColor(xssfColor);
          mapCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          Cell mapCell = matchRow.createCell(0);
          mapCell.setCellValue(matchIdentifier);
          mapCell.setCellStyle(mapCellStyle);

          log.info("Добавлена строка матча для дня {} с идентификатором {} в строке {}",
              dayKey, matchIdentifier, insertRowIndex);

          // Заполнение строки данными для каждого игрока
          if (sampleMatch.getType() == MatchType.WINGMAN) {
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
    }
    for (String sheetName : grouped.keySet()) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet != null) {
        updateAverageFormula.apply(sheet);
      }
    }
    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  /**
   * Поиск строки, в которой в ячейке A записана искомая дата (формат dd.MM.yyyy).
   *
   * @param sheet   лист Excel
   * @param dateStr искомая дата
   * @return индекс строки или -1, если не найдено
   */
  private int findDateRowIndex(Sheet sheet, String dateStr) {
    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell != null && cell.getCellType() == CellType.STRING) {
          if (dateStr.equals(cell.getStringCellValue().trim())) {
            return i;
          }
        }
      }
    }
    return -1;
  }

  /**
   * Возвращает индекс строки для вставки новой записи. Если с конца находится "фантомная" строка
   * (ячейка A пуста или содержит пустую строку), вставка производится перед ней.
   *
   * @param sheet лист Excel
   * @return индекс для вставки новой строки
   */
  private int getInsertRowIndex(Sheet sheet) {
    int lastRowNum = sheet.getLastRowNum();
    for (int i = lastRowNum; i >= 0; i--) {
      Row row = sheet.getRow(i);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell == null) {
          return i;
        }
        if (cell.getCellType() == CellType.BLANK) {
          return i;
        }
        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
          return i;
        }
      }
    }
    return lastRowNum + 1;
  }

  /**
   * Обходит весь лист и находит максимальный номер матча (из ячеек A, содержащих строку вида "N
   * map").
   *
   * @param sheet лист Excel
   * @return максимальный номер матча, найденный в листе (если матчей нет, возвращается 0)
   */

  //TODO Переписать
  private int getGlobalNextMatchNumber(Sheet sheet) {
    int max = 0;
    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell != null && cell.getCellType() == CellType.STRING) {
          String val = cell.getStringCellValue().trim();
          if (val.toLowerCase().contains("map")) {
            try {
              String[] parts = val.split("\\s+");
              int num = Integer.parseInt(parts[0]);
              if (num > max) {
                max = num;
              }
            } catch (NumberFormatException e) {
              log.warn("Неверный формат в getGlobalNextMatchNumber");
              // Игнорировать неверный формат
            }
          }
        }
      }
    }
    return max;
  }

}
