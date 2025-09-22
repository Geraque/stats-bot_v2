package com.cs.doceho.stats.bot.v2.excel.utils;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateMapAndPlayerStatistics {

    public static Map<String, MapCellBlock> MAP_CELL_BLOCK = Map.ofEntries(
        Map.entry("DUST", new MapCellBlock(0, 1, 87)),   // Общий: CJ1, Индив.: CJ2, Итог: CJ3 (столбцы CJ-CO)
        Map.entry("ANCIENT", new MapCellBlock(3, 4, 87)),   // Общий: CJ4, Индив.: CJ5, Итог: CJ6
        Map.entry("MIRAGE", new MapCellBlock(0, 1, 93)),   // Общий: CP1, Индив.: CP2, Итог: CP3 (столбцы CP-CU)
        Map.entry("OFFICE", new MapCellBlock(3, 4, 93)),   // Общий: CP4, Индив.: CP5, Итог: CP6
        Map.entry("INFERNO", new MapCellBlock(0, 1, 99)),   // Общий: CV1, Индив.: CV2, Итог: CV3 (столбцы CV-DA)
        Map.entry("VERTIGO", new MapCellBlock(3, 4, 99)),   // Общий: CV4, Индив.: CV5, Итог: CV6
        Map.entry("TRAIN", new MapCellBlock(0, 1, 105)),  // Общий: DB1, Индив.: DB2, Итог: DB3 (столбцы DB-DG)
        Map.entry("ANUBIS", new MapCellBlock(3, 4, 105)),  // Общий: DB4, Индив.: DB5, Итог: DB6
        Map.entry("NUKE", new MapCellBlock(0, 1, 111)),  // Общий: DH1, Индив.: DH2, Итог: DH3 (столбцы DH-DM)
        Map.entry("ITALY", new MapCellBlock(3, 4, 111)),  // Общий: DH4, Индив.: DH5, Итог: DH6
        Map.entry("EDIN", new MapCellBlock(0, 1, 117)),  // Общий: DN1, Индив.: DN2, Итог: DN3 (столбцы DN-DS)
        Map.entry("OVERPASS", new MapCellBlock(3, 4, 117)),  // Общий: DN4, Индив.: DN5, Итог: DN6
        Map.entry("BASALT", new MapCellBlock(0, 1, 123)),   // Общий: DT1, Индив.: DT2, Итог: DT3 (столбцы DT-DY)
        Map.entry("AGENCY", new MapCellBlock(3, 4, 123)),   // Общий: DT4, Индив.: DT5, Итог: DT6
        Map.entry("GRAIL", new MapCellBlock(0, 1, 129))   // Общий: DZ1, Индив.: DZ2, Итог: DZ6
    );

    static Map<String, WingmanMapCellBlock> WINGMAN_MAP_CELL_BLOCK = Map.of(
        "INFERNO", new WingmanMapCellBlock(2, 3, 7),   // H3-K3, H4-K4
        "NUKE",    new WingmanMapCellBlock(2, 3, 11),  // L3-O3, L4-O4
        "WHISTLE", new WingmanMapCellBlock(2, 3, 15),  // P3-S3, P4-S4
        "PALAIS",  new WingmanMapCellBlock(2, 3, 19),  // T3-V3, T4-W4
        "OVERPASS",new WingmanMapCellBlock(2, 3, 23),  // X3-Z3, X4-AA4
        "VERTIGO", new WingmanMapCellBlock(2, 3, 27),   // AB3-AE3, AB4-AE4
        "BREWERY", new WingmanMapCellBlock(2, 3, 27),   // AF3-AI3, AF4-AI4
        "DOGTOWN", new WingmanMapCellBlock(2, 3, 27)   // AJ3-AM3, AJ4-AM4
    );

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

    public void matchmaking(Workbook workbook, MatchItem match, int matchRowIndex) {
        // Определение листа по типу матча (обновление для MATCH_MAKING и PREMIER)
        String sheetName = null;
        if (match.getType() == MatchType.MATCH_MAKING) {
            sheetName = "2025 mm";
        } else if (match.getType() == MatchType.PREMIER) {
            sheetName = "Premier 2025";
        } else if (match.getType() == MatchType.FACEIT) {
            sheetName = "Faceit 2025";
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

    public void wingman(Workbook workbook, MatchItem match, int matchRowIndex) {
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
