package cn.com.pcauto.wenda.web.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/admin/answer")
public class AdminAnswerController {
	
	@Autowired
	private AnswerService answerService;
	@Autowired
	private QuestionService questionService;
	
	@RequestMapping(value = "/list")
	public String All(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
		long qid = WebUtils.paramLong(request, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null) {
			WebUtils.send404(response, "问题ID错误");
			return null;
		}
		//查询回答的参数
		long aid = WebUtils.paramLong(request, "aid", 0);
		String content = WebUtils.param(request, "content", "");
		long uid = WebUtils.paramLong(request, "uid", 0);
		String createBegin = WebUtils.param(request, "createBegin", "");
		String createEnd = WebUtils.param(request, "createEnd", "");
		QueryParam queryParam = new QueryParam();
		queryParam.and("qid", qid);
		if (aid > 0) {
			queryParam.and("id", aid);
			request.setAttribute("aid", aid);
		}
		if (StringUtils.isNotBlank(content)) {
			queryParam.and("content",content);
			request.setAttribute("content", content);
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
		Pager<Answer> pager = answerService.pager(pageNo, pageSize, qid,queryParam);
		
		request.setAttribute("question", question);
		request.setAttribute("pager", pager);
		return "admin/answer/list";
	}

	@RequestMapping(value = "/deleteAll")
	public void Delete(HttpServletRequest request,HttpServletResponse response){
		long qid = WebUtils.paramLong(request, "qid", 0);
		if (qid <= 0) {
			WebPrintUtils.errorMsg(request, response, "问题id错误");
			return;
		}
		String ids = WebUtils.param(request, "ids", "");
		if (StringUtils.isBlank(ids)) {
			WebPrintUtils.errorMsg(request, response, "回答ids错误");
			return;
		}
		String[] split = ids.split(",");
		for (String id : split) {
			Answer answer = answerService.findById(qid, Long.valueOf(id));
			if (answer == null) {
				WebPrintUtils.errorMsg(request, response, "回答id错误:"+id);
				return;
			}
			answerService.updateAnswerStatus(answer,Const.STATUS_DELETE);
		}
		JSONObject json = new JSONObject();
		json.put("ids", split);
		WebPrintUtils.successMsg(request, response, json);
	}
	
	@RequestMapping(value = "/detail")
	public void Detail(HttpServletRequest request, HttpServletResponse response){
		long qid = WebUtils.paramLong(request, "qid", 0);
		long aid = WebUtils.paramLong(request, "aid", 0);
		Answer answer = answerService.findById(qid, aid);
		if (answer == null) {
			WebPrintUtils.errorMsg(request, response, "qid或aid错误");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("qid", qid);
		jsonObject.put("aid", aid);
		jsonObject.put("content", answer.getContent());
		WebPrintUtils.successMsg(request, response, jsonObject);
	}
	
	@RequestMapping(value = "/update")
	public void Update(HttpServletRequest request, HttpServletResponse response){
		long qid = WebUtils.paramLong(request, "qid", 0);
		long aid = WebUtils.paramLong(request, "aid", 0);
		Answer answer = answerService.findById(qid, aid);
		if (answer == null) {
			WebPrintUtils.errorMsg(request, response, "qid或aid错误");
			return;
		}
		String content = WebUtils.param(request, "content", "");
		if (StringUtils.isBlank(content)) {
			WebPrintUtils.errorMsg(request, response, "回答内容为空");
			return;
		}
		answerService.updateAnswer(answer,content);
		WebPrintUtils.successMsg(request, response);
	}
}
