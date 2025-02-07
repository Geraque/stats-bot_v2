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

/**
 * Сервис для изменения Excel-файла statistics.xlsx. Если несколько объектов имеют одинаковую дату,
 * то они относятся к одному дню, но каждый новый матч (набор данных для всех участников)
 * добавляется как отдельная строка, с идентификатором = (номер последнего матча по всему листу +
 * 1). При этом, если в конце листа находится строка со сводными данными (ячейка A пустая), новая
 * строка вставляется перед ней.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangingExcelService {

  // Файл находится в ресурсах проекта; путь указывается относительно корня проекта
  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/statistics.xlsx";

  /**
   * Добавление матчей из списка в Excel-файл. Если несколько объектов имеют одинаковую дату, то они
   * относятся к одному дню. Для каждого нового матча создаётся отдельная строка с номером, равным
   * (номер последнего матча по всему листу + 1). При этом идентификатор матча дополняется названием
   * карты.
   *
   * @param matchList список объектов MatchItem для записи в файл
   * @throws IOException при ошибках работы с файлом
   */
  public void addMatches(List<MatchItem> matchList) throws IOException {
    log.info("matchList1231: {}", matchList);
    FileInputStream fis = new FileInputStream(FILE_PATH);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Группировка по имени листа и дате (форматированная дата).
    // Если несколько объектов MatchItem имеют одинаковую дату, то они группируются вместе.
    Map<String, Map<String, Map<PlayerName, MatchItem>>> grouped = new HashMap<>();
    for (MatchItem match : matchList) {
      String sheetName = getSheetName(match.getType());
      if (sheetName == null) {
        log.info("Тип матча не распознан, пропуск");
        continue;
      }
      String dateStr = match.getDate().format(dtf);
      grouped.computeIfAbsent(sheetName, k -> new HashMap<>())
          .computeIfAbsent(dateStr, k -> new HashMap<>())
          .put(match.getPlayerName(), match);
      log.info("grouped123: {}", grouped);
    }

    // Обработка каждой группы – для каждого листа и для каждой даты
    for (String sheetName : grouped.keySet()) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        sheet = workbook.createSheet(sheetName);
        // При необходимости можно добавить заголовок
      }
      // Определение глобального номера для нового матча по всему листу
      int globalCounter = getGlobalNextMatchNumber(sheet);
      Map<String, Map<PlayerName, MatchItem>> dateGroups = grouped.get(sheetName);
      log.info("dateGroups123: {}", dateGroups);
      for (String dateStr : dateGroups.keySet()) {
        Map<PlayerName, MatchItem> matchData = dateGroups.get(dateStr);
        // 1. Найти или создать строку с датой.
        int dateRowIndex = findDateRowIndex(sheet, dateStr);
        if (dateRowIndex == -1) {
          int insertRowIndex = getInsertRowIndex(sheet);
          sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
          Row dateRow = sheet.createRow(insertRowIndex);
          dateRow.createCell(0).setCellValue(dateStr);
          log.info("Добавлена строка с датой: {} в строке {}", dateStr, insertRowIndex);
          dateRowIndex = insertRowIndex;
        }
        // 2. Определить номер нового матча глобально
        int nextMatchNumber = ++globalCounter;
        // 3. Вставить новую строку для матча перед сводной строкой (если она есть)
        int insertRowIndex = getInsertRowIndex(sheet);
        if (insertRowIndex <= dateRowIndex) {
          insertRowIndex = dateRowIndex + 1;
        }
        sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), 1);
        Row matchRow = sheet.createRow(insertRowIndex);
        // Получаем из группы одного участника и извлекаем название карты.
        // Предполагается, что все MatchItem в группе имеют одинаковую карту.
        MatchItem sampleMatch = matchData.values().iterator().next();
        String mapName = sampleMatch.getMap().getName();
        String matchIdentifier = nextMatchNumber + " map (" + mapName + ")";
        matchRow.createCell(0).setCellValue(matchIdentifier);
        log.info("Добавлена строка матча для даты {} с идентификатором {} в строке {}",
            dateStr, matchIdentifier, insertRowIndex);

        // 4. Заполнить строку данными для каждого игрока
        if (matchData.values().iterator().next().getType() == MatchType.WINGMAN) {
          // Для листа "2х2 2025": столбцы B, C, D, E – рейтинги для DESMOND, BLACK_VISION, GLOXINIA, WOLF_SMXL
          for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
            int targetCol = getWingmanRatingColumn(entry.getKey());
            if (targetCol != -1 && entry.getValue().getRating() != null) {
              Cell cell = matchRow.createCell(targetCol);
              cell.setCellValue(entry.getValue().getRating());
            }
          }
        } else {
          // Для листов "2025 mm", "Premier 2025", "Faceit 2025":
          // Рейтинг – колонки B, C, D; статистика – диапазоны для каждого игрока.
          for (Map.Entry<PlayerName, MatchItem> entry : matchData.entrySet()) {
            // Рейтинг
            int ratingCol = getNonWingmanRatingColumn(entry.getKey());
            if (ratingCol != -1 && entry.getValue().getRating() != null) {
              Cell cell = matchRow.createCell(ratingCol);
              cell.setCellValue(entry.getValue().getRating());
            }
            // Статистика
            int statsStartCol = getNonWingmanStatsStartColumn(entry.getKey());
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

    // Сохранение изменений в файл
    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  /**
   * Метод ищет строку, где в ячейке A ровно записана искомая дата (формат dd.MM.yyyy).
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
   * Возвращает индекс строки, куда следует вставить новую запись. Если с конца находится
   * "фантомная" строка (ячейка A пуста или содержит пустую строку), вставка производится перед
   * ней.
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
        if (cell.getCellType() == CellType.STRING) {
          if (cell.getStringCellValue().trim().isEmpty()) {
            return i;
          }
        }
      }
    }
    return lastRowNum + 1;
  }

  /**
   * Обходит весь лист и находит максимальный номер матча (из ячеек A, содержащих формат "N map").
   *
   * @param sheet лист Excel
   * @return максимальный номер матча, найденный в листе (если матчей нет, возвращается 0)
   */
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
              // Игнорировать неверный формат
            }
          }
        }
      }
    }
    return max;
  }

  // Методы для определения столбцов остаются без изменений

  private String getSheetName(MatchType type) {
    if (type == null) {
      log.info("type == null");
      return null;
    }
    switch (type) {
      case WINGMAN:
        return "2х2 2025";
      case MATCH_MAKING:
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
    if (playerName == null) {
      return -1;
    }
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
    if (playerName == null) {
      return -1;
    }
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

  private int getNonWingmanStatsStartColumn(PlayerName playerName) {
    if (playerName == null) {
      return -1;
    }
    switch (playerName) {
      case DESMOND:
        return 5;
      case BLACK_VISION:
        return 18;
      case GLOXINIA:
        return 31;
      case WOLF_SMXL:
        return 44;
      default:
        return -1;
    }
  }
}
