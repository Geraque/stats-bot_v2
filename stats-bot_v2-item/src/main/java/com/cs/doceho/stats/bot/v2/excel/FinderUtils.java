package com.cs.doceho.stats.bot.v2.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinderUtils {


  /**
   * Возвращает индекс строки для вставки новой записи. Если с конца находится "фантомная" строка
   * (ячейка A пуста или содержит пустую строку), вставка производится перед ней.
   *
   * @param sheet лист Excel
   * @return индекс для вставки новой строки
   */
  public int getInsertRowIndex(Sheet sheet) {
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
  public int getGlobalNextMatchNumber(Sheet sheet) {
    int lastMapRow = sheet.getLastRowNum() - 1;
    int lastMapNumber = 0;
    Row row = sheet.getRow(lastMapRow);
    if (row != null) {
      Cell cell = row.getCell(0);
      if (cell != null && cell.getCellType() == CellType.STRING) {
        String val = cell.getStringCellValue().trim();
        if (val.toLowerCase().contains("map")) {
          try {
            String[] parts = val.split("\\s+");
            lastMapNumber =  Integer.parseInt(parts[0]);
          } catch (NumberFormatException e) {
            log.warn("Неверный формат в getGlobalNextMatchNumber");
            // Игнорировать неверный формат
          }
        }
      }
    }
    return lastMapNumber;
  }

  /**
   * Поиск строки, в которой в ячейке A записана искомая дата (формат dd.MM.yyyy).
   *
   * @param sheet   лист Excel
   * @param dateStr искомая дата
   * @return индекс строки или -1, если не найдено
   */
  public int findDateRowIndex(Sheet sheet, String dateStr) {
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
}
