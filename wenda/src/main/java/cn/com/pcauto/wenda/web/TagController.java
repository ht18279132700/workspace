package cn.com.pcauto.wenda.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;

@Controller
@RequestMapping(value = "/tag")
public class TagController {
	@Autowired
	private TagService tagService;
	
	/**
	 * 热门标签索引页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/index")
	public String Index(HttpServletRequest request, HttpServletResponse response){
		int pageSize = WebUtils.paramInt(request, "pageSize", 1200);
		Date start = DateUtils.getBefore7Day();
		Date end = DateUtils.getBefore0Day();
		List<Tag> listHotTag = tagService.listHotTag(start,end,pageSize);
		request.setAttribute("letter", "hot");
		request.setAttribute("list", listHotTag);
		return "template/tag/sort";
	}
	/**
	 * 标签分类页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/sort")
	public String Sort(HttpServletRequest request, HttpServletResponse response){
		String letter = WebUtils.param(request, "letter", "");
    	if(StringUtils.isBlank(letter)){
    		WebUtils.send404(response,"letter is null");
			return null;
    	}
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 1200);
		Pager<Tag> pager = tagService.sortTag(letter, pageNo, pageSize);
		request.setAttribute("letter", letter);
		request.setAttribute("pager", pager);
		return "template/tag/sort";
	}
}
