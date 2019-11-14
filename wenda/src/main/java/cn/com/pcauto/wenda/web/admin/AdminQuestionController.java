package cn.com.pcauto.wenda.web.admin;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/admin/question")
public class AdminQuestionController {

	@Autowired
	private TagService tagService;
	@Autowired
	private QuestionService questionService;
	
	@RequestMapping(value = "/brandQuestion")
	public String BrandQuestion(HttpServletRequest request,HttpServletResponse response){
		return "/admin/question/brandQuestion";
	}
	
	@RequestMapping(value = "/keywordQuestion")
	public String KeywordQuestion(HttpServletRequest request,HttpServletResponse response){
		return "/admin/question/keywordQuestion";
	}
	
	@RequestMapping(value = "/question")
	public String Question(HttpServletRequest request,HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
		//查询问题的参数
		long qid = WebUtils.paramLong(request, "qid", 0);
		String title = WebUtils.param(request, "title", "").trim();
		long uid = WebUtils.paramLong(request, "uid", 0);
		String createBegin = WebUtils.param(request, "createBegin", "");
		String createEnd = WebUtils.param(request, "createEnd", "");
		//问题标签的参数
		long tid = WebUtils.paramLong(request, "tid", 0);
		if (tid < 0) {
			WebUtils.send404(response, "标签ID错误");
			return null;
		}

		Pager<Question> pager = new Pager<Question>();
		QueryParam queryParam = new QueryParam();
		if (qid > 0) {
			if (tid == 0) {
				queryParam.and("id", qid);
			}else {
				queryParam.and("qid", qid);
			}
			request.setAttribute("qid", qid);
		}
		if (StringUtils.isNotBlank(title)) {
			queryParam.and("title",title);
			request.setAttribute("title", title);
		}
		if (uid > 0) {
			queryParam.and("create_by",uid);
			request.setAttribute("uid", uid);
		}
		if (StringUtils.isNotBlank(createBegin)) {
			queryParam.and("create_at", Relation.GEQ, createBegin);
			request.setAttribute("createBegin", createBegin);
		}
		if (StringUtils.isNotBlank(createEnd)) {
			queryParam.and("create_at", Relation.LEQ, createEnd);
			request.setAttribute("createEnd", createEnd);
		}
		queryParam.orderBy("create_at");
		
		if (tid == 0) {
			queryParam.and("tags", "");
			pager = questionService.pager(queryParam, pageNo, pageSize);
		}else {
			queryParam.and("tid", tid);
			pager = questionService.pagerTagQuestion(queryParam,tid, pageNo, pageSize);
		}
		
		request.setAttribute("tid", tid);
		request.setAttribute("pager", pager);
		return "/admin/question/question";
	}
	
	@RequestMapping(value = "/detail")
	public void Detail(HttpServletRequest request, HttpServletResponse response){
		long qid = WebUtils.paramLong(request, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null) {
			WebPrintUtils.errorMsg(request, response, "qid错误");
			return;
		}
		JSONObject json = new JSONObject();
		json.put("qid", question.getId());
		json.put("title", question.getTitle());
		json.put("createAt", DateUtils.formatYYMMDDHM(question.getCreateAt()));
		json.put("content", question.getContent());
		WebPrintUtils.successMsg(request, response, json);
	}
	
	@RequestMapping(value = "/update")
	public void Update(HttpServletRequest request, HttpServletResponse response){
		long qid = WebUtils.paramLong(request, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null) {
			WebPrintUtils.errorMsg(request, response, "问题ID错误");
			return;
		}
		String title = WebUtils.param(request, "title", "");
		if (StringUtils.isBlank(title)) {
			WebPrintUtils.errorMsg(request, response, "标题不能为空");
			return;
		}
		String time = WebUtils.param(request, "time", "");
		Date createAt = DateUtils.parseDate(time, "yyyy-MM-dd HH:mm");
		if (createAt == null) {
			WebPrintUtils.errorMsg(request, response, "时间格式错误");
			return;
		}
		String content = WebUtils.param(request, "content", "");
		
		question.setTitle(title);
		question.setCreateAt(createAt);
		question.setContent(content);
		
		boolean update = questionService.updateQuestion(question);
		if (update) {
			WebPrintUtils.successMsg(request, response, "更新成功");
		}else {
			WebPrintUtils.errorMsg(request, response, "更新失败");
		}
	}
	
	@RequestMapping(value = "/deleteQuestion")
	public void DeleteQuestion(HttpServletRequest request, HttpServletResponse response){
		String ids = WebUtils.param(request, "ids", "");
		if (StringUtils.isBlank(ids)) {
			WebPrintUtils.errorMsg(request, response, "参数ids错误");
			return;
		}
		String[] split = ids.split(",");
		for (String id : split) {
			Question question = questionService.findById(Long.valueOf(id));
			if (question != null) {
				questionService.updateQuestionStatus(question, Const.STATUS_DELETE);
			}else {
				WebPrintUtils.errorMsg(request, response, "删除失败，问答id="+id);
				return;
			}
		}
		JSONObject json = new JSONObject();
		json.put("ids", split);
		WebPrintUtils.successMsg(request, response,json);
	}
}
