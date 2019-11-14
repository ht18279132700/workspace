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
import org.gelivable.auth.entity.GeliSession;
import org.gelivable.param.OrderBy;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.com.pcauto.wenda.entity.AnswerImport;
import cn.com.pcauto.wenda.entity.QuestionImport;
import cn.com.pcauto.wenda.service.AnswerImportService;
import cn.com.pcauto.wenda.service.QuestionImportService;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.IpUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.com.pcauto.wenda.util.excel.AbsObj;
import cn.com.pcauto.wenda.util.excel.ExcelUtil;

@Controller
@RequestMapping(value = "/admin/questionImport")
public class AdminQuestionImportController {
	
	@Resource(name="importThreadPool")
	private ExecutorService executorService;
	
	@Autowired
	private QuestionImportService questionImportService;
	@Autowired
	private AnswerImportService answerImportService;
	
	private final static Logger LOG = LoggerFactory.getLogger(AdminQuestionImportController.class);
	
	public static final String FILE_NAME = "thirdData.csv";
	static String result = "没有正在处理的请求！";
	public static final List<String> THIRDDATAHEADERS = new ArrayList<String>();
	static {
		THIRDDATAHEADERS.add("URL");
		THIRDDATAHEADERS.add("标签ID");
		THIRDDATAHEADERS.add("标题");
		THIRDDATAHEADERS.add("主楼内容");
		THIRDDATAHEADERS.add("主楼时间");
		THIRDDATAHEADERS.add("主楼用户");
		THIRDDATAHEADERS.add("图片");
		for (int i = 1; i <= 20; i++) {
			THIRDDATAHEADERS.add(i + "楼回复");
			THIRDDATAHEADERS.add(i + "楼时间");
			THIRDDATAHEADERS.add(i + "楼用户");
		}
	}
	
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("thirdDataHeaders", THIRDDATAHEADERS.toString());
		return "admin/questionImport/index";
	}
	
	@RequestMapping(value = "/publish")
	public void publish(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String result = "发布任务已在后台运行...";
		try {
			executorService.execute(new Runnable(){
				@Override
				public void run() {
					try {
						new ProcessBuilder("sh", "/data/web/task/groovy/publish.sh").start();
					} catch (Exception e) {
						LOG.error("start publish script error", e);
					}
				}
			});
		} catch (RejectedExecutionException e) {
			result = "当前系统中有导入问题或发布问题的任务正在运行，请稍后再试！";
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "发布问题任务");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/importThirdData", method = RequestMethod.POST)
	public void importThirdData(@RequestParam("thirdDataExcel")final MultipartFile file, final HttpServletRequest req, HttpServletResponse resp) {
		result = "开始处理请求，请耐心等候！";
		final long userId = GeliSession.getCurrentUser().getUserId();
		
		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					result = "请求正在处理中，请耐心等候！";
					List<AbsObj> sources = ExcelUtil.getExcelData(file, THIRDDATAHEADERS, req);
					if (sources == null) {
						result = (String) req.getAttribute("message");
					}else{
						result = questionImportService.questionImport(sources, userId);
					}
				}
			});
		} catch (RejectedExecutionException e) {
			result = "当前系统中有导入问题或发布问题的任务正在运行，请稍后再试！";
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "导入问题任务");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(req, resp, jsonObject);
	}
	
	@RequestMapping(value = "/taskStatus", method = RequestMethod.GET)
	public void taskStatus(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "导入问题任务状态");
		jsonObject.put("result", result);
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/listQuestionImport")
	public String listQuestionImport(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
		int status = WebUtils.paramInt(request, "status", 0);
		long createBy = WebUtils.paramLong(request, "createBy", 0);
		String createBegin = WebUtils.param(request, "createBegin", "");
		String createEnd = WebUtils.param(request, "createEnd", "");
		
		QueryParam param = new QueryParam();
		if (createBy > 0) {
			param.and("create_by", createBy);
		}
		if (StringUtils.isNotBlank(createBegin)) {
			param.and("create_at", Relation.GEQ, createBegin);
		}
		if (StringUtils.isNotBlank(createEnd)) {
			param.and("create_at", Relation.LEQ, createEnd);
		}
		param.orderBy("create_at", OrderBy.DESC);
		Pager<QuestionImport> pager = questionImportService.pager(pageNo, pageSize, param);
		
		request.setAttribute("pager", pager);
		request.setAttribute("createBy", createBy);
		request.setAttribute("createBegin", createBegin);
		request.setAttribute("createEnd", createEnd);
		return "admin/questionImport/list";
	}
	
	@RequestMapping(value = "/delete")
	public void Delete(HttpServletRequest request, HttpServletResponse response){
		long id = WebUtils.paramLong(request, "id", 0);
		QuestionImport questionImport = questionImportService.findById(id);
		if (questionImport == null) {
			WebPrintUtils.errorMsg(request, response, "导入问题ID错误");
			return;
		}
		int falg = questionImportService.delete(id);
		if (falg > 0) {
			WebPrintUtils.successMsg(request, response, "删除导入问题成功");
		}else {
			WebPrintUtils.errorMsg(request, response, "删除导入问题失败");
		}
	}
	
	@RequestMapping(value = "/update")
	public void Update(HttpServletRequest request, HttpServletResponse response){
		long userId = GeliSession.getCurrentUser().getUserId();
		long id = WebUtils.paramLong(request, "id", 0);
		QuestionImport questionImport = questionImportService.findById(id);
		if (questionImport == null) {
			WebPrintUtils.errorMsg(request, response, "导入问题ID错误");
			return;
		}
		String title = WebUtils.param(request, "title", "");
		String content = WebUtils.param(request, "content", "");
		if (StringUtils.isBlank(title)) {
			WebPrintUtils.errorMsg(request, response, "标题为空");
			return;
		}
		questionImport.setTitle(title);
		questionImport.setContent(content);
		questionImport.setUpdateAt(new Date());
		questionImport.setUpdateBy(userId);
		
		int flag = questionImportService.update(questionImport);
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response, "导入问题更新成功");
		} else {
			WebPrintUtils.errorMsg(request, response, "导入问题更新失败");
		}
	}
	
	@RequestMapping(value = "/detail")
	public String Detail(HttpServletRequest request, HttpServletResponse response){
		long id = WebUtils.paramLong(request, "id", 0);
		QuestionImport questionImport = questionImportService.findById(id);
		if (questionImport == null) {
			WebUtils.send404(response,"导入问题ID错误");
			return null;
		}
		List<AnswerImport> list = answerImportService.listAnswerImport(questionImport.getId());
		
		request.setAttribute("questionImport", questionImport);
		request.setAttribute("list", list);
		return "admin/questionImport/detail";
	}
	
	
	@RequestMapping(value = "/questionExport", method = RequestMethod.POST)
	public void QuestionExport(HttpServletRequest request, HttpServletResponse response){
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
    				Arrays.asList("id", "qid", "title", "url"));
    		for(String word : headers) {
    			header.append(word).append(",");
    		}
    		out.println(header);
    		while(true) {
    			String data = questionImportService.exportQuestion(begin, end, pageNo, pageSize);
    			if(StringUtils.isBlank(data)) {
    				break;
    			}
    			pageNo++;
    			out.println(data);
    		}
		} catch (IOException e) {
			LOG.error("导出导入问题错误："+e);
		} finally {
    		out.flush();
			out.close();
        }
	} 

}
