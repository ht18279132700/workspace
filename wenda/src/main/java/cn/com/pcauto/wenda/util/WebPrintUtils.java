package cn.com.pcauto.wenda.util;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 此类是为了统一规范化目前分散的、各种输出字段规则等不一致、混乱的问题，<br>
 * 优化之后，将移除如下类或其中的部分代码<br><br>
 * 
 * WebUtils.java 需要request,response等web api的工具方法<br>
 * AppUtils.java 不需要request,response等web api的工具方法<br>
 * ActionKit.java 删除<br>
 * WebKit.java 删除<br>
 * AppControllerStatic.java 删除<br>
 * 
 * @author fxr
 *
 */
public class WebPrintUtils {
	
	private final static Logger LOG = LoggerFactory.getLogger(WebPrintUtils.class);
	
	// ---------- 输出返回信息-----------
	/**
	 * 数据信息输出<br>
	 * 
	 * 根据参数自动添加contentType
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void printMsg(HttpServletRequest request, 
			HttpServletResponse response, String message) {
		// 响应编码
		String charsetName = StringUtils.defaultIfBlank(
				request.getParameter("resp_enc"), SystemConstant.CHARSET_UTF8);
		if (!Charset.isSupported(charsetName)) {
			charsetName = SystemConstant.CHARSET_UTF8;
		}
		
		// contentType
		String contentType = StringUtils.defaultIfBlank(
				request.getParameter("content_type"), SystemConstant.CONTENTTYPE_JSON);
		contentType = EncodeUtils.encodeForHTMLFilterNull(contentType);
		
		// 目前只支持两种
		if (SystemConstant.CONTENTTYPE_JSON.equals(contentType)) {
			contentType = "application/json; charset=" + charsetName;
		} else {
			contentType = "text/html; charset=" + charsetName;
		}
		
		try {
			response.setCharacterEncoding(charsetName);
			response.setContentType(contentType);
			response.getWriter().println(message);
		} catch (IOException e) {
			LOG.error("printMsg catch IOException.");
			//throw new AutohmException("printMsg catch IOException.");
		}
	}
	
	
	
	
	/**
	 * 数据信息输出<br>
	 * 
	 * 根据参数自动添加contentType
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void printMsg(HttpServletRequest request, 
			HttpServletResponse response, String contentType,String message) {
		// 响应编码
		String charsetName = StringUtils.defaultIfBlank(
				request.getParameter("resp_enc"), SystemConstant.CHARSET_UTF8);
		if (!Charset.isSupported(charsetName)) {
			charsetName = SystemConstant.CHARSET_UTF8;
		}
		if(StringUtils.isBlank(contentType)){
			contentType = StringUtils.defaultIfBlank(
					request.getParameter("content_type"), SystemConstant.CONTENTTYPE_JSON);
		}

		try {
			response.setCharacterEncoding(charsetName);
			response.setContentType(contentType);
			response.getWriter().println(message);
		} catch (IOException e) {
			LOG.error("printMsg catch IOException.");
			//throw new AutohmException("printMsg catch IOException.");
		}
	}
	
	/**
	 * 添加callback参数
	 * 
	 * @param result
	 * @param callback
	 * @return
	 */
	public static String cbMsg(String result, String callback) {
		return StringUtils.isBlank(callback) ? result : 
    		(callback.concat("(").concat(result).concat(")"));
	}
	
	/**
	 * 添加回调参数
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void cbMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		String callback = request.getParameter("callback");
		// 过滤XSS
		callback = EncodeUtils.encodeForHTMLFilterNull(callback);
		// 过滤MHTML
		callback = EncodeUtils.filterMHTML(callback);
		printMsg(request, response, cbMsg(message, callback));
	}
	
	
	
	/**
	 * 添加回调参数
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void cbMsg(HttpServletRequest request, 
    		HttpServletResponse response,String contentType ,String message) {
		String callback = request.getParameter("callback");
		// 过滤XSS
		callback = EncodeUtils.encodeForHTMLFilterNull(callback);
		// 过滤MHTML
		callback = EncodeUtils.filterMHTML(callback);
		printMsg(request, response,contentType, cbMsg(message, callback));
	}
	public static void printMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, int status) {
		json = JsonUtils.getReturnJson(json, status);
    	cbMsg(request, response, json.toString());
    }
	
	/**
	 * 数据错误返回
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void dataErrorMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		cbMsg(request, response, JsonUtils.dataErrorJson(message).toJSONString());
	}
	
	/**
	 * 未授权错误
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void noPermissionMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		cbMsg(request, response, JsonUtils.permissionsErrorJson(message).toJSONString());
	}
	
	/**
	 * 成功返回
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void successMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		cbMsg(request, response, JsonUtils.getJsonStr(SystemConstant.SUCCESS, message));
	}
	
	/**
	 * 成功返回
	 * @param request
	 * @param response
	 */
	public static void successMsg(HttpServletRequest request, HttpServletResponse response) {
		cbMsg(request, response, JsonUtils.getJsonStr(SystemConstant.SUCCESS, "操作成功"));
	}
	
