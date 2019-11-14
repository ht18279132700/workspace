package cn.com.pcauto.wenda.util.excel;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 通过给定的数据输出成Excel文件. 输出时可以指定Excel的模板.
 * @author chensy
 */
public class ExcelGenerator {

	/**
	 * 将数据转成Excel输出到OutputStream. 以数据Map中的Key为表头.
	 * @param data 要输出的数据.
	 * @param outputStream
	 * @throws IOException
	 */
	static public void generate(
			List<Map<String, Object>> data, OutputStream outputStream)
			throws IOException {
		List<String> header = new ArrayList<String>(data.get(0).keySet());
		generate(data, outputStream, header);
	}

	/**
	 * 将数据转成Excel输出到OutputStream. 通过header指定表头和列顺序.
	 * @param data 要输出的数据.
	 * @param outputStream
	 * @param header 表头. 其中的内容必须在data中存在, header里的顺序指定了输出的Excel中的列顺序.
	 * @throws IOException
	 */
	static public void generate(
			List<Map<String, Object>> data, OutputStream outputStream,
			List<String> header) throws IOException {
		Workbook template = createTemplate(header);
		generate(data, outputStream, template);
	}

	private static Workbook createTemplate(List<String> header) {
		HSSFWorkbook template = new HSSFWorkbook();
		Sheet sheet = template.createSheet();
		Row row = sheet.createRow(0);
		int index = 0;
		for (String s : header) {
			Cell cell = row.createCell(index);
			cell.setCellValue(s);
			index++;
		}
		return template;
	}

	/**
	 * 将数据转成Excel输出到OutputStream. 可以通过指定excel文件作为模板.
	 * @param data 要输出的数据.
	 * @param outputStream
	 * @param template Excel模板文件.
	 * @throws IOException
	 * @throws IsNotExcelFileException 如果template不是Excel文件.
	 */
	static public void generate(
			List<Map<String, Object>> data, OutputStream outputStream,
			File template) throws IOException {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(template);
		} catch (InvalidFormatException e) {
			throw new IsNotExcelFileException(e);
		}
		generate(data, outputStream, workbook);
	}

	/**
	 * 将数据转成Excel输出到OutputStream. 可以通过指定excel文件作为模板.
	 * @param data 要输出的数据.
	 * @param outputStream
	 * @param templateFile Excel模板文件的路径.
	 * @throws IOException
	 * @throws IsNotExcelFileException 如果templateFile不是Excel文件.
	 */
	static public void generate(
			List<Map<String, Object>> data, OutputStream outputStream,
			String templateFile) throws IOException {
		generate(data, outputStream, new File(templateFile));
	}

	static private void generate(
			List<Map<String, Object>> data, OutputStream outputStream,
			Workbook template) throws IOException {
		Sheet sheet = template.getSheetAt(template.getActiveSheetIndex());
		MapAbsObj header = HeaderExtractor.extraHeader(sheet);
		fillDataFromRow(sheet, data, header);
		template.write(outputStream);
	}

	private static void fillDataFromRow(Sheet sheet,
			List<Map<String, Object>> data, MapAbsObj header) {
		@SuppressWarnings("unchecked")
		Map<String, Integer> attriIndices = (Map<String, Integer>) header.get(
				HeaderExtractor.key_header);
		int startRowIndex = (Integer) header.getNumber(
				HeaderExtractor.key_startRowIndex);
		int rowIndex = startRowIndex;
		for (Map<String, Object> obj : data) {
			Row row = sheet.createRow(rowIndex);
			for (Map.Entry<String, Integer> attri : attriIndices.entrySet()) {
				Object value = obj.get(attri.getKey());
				if (value == null) {
					continue;
				}
				Cell cell = row.createCell(attri.getValue());
				ExcelUtil.setValueToCell(cell, value);
			}
			rowIndex++;
		}
	}

	private ExcelGenerator() {
	}
}
