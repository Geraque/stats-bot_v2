package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
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
public class ChangingExcel {

  static String FILE_PATH = "stats-bot_v2-app/src/main/resources/statistics.xlsx";


  /**
   * Добавление матчей из списка. Предполагается, что список MatchItem уже сгруппирован по матчам.
   * Для WINGMAN ожидается 4 записи подряд, для остальных – 3 записи подряд.
   *
   * @param matchItems список записей матчей
   * @throws Exception в случае ошибки чтения/записи файла
   */
  public void addMatches(List<MatchItem> matchItems) throws Exception {
    FileInputStream fis = new FileInputStream(FILE_PATH);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    fis.close();

    // Итерация по списку матчей. Предположим, что матч состоит из groupSize записей
    int index = 0;
    while (index < matchItems.size()) {
      // Определить тип матча по первой записи группы
      MatchItem firstItem = matchItems.get(index);
      MatchType matchType = firstItem.getType();

      // Определить ожидаемое число записей в одном матче
      int groupSize = (matchType == MatchType.WINGMAN) ? 4 : 3;
      if (index + groupSize > matchItems.size()) {
        System.out.println(
            "Недостаточно данных для формирования полного матча. Требуется уточнение.");
        break;
      }

      // Извлечь группу матчей (по игрокам)
      List<MatchItem> matchGroup = new ArrayList<>();
      for (int i = 0; i < groupSize; i++) {
        matchGroup.add(matchItems.get(index + i));
      }
      index += groupSize;

      // Получить имя листа в зависимости от типа матча
      String sheetName = getSheetNameByMatchType(matchType);
      if (sheetName == null) {
        // Если тип матча не поддерживается – пропустить
        continue;
      }

      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
        System.out.println("Лист с именем " + sheetName + " не найден.");
        continue;
      }

      // Определить номер нового матча: подсчитать количество заполненных строк (начиная со второй)
      int lastMatchNumber = getMatchCount(sheet);
      int newMatchNumber = lastMatchNumber + 1;

      // Добавление новой строки в конец
      int newRowIndex = sheet.getLastRowNum() + 1;
      Row row = sheet.createRow(newRowIndex);

      // Запись номера матча в ячейку A (индекс 0)
      Cell cellA = row.createCell(0);
      cellA.setCellValue(newMatchNumber + " map");

      // В зависимости от типа листа заполняются соответствующие ячейки
      if (matchType == MatchType.WINGMAN) {
        // Лист "2х2 2025": столбцы B, C, D, E для игроков:
        // порядок: DESMOND, BLACK_VISION, GLOXINIA, WOLF_SMXL
        // Для каждого игрока используется только рейтинг.
        // Создать вспомогательный метод для получения рейтинга по имени игрока
        int[] targetColumns = {1, 2, 3, 4}; // индексы для столбцов B–E
        PlayerName[] expectedPlayers = {PlayerName.DESMOND, PlayerName.BLACK_VISION,
            PlayerName.GLOXINIA, PlayerName.WOLF_SMXL};

        for (int i = 0; i < expectedPlayers.length; i++) {
          MatchItem item = findMatchItemByPlayer(matchGroup, expectedPlayers[i]);
          if (item != null) {
            Cell cell = row.createCell(targetColumns[i]);
            cell.setCellValue(item.getRating());
          }
        }
      } else {
        // Листы: "2025 mm", "Premier 2025", "Faceit 2025"
        // Структура: столбцы B, C, D – рейтинги для DESMOND, BLACK_VISION, GLOXINIA соответственно.
        // Затем для каждого игрока — набор статистических данных.
        // Заполнение рейтингов:
        int[] ratingCols = {1, 2, 3}; // B, C, D
        PlayerName[] expectedPlayers = {PlayerName.DESMOND, PlayerName.BLACK_VISION,
            PlayerName.GLOXINIA};

        for (int i = 0; i < expectedPlayers.length; i++) {
          MatchItem item = findMatchItemByPlayer(matchGroup, expectedPlayers[i]);
          if (item != null) {
            Cell cell = row.createCell(ratingCols[i]);
            cell.setCellValue(item.getRating());
          }
        }
        // Заполнение статистики:
        // Для DESMOND: столбцы E–Q (индексы 4 до 16)
        // Для BLACK_VISION: столбцы R–AD (индексы 17 до 29)
        // Для GLOXINIA: столбцы AE–AQ (индексы 30 до 42)
        int[][] statsColumns = {
            {4, 16},
            {17, 29},
            {30, 42}
        };
        // Порядок статистики: smokeKill, openKill, threeKill, fourKill, ace, flash, trade,
        // wallBang, clutchOne, clutchTwo, clutchThree, clutchFour, clutchFive.
        // Для каждого игрока запишем значения последовательно.
        for (int i = 0; i < expectedPlayers.length; i++) {
          MatchItem item = findMatchItemByPlayer(matchGroup, expectedPlayers[i]);
          if (item != null) {
            int startCol = statsColumns[i][0];
            List<Integer> stats = getStatsList(item);
            for (int j = 0; j < stats.size(); j++) {
              Cell cell = row.createCell(startCol + j);
              cell.setCellValue(stats.get(j));
            }
          }
        }
      }
    }

    // Сохранить изменения в файл
    FileOutputStream fos = new FileOutputStream(FILE_PATH);
    workbook.write(fos);
    fos.close();
    workbook.close();
  }

  // Метод возвращает имя листа в зависимости от типа матча
  private String getSheetNameByMatchType(MatchType type) {
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

  // Метод для определения количества матчей в листе (пропускаем первую строку – заголовок).
  // Считается, что пустая ячейка в столбце A (за исключением заголовка) означает конец записей.
  private int getMatchCount(Sheet sheet) {
    int count = 0;
    // Начиная со второй строки (индекс 1)
    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null) {
        break;
      }
      Cell cell = row.getCell(0);
      if (cell == null || cell.getCellType() == CellType.BLANK) {
        break;
      }
      count++;
    }
    return count;
  }

  // Вспомогательный метод для поиска записи матча по имени игрока в группе.
  private MatchItem findMatchItemByPlayer(List<MatchItem> group, PlayerName player) {
    for (MatchItem item : group) {
      if (item.getPlayerName() == player) {
        return item;
      }
    }
    return null;
  }

  // Вспомогательный метод для получения списка статистических значений из MatchItem.
  // Порядок: smokeKill, openKill, threeKill, fourKill, ace, flash, trade, wallBang, clutchOne, clutchTwo, clutchThree, clutchFour, clutchFive.
  private List<Integer> getStatsList(MatchItem item) {
    List<Integer> stats = new ArrayList<>();
    stats.add(item.getSmokeKill());
    stats.add(item.getOpenKill());
    stats.add(item.getThreeKill());
    stats.add(item.getFourKill());
    stats.add(item.getAce());
    stats.add(item.getFlash());
    stats.add(item.getTrade());
    stats.add(item.getWallBang());
    stats.add(item.getClutchOne());
    stats.add(item.getClutchTwo());
    stats.add(item.getClutchThree());
    stats.add(item.getClutchFour());
    stats.add(item.getClutchFive());
    return stats;
  }
}

