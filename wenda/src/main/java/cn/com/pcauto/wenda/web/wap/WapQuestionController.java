package cn.com.pcauto.wenda.web.wap;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.CounterService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.pconline.r.client.RClient;

@Controller
@RequestMapping(value = "/m/question")
public class WapQuestionController {
	
	@Autowired
	private TagService tagService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private CounterService counterService;
	@Autowired
	private RClient rClient;
	@Autowired
	private SystemConfig systemConfig;
	
	public static final int PAGE_SIZE = 20;

	@RequestMapping(value = "/index")
	public String Index(HttpServletRequest req, HttpServletResponse resp){
		int pageNo = WebUtils.paramPageNoMin1(req);
		//热门话题
		List<Tag> tags = tagService.listHotTagDaily();
		if (tags == null) {
			tags = tagService.listTagForNum(10);
		}
		//最新问答
		Pager<Question> pager = questionService.pagerForANum(pageNo, PAGE_SIZE, 5);
		req.setAttribute("tags", tags);      
		req.setAttribute("pager", pager);
		req.setAttribute("pageCount", pager.getPageCount());
		return "/wap_template/question/index";
	}
	@RequestMapping(value = "/detail")
	public String Detail(HttpServletRequest req, HttpServletResponse resp){
		User user = (User) req.getAttribute("user");
		int pageNo = WebUtils.paramPageNoMin1(req);
		long qid = WebUtils.paramLong(req, "qid", 0);
		Question question = questionService.findById(qid);
		if (question == null || question.getStatus() == Const.STATUS_DELETE) {
			WebUtils.send404(resp, "问题不存在或已删除");
			return null;
		}
		if (question.getStatus() == Const.STATUS_PENDING && systemConfig.getCensorType() == 1 && user.getUid() != question.getCreateBy()) {
			WebUtils.send404(resp, "问题正在审核中");
			return null;
		}
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
		req.setAttribute("pageCount", pager.getPageCount());
		req.setAttribute("relateQuestion", relateQuestion);
		return "wap_template/question/detail";
	}
	
	@RequestMapping(value = "/search")
	public String Search(HttpServletRequest request, HttpServletResponse response)throws IOException{
		int pageNo = WebUtils.paramPageNoMin1(request);
		String keywords = WebUtils.param(request, "keywords", "");
    	if(StringUtils.isBlank(keywords)){
    		return "redirect:"+systemConfig.getWapRoot()+"/";
    	}
    	String enKeywords = URLEncoder.encode(keywords,"UTF-8");
    	String url = systemConfig.getKsRoot() + "/wd/" + enKeywords + "/?pageNo=" + pageNo + "&pageSize=" + PAGE_SIZE;
    	Pager<Question> pager = questionService.pagerForKS(url, pageNo, PAGE_SIZE);
    	
    	List<Question> SearchQuestions = pager.getResultList();
    	
		request.setAttribute("pageNo", pageNo);
		request.setAttribute("pageSize", PAGE_SIZE);
		request.setAttribute("pageTotal", pager.getTotal());
		request.setAttribute("pageCount", pager.getPageCount());
		request.setAttribute("keywords", keywords);
		request.setAttribute("SearchQuestions", SearchQuestions);
		return "wap_template/question/search";
	}
}
