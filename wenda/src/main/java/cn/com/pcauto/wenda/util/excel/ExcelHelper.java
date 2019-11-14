package cn.com.pcauto.wenda.util.excel;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelHelper {
	
	private int columnWidth;
	private List<String> sheetNames = new ArrayList<String>();
	private List<List<Object[]>> datas = new ArrayList<List<Object[]>>();
	private String filename = "导出的文件";

	public ExcelHelper(){
	}
	
	public ExcelHelper(String filename) {
		this.filename = filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void addSheetData(String sheetName, List<Object[]> data){
		sheetNames.add(sheetName);
		datas.add(data);
	}
	
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	public void exportToExcel(HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();

		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=" + new String((filename + ".xls").getBytes("UTF-8"), "ISO8859-1"));
		response.setContentType("application/msexcel");

		WritableWorkbook writableWorkbook = Workbook.createWorkbook(out);
		WritableCellFormat writableCellFormat = new WritableCellFormat();
		writableCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		
		for(int s = 0; s < sheetNames.size() && s < datas.size(); s++){
			WritableSheet writableSheet = writableWorkbook.createSheet(sheetNames.get(s), s);
			List<Object[]> list = datas.get(s);
			if(columnWidth > 0 && list.size() > 0){
				for(int i = 0; i < list.get(0).length; i++){
					writableSheet.setColumnView(i, columnWidth);
				}
			}
			for (int r = 0; r < list.size(); r++) {
				Object[] objs = list.get(r);
				for (int c = 0; c < objs.length; c++) {
					Object obj = objs[c];
					String cellContent = obj == null ? "" : obj.toString();
					writableSheet.addCell(new Label(c, r, cellContent, writableCellFormat));
				}
			}
		}
		writableWorkbook.write();
		writableWorkbook.close();
	}
}
