package cn.com.pcauto.wenda.web.monitor;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.pcauto.wenda.util.mail.MailUtil;
import cn.com.pcauto.wenda.util.monitor.Monitor;
import cn.pconline.r.client.ResponseExtractor;
import cn.pconline.r.client.SimpleHttpTemplate;

/**
 * 负载监控Controller
 * @author 0
 *
 */
@Controller
public class LoadMonitorController {

	@Autowired
	private SimpleHttpTemplate sht;
	
	@RequestMapping("/load/monitor/timer")
	@ResponseBody
	public String loadMonitor(HttpServletRequest request, HttpServletResponse response) throws MessagingException{
		// 收件人列表
		String[] to = {"turui@pconline.com.cn", "xieshaoyuan@pconline.com.cn", "chenchang@pconline.com.cn"};
		String subject = "【WARN】车问答-应用服务器负载监控异常";  // 邮件主题
		
		float referenceVal = 8;  // 系统负载标准值
		String interfaceName = "/util/monitor/jvm.jsp";  //调用接口名称
		
		final StringBuffer errorTemplate = new StringBuffer();
		errorTemplate.append("[ERROR] invoke ").append(interfaceName).append(", ");
		
		for(String ip : Monitor.serverArray){
			String result = null;
			try {
				result = sht.get("http://" + ip + interfaceName, null, null, new ResponseExtractor<String>() {
					@Override
					public String extractData(HttpResponse response){
						StringBuffer sb = new StringBuffer();
						int sc = response.getStatusLine().getStatusCode();
						if(sc != 200){
							sb.append(errorTemplate).append("the http status code is ")
							.append(sc).append("<br>");
						}
						try {
							String string = EntityUtils.toString(response.getEntity(), "UTF-8");
							sb.append(string);
						} catch (Exception e) {
							sb.append(errorTemplate).append("parse http entity error, ").append(e.getMessage());
							e.printStackTrace();
						}
						return sb.toString();
					}
				});
				
			} catch (Exception e) {
				result = errorTemplate + e.getMessage();
			}
			
			if(result.contains(errorTemplate)){
				// 调用接口有错误，发送邮件
				MailUtil.sendEmail(subject, ip + " " + result, to);
			}else{
				String[] split = result.split(",");
				float load = Float.parseFloat(split[2].trim().replace("'", ""));
				if(load > referenceVal){
					// 负载超过标准，发邮件
					MailUtil.sendEmail(subject, ip + " system load average is " + load, to);
				}
			}
		}
		return "OK";
	}
	
}
