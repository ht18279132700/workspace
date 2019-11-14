package cn.com.pcauto.wenda.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author chensy
 */
public class ExcelReader {

    /**
     * 从输入流里读取Excel的数据.
     *
     * @param in Excel文件的输入流.
     * @param sheetName 要读取数据的表名.
     * @param columnNames 要读取数据的列名.
     * @return
     * @throws IOException
     * @throws IsNotExcelFileException 输入流里的文件不是excel文件.
     * @throws NotEnoughColumnsException Excel文件里没有指定的列.
     */
    public static List<AbsObj> fromInputStream(
            InputStream in, String sheetName, Set<String> columnNames)
            throws IOException {
        Workbook workbook = createWorkbook(in);
        Sheet sheet = workbook.getSheet(sheetName);
        return readDataFromSheet(sheet, trimStrings(columnNames));
    }

    private static Workbook createWorkbook(InputStream in)
            throws IOException {
        try {
            return WorkbookFactory.create(in);
        } catch (InvalidFormatException e) {
            throw new IsNotExcelFileException(e);
        }
    }

    /**
     * 从输入流里读取Excel的数据. 读取时从活动表里读出.
     *
     * @param in Excel文件的输入流.
     * @param columnNames 要读取数据的列名.
     * @return
     * @throws IOException
     * @throws IsNotExcelFileException 输入流里的文件不是excel文件.
     * @throws NotEnoughColumnsException Excel文件里没有指定的列.
     */
    public static List<AbsObj> fromInputStream(
            InputStream in, Set<String> columnNames)
            throws IOException {
        Workbook workbook = createWorkbook(in);
        Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        return readDataFromSheet(sheet, trimStrings(columnNames));
    }

    static public Set<String> trimStrings(Set<String> ss) {
        Set<String> newSs = new HashSet<String>(ss.size() * 2);
        for (String s : ss) {
            newSs.add(s.trim());
        }
        return newSs;
    }

    private static List<AbsObj> readDataFromSheet(
            Sheet sheet, Set<String> columnNames) {
        Row row = sheet.getRow(0);
        Map<String, Integer> columnNameIndices =
                readHeader(row, columnNames);
        List<AbsObj> data = readDataAfterSecondRow(sheet, columnNameIndices);
        return data;
    }

    private static Map<String, Integer> readHeader(
            Row row, Set<String> columnNameSet) {
        Set<String> remainColumnNameSet = new HashSet<String>(columnNameSet);
        if (row == null) {
            throw new NotEnoughColumnsException(remainColumnNameSet);
        }
        Map<String, Integer> header =
                new HashMap<String, Integer>(columnNameSet.size() * 2);
        for (Cell cell : row) {
            String cellValue = getStringFromCell(cell);
            if (cellValue == null || cellValue.isEmpty()) {
                continue;
            }
            String value = cellValue.trim();
            if (remainColumnNameSet.contains(value)) {
                header.put(value, cell.getColumnIndex());
                remainColumnNameSet.remove(value);
            }
        }
        if (!remainColumnNameSet.isEmpty()) {
            throw new NotEnoughColumnsException(remainColumnNameSet);
        }
        return header;
    }

    private static String getStringFromCell(Cell cell) {
        try {
            return cell.getStringCellValue();
        } catch (Exception e) {
            // unnecessary to process
        }
        return "";
    }

    private static List<AbsObj> readDataAfterSecondRow(
            Sheet sheet, Map<String, Integer> columnNameIndices) {
        List<AbsObj> data = new ArrayList<AbsObj>(sheet.getLastRowNum() + 1);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            AbsObj obj = readDataFromRow(row, columnNameIndices);
            data.add(obj);
        }
        return data;
    }

    private static AbsObj readDataFromRow(Row row, Map<String, Integer> columnNameIndices) {
        AbsObj obj = MapAbsObj.newInstance();
        for (Map.Entry<String, Integer> entry : columnNameIndices.entrySet()) {
            int columnIndex = entry.getValue();
            Cell cell = row.getCell(columnIndex);
            Object cellValue = ExcelUtil.getCellValue(cell);
            if (cellValue == null) {
                continue;
            }
            String attributeName = entry.getKey();
            obj.set(attributeName, cellValue);
        }
        return obj;
    }

    private ExcelReader() {
    }
}
