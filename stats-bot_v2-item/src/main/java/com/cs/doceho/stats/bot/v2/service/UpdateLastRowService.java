package com.cs.doceho.stats.bot.v2.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateLastRowService {

  public void apply(Sheet sheet) {
    int lastRowNum = sheet.getLastRowNum();
    Row averageRow = sheet.getRow(lastRowNum);
    if (averageRow == null) {
      return;
    }

    // Выбор диапазона столбцов в зависимости от названия листа
    log.info("sheet.getSheetName(): {}", sheet.getSheetName());
    List<String> symbols = sheet.getSheetName().contains("2x2")
        ? List.of("B", "C", "D", "E")
        : generateColumnSymbols("B", "BE");

    // Обновление формулы для каждого столбца из списка
    for (int i = 0; i < symbols.size(); i++) {
      // Номер ячейки на строке среднего может начинаться не с 0
      Cell avgCell = averageRow.getCell(i + 1);

      if (avgCell != null && avgCell.getCellType() == CellType.FORMULA) {
        String column = symbols.get(i);
        // Используется lastRowNum как номер последней строки (учёт того, что строки нумеруются с 0)
        String newFormula = "AVERAGE(" + column + "3:" + column + lastRowNum + ")";
        avgCell.setCellFormula(newFormula);
      }
    }
  }

  /**
   * Генерация списка Excel-столбцов от начального до конечного (включительно). Пример:
   * generateColumnSymbols("B", "BE") вернёт список всех столбцов от B до BE.
   */
  private List<String> generateColumnSymbols(String start, String end) {
    List<String> symbols = new ArrayList<>();
    int startIndex = excelColumnToNumber(start);
    int endIndex = excelColumnToNumber(end);
    for (int i = startIndex; i <= endIndex; i++) {
      symbols.add(numberToExcelColumn(i));
    }
    return symbols;
  }

  /**
   * Преобразование Excel-обозначения столбца (например, "B") в число (например, 2).
   */
  private int excelColumnToNumber(String column) {
    int result = 0;
    for (char ch : column.toUpperCase().toCharArray()) {
      result = result * 26 + (ch - 'A' + 1);
    }
    return result;
  }

  /**
   * Преобразование числа в Excel-обозначение столбца.
   */
  private String numberToExcelColumn(int number) {
    StringBuilder column = new StringBuilder();
    while (number > 0) {
      int rem = (number - 1) % 26;
      column.insert(0, (char) (rem + 'A'));
      number = (number - 1) / 26;
    }
    return column.toString();
  }
}
