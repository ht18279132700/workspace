package cn.com.pcauto.wenda.web.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.pcauto.wenda.entity.WdDailyStat;
import cn.com.pcauto.wenda.service.WdDailyStatService;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.com.pcauto.wenda.util.excel.ExcelHelper;

@Controller
@RequestMapping("/admin/wdDailyStat")
public class AdminWdDailyStatController {

	@Autowired
	private WdDailyStatService wdDailyStatService;
	
	@RequestMapping("/list")
	public String queryWdDailyStat(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramPageNoMin1(request);
		int pageSize = WebUtils.paramPageSizeDef100(request);
		String beginDayStr = WebUtils.param(request, "beginDay", "");
		String endDayStr = WebUtils.param(request, "endDay", "");
		int beginDay = dayStrToInt(beginDayStr);
		int endDay = dayStrToInt(endDayStr);
		Pager<WdDailyStat> pager = wdDailyStatService.pagerByDay(beginDay, endDay, pageNo, pageSize);
		request.setAttribute("pager", pager);
		request.setAttribute("beginDay", beginDayStr);
		request.setAttribute("endDay", endDayStr);
		return "admin/wdDailyStat/list";
	}
	
	@RequestMapping("/export")
	@ResponseBody
	public void exportWdDailyStat(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String beginDayStr = WebUtils.param(request, "beginDay", "");
		String endDayStr = WebUtils.param(request, "endDay", "");
		int beginDay = dayStrToInt(beginDayStr);
		int endDay = dayStrToInt(endDayStr);
		List<WdDailyStat> list = wdDailyStatService.listByDay(beginDay, endDay);
		
		//构造导出数据
		List<Object[]> data = new ArrayList<Object[]>();
		//添加列头
		data.add(new Object[]{"时间","SEO提问数","SEO回答数","网友提问数","网友回答数","网友回复数"});
		//添加数据
		for(WdDailyStat d : list){
			data.add(new Object[]{
					Functions.joinDay(d.getDay(), "/"),
					d.getSeoQuestionNum(),
					d.getSeoAnswerNum(),
					d.getUserQuestionNum(),
					d.getUserAnswerNum(),
					d.getUserReplyNum()
			});
		}
		
		//使用Excel助手导出数据
		ExcelHelper helper = new ExcelHelper("问答数日计");
		helper.setColumnWidth(16);
		helper.addSheetData("Sheet1", data);
		helper.exportToExcel(response);
	}
	
	private int dayStrToInt(String dayStr) {
		try {
			return Integer.parseInt(dayStr.replace("-", "").substring(0, 8));
		} catch (Exception e) {
			return 0;
		}
	}
	
}
