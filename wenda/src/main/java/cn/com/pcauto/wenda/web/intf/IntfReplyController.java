package cn.com.pcauto.wenda.web.intf;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.pcauto.wenda.config.SystemConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Reply;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.ReplyService;
import cn.com.pcauto.wenda.service.censor.CensorReplyService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.IpUtils;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.pconline.passport3.account.entity.Account;

import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;

@Controller
@RequestMapping(value = "/intf/reply")
public class IntfReplyController {
	@Autowired
	private ReplyService replyService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private CensorReplyService censorReplyService;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private MemCachedClient mcc;

	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	public void publish(HttpServletRequest request,HttpServletResponse response)throws IOException{
		Functions.setAllowCredentialHeader(request, response);
		if (systemConfig.getForbid()==1) {
			WebPrintUtils.errorMsg(request, response, systemConfig.forbidTips);
			return;
		}
		User user = (User) request.getAttribute("user");
		if (user == null  || user.getUid() <= 0) {
			WebPrintUtils.errorMsg(request, response, "用户未登录");
			return;
		}
		Account account = Functions.getPassportAccount(user.getUid());
		if(account == null || !account.isActivatedMobile()){
			WebPrintUtils.errorMsg(request, response, "请先绑定手机号");
			return;
		}
		int agent = WebUtils.paramInt(request, "agent", 0);
		String content = WebUtils.param(request, "content", "");
		if (StringUtils.isBlank(content)) {
			WebPrintUtils.errorMsg(request, response, "回复内容不能为空");
			return;
		}
		String md5 = DigestUtils.md5Hex(content);
		if(!mcc.add("pubReply"+user.getUid()+md5, true, McCacheTime.getMc30sTimes())){
			WebPrintUtils.errorMsg(request, response, "请勿提交重复内容");
			return;
		}
		long qid = WebUtils.paramLong(request, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null) {
			WebPrintUtils.errorMsg(request, response, "参数qid错误");
			return;
		}
		long aid = WebUtils.paramLong(request, "aid", 0);
		Answer answer = answerService.findById(qid, aid);
		if (answer == null) {
			WebPrintUtils.errorMsg(request, response, "参数aid错误");
			return;
		}
		long rid = WebUtils.paramLong(request, "rid", 0);
		Reply reply = new Reply();
		reply.setBeRepliedAid(aid);
		reply.setBeRepliedRid(rid);
		reply.setBeRepliedUid(user.getUid());
		reply.setQid(qid);
		reply.setContent(content);
		reply.setAgent(agent);
		if (rid > 0) {
			 Reply scrReply = replyService.findById(aid, rid);
			 reply = replyService.create(user, answer, scrReply, reply);
		}else {
			reply = replyService.create(user, answer, reply);
		}
		if (reply.getId() > 0) {
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("nickName", user.getNickName());
			data.put("avatarUrl", Functions.getUserIcon(user.getUid(), 50));
			data.put("time", reply.getShowCreateAt());
			data.put("uid", reply.getCreateBy());
			data.put("aid", reply.getBeRepliedAid());
			data.put("rid", reply.getId());
			json.put("data", data);
			WebPrintUtils.successMsg(request, response, json);
			//通知审核平台创建回复
			censorReplyService.notifyCensorCreateReply(user, question, reply, IpUtils.getIp(request));
		}else {
			WebPrintUtils.errorMsg(request, response, "回复失败");
		}
	}
}
