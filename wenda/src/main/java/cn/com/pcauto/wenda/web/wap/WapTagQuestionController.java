package cn.com.pcauto.wenda.web.wap;

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
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;

@Controller
@RequestMapping(value = "/m/tagQuestion")
public class WapTagQuestionController {
	
	@Autowired
	private TagService tagService;
	@Autowired
	private QuestionService questionService;
	
	public static final int PAGE_SIZE = 20;
	
	@RequestMapping(value = "/topic")
	public String topic(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		long tid = WebUtils.paramTagId(req);
		//话题标签
		Tag tag = tagService.findById(tid);
		if (tag == null || tag.getStatus() != Tag.STATUS_PASS) {
			WebUtils.send404(resp, "话题标签不存在或被禁用");
			return null;
		}
		
		int pageNo = WebUtils.paramPageNoMin1(req);
		//话题标签下的所有问题
		Pager<Question> pager = questionService.pagerTagQuestion(pageNo, PAGE_SIZE, tid);
		//相关话题标签的规则
		List<Tag> relateTags = tagService.listRelateTag2(tag, 12);
		
		req.setAttribute("tag", tag);
		req.setAttribute("pager", pager);
		req.setAttribute("pageCount", pager.getPageCount());
		req.setAttribute("relateTags", relateTags);
		return "wap_template/question/topic";
	}

}