	/**
	 * 成功返回
	 * 
	 * @param request
	 * @param response
	 * @param json
	 */
	public static void successMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, String... message) {
		json = JsonUtils.getReturnJson(json, SystemConstant.SUCCESS);
		if (message != null && message.length >= 1) {
			json.put("message", message[0]);
		} else {
			json.put("message", "操作成功");
		}
		cbMsg(request, response, json.toJSONString());
	}
	
	public static void successMsgForContentType(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, String contentType) {
		json = JsonUtils.getReturnJson(json, SystemConstant.SUCCESS);
		cbMsg(request, response,contentType, json.toJSONString());
	}
	
	public static void errorMsgForContentType(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, String contentType) {
		json = JsonUtils.getReturnJson(json, SystemConstant.ERROR);
		cbMsg(request, response,contentType, json.toJSONString());
	}
	
	public static void successDataMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, String... message) {
		JSONObject jsonObject = JsonUtils.getReturnJson(null, SystemConstant.SUCCESS);
		if (message != null && message.length >= 1) {
			jsonObject.put("message", message[0]);
		} else {
			jsonObject.put("message", "操作成功");
		}
		if (json != null) {
			jsonObject.put("data", json);
		}
		cbMsg(request, response, jsonObject.toJSONString());
	}
	
	/**
	 * 成功返回（添加分页字段）
	 * @param request
	 * @param response
	 * @param json
	 * @param pager
	 */
	public static void successPagerMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, Pager<?> pager) {
		json = JsonUtils.getReturnJson(json, SystemConstant.SUCCESS);
		
		if (pager != null && pager.getResultList() != null && !pager.getResultList().isEmpty()) {
			json.put(SystemConstant.PAGE_NO, pager.getPageNo());
			json.put(SystemConstant.PAGE_SIZE, pager.getPageSize());
			json.put(SystemConstant.PAGER_TOTAL, pager.getTotal());
			json.put(SystemConstant.PAGER_PAGECOUNT, pager.getPageCount());
		} else {
			json.put(SystemConstant.PAGE_NO, 1);
			json.put(SystemConstant.PAGE_SIZE, SystemConstant.PAGE_SIZE_20);
			json.put(SystemConstant.PAGER_TOTAL, 0);
			json.put(SystemConstant.PAGER_PAGECOUNT, 0);
		}
		
		cbMsg(request, response, json.toJSONString());
	}
	
	/**
	 * 错误返回
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void errorMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		cbMsg(request, response, JsonUtils.getJsonStr(SystemConstant.ERROR, message));
	}
	
	/**
	 * 错误返回
	 * 
	 * @param request
	 * @param response
	 * @param json
	 */
	public static void errorMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json) {
		json = JsonUtils.getReturnJson(json, SystemConstant.ERROR);
		cbMsg(request, response, json.toJSONString());
	}
	
	/**
	 * 失败返回
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void failMsg(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		cbMsg(request, response, JsonUtils.getJsonStr(SystemConstant.FAILED, message));
	}
	
	/**
	 * 失败返回
	 * @param request
	 * @param response
	 * @param json
	 */
	public static void failMsg(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json) {
		json = JsonUtils.getReturnJson(json, SystemConstant.FAILED);
		cbMsg(request, response, json.toJSONString());
	}
	
	/**
	 * 成功返回（windowname方式）
	 * @param request
	 * @param response
	 * @param json
	 * @param message
	 */
	public static void successWithWindowName(HttpServletRequest request, 
    		HttpServletResponse response, JSONObject json, String... message) {
		json = JsonUtils.getReturnJson(json, SystemConstant.SUCCESS);
		if (message != null && message.length >= 1) {
			json.put("message", message[0]);
		} else {
			json.put("message", "操作成功");
		}
		
		cbMsg(request, response, appendWindowname(request, json.toJSONString()));
	}
	
	/**
	 * 错误返回（windowname方式）
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void errorWithWindowname(HttpServletRequest request, 
    		HttpServletResponse response, String message) {
		String msg = JsonUtils.getJsonStr(SystemConstant.ERROR, message);
		
		msg = appendWindowname(request, msg);
		
		cbMsg(request, response, msg);
	}
	
	/**
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	private static String appendWindowname(HttpServletRequest request, String message) {
		int windowname = WebUtils.paramWindowName(request);
		String data = windowname == SystemConstant.IS_WINDOW_NAME_CROSS ? 
				("<script>window.name= '" + message + "';</script>")
				: message;
		return data;
	}
	
	/**
	 * 用户未登录
	 * 
	 * @param request
	 * @param response
	 */
	public static void unLoginMsg(HttpServletRequest request, 
    		HttpServletResponse response) {
		cbMsg(request, response, 
				JsonUtils.getJsonStr(SystemConstant.STATUS_NOLOGIN_ERROR, 
						"用户未登录"));
	}
}
