package cn.com.pcauto.wenda.web.intf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Praise;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.PraiseService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/intf/praise")
public class IntfPraiseController {

	@Autowired
	private PraiseService praiseService;
	@Autowired
	private AnswerService answerService;
	
	@RequestMapping(value = "/praiseAndTread")
	public void praiseAndTread(HttpServletRequest req, HttpServletResponse resp){
		Functions.setAllowCredentialHeader(req, resp);
		User user = (User)req.getAttribute("user");
		if (user == null || user.getUid() <= 0) {
			WebPrintUtils.errorMsg(req, resp, "用户未登录");
			return;
		}
		long answerId = WebUtils.paramLong(req, "answerId", 0);
		if (answerId <= 0) {
			WebPrintUtils.errorMsg(req, resp, "回答ID错误");
			return;
		}
		long qid = WebUtils.paramLong(req, "qid", 0);
		if (qid <= 0) {
			WebPrintUtils.errorMsg(req, resp, "问题ID错误");
			return;
		}
		int status = WebUtils.paramInt(req, "status", 99);
		if (status != Const.TREAD && status != Const.PRAISE) {
			WebPrintUtils.errorMsg(req, resp, "点赞状态status错误");
			return;
		}
		Answer answer = answerService.findById(qid, answerId);
		if(answer == null){
			WebPrintUtils.errorMsg(req, resp, "答案不存在，不能赞/踩");
			return;
		}
		Praise praise = praiseService.praise(user, answer, status);
		JSONObject json = new JSONObject();
		if (praise != null) {
			answer = answerService.findById(qid, answerId);
			json.put("praiseNum", answer.getPraiseNum());
			json.put("treadNum", answer.getTreadNum());
			json.put("status", praise.getStatus());
			WebPrintUtils.successMsg(req, resp, json);
		}else{
			WebPrintUtils.errorMsg(req, resp, "点赞操作错误");
		}
	}


}
