package cn.com.pcauto.wenda.web.admin;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gelivable.auth.entity.GeliSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.AnswerImport;
import cn.com.pcauto.wenda.service.AnswerImportService;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;

@Controller
@RequestMapping(value = "/admin/answerImport")
public class AdminAnswerImportController {
	
	@Autowired
	private AnswerImportService answerImportService;

	@RequestMapping(value = "/update")
	public void Update(HttpServletRequest request, HttpServletResponse response){
		long id = WebUtils.paramLong(request, "id", 0);
		AnswerImport answerImport = answerImportService.findById(id);
		if (answerImport == null) {
			WebPrintUtils.errorMsg(request, response, "导入答案ID错误");
			return;
		}
		String content = WebUtils.param(request, "content", "");
		if (StringUtils.isBlank(content)) {
			WebPrintUtils.errorMsg(request, response, "内容为空");
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		answerImport.setContent(content);
		answerImport.setUpdateAt(new Date());
		answerImport.setUpdateBy(userId);
		
		int flag = answerImportService.update(answerImport);
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response,"导入答案更新成功");
		}else {
			WebPrintUtils.errorMsg(request, response, "导入答案更新失败");
		}
	}
	
	@RequestMapping(value = "/delete")
	public void Delete(HttpServletRequest request, HttpServletResponse response){
		long id = WebUtils.paramLong(request, "id", 0);
		AnswerImport answerImport = answerImportService.findById(id);
		if (answerImport == null) {
			WebPrintUtils.errorMsg(request, response, "导入答案ID错误");
			return;
		}
		int flag = answerImportService.delete(id);
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response, "删除导入问题成功");
		} else {
			WebPrintUtils.errorMsg(request, response, "删除导入问题失败");
		}
	}
}
