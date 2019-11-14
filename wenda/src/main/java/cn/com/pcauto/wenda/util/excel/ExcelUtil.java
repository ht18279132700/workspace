package cn.com.pcauto.wenda.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author chensy
 */
public class ExcelUtil {

	private ExcelUtil() {}
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return (cell.getBooleanCellValue());
            case Cell.CELL_TYPE_NUMERIC: {
                double value = cell.getNumericCellValue();
                /*
                 所有日期格式都可以通过getDataFormat()值来判断
                 yyyy-MM-dd-----	14
                 yyyy年m月d日---	31
                 yyyy年m月-------	57
                 m月d日  ----------	58
                 HH:mm-----------	20
                 h时mm分  -------	32
                 */
                short format = cell.getCellStyle().getDataFormat();
                if (format == 14 || format == 31 || format == 57 || format == 58) {
                    //日期
                    //sdf = new SimpleDateFormat("yyyy-MM-dd");
                } else if (format == 20 || format == 32) {
                    //时间
                    //sdf = new SimpleDateFormat("HH:mm");
                } else {
                    return value;
                }
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
            }
            case Cell.CELL_TYPE_STRING:
                return (cell.getStringCellValue());
        }
        return null;
    }

    public static String getStringFromCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
        }
        return "";
    }

    public static void setValueToCell(Cell cell, Object value) {
        if (value instanceof Number) {
            Number numberValue = (Number) value;
            cell.setCellValue(numberValue.doubleValue());
        } else if (value instanceof Boolean) {
            Boolean booleanValue = (Boolean) value;
            cell.setCellValue(booleanValue);
        } else if (value instanceof Date) {
            Date dateValue = (Date) value;
            cell.setCellValue(dateValue);
        } else {
            String stringValue = value.toString();
            cell.setCellValue(stringValue);
        }
    }

    /**
     *
     * @param excel
     * @param columnNames
     * @param sheetName
     * @return
     */
    public static List<AbsObj> readColumnsFromFile(InputStream excel, List<String> columnNames, String sheetName) {
        Set<String> columnSet = new HashSet<String>(columnNames.size());
        for (String col : columnNames) {
            columnSet.add(col);
        }
        if (sheetName == null || sheetName.isEmpty()) {
            try {
                return ExcelReader.fromInputStream(excel, columnSet);
            } catch (IOException ex) {
                Logger.getLogger(ExcelUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                return ExcelReader.fromInputStream(excel, sheetName, columnSet);
            } catch (IOException ex) {
                Logger.getLogger(ExcelUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return new ArrayList<AbsObj>(0);
    }

	// 判断是否是个正常的excel数据
	public static List<AbsObj> getExcelData(MultipartFile file,
			List<String> HEADERS, HttpServletRequest req) {
		if (file == null || file.isEmpty()) {
			req.setAttribute("message", "请上传excel文件");
			return null;
		}

		try {
			List<AbsObj> sources = readColumnsFromFile(
					file.getInputStream(), HEADERS, null);
			if (sources.isEmpty()) {
				req.setAttribute("message", file.getOriginalFilename() + "没有数据");
				return null;
			}

			return sources;
		} catch (IsNotExcelFileException e) {
			req.setAttribute("message", e.getMessage());
		} catch (NotEnoughColumnsException e) {
			req.setAttribute("message", e.getMessage());
		} catch (Exception e) {
			req.setAttribute("message", "您上传的文件有问题：" + e.getMessage());
		}
		return null;
	}
}
