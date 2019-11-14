package cn.com.pcauto.wenda.util.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel抽象类
 * 
 * @author fxr
 *
 */
public abstract class ExcelObj {
	// 初始化工作簿

	protected XSSFWorkbook workbook = new XSSFWorkbook();

	/**
	 * 只能操作一个sheet完成后再create一个， 暂时不支持同时操作多个sheet， 需要的话可以使用ArrayList保存
	 */
	protected XSSFSheet sheet;

	/**
	 * 创建sheet
	 * 
	 * @param sheetName
	 */
	protected void createSheet(String sheetName) {
		if (StringUtils.isBlank(sheetName)) {
			sheet = workbook.createSheet();
		} else {
			sheet = workbook.createSheet(sheetName);
		}
	}

	/**
	 * 创建单行
	 * 
	 * @param contents
	 * @param index
	 * @param style
	 */
	protected void createRow4Content(Object[] contents, int index,
			XSSFCellStyle style) {
		if (contents == null || contents.length == 0 || index < 0) {
			// TODO 异常处理
		}

		XSSFRow row = sheet.createRow(index);
		row.setHeight((short) 450); // 设置行高
		
		XSSFCell cell = null;
		for (int j = 0, len = contents.length; j < len; j++) {
			cell = row.createCell(j);
			
			String content = contents[j] == null ? "" : contents[j].toString();
			if (NumberUtils.isNumber(content)) {
				cell.setCellValue(NumberUtils.toDouble(content));
			} else {
				cell.setCellValue(content);
			}
			
			if (style != null) {
				cell.setCellStyle(style);
			}
		}
	}

	/**
	 * 创建单行
	 * 
	 * @param contents
	 * @param index
	 */
	protected void createRow4Content(Object[] contents, int index) {
		createRow4Content(contents, index, null);
	}

	/**
	 * <p>
	 * Adjusts the column width to fit the contents.
	 * 
	 * <p>
	 * This process can be relatively slow on large sheets, so this should
	 * normally only be called once per column, at the end of your processing.
	 * 
	 * @param begin
	 *            起始列
	 * @param end
	 *            结束列
	 */
	protected void autoSizeColumn(int begin, int end) {
		for (int i = begin; i < end; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	/**
	 * 合并单元格
	 * 
	 * @param a
	 *            起始行
	 * @param b
	 *            结束行
	 * @param c
	 *            起始列
	 * @param d
	 *            结束列
	 */
	public void addMergedRegion(int a, int b, int c, int d) {
		// TODO 参数校验，或使用时规范注意

		CellRangeAddress cra = new CellRangeAddress(a, b, c, d);
		sheet.addMergedRegion(cra);
	}

	/**
	 * 批量合并区域
	 * 
	 * @param params
	 */
	public void addMergedRegion(int[][] params) {
		// TODO 参数校验，或使用时规范注意

		for (int[] param : params) {
			addMergedRegion(param[0], param[1], param[2], param[3]);
		}
	}
	
	/**
	 * 雅黑字体
	 */
	private XSSFFont font;
	protected XSSFFont getFont() {
		if (font != null) {
			return font;
		}
		
		XSSFFont font = workbook.createFont(); // 字体样式
		font.setFontName("微软雅黑");
		font.setFontHeight(11);
		
		return font;
	}

	/**
	 * 表头样式
	 * 
	 * @return
	 */
	private XSSFCellStyle headStyle;

	protected XSSFCellStyle getHeadStyle() {
		if (headStyle != null) {
			return headStyle;
		}
		headStyle = workbook.createCellStyle();

		headStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中

		headStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		headStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		XSSFFont font = getFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
		font.setColor(IndexedColors.WHITE.getIndex());

		headStyle.setFont(font);

		return headStyle;
	}

	/**
	 * 内容样式
	 * 
	 * @return
	 */
	private XSSFCellStyle contentStyle;

	protected XSSFCellStyle getContentStyle() {
		if (contentStyle != null) {
			return contentStyle;
		}

		contentStyle = workbook.createCellStyle();

		contentStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中

		contentStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); // 垂直居中

		contentStyle.setFont(getFont());

		return contentStyle;
	}

	// TO BE CONTINUE...

	/**
	 * 填充excel内容
	 * 
	 * @return
	 */
	public abstract XSSFWorkbook doExcel();
}

