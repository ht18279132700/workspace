package cn.com.pcauto.wenda.util.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author chensy
 */
public class HeaderExtractor {

    final static String key_header = "header";
    final static String key_startRowIndex = "startRowIndex";
    final static private Pattern attributePattern = Pattern.compile("^\\$\\{([^${}]+)\\}$");

    static MapAbsObj extraHeader(Sheet sheet) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                String attribute = extraAttribute(cell);
                if (attribute == null || attribute.isEmpty()) {
                    continue;
                }
                return extraComplexHeaderFromRow(row);
            }
            if (row.getRowNum() == 9) {
                break;
            }
        }
        return extraSimpleHeaderFromFirstRow(sheet.getRow(0));
    }

    private static String extraAttribute(Cell cell) {
        String cellValue = ExcelUtil.getStringFromCell(cell);
        if ((cellValue == null || cellValue.isEmpty())) {
            return "";
        }
        String stringValue = cellValue.trim();
        Matcher matcher = attributePattern.matcher(stringValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static MapAbsObj extraComplexHeaderFromRow(
            Row row) {
        Map<String, Integer> header =
                new HashMap<String, Integer>(row.getLastCellNum() * 2);
        for (Cell cell : row) {
            String attribute = extraAttribute(cell);
            if (attribute == null || attribute.isEmpty()) {
                continue;
            }
            String name = attribute.trim();
            if (header.containsKey(name)) {
                continue;
            }
            header.put(name, cell.getColumnIndex());
        }
        MapAbsObj result = MapAbsObj.newInstance();
        result.set(key_header, header);
        result.set(key_startRowIndex, row.getRowNum());
        return result;
    }

    private static MapAbsObj extraSimpleHeaderFromFirstRow(Row row) {
        Map<String, Integer> header =
                new HashMap<String, Integer>(row.getLastCellNum() * 2);
        for (Cell cell : row) {
            String attribute = ExcelUtil.getStringFromCell(cell);
            if (attribute == null || attribute.isEmpty()) {
                continue;
            }
            String name = attribute.trim();
            if (header.containsKey(name)) {
                continue;
            }
            header.put(name, cell.getColumnIndex());
        }
        MapAbsObj result = MapAbsObj.newInstance();
        result.set(key_header, header);
        result.set(key_startRowIndex, row.getRowNum() + 1);
        return result;
    }
}
