package cn.com.pcauto.wenda.web.intf;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.gelivable.param.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.CounterService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.service.censor.CensorQuestionService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.EncodeUtils;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.IpUtils;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.pconline.passport3.account.entity.Account;
import cn.pconline.r.client.RClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.amber.field.IdField;
import com.danga.MemCached.MemCachedClient;

@Controller
@RequestMapping(value = "/intf/question")
public class IntfQuestionController {
	
	@Autowired
	private QuestionService questionService;
	@Autowired
	private TagService tagService;
	@Autowired
	private CensorQuestionService censorQuestionService;
	@Autowired
	private RClient rClient;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private MemCachedClient mcc;
	@Autowired
	private CounterService counterService;
	
	private static final Logger log = Logger.getLogger(IntfQuestionController.class);

	/**
	 * 首页-问题列表接口
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/index")
	public void index(HttpServletRequest req, HttpServletResponse resp){
		Functions.setAllowCredentialHeader(req, resp);
		int pageNo = WebUtils.paramPageNoMin1(req);
		int pageSize = WebUtils.paramPageSizeDef20(req);
		int encode = WebUtils.paramInt(req, "encode", 0);
		//最新问答
		Pager<Question> pager = questionService.pagerForANum(pageNo, pageSize, 5);
		JSONObject json = new JSONObject();
		if (pager.getResultList() !=null ) {
			List<Question> list = pager.getResultList();
			JSONArray questionArray = new JSONArray();
			for (Question question : list) {
				JSONObject object = questionToJson(question,encode);
				questionArray.add(object);
			}
			json.put("questionArray", questionArray);
			json.put("total", pager.getTotal());
			json.put("pageSize", pager.getPageSize());
			json.put("pageNo", pager.getPageNo());
			json.put("pageCount", pager.getPageCount());
			WebPrintUtils.successMsg(req, resp, json);
		}else {
			WebPrintUtils.errorMsg(req, resp, json);
		}
	}
	
	/**
	 * 单个问题详情接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/question")
	public void question(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		User user = (User) request.getAttribute("user");
		long qid = WebUtils.paramLong(request, "qid", 0);
		int encode = WebUtils.paramInt(request, "encode", 0);
		Question question = questionService.findById(qid);
		if (question == null || question.getStatus() == Const.STATUS_DELETE) {
			WebPrintUtils.errorMsg(request, response, "param qid is error or Question object is not find");
			return;
		}
		if (question.getStatus() == Const.STATUS_PENDING && systemConfig.getCensorType() == 1 && user.getUid() != question.getCreateBy()) {
			WebPrintUtils.errorMsg(request, response, "param qid is error or Question object is not find");
			return;
		}
		//增加问题的浏览量
		counterService.incrQuestionPv(qid);
		//相关车系
		List<Tag> tagList = question.getTagList();
		//相关问答
		List<Question> relateQuestion = questionService.listRelateQuestion(question, 5);
		
		//问题相关数据
		JSONObject json = new JSONObject();
		json.put("id", question.getId());
		if (encode > 0) {
			json.put("title", question.getTitle());
		}else {
			json.put("title", EncodeUtils.encodeForHTML(question.getTitle()));
		}
		json.put("createBy", question.getCreateBy());
		json.put("userIcon", Functions.getUserIcon(question.getCreateBy(), 50));
		json.put("nickName", question.getUser().getNickName());
		json.put("showCreateAt", question.getShowCreateAt());
		if (encode > 0) {
			json.put("content", question.getContent()!=null ? question.getContent() : "");
		}else {
			json.put("content", EncodeUtils.encodeForHTML(question.getContent()!=null ? question.getContent() : ""));
		}
		List<QuestionPhoto> photos = question.getPhotos();
		JSONArray photoArray = new JSONArray();
		if (photos != null && photos.size() > 0) {
			for (QuestionPhoto questionPhoto : photos) {
				JSONObject photoObject = new JSONObject();
				photoObject.put("imgUrl", Functions.getImgSize(questionPhoto.getUrl(), "789x789"));
				photoArray.add(photoObject);
			}
		}
		json.put("imgList", photoArray);
		
		//相关问答数据
		JSONArray relateArray = new JSONArray();
		if (relateQuestion != null && relateQuestion.size() > 0) {
			for(Question relate : relateQuestion){
				JSONObject relateObject = new JSONObject();
				relateObject.put("qid", relate.getId());
				if (encode > 0) {
					relateObject.put("title", relate.getTitle());
				}else {
					relateObject.put("title", EncodeUtils.encodeForHTML(relate.getTitle()));
				}
				relateArray.add(relateObject);
			}
		}
		json.put("relateQuestions", relateArray);
		
		//相关车系数据
		long serialId = 0;
		JSONArray tagArray = new JSONArray();
		if (tagList != null && tagList.size() > 0) {
			for(Tag tag : tagList){
				JSONObject tagObject = new JSONObject();
				tagObject.put("tid", tag.getId());
				tagObject.put("name", tag.getName());
				tagArray.add(tagObject);
				if (serialId == 0) {
					serialId = tag.getSerialId();
				}
			}
		}
		json.put("tagList", tagArray);
		json.put("serialId", serialId);
		WebPrintUtils.successDataMsg(request, response, json, "操作成功");
	}
	
	/**
	 * 标签页-问题列表接口
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/topic")
	public void topic(HttpServletRequest req, HttpServletResponse resp){
		Functions.setAllowCredentialHeader(req, resp);
		long tid = WebUtils.paramTagId(req);
		int encode = WebUtils.paramInt(req, "encode", 0);
		Tag tag = tagService.findById(tid);
		if (tag == null) {
			WebPrintUtils.errorMsg(req, resp, "话题标签为空");
			return;
		}
		int pageNo = WebUtils.paramPageNoMin1(req);
		int pageSize = WebUtils.paramPageSizeDef20(req);
		//话题标签下的所有问题
		Pager<Question> pager = questionService.pagerTagQuestion(pageNo, pageSize, tid);
		JSONObject json = new JSONObject();
		if (pager.getResultList() !=null ) {
			List<Question> list = pager.getResultList();
			JSONArray questionArray = new JSONArray();
			for (Question question : list) {
				JSONObject object = questionToJson(question,encode);
				questionArray.add(object);
			}
			json.put("questionArray", questionArray);
			json.put("total", pager.getTotal());
			json.put("pageSize", pager.getPageSize());
			json.put("pageNo", pager.getPageNo());
			json.put("pageCount", pager.getPageCount());
			json.put("serialId", tag.getSerialId());
			json.put("tid", tag.getId());
			json.put("name", tag.getName());
			WebPrintUtils.successMsg(req, resp, json);
		}else {
			WebPrintUtils.errorMsg(req, resp, json);
		}
	}
	/**
	 * 发布问题接口
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	public void publish(HttpServletRequest request, HttpServletResponse response)throws IOException{
		Functions.setAllowCredentialHeader(request, response);
		if (systemConfig.getForbid()==1) {
			WebPrintUtils.errorMsg(request, response, systemConfig.forbidTips);
			return;
		}
		User user = (User) request.getAttribute("user");
		if (user == null || user.getUid() <= 0) {
			WebPrintUtils.errorMsg(request, response, "用户未登录");
			return;
		}
		Account account = Functions.getPassportAccount(user.getUid());
		if(account == null || !account.isActivatedMobile()){
			WebPrintUtils.errorMsg(request, response, "请先绑定手机号");
			return;
		}
		String title = WebUtils.param(request, "title", "");
		if (StringUtils.isBlank(title)) {
			WebPrintUtils.errorMsg(request, response, "标题不能为空");
			return;
		}
		if (title.length() > 255) {
			WebPrintUtils.errorMsg(request, response, "标题内容过长");
			return;
		}
		String md5 = DigestUtils.md5Hex(title);
		if(!mcc.add("pubQuestion"+user.getUid()+md5, true, McCacheTime.getMc30sTimes())){
			WebPrintUtils.errorMsg(request, response, "请勿提交重复内容");
			return;
		}
		if(!mcc.add("userQuestion-"+user.getUid(), true, McCacheTime.getMc1HTimes())){
			WebPrintUtils.errorMsg(request, response, "1小时只能提问1次");
			return;
		}
		log.info("user ID is " + user.getUid() + ", IP is " +IpUtils.getIp(request));
		String content = WebUtils.param(request, "content", "");
		String photos = WebUtils.param(request, "photos", "");
		String tags = WebUtils.param(request, "tags", "");
		Question question = new Question();
		question.setTitle(title);
		question.setContent(content);
		question.setTags(tags);
		if (StringUtils.isBlank(photos)) {
			question = questionService.create(user, question);
		}else {
			List<QuestionPhoto> list = new ArrayList<QuestionPhoto>();
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
			question = questionService.create(user, question, list);
		}
		if (question.getId() > 0) {
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("nickName", user.getNickName());
			data.put("avatarUrl", Functions.getUserIcon(user.getUid(), 50));
			data.put("time", question.getShowCreateAt());
			data.put("url", systemConfig.getRoot()+"/"+question.getId()+".html");
			json.put("data", data);
			WebPrintUtils.successMsg(request, response, json);
			//通知审核平台创建问题
			censorQuestionService.notifyCensorCreateQuestion(user, question, IpUtils.getIp(request));
		}else {
			WebPrintUtils.errorMsg(request, response, "创建问题失败");
		}
	}
	
	/**
	 * 搜索问题页-问题列表接口
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/search")
	public void Search(HttpServletRequest request, HttpServletResponse response)throws IOException{
		Functions.setAllowCredentialHeader(request, response);
		int pageNo = WebUtils.paramPageNoMin1(request);
		int pageSize = WebUtils.paramPageSizeDef20(request);
		int encode = WebUtils.paramInt(request, "encode", 0);
		String keywords = WebUtils.param(request, "keywords", "");
    	if(StringUtils.isBlank(keywords)){
    		WebPrintUtils.errorMsg(request, response, "搜索的关键词为空");
			return;
    	}
    	String enKeywords = URLEncoder.encode(keywords,"UTF-8");
    	String url = systemConfig.getKsRoot() + "/wd/" + enKeywords + "/?pageNo=" + pageNo + "&pageSize=" + pageSize;
    	Pager<Question> pager = questionService.pagerForKS(url, pageNo, pageSize);
    	List<Question> searchQuestions = pager.getResultList();
		JSONObject json = new JSONObject();
		if (searchQuestions != null) {
			JSONArray questionArray = new JSONArray();
			for (Question question : searchQuestions) {
				JSONObject questionObject = questionToJson(question,encode);
				questionArray.add(questionObject);
			}
			json.put("questionArray", questionArray);
			json.put("total", pager.getTotal());
			json.put("pageCount", pager.getPageCount());
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response, json);
		}
	}
	
	public JSONObject questionToJson(Question question,int encode){
		JSONObject object = new JSONObject();
		object.put("id", question.getId());
		if (encode > 0) {
			object.put("title", question.getTitle());
		}else {
			object.put("title", EncodeUtils.encodeForHTML(question.getTitle()));
		}
		object.put("createBy", question.getCreateBy());
		object.put("userIcon", Functions.getUserIcon(question.getCreateBy(), 50));
		object.put("showCreateAt", question.getShowCreateAt());
		object.put("answerNum", Functions.getFormatStr(question.getAnswerNum()));
		object.put("nickname", (question.getUser()==null || question.getUser().getUid()== 0)?"": question.getUser().getNickName());
		if (question.getMostPraiseAnswer() != null) {
			if (encode > 0) {
				object.put("mostPraiseAnswer", question.getMostPraiseAnswer().getContent());
			}else {
				object.put("mostPraiseAnswer", EncodeUtils.encodeForHTML(question.getMostPraiseAnswer().getContent()));
			}
			object.put("answerImageNum", question.getMostPraiseAnswer().getImageNum());
			if (question.getMostPraiseAnswer().getFirstPhoto() != null) {
				object.put("answerPhotoUrl", Functions.getImgSize(question.getMostPraiseAnswer().getFirstPhoto().getUrl(), "240x160"));
			}else {
				object.put("answerPhotoUrl", "");
			}
		}else {
			object.put("mostPraiseAnswer", "");
			object.put("answerImageNum", "");
		}
		return object;
	}
	
	/**
	 * 相关问答接口，提供给其他应用调用，出指定车系的问答
	 * 不传serialIds或serialIds=0时，从全部问答中获取数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/relevant")
	public void relevant(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		String serialIds = WebUtils.param(request, "serialIds", "0");
		int num = NumberUtils.toInt(request.getParameter("num"), 4);
		int encode = WebUtils.paramInt(request, "encode", 0);
		if(num <= 0){
			num = 1;
		}
		String key = "relevantQuestion"+serialIds+"-"+num;
		String value = (String)mcc.get(key);
		if(StringUtils.isNotBlank(value)){
			JSONObject json = JSONObject.parseObject(value);
			WebPrintUtils.successMsg(request, response, json);
			return;
		}
		
		JSONArray outerArr = new JSONArray();
		List<Long> sids = Functions.stringArr2LongList(serialIds.split(","));
		for (long sid : sids) {
			List<Question> list = null;
			Tag tag = tagService.getTagBySid(sid);
			if(tag != null){
				list = questionService.listRelevantQuestionByTag(tag, num);
			}else if(sid == 0){
				list = getQuestionFromAll(num);
			}else{
				list = Collections.emptyList();
			}
			
			JSONArray array = new JSONArray();
			for (Question q : list) {
				if(q == null){
					continue;
				}
				JSONObject jo = new JSONObject();
				jo.put("qid", q.getId());
				jo.put("img", Functions.getUserIcon(q.getCreateBy(), 50));
				if (encode > 0) {
					jo.put("title", q.getTitle());
				}else {
					jo.put("title", EncodeUtils.encodeForHTML(q.getTitle()));
				}
				jo.put("url", systemConfig.getRootMoveHttp()+"/"+q.getId()+".html");
				jo.put("answerNum", q.getAnswerNum());
				jo.put("date", q.getCreateAt().getTime());
				array.add(jo);
			}
			JSONObject json = new JSONObject();
			json.put("topics", array);
			json.put("serialId", sid);
			outerArr.add(json);
		}
		JSONObject outerJson = new JSONObject();
		outerJson.put("data", outerArr);
		mcc.set(key, outerJson.toJSONString(), McCacheTime.getMc4HTimes());
		WebPrintUtils.successMsg(request, response, outerJson);
	}
	
	/**
	 * 从所有问题中获取num个问题
	 * @param num
	 * @return
	 */
	private List<Question> getQuestionFromAll(int num) {
		QueryParam param = new QueryParam();
		param.and("status", Const.STATUS_PASS);
		param.orderBy("last_answer_at");
		param.orderBy("create_at");
		return questionService.list(param, num);
	}
	
	/**
	 * 车系论坛-提问帖-详情页底部的车问答入口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/randomQuestion")
	public void getRandomQuestion(HttpServletRequest request,HttpServletResponse response){
		int limit = WebUtils.paramInt(request, "limit", 16);
		if (limit <=0) {
			WebPrintUtils.errorMsg(request, response, "limit 参数错误");
			return;
		}
		Date start = DateUtils.getBefore7Day();
		Date end = new Date();
		int count = questionService.countRandomQuestionCache(start,end);
		int random = 0;
		if ((count-limit) > 0) {
			random = (int) (Math.random()*(count-limit));
		}
		List<Question> list = questionService.listRandomQuestion(start, end, random, limit);
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		if (list != null && list.size() > 0) {
			for (Question question : list) {
				JSONObject questionObject = new JSONObject();
				questionObject.put("id", question.getId());
				questionObject.put("title", question.getTitle());
				array.add(questionObject);
			}
			object.put("data", array);
			WebPrintUtils.successMsg(request, response, object, "success");
		}else {
			object.put("message", "data is null");
			object.put("data", array);
			WebPrintUtils.errorMsg(request, response, object);
		}
	}
}
