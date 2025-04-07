package com.cs.doceho.stats.bot.v2.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CellStyleService {

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

    public CellStyle createCellStyle(XSSFWorkbook workbook, XSSFColor color) {
        CellStyle mapCellStyle = workbook.createCellStyle();
        mapCellStyle.setFillForegroundColor(color);
        mapCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return mapCellStyle;
    }

    public void applyColorStyle(XSSFWorkbook workbook, Row row) {
        int[] styleColumns = new int[] {7, 15, 20, 28, 33, 41, 46, 54, 59, 67, 72, 80};
        for (int i = 0; i < MATCH_ROW_COLORS.size(); i++) {
            applyColorStyleCell(workbook, row, MATCH_ROW_COLORS.get(i), styleColumns[i]);
        }
    }

    // Метод для применения стиля с заданным цветом для ячейки в указанном столбце
    private void applyColorStyleCell(XSSFWorkbook workbook, Row row, byte[] color, int colIndex) {
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
}
