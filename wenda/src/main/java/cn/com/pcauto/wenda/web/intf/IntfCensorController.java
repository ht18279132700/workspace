package cn.com.pcauto.wenda.web.intf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.service.censor.CensorAnswerService;
import cn.com.pcauto.wenda.service.censor.CensorQuestionService;
import cn.com.pcauto.wenda.service.censor.CensorReplyService;
import cn.com.pcauto.wenda.service.censor.CensorService;
import cn.com.pcauto.wenda.util.Const;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/intf/censor")
public class IntfCensorController {
	
	@Autowired
	private CensorService censorService;
	@Autowired
	private CensorQuestionService censorQuestionService;
	@Autowired
	private CensorAnswerService censorAnswerService;
	@Autowired
	private CensorReplyService censorReplyService;
	
	private static final Logger LOG = LoggerFactory.getLogger(IntfCensorController.class);
	
	/**
	 * 审核回调接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/callback")
	public void Callback(HttpServletRequest request, HttpServletResponse response){
		// 此接口接收的是信息队列发送过来审核平台的信息
		String json = request.getParameter("msg");
		if (StringUtils.isBlank(json)) {
			LOG.info("msg is null or is empty");
			return;
		}
		JSONObject params = JSONObject.parseObject(json);
		params = JSONObject.parseObject(params.getString("msg"));
		long version = params.getLong("version");
		int status = params.getIntValue("status");
		String url = params.getString("url");
		long typeId = censorService.getTypeId(url);
		if (Const.CENSOR_TYPEID_QUESTION == typeId) {
			censorQuestionService.callback(url, status, version);
		}
		if (Const.CENSOR_TYPEID_ANSWER == typeId) {
			censorAnswerService.callback(url, status, version);
		}
		if (Const.CENSOR_TYPEID_REPLY == typeId) {
			censorReplyService.callback(url, status, version);
		}
	}
}
