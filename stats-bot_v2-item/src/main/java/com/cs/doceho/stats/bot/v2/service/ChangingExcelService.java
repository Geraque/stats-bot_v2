package com.cs.doceho.stats.bot.v2.service;


import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Класс для изменения Excel-файла statistics.xlsx.
 * Принимает список объектов MatchItem и добавляет данные матчей (одна запись = один матч для конкретного игрока)
 * в конец соответствующего листа.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangingExcelService {

  static String FILE_PATH = "statistics.xlsx";

  /**
   * Добавление матчей из списка в Excel-файл.
   * Для каждого MatchItem определяется нужный лист, вычисляется номер нового матча (match identifier)
   * и добавляется новая строка с данными.
   *
   * @param matchList список объектов MatchItem для записи в файл
   * @throws IOException при ошибках работы с файлом
   */
  public void addMatches(List<MatchItem> matchList) throws IOException {
    FileInputStream fis = new FileInputStream(FILE_PATH);
    Workbook workbook = new XSSFWorkbook(fis);
    fis.close();

    // Обработка каждого матча из списка
    for (MatchItem match : matchList) {
      String sheetName = getSheetName(match.getType());
      if (sheetName == null) {
        // Пропуск, если тип матча не распознан
        continue;
      }
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        sheet = workbook.createSheet(sheetName);
        // При необходимости можно добавить заголовочную строку
      }

      // Определение нового номера карты:
      String matchIdentifier = getNextMatchIdentifier(sheet);

      // Определение индекса новой строки (добавление в конец листа)
      int newRowIndex = sheet.getLastRowNum() + 1;
      // Если лист пуст (нет заголовка) – можно начать со строки 1
      if (newRowIndex == 0) {
        newRowIndex = 1;
      }
      Row row = sheet.createRow(newRowIndex);

      // Запись идентификатора матча в колонку A (индекс 0)
      Cell cellA = row.createCell(0);
      cellA.setCellValue(matchIdentifier);

      // В зависимости от типа матча заполняются колонки с данными
      if (match.getType() == MatchType.WINGMAN) {
        // Для листа "2х2 2025": только рейтинг в соответствующую колонку
        int targetCol = getWingmanRatingColumn(match.getPlayerName());
        if (targetCol != -1 && match.getRating() != null) {
          Cell ratingCell = row.createCell(targetCol);
          ratingCell.setCellValue(match.getRating());
        }
      } else {
        // Для листов "2025 mm", "Premier 2025" и "Faceit 2025":
        // Запись рейтинга в колонку (B-D) в зависимости от playerName
        int ratingCol = getNonWingmanRatingColumn(match.getPlayerName());
        if (ratingCol != -1 && match.getRating() != null) {
          Cell ratingCell = row.createCell(ratingCol);
          ratingCell.setCellValue(match.getRating());
        }
        // Запись статистики в соответствующий диапазон колонок
        int statsStartCol = getNonWingmanStatsStartColumn(match.getPlayerName());
        if (statsStartCol != -1) {
          // Список статистических показателей в нужном порядке
          int[] stats = new int[13];
          stats[0] = (match.getSmokeKill() != null) ? match.getSmokeKill() : 0;
          stats[1] = (match.getOpenKill() != null) ? match.getOpenKill() : 0;
          stats[2] = (match.getThreeKill() != null) ? match.getThreeKill() : 0;
          stats[3] = (match.getFourKill() != null) ? match.getFourKill() : 0;
          stats[4] = (match.getAce() != null) ? match.getAce() : 0;
          stats[5] = (match.getFlash() != null) ? match.getFlash() : 0;
          stats[6] = (match.getTrade() != null) ? match.getTrade() : 0;
          stats[7] = (match.getWallBang() != null) ? match.getWallBang() : 0;
          stats[8] = (match.getClutchOne() != null) ? match.getClutchOne() : 0;
          stats[9] = (match.getClutchTwo() != null) ? match.getClutchTwo() : 0;
          stats[10] = (match.getClutchThree() != null) ? match.getClutchThree() : 0;
          stats[11] = (match.getClutchFour() != null) ? match.getClutchFour() : 0;
          stats[12] = (match.getClutchFive() != null) ? match.getClutchFive() : 0;

          for (int i = 0; i < stats.length; i++) {
            Cell cell = row.createCell(statsStartCol + i);
            cell.setCellValue(stats[i]);
          }
        }
      }
    }

    // Запись изменений в файл
    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  /**
   * Определение имени листа по типу матча.
   *
   * @param type тип матча (MatchType)
   * @return имя листа или null, если тип не распознан
   */
  private String getSheetName(MatchType type) {
    if (type == null) {
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

  /**
   * Определение следующего номера матча (match identifier) для нового ряда.
   * Ищется последняя заполненная строка (начиная со строки 2) с ненулевым значением в колонке A,
   * содержащим шаблон «N map». Если найден, то N увеличивается на 1, иначе возвращается «1 map».
   *
   * @param sheet лист Excel
   * @return строка-номер матча, например, «4 map»
   */
  private String getNextMatchIdentifier(Sheet sheet) {
    // Предполагается, что первая строка (индекс 0) – заголовок.
    for (int i = sheet.getLastRowNum(); i >= 1; i--) {
      Row row = sheet.getRow(i);
      if (row == null) continue;
      Cell cell = row.getCell(0);
      if (cell == null) continue;
      if (cell.getCellType() == CellType.STRING) {
        String value = cell.getStringCellValue();
        if (value != null && value.toLowerCase().contains("map")) {
          String[] parts = value.trim().split("\\s+");
          try {
            int num = Integer.parseInt(parts[0]);
            return (num + 1) + " map";
          } catch (NumberFormatException e) {
            // Если не удалось распарсить число – продолжить поиск
          }
        }
      }
    }
    return "1 map";
  }

  /**
   * Определение колонки для записи рейтинга в листе «2х2 2025» (WINGMAN).
   *
   * @param playerName имя игрока
   * @return индекс колонки или -1, если имя не распознано
   */
  private int getWingmanRatingColumn(PlayerName playerName) {
    // Структура листа "2х2 2025":
    // A: номер матча, B: DESMOND, C: BLACK_VISION, D: GLOXINIA, E: WOLF_SMXL
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

  /**
   * Определение колонки для записи рейтинга в листах "2025 mm", "Premier 2025", "Faceit 2025".
   *
   * @param playerName имя игрока
   * @return индекс колонки (B-D) или -1, если имя не распознано
   */
  private int getNonWingmanRatingColumn(PlayerName playerName) {
    // Структура листов: A: номер матча, B: DESMOND, C: BLACK_VISION, D: GLOXINIA, далее статистика
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
      default:
        return -1;
    }
  }

  /**
   * Определение стартовой колонки для записи статистики в листах "2025 mm", "Premier 2025", "Faceit 2025".
   *
   * @param playerName имя игрока
   * @return индекс стартовой колонки или -1, если имя не распознано.
   *         Для DESMOND – E (индекс 4), BLACK_VISION – R (индекс 17), GLOXINIA – AE (индекс 30).
   */
  private int getNonWingmanStatsStartColumn(PlayerName playerName) {
    if (playerName == null) {
      return -1;
    }
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
