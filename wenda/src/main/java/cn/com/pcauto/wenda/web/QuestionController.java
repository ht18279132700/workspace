package cn.com.pcauto.wenda.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.CounterService;
import cn.com.pcauto.wenda.service.PhotoImportService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.IpUtils;
import cn.com.pcauto.wenda.util.JsonUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.pconline.r.client.RClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/question")
public class QuestionController {
	
	@Autowired
	private QuestionService questionService;
	@Autowired
	private TagService tagService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserService userService;
	@Autowired
	private CounterService counterService;
	@Autowired
	private PhotoImportService photoImportService;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private RClient rClient;
	
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	public static final int PAGE_SIZE = 20;

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest req, HttpServletResponse resp){
		int pageNo = WebUtils.paramPageNoMin1(req);
		//热门话题
		List<Tag> tags = tagService.listHotTagDaily();
		if (tags == null) {
			tags = tagService.listTagForNum(10);
		}
		//最新问答
		Pager<Question> pager = questionService.pagerForANum(pageNo, PAGE_SIZE, 5);
		//热门问答
		List<Question> hotQuestions = questionService.listForPV(20, 5);
		req.setAttribute("tags", tags);      
		req.setAttribute("pager", pager);
		req.setAttribute("hotQuestions", hotQuestions);
		return "template/question/index";
	}
	
	@RequestMapping(value = "/detail")
	public String detail(HttpServletRequest req, HttpServletResponse resp){
		User user = (User) req.getAttribute("user");
		int pageNo = WebUtils.paramPageNoMin1(req);
		long qid = WebUtils.paramLong(req, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null || question.getStatus() == Const.STATUS_DELETE) {
			WebUtils.send404(resp,"param qid is error or Question object is not find");
			return null;
		}
		if (question.getStatus() == Const.STATUS_PENDING && systemConfig.getCensorType() == 1 && user.getUid() != question.getCreateBy()) {
			WebUtils.send404(resp,"param qid is error or Question object is not find");
			return null;
		}
		//增加问题的浏览量
		counterService.incrQuestionPv(qid);
		
		List<Tag> tagList = question.getTagList();
		Pager<Answer> pager = answerService.pager(pageNo, PAGE_SIZE, qid);
		List<Question> relateQuestion = questionService.listRelateQuestion(question, 5);
		
		req.setAttribute("user", user);
		req.setAttribute("question", question);
		req.setAttribute("tagList", tagList);
		req.setAttribute("descript", StringUtils.isNotBlank(question.getContent())?(question.getContent().length()>120
				?question.getContent().substring(0, 120):question.getContent()):question.getTitle());
		req.setAttribute("pager", pager);
		req.setAttribute("relateQuestion", relateQuestion);
		if(tagList.size() > 0){
			req.setAttribute("relateTags", tagService.listRelateTag2(tagList.get(0), 12));
		}
		if (pageNo == 1) {
			return "template/question/detail";
		}else {
			return "template/question/detail_pager";
		}
	}
	
	@RequestMapping(value = "/search")
	public String Search(HttpServletRequest request, HttpServletResponse response) throws IOException{
		int pageNo = WebUtils.paramPageNoMin1(request);
		String keywords = WebUtils.param(request, "keywords", "");
    	if(StringUtils.isBlank(keywords)){
    		WebUtils.send404(response,"search keywords is null");
			return null;
    	}
    	String enKeywords = URLEncoder.encode(keywords,"UTF-8");
    	String url = systemConfig.getKsRoot() + "/wd/" + enKeywords + "/?pageNo=" + pageNo + "&pageSize=" + PAGE_SIZE;
    	Pager<Question> pager = questionService.pagerForKS(url, pageNo, PAGE_SIZE);

    	//搜索内容中含有话题标签时显示对应的标签
    	JSONObject jsonTag = tagService.getMCTag();
    	JSONArray tagArray = getRelateTagList(jsonTag, keywords);
    	boolean hasTags = false;
    	Object tagObject = null;
    	if (tagArray != null && tagArray.size() > 0) {
    		hasTags = true;
    		tagObject = tagArray;
		}
    	
    	List<Question> searchQuestions = pager.getResultList();//搜索问题页的结果
		if (searchQuestions != null && !hasTags) {
			Set<Tag> searchTags = new HashSet<Tag>();//搜索结果问答中打的二级关键词标签
			for (Question question : searchQuestions) {
				List<Tag> tagList = question.getTagList();
				if (tagList != null) {
					for (Tag tag : tagList) {
						if ("2".equals(tag.getTagType())) {
							searchTags.add(tag);
						}
					}
				}
			}
			if (searchTags.size() > 0) {
				hasTags = true;
	    		tagObject = searchTags;
			}
		}
		//无标签推荐时显示首页的热门标签
		if (!hasTags) {
			tagObject = tagService.listHotTagDaily();
			if (tagObject == null) {
				tagObject = tagService.listTagForNum(10);
			}
		}
		
		request.setAttribute("pageNo", pageNo);
		request.setAttribute("pageSize", PAGE_SIZE);
		request.setAttribute("pageTotal", pager.getTotal());
		request.setAttribute("pageCount", pager.getPageCount());
		request.setAttribute("keywords", keywords);
		request.setAttribute("urlKeywords", Functions.escapeUrlPath(keywords));
		request.setAttribute("SearchQuestions", searchQuestions);
		request.setAttribute("searchTags", tagObject);
		return "template/question/search";
	}
	
	private JSONArray getRelateTagList(JSONObject json,String content){
		if (json == null) {
			return null;
		}
		content = content.toLowerCase();
		JSONArray resultArray = new JSONArray();
		JSONArray tagList = json.getJSONArray("data");
		for (Object object : tagList) {
			JSONObject tagObject = (JSONObject) object;
			int indexOf = content.indexOf(tagObject.getString("name").toLowerCase());
			if (indexOf >= 0) {
				resultArray.add(tagObject);
			}
		}
		return resultArray;
	}
	
	@RequestMapping(value = "/seo/publish")
	@ResponseBody
	public String seoPublish(HttpServletRequest request, HttpServletResponse response){
		String ip = IpUtils.getIp(request);
		if(!ip.equals("127.0.0.1") && ip.indexOf("192.168.") == -1){
			return JsonUtils.permissionsErrorJson("permission deny, your ip is " + ip).toJSONString();
		}
		
		long qid = NumberUtils.toLong(request.getParameter("qid"));
		long uid = NumberUtils.toLong(request.getParameter("uid"));
		long createAt = NumberUtils.toLong(request.getParameter("createAt"));
		String type = request.getParameter("type");
		String tags = request.getParameter("tags");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String photos = request.getParameter("photos");
		
		if(createAt <= 0){
			return JsonUtils.errorJson("createAt error, your createAt is " + createAt).toJSONString();
		}
		
		User user = userService.findById(uid);
		if(user == null){
			user = Functions.getRemoteUser(uid);
			if(user != null){
				user.setUserType(Const.USER_TYPE_MJ);
				userService.create(user);
			}
		}
		
		if(user == null){
			return JsonUtils.errorJson("user is null, your uid is " + uid).toJSONString();
		}
		
		List<QuestionPhoto> photoList = null;
		if(StringUtils.isNotBlank(photos)){
			if(photos.charAt(0) == '['){
				try {
					photoList = JSONArray.parseArray(photos, QuestionPhoto.class);
				} catch (Exception e) {
					log.error("批量发布导入问题时，无法解析图片JSON，photos="+photos, e);
					return JsonUtils.errorJson("json parse error, photos is " + photos).toJSONString();
				}
			}else{
				List<Long> photoIds = Functions.stringArr2LongList(photos.split(","));
				photoList = photoImportService.getQuestionPhotoByIds(photoIds);
			}
		}
		
		if("question".equals(type)){
			if(StringUtils.isBlank(title)){
				return JsonUtils.errorJson("question title must not be null").toJSONString();
			}
			Question question = new Question();
			question.setTitle(title);
			question.setContent(content);
			question.setTags(tags);
			question.setCreateAt(new Date(createAt));
			question.setStatus(Const.STATUS_PASS);
			question.setAgent(Const.AGENT_SEO);
			question = questionService.create(user, question, photoList);
			JSONObject json = JsonUtils.successJson("OK");
			json.put("qid", question.getId());
			return json.toJSONString();
		}else if("answer".equals(type)){
			if(StringUtils.isBlank(content)){
				return JsonUtils.errorJson("answer content must not be null").toJSONString();
			}
			Question question = questionService.findById(qid);
			if(question == null){
				return JsonUtils.errorJson("question is null, can not answer, your qid is " + qid).toJSONString();
			}
			Answer answer = new Answer();
			answer.setContent(content);
			answer.setCreateAt(new Date(createAt));
			answer.setStatus(Const.STATUS_PASS);
			answer.setAgent(Const.AGENT_SEO);
			answer = answerService.create(user, question, answer);
			JSONObject json = JsonUtils.successJson("OK");
			json.put("aid", answer.getId());
			return json.toJSONString();
		}else{
			return JsonUtils.errorJson("type error").toJSONString();
		}
	}
}
