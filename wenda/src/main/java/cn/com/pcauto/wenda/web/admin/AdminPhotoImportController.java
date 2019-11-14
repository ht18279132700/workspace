package cn.com.pcauto.wenda.web.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.gelivable.auth.entity.GeliSession;
import org.gelivable.param.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.com.pcauto.wenda.entity.PhotoImport;
import cn.com.pcauto.wenda.service.PhotoImportService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.com.pcauto.wenda.util.excel.AbsObj;
import cn.com.pcauto.wenda.util.excel.ExcelUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/admin/photoImport")
public class AdminPhotoImportController {

	@Resource(name="importThreadPool")
	private ExecutorService executorService;
	
	@Autowired PhotoImportService photoImportService;
	
	private final static Logger log = Logger.getLogger(AdminPhotoImportController.class); 
	static String result = "没有正在处理的请求！";
	public static final List<String> ThirdPhotoHeaders = new ArrayList<String>();
	public static final String FILE_NAME = "photoData.csv";
	static {
		ThirdPhotoHeaders.add("原图url");
	}
	
	@RequestMapping(value = "/importPhotoData")
	public void ImportPhotoData(@RequestParam("PhotoDataExcel")final MultipartFile file, final HttpServletRequest request, HttpServletResponse response){
		result = "开始处理请求，请耐心等候！";
		final long userId = GeliSession.getCurrentUser().getUserId();
		
		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					result = "请求正在处理中，请耐心等候！";
					List<AbsObj> sources = ExcelUtil.getExcelData(file, ThirdPhotoHeaders, request);
					if (sources == null) {
						result = (String) request.getAttribute("message");
					}else{
						result = photoImportService.photoImport(sources, userId);
					}
				}
			});
		} catch (RejectedExecutionException e) {
			result = "当前系统中有导入图片或切图任务正在运行，请稍后再试！";
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "导入图片任务状态");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/taskStatus", method = RequestMethod.GET)
	public void taskStatus(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "导入图片任务状态");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/listPhotoImport")
	public String ListPhotoImport(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNum", 1);
		int pageSize = WebUtils.paramInt(request, "numPerPage", 20);
		QueryParam param = new QueryParam();
		param.and("status", Const.STATUS_PASS);
		param.orderBy("update_at");
		Pager<PhotoImport> pager = photoImportService.pager(pageNo, pageSize, param);
		
		request.setAttribute("pager", pager);
		return "admin/photoImport/list";
	}
	
	@RequestMapping(value = "/publish")
	public void publish(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String result = "切图任务已在后台运行...";
		try {
			executorService.execute(new Runnable(){
				@Override
				public void run() {
					try {
						new ProcessBuilder("sh", "/data/web/task/groovy/cutPhoto.sh").start();
					} catch (Exception e) {
						log.error("start publish script error", e);
					}
				}
			});
		} catch (RejectedExecutionException e) {
			result = "当前系统中有导入图片或切图任务正在运行，请稍后再试！";
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "切图任务");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/getJsonPhoto")
	public void GetJsonPhoto(HttpServletRequest request, HttpServletResponse response){
		String ids = WebUtils.param(request, "ids", "");
		if (StringUtils.isBlank(ids)) {
			WebPrintUtils.errorMsg(request, response, "参数ids错误");
			return;
		}
		String[] split = ids.split(",");
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (String str : split) {
			JSONObject photoObject = new JSONObject();
			PhotoImport photoImport = photoImportService.findById(Integer.valueOf(str));
			photoObject.put("url", photoImport.getWdUrl());
			photoObject.put("height", photoImport.getHeight());
			photoObject.put("width", photoImport.getWidth());
			photoObject.put("size", photoImport.getSize());
			jsonArray.add(photoObject);
		}
		json.put("photos", jsonArray.toString());
		if (jsonArray.toArray().length < 1000) {
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response, "图片数据长度超过1000");
		}
	}
	
	@RequestMapping(value = "/photoExport")
	public void PhotoExport(HttpServletRequest request, HttpServletResponse response){
		String beginDate = WebUtils.param(request, "beginDate", "");
		String endDate = WebUtils.param(request, "endDate", "");
		if (StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
			WebPrintUtils.errorMsg(request, response, "开始或结束时间为空");
			return;
		}
		Date begin = DateUtils.parseDate(beginDate);
		Date end = DateUtils.parseDate(endDate);
		
		PrintWriter out = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + "filename=" + FILE_NAME);
			out = new PrintWriter(response.getOutputStream());
			
			int pageSize = 1000000;
    		int pageNo = 1;
    		StringBuilder header = new StringBuilder();
    		ArrayList<String> headers = new ArrayList<String>(
    				Arrays.asList("原图ID", "原图URL"));
    		for(String word : headers) {
    			header.append(word).append(",");
    		}
    		out.println(header);
    		while(true) {
    			String data = photoImportService.exportPhoto(begin, end, pageNo, pageSize);
    			if(StringUtils.isBlank(data)) {
    				break;
    			}
    			pageNo++;
    			out.println(data);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
    		out.flush();
			out.close();
        }
	}
}
