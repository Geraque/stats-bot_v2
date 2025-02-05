package com.cs.doceho.stats.bot.v2.service;


import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangingExcelService {

  // Файл располагается в ресурсах проекта
  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/statistics.xlsx";

  /**
   * Добавление матчей из списка в Excel-файл.
   * Если несколько объектов имеют одинаковую дату, данные объединяются в одну строку (один матч).
   * Если последняя строка – сводная (ячейка A пуста), новый матч добавляется перед ней.
   *
   * @param matchList список объектов MatchItem для записи в файл
   * @throws IOException при ошибках работы с файлом
   */
  public void addMatches(List<MatchItem> matchList) throws IOException {
    FileInputStream fis = new FileInputStream(FILE_PATH);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Группировка: ключ – имя листа, затем по дате (строковое представление), значение – сопоставление PlayerName -> MatchItem
    Map<String, Map<String, Map<PlayerName, MatchItem>>> grouped = new HashMap<>();
    for (MatchItem match : matchList) {
      String sheetName = getSheetName(match.getType());
      if (sheetName == null) {
        log.info("Тип матча не распознан, пропуск");
        continue;
      }
      String dateStr = match.getDate().format(dtf);
      grouped
          .computeIfAbsent(sheetName, k -> new HashMap<>())
          .computeIfAbsent(dateStr, k -> new HashMap<>())
          .put(match.getPlayerName(), match);
    }

    // Обработка каждой группы (один матч = одна дата в определённом листе)
    for (String sheetName : grouped.keySet()) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        sheet = workbook.createSheet(sheetName);
        // При необходимости можно создать заголовочную строку
      }
      Map<String, Map<PlayerName, MatchItem>> dateGroups = grouped.get(sheetName);
      for (String dateStr : dateGroups.keySet()) {
        Map<PlayerName, MatchItem> matchData = dateGroups.get(dateStr);
        // 1. Найти или вставить строку с датой (блок матчей данного дня)
        int dateRowIndex = findDateRowIndex(sheet, dateStr);
        if (dateRowIndex == -1) {
          int insertRowIndex = getInsertRowIndex(sheet);
          // Вставляем строку с датой
          sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
          Row dateRow = sheet.createRow(insertRowIndex);
          dateRow.createCell(0).setCellValue(dateStr);
          log.info("Добавлена строка с датой: {} в строке {}", dateStr, insertRowIndex);
          dateRowIndex = insertRowIndex;
        }
        // 2. Найти строку с данными матча для данного дня (строка, где ячейка A содержит "map")
        int matchRowIndex = findMatchRowIndexForDate(sheet, dateRowIndex);
        Row matchRow;
        if (matchRowIndex == -1) {
          // Если строки с матчем ещё нет, вставляем её перед сводной (если сводная есть) или в конец блока
          int insertRowIndex = getInsertRowIndex(sheet);
          // Гарантируем, что вставка производится после строки с датой
          if (insertRowIndex <= dateRowIndex) {
            insertRowIndex = dateRowIndex + 1;
          }
          sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
          matchRow = sheet.createRow(insertRowIndex);
          // Устанавливаем идентификатор матча – поскольку все данные объединяются, используется, например, "1 map"
          matchRow.createCell(0).setCellValue("1 map");
          log.info("Добавлена строка матча для даты {} в строке {}", dateStr, insertRowIndex);
        } else {
          // Если строка уже существует, получаем её
          matchRow = sheet.getRow(matchRowIndex);
        }
        // 3. Обновляем строку матча данными для каждого игрока
        // При этом все объекты из группы имеют одинаковый тип
        MatchType type = matchData.values().iterator().next().getType();
        if (type == MatchType.WINGMAN) {
          // Для листа "2х2 2025": столбцы B, C, D, E – рейтинги для DESMOND, BLACK_VISION, GLOXINIA, WOLF_SMXL
          for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
            int targetCol = getWingmanRatingColumn(entry.getKey());
            if (targetCol != -1 && entry.getValue().getRating() != null) {
              Cell cell = matchRow.getCell(targetCol);
              if (cell == null) {
                cell = matchRow.createCell(targetCol);
              }
              cell.setCellValue(entry.getValue().getRating());
            }
          }
        } else {
          // Для листов "2025 mm", "Premier 2025", "Faceit 2025":
          // Рейтинг записывается в колонки B, C, D; статистика – в заданных диапазонах.
          for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
            // Рейтинг
            int ratingCol = getNonWingmanRatingColumn(entry.getKey());
            if (ratingCol != -1 && entry.getValue().getRating() != null) {
              Cell cell = matchRow.getCell(ratingCol);
              if (cell == null) {
                cell = matchRow.createCell(ratingCol);
              }
              cell.setCellValue(entry.getValue().getRating());
            }
            // Статистика
            int statsStartCol = getNonWingmanStatsStartColumn(entry.getKey());
            if (statsStartCol != -1) {
              MatchItem mi = entry.getValue();
              int[] stats = new int[13];
              stats[0]  = (mi.getSmokeKill()   != null) ? mi.getSmokeKill()   : 0;
              stats[1]  = (mi.getOpenKill()    != null) ? mi.getOpenKill()    : 0;
              stats[2]  = (mi.getThreeKill()   != null) ? mi.getThreeKill()   : 0;
              stats[3]  = (mi.getFourKill()    != null) ? mi.getFourKill()    : 0;
              stats[4]  = (mi.getAce()         != null) ? mi.getAce()         : 0;
              stats[5]  = (mi.getFlash()       != null) ? mi.getFlash()       : 0;
              stats[6]  = (mi.getTrade()       != null) ? mi.getTrade()       : 0;
              stats[7]  = (mi.getWallBang()    != null) ? mi.getWallBang()    : 0;
              stats[8]  = (mi.getClutchOne()   != null) ? mi.getClutchOne()   : 0;
              stats[9]  = (mi.getClutchTwo()   != null) ? mi.getClutchTwo()   : 0;
              stats[10] = (mi.getClutchThree() != null) ? mi.getClutchThree() : 0;
              stats[11] = (mi.getClutchFour()  != null) ? mi.getClutchFour()  : 0;
              stats[12] = (mi.getClutchFive()  != null) ? mi.getClutchFive()  : 0;
              for (int i = 0; i < stats.length; i++) {
                Cell cell = matchRow.getCell(statsStartCol + i);
                if (cell == null) {
                  cell = matchRow.createCell(statsStartCol + i);
                }
                cell.setCellValue(stats[i]);
              }
            }
          }
        }
      }
    }

    // Сохранение изменений
    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  /**
   * Ищет в листе строку, где в ячейке A ровно записана нужная дата.
   *
   * @param sheet лист Excel
   * @param dateStr искомая дата (формат dd.MM.yyyy)
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
   * Ищет строку для матча в блоке, начиная со строки сразу после dateRowIndex.
   * Ожидается, что строка с данными матча содержит в ячейке A текст вида "N map".
   *
   * @param sheet лист Excel
   * @param dateRowIndex индекс строки с датой
   * @return индекс строки с матчем или -1, если не найдено
   */
  private int findMatchRowIndexForDate(Sheet sheet, int dateRowIndex) {
    for (int i = dateRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null) continue;
      Cell cell = row.getCell(0);
      if (cell == null) continue;
      if (cell.getCellType() == CellType.STRING) {
        String val = cell.getStringCellValue().trim();
        if (val.toLowerCase().contains("map")) {
          return i;
        }
        // Если встретилась новая дата – прекращаем поиск
        if (!val.isEmpty() && !val.toLowerCase().contains("map")) {
          break;
        }
      }
    }
    return -1;
  }

  /**
   * Возвращает индекс строки, куда следует вставить новую запись.
   * Если последняя "реальная" строка (без пустой ячейки A) является строкой со сводной статистикой,
   * новый ряд вставляется перед ней.
   *
   * @param sheet лист Excel
   * @return индекс для вставки новой строки
   */
  private int getInsertRowIndex(Sheet sheet) {
    int lastRowNum = sheet.getLastRowNum();
    // Ищем с конца строку, где ячейка A пустая – считаем её сводной (если такая есть)
    for (int i = lastRowNum; i >= 0; i--) {
      Row row = sheet.getRow(i);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell == null || cell.getCellType() == CellType.BLANK || cell.getStringCellValue().trim().isEmpty()) {
          return i;
        }
      }
    }
    return lastRowNum + 1;
  }

  // Остальные методы для определения номера столбца остаются без изменений

  private String getSheetName(MatchType type) {
    if (type == null) {
      log.info("type == null");
      return null;
    }
    switch (type) {
      case WINGMAN:
        return "2х2 2025";
      case MATCH_MAKING:
        log.info("return \"2025 mm\"");
        return "2025 mm";
      case PREMIER:
        return "Premier 2025";
      case FACEIT:
        return "Faceit 2025";
      default:
        return null;
    }
  }

  private int getWingmanRatingColumn(PlayerName playerName) {
    // Структура листа "2х2 2025": A – номер матча, B – DESMOND, C – BLACK_VISION, D – GLOXINIA, E – WOLF_SMXL
    if (playerName == null) return -1;
    switch (playerName) {
      case DESMOND:
        return 1;
      case BLACK_VISION:
        return 2;
      case GLOXINIA:
        return 3;
      case WOLF_SMXL:
        return 4;
      default:
        return -1;
    }
  }

  private int getNonWingmanRatingColumn(PlayerName playerName) {
    // Структура листов: A – номер матча, B – DESMOND, C – BLACK_VISION, D – GLOXINIA, далее статистика
    if (playerName == null) return -1;
    switch (playerName) {
      case DESMOND:
        return 1;
      case BLACK_VISION:
        return 2;
      case GLOXINIA:
        return 3;
      default:
        return -1;
    }
  }

  private int getNonWingmanStatsStartColumn(PlayerName playerName) {
    // Для DESMOND – E (индекс 4), для BLACK_VISION – R (индекс 17), для GLOXINIA – AE (индекс 30)
    if (playerName == null) return -1;
    switch (playerName) {
      case DESMOND:
        return 4;
      case BLACK_VISION:
        return 17;
      case GLOXINIA:
        return 30;
      default:
        return -1;
    }
  }
}
