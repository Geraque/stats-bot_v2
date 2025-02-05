package com.cs.doceho.stats.bot.v2.service;


import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChangingExcelService {

  // Файл располагается в ресурсах проекта, путь можно указать относительно корня
  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/statistics.xlsx";

  /**
   * Добавление матчей из списка в Excel-файл. Если для нового матча ещё не создан блок с датой,
   * сначала добавляется строка с датой. Затем определяется следующий номер матча для данного дня и
   * создаётся строка с данными.
   *
   * @param matchList список объектов MatchItem для записи в файл
   * @throws IOException при ошибках работы с файлом
   */
  public void addMatches(List<MatchItem> matchList) throws IOException {
    FileInputStream fis = new FileInputStream(FILE_PATH);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    // Форматирование даты: dd.MM.yyyy
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    for (MatchItem match : matchList) {
      String sheetName = getSheetName(match.getType());
      if (sheetName == null) {
        log.info("sheetName == null");
        continue;
      }
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        sheet = workbook.createSheet(sheetName);
        // При необходимости можно создать заголовочную строку
      }

      // Получение даты матча в строковом формате
      String matchDateStr = match.getDate().format(dtf);

      // Найти последнюю строку, где записана дата (ячейка A не содержит "map")
      int lastDateRowIndex = findLastDateRowIndex(sheet);
      int newRowIndex;
      int matchNumber;

      if (lastDateRowIndex == -1) {
        // Если ещё нет ни одной строки с датой (например, лист пуст или только заголовок)
        // Предполагается, что заголовок находится в строке 0, поэтому дата добавляется в строке 1.
        newRowIndex = sheet.getLastRowNum() + 1;
        if (newRowIndex < 1) {
          newRowIndex = 1;
        }
        Row dateRow = sheet.createRow(newRowIndex);
        dateRow.createCell(0).setCellValue(matchDateStr);
        log.info("Добавлена строка с датой: {} в строке {}", matchDateStr, newRowIndex);
        // Следующая строка для матча
        newRowIndex++;
        matchNumber = 1;
      } else {
        // Если найдена хотя бы одна строка с датой, получаем значение даты из последней такой строки
        Row lastDateRow = sheet.getRow(lastDateRowIndex);
        String lastDateStr = lastDateRow.getCell(0).getStringCellValue().trim();
        if (lastDateStr.equals(matchDateStr)) {
          // Новый матч относится к уже существующему блоку (одинаковая дата)
          int lastMatchRowIndex = findLastMatchRowIndexForDateSection(sheet, lastDateRowIndex);
          newRowIndex = lastMatchRowIndex + 1;
          if (lastMatchRowIndex == lastDateRowIndex) {
            // В блоке ещё нет матчей – это первый матч за этот день
            matchNumber = 1;
          } else {
            // Из предыдущей строки с матчем получаем идентификатор вида "N map"
            Row lastMatchRow = sheet.getRow(lastMatchRowIndex);
            String lastMatchId = lastMatchRow.getCell(0).getStringCellValue().trim();
            try {
              String[] parts = lastMatchId.split("\\s+");
              matchNumber = Integer.parseInt(parts[0]) + 1;
            } catch (NumberFormatException e) {
              matchNumber = 1;
            }
          }
        } else {
          // Дата нового матча отличается от последней записанной даты.
          // Добавляем новую строку с датой в конец листа.
          newRowIndex = sheet.getLastRowNum() + 1;
          Row dateRow = sheet.createRow(newRowIndex);
          dateRow.createCell(0).setCellValue(matchDateStr);
          log.info("Добавлена новая строка с датой: {} в строке {}", matchDateStr, newRowIndex);
          newRowIndex++;
          matchNumber = 1;
        }
      }

      // Создание строки с данными матча и установка идентификатора (например, "1 map", "2 map", …)
      Row matchRow = sheet.createRow(newRowIndex);
      String matchIdentifier = matchNumber + " map";
      matchRow.createCell(0).setCellValue(matchIdentifier);

      // Запись данных в зависимости от типа матча
      if (match.getType() == MatchType.WINGMAN) {
        int targetCol = getWingmanRatingColumn(match.getPlayerName());
        if (targetCol != -1 && match.getRating() != null) {
          matchRow.createCell(targetCol).setCellValue(match.getRating());
        }
      } else {
        int ratingCol = getNonWingmanRatingColumn(match.getPlayerName());
        if (ratingCol != -1 && match.getRating() != null) {
          matchRow.createCell(ratingCol).setCellValue(match.getRating());
        }
        int statsStartCol = getNonWingmanStatsStartColumn(match.getPlayerName());
        if (statsStartCol != -1) {
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
            matchRow.createCell(statsStartCol + i).setCellValue(stats[i]);
          }
        }
      }
    }

    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  /**
   * Поиск последней строки, содержащей дату (ячейка A не пуста и не содержит "map").
   *
   * @param sheet лист Excel
   * @return индекс последней строки с датой или -1, если такой строки нет
   */
  private int findLastDateRowIndex(Sheet sheet) {
    int lastDateRow = -1;
    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        Cell cell = row.getCell(0);
        if (cell != null && cell.getCellType() == CellType.STRING) {
          String val = cell.getStringCellValue().trim();
          if (!val.isEmpty() && !val.toLowerCase().contains("map")) {
            lastDateRow = i;
          }
        }
      }
    }
    return lastDateRow;
  }

  /**
   * Поиск последней строки для текущего блока (после строки с датой), где записан идентификатор
   * матча (ячейка A содержит "map").
   *
   * @param sheet        лист Excel
   * @param dateRowIndex индекс строки с датой
   * @return индекс последней строки, содержащей идентификатор матча для данного дня
   */
  private int findLastMatchRowIndexForDateSection(Sheet sheet, int dateRowIndex) {
    int lastMatchRow = dateRowIndex;
    for (int i = dateRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null) {
        break;
      }
      Cell cell = row.getCell(0);
      if (cell == null || cell.getCellType() != CellType.STRING) {
        break;
      }
      String val = cell.getStringCellValue().trim();
      if (val.isEmpty()) {
        break;
      }
      // Если значение не содержит "map", значит это новая дата – выходим из цикла
      if (!val.toLowerCase().contains("map")) {
        break;
      }
      lastMatchRow = i;
    }
    return lastMatchRow;
  }

  // Остальные методы остаются без изменений

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
