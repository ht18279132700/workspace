package cn.com.pcauto.wenda.web.intf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.Reply;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.censor.CensorAnswerService;
import cn.com.pcauto.wenda.util.EncodeUtils;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.IpUtils;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.pconline.passport3.account.entity.Account;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;


@Controller
@RequestMapping(value = "/intf/answer")
public class IntfAnswerController {
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private CensorAnswerService censorAnswerService;
	@Autowired
	private MemCachedClient mcc;
	
	@RequestMapping(value = "/detail")
	public void detail(HttpServletRequest req, HttpServletResponse resp){
		Functions.setAllowCredentialHeader(req, resp);
		int pageNo = WebUtils.paramPageNoMin1(req);
		int pageSize = WebUtils.paramPageSizeDef20(req);
		int encode = WebUtils.paramInt(req, "encode", 0);
		long qid = WebUtils.paramLong(req, "qid", 0);
		if (qid <= 0 ) {
			WebPrintUtils.errorMsg(req, resp, "问题id错误");
			return;
		}
		Pager<Answer> pager = answerService.pager(pageNo, pageSize, qid);
		JSONObject json = new JSONObject();
		if (pager.getResultList() != null) {
			List<Answer> resultList = pager.getResultList();
			JSONArray answerArray = new JSONArray();
			for (Answer answer : resultList) {
				JSONObject object = answerToJson(answer,encode);
				answerArray.add(object);
			}
			json.put("answerArray", answerArray);
			json.put("total", pager.getTotal());
			json.put("pageSize", pager.getPageSize());
			json.put("pageNo", pager.getPageNo());
			json.put("pageCount", pager.getPageCount());
			WebPrintUtils.successMsg(req, resp, json);
		}else {
			WebPrintUtils.errorMsg(req, resp, json);
		}
	}
	
	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	public void publish(HttpServletRequest request, HttpServletResponse response)throws IOException{
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
		long qid = WebUtils.paramLong(request, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null) {
			WebPrintUtils.errorMsg(request, response, "问题id错误");
			return;
		}
		String content = WebUtils.param(request, "content", "");
		String photos = WebUtils.param(request, "photos", "");
		if (StringUtils.isBlank(content) && (StringUtils.isBlank(photos) || "[]".equals(photos))) {
			WebPrintUtils.errorMsg(request, response, "回答内容不能为空");
			return;
		}
		String md5 = DigestUtils.md5Hex(content);
		if(!mcc.add("pubAnswer"+user.getUid()+md5, true, McCacheTime.getMc30sTimes())){
			WebPrintUtils.errorMsg(request, response, "请勿提交重复内容");
			return;
		}
		Answer answer = new Answer();
		answer.setQid(qid);
		answer.setContent(content);
		answer.setAgent(agent);
		List<QuestionPhoto> list = new ArrayList<QuestionPhoto>();
		if (StringUtils.isBlank(photos)) {
			answer = answerService.create(user, question, answer);
		}else {
			JSONArray photoArray = JSONArray.parseArray(photos);
			for (Object object : photoArray) {
				JSONObject photoObject = (JSONObject) object;
				QuestionPhoto questionPhoto = new QuestionPhoto();
				questionPhoto.setUrl(photoObject.getString("url"));
				questionPhoto.setSize(photoObject.getIntValue("size"));
				questionPhoto.setHeight(photoObject.getIntValue("height"));
				questionPhoto.setWidth(photoObject.getIntValue("width"));
				list.add(questionPhoto);
			}
			answer = answerService.create(user, question, answer, list);
		}
		if (answer.getId() > 0) {
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("nickName", user.getNickName());
			data.put("avatarUrl", Functions.getUserIcon(user.getUid(), 50));
			data.put("time", answer.getShowCreateAt());
			data.put("aid", answer.getId());
			data.put("uid", answer.getCreateBy());
			json.put("data", data);
			WebPrintUtils.successMsg(request, response, json);
			//通知审核平台创建答案
			censorAnswerService.notifyCensorCreateAnswer(user, question, answer, IpUtils.getIp(request));
		}else {
			WebPrintUtils.errorMsg(request, response, "回答失败");
		}
		
	}
	
	public JSONObject answerToJson(Answer answer,int encode){
		JSONObject object = new JSONObject();
		object.put("id", answer.getId());
		object.put("qid", answer.getQid());
		if (encode > 0) {
			object.put("content", answer.getContent()!=null ? answer.getContent() : "");
		}else {
			object.put("content", EncodeUtils.encodeForHTML(answer.getContent()!=null ? answer.getContent() : ""));
		}
		object.put("imageNum", answer.getImageNum());
		object.put("status", answer.getStatus());
		object.put("createBy", answer.getCreateBy());
		object.put("createAt", answer.getShowCreateAt());
		object.put("praiseNum", answer.getPraiseNum());
		object.put("treadNum", answer.getTreadNum());
		object.put("praiseStatus", answer.getPraiseStatus());
		object.put("replyNum", answer.getReplyNum());
		object.put("userIcon", Functions.getUserIcon(answer.getCreateBy(), 50));
		object.put("nickName", (answer.getUser()==null || answer.getUser().getUid()== 0)?"": answer.getUser().getNickName());
		List<QuestionPhoto> photos = answer.getPhotos();
		JSONArray photoArray = new JSONArray();
		for (QuestionPhoto questionPhoto : photos) {
			JSONObject photoObject = new JSONObject();
			photoObject.put("url", Functions.getImgSize(questionPhoto.getUrl(), "789x789"));
			photoArray.add(photoObject);
		}
		object.put("photoArray", photoArray);
		List<Reply> listReply = answer.getListReply();
		JSONArray replyArray = new JSONArray();
		for (Reply reply : listReply) {
			JSONObject replyObject = new JSONObject();
			replyObject.put("id", reply.getId());
			replyObject.put("createBy", reply.getCreateBy());
			replyObject.put("aid", answer.getId());
			if (encode > 0) {
				replyObject.put("content", reply.getContent());
			}else {
				replyObject.put("content", EncodeUtils.encodeForHTML(reply.getContent()));
			}
			replyObject.put("userIcon", Functions.getUserIcon(reply.getCreateBy(), 50));
			replyObject.put("userNickName", (reply.getUser()==null || reply.getUser().getUid()== 0)?"": reply.getUser().getNickName());
			replyObject.put("createAt", reply.getShowCreateAt());
			replyObject.put("beRepliedUserNickName", (reply.getBeRepliedUser()==null || reply.getBeRepliedUser().getUid()== 0)?"": reply.getBeRepliedUser().getNickName());
			replyArray.add(replyObject);
		}
		object.put("replyArray", replyArray);
		return object;
	}
}
