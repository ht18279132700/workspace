package cn.com.pcauto.wenda.util.excel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 导出工具
 * 
 * @author fxr
 *
 */
public enum ExportUtils {
	EXCEL;
	
	private static final Logger log = LoggerFactory.getLogger(ExportUtils.class);
	
	/**
	 * 导出通用方法
	 * 
	 * @param response 
	 * @param fileName 文件名
	 * @param workbook
	 * @return
	 */
	public String export(HttpServletRequest request, HttpServletResponse response, 
			String fileName, XSSFWorkbook workbook) {
		
		if (response == null || fileName == null || workbook == null) {
			throw new RuntimeException("参数错误.");
		}
		
		try {
			response.setContentType("application/octet-stream");
			response.setCharacterEncoding("UTF-8");
			
			String header = request.getHeader("User-Agent").toUpperCase();
			if(header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")){
				fileName = URLEncoder.encode(fileName, "UTF-8");
				fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
		    }else{
		    	fileName = new String(fileName.getBytes("UTF-8"), "iso8859-1");
		    }
			response.setHeader("content-disposition", "attachment;filename=\"" + fileName+"\"");
			
			OutputStream out = response.getOutputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			byte[] b = baos.toByteArray();
			out.write(b, 0, b.length);
			
			baos.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("export excel error, e: {}", e);
		}
		
		return null;
	}
}