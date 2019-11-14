package cn.com.pcauto.wenda.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.TagQuestionService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;

@Controller
@RequestMapping(value = "/tagQuestion")
public class TagQuestionController {

	@Autowired
	private TagService tagService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private TagQuestionService taQuestionService;
	
	public static final int PAGE_SIZE = 20;
	
	@RequestMapping(value = "/topic")
	public String index(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		long tid = WebUtils.paramTagId(req);
		if (tid < 1) {
			WebUtils.send404(resp, "param tid is error");
			return null;
		}
		Tag tag = tagService.getTag(tid, Tag.STATUS_PASS);
		if (tag == null || Tag.STATUS_PASS != tag.getStatus()) {
			WebUtils.send404(resp, "Tag object is not find");
			return null;
		}
		int pageNo = WebUtils.paramPageNoMin1(req);
		//话题标签
		//话题标签下的所有问题
		Pager<Question> pager = questionService.pagerTagQuestion(pageNo, PAGE_SIZE, tid);
		//相关话题标签的规则
		List<Tag> relateTags = tagService.listRelateTag2(tag, 12);
		
		req.setAttribute("tag", tag);
		req.setAttribute("tid", tid);
		req.setAttribute("pager", pager);
		req.setAttribute("relateTags", relateTags);
		return "template/question/topic";
	}
}
