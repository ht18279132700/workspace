package cn.com.pcauto.wenda.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.gelivable.webmvc.JSONBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.alibaba.fastjson.JSONArray;

/**
 * web工具类
 * 
 * <p>
 * PS: 该工具类方法只用于需要request,response等web api
 * 
 * @author fxr
 * 
 */
public class WebUtils {
	private WebUtils() {
		throw new UnsupportedOperationException();
	}

	private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
	
	public final static String PARAM_NONCE = "nonce";        //请求参数,随机数
	public final static String PARAM_TIMESTAMP = "timestamp";        //请求参数,时间戳
	public final static String PARAM_SIGNATURE = "signature";        //请求参数,数据签名

	// ----------- 参数部分 ----------
	/**
	 * String型参数
	 * 
	 * @param request
	 * @param name
	 * @param def
	 * @return
	 */
	public static String param(HttpServletRequest request, String name,
			String def) {
		String v = request.getParameter(name);
		return v == null ? def : v;
	}

	/**
	 * String型参数（去两边空格）
	 * 
	 * @param request
	 * @param name
	 * @param def
	 * @return
	 */
	public static String paramTrim(HttpServletRequest request, String name,
			String def) {
		String s = param(request, name, def);
		return s == null ? s : s.trim();
	}

	/**
	 * int型参数
	 * 
	 * @param request
	 * @param name
	 * @param def
	 * @return
	 */
	public static int paramInt(HttpServletRequest request, String name, int def) {
		return NumberUtils.toInt(request.getParameter(name), def);
	}

	public static int paramInt(HttpServletRequest request, String name,
			int def, int min, int max) {
		String v = request.getParameter(name);
		if ((v == null) || (v.length() == 0)) {
			return def;
		}
		try {
			return Math.min(Math.max(paramInt(request, name, def), min), max);
		} catch (Exception ex) {
		}
		return def;
	}

	/**
	 * 
	 * @param request
	 * @param name
	 * @param def
	 * @return
	 */
	public static long paramLong(HttpServletRequest request, String name,
			long def) {
		String v = request.getParameter(name);
		return NumberUtils.toLong(v, def);
	}

	/**
	 * double型
	 * 
	 * @param request
	 * @param name
	 * @param def
	 * @return
	 */
	public static double paramDouble(HttpServletRequest request, String name,
			double def) {
		return NumberUtils.toDouble(request.getParameter(name), def);
	}

	/**
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Date parseDate(HttpServletRequest request, String name) {
		return parseDate(request, name, "");
	}

	/**
	 * 
	 * @param request
	 * @param name
	 * @param time
	 * @return
	 */
	public static Date parseDate(HttpServletRequest request, String name,
			String time) {
		String dateStr = param(request, name, "");
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		try {
			if (StringUtils.isEmpty(time)) {
				return DateUtils.parseDate(dateStr);
			}
			return DateUtils.parseDate(dateStr + " " + time);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * 解析类似这种的字符串 id,id,id,id,id
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Long> paramLongs(HttpServletRequest request, String name) {
		String[] idsStr = param(request, name, "").split(",");
		List<Long> ids = new ArrayList<Long>();
		for (String id : idsStr) {
			try {
				ids.add(Long.parseLong(id));
			} catch (NumberFormatException e) {
				continue;
			}
		}
		return ids;
	}
	
	/**
	 * 解析类似这种的字符串id=x,id=x,id=x,id=x
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Long> paramLongArr(HttpServletRequest request, String name) {
		String[] idsStr = request.getParameterValues(name);;
		List<Long> ids = new ArrayList<Long>();
		for (String id : idsStr) {
			try {
				long idIdx = T.longValue(id, 0);
				if(idIdx >0){
					ids.add(idIdx);
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}
		return ids;
	}

	// ---------- 常用的参数，单独拿出来 ----------
	/**
	 * 
	 * @param request
	 * @return
	 */
	private static int paramPageNo(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_NO,
				SystemConstant.PAGE_NO_1);
	}

	/**
	 * pageNo最小只能为1
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageNoMin1(HttpServletRequest request) {
		int pageNo = paramPageNo(request);
		if (pageNo <= 0) {
			pageNo = SystemConstant.PAGE_NO_1;
		}
		return pageNo;
	}

	/**
	 * PS：默认为5，最小1，最大100
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef5(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_5, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为10，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef10(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_10, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为15，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef15(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_15, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为20，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef20(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_20, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为25，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef25(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_25, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为30，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef30(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_30, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * PS：默认为100，最小1，最大100，不喜请绕道
	 * 
	 * @param request
	 * @return
	 */
	public static int paramPageSizeDef100(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PAGE_SIZE,
				SystemConstant.PAGE_SIZE_100, SystemConstant.PAGE_SIZE_1,
				SystemConstant.PAGE_SIZE_100);
	}

	/**
	 * xssencode
	 * 
	 * @param request
	 * @return
	 */
	public static int paramXssEncode(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PARAM_XSSENCODE,
				SystemConstant.PARAM_XSSENCODE_DEF);
	}

	/**
	 * xssencode
	 * 
	 * @param request
	 * @return
	 */
	public static int paramXssEncodeDef1(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PARAM_XSSENCODE,
				SystemConstant.PARAM_XSSENCODE_1);
	}

	/**
	 * xssencode
	 * 
	 * @param request
	 * @return
	 */
	public static int paramXssEncodeDef2(HttpServletRequest request) {
		return paramInt(request, SystemConstant.PARAM_XSSENCODE,
				SystemConstant.PARAM_XSSENCODE_2);
	}

	/**
	 * callback
	 * 
	 * @param request
	 * @return
	 */
	public static String paramCallback(HttpServletRequest request) {
		return param(request, SystemConstant.PARAM_CALLBACK, "");
	}

	/**
	 * 话题标签id参数
	 * 
	 * @param request
	 * @return
	 */
	public static long paramTagId(HttpServletRequest request) {
		String v = "";
		Object obj = request.getAttribute("tid");
		if (obj != null) {
			v = obj.toString();
		}
		if (StringUtils.isBlank(v)) {
			v = request.getParameter("tid");
		}
		return NumberUtils.toLong(v, 0L);
	}

	/**
	 * windowName
	 * 
	 * @param request
	 * @return
	 */
	public static int paramWindowName(HttpServletRequest request) {
		return paramInt(request, SystemConstant.WINDOW_NAME, 0);
	}

	public static String cookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(cookieName)) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	public static void noCache(HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0L);
	}

	public static void setCacheHeader(HttpServletResponse response,
			long expiresSecond) {
		setCacheHeader(response, System.currentTimeMillis(), expiresSecond);
	}

	public static void setCacheHeader(HttpServletResponse response,
			long lastModifiedMillis, long expiresSecond) {
		response.setHeader("Cache-Control", "max-age=" + expiresSecond);
		response.setDateHeader("Last-Modified", lastModifiedMillis);
		response.setDateHeader("Expires", lastModifiedMillis
				+ (expiresSecond * 1000));
	}

	/**
	 * 回应if-modifed-since请求，如果内容未发生更改，直接返回304给前端，
	 * 不再生成后面的内容，注意此方法仅在ATS打开相关cache设置才起作用。
	 * 
	 * @param request
	 *            请求
	 * @param lastModified
	 *            当前页面业务内容的最后更新时间，譬如车友会的最后更新时间等。
	 * @return true 已更改，页面应出完整内容，false 未发生更改，页面应直接返回304
	 */
	public static boolean isCacheStaled(HttpServletRequest request,
			long lastModified) {
		long revalidate = request.getDateHeader("If-Modified-Since");
		return (revalidate == 0 || revalidate < lastModified);
	}

	/**
	 * 拼接用户头像图片
	 * 
	 * 请使用AppUtils中方法
	 * 
	 * @param userId
	 *            用户ID
	 * @param width
	 *            头像大小
	 * @return 图片链接
	 */
	@Deprecated
	public static String face(long userId, int width, long no) {
		String id = String.valueOf(userId);
		StringBuilder sb = new StringBuilder("http://i").append(no).append(
				".3conline.com/images/upload/upc/face/");
		for (int i = 0, c = id.length(); i < c; i += 2) {
			sb.append(id.charAt(i));
			if (i < c - 1) {
				sb.append(id.charAt(i + 1));
			}
			sb.append('/');
		}
		return sb.append(id).append("_").append(width).append("x")
				.append(width).toString();
	}

	/**
	 * 拼接用户头像图片(默认为i1)
	 * 
	 * 请使用AppUtils中方法
	 * 
	 * @param userId
	 * @param width
	 * @return
	 */
	@Deprecated
	public static String face(long userId, int width) {
		return face(userId, width, 6L);
	}

	/**
	 * get Spring Bean
	 * 
	 * @param <T>
	 * @param request
	 * @param type
	 * @return
	 */
	public static T getBean(HttpServletRequest request, Class<T> type) {
		return RequestContextUtils.getWebApplicationContext(request).getBean(
				type);
	}

	/**
	 * 判断请求是否是Get
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isGet(HttpServletRequest request) {
		return "GET".equalsIgnoreCase(request.getMethod());
	}

	/**
	 * 判断请求是否是Post
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isPost(HttpServletRequest request) {
		return "POST".equalsIgnoreCase(request.getMethod());
	}

	/**
	 * 接口 设置编码
	 * 
	 * @param req
	 * @param resp
	 */
	public static void setEncoding(HttpServletRequest req,
			HttpServletResponse resp) {
		String queryString = req.getQueryString();
		String req_enc = getParameterBeforeSettingCharacterEncoding(
				queryString, "req_enc");
		String resp_enc = getParameterBeforeSettingCharacterEncoding(
				queryString, "resp_enc");
		if (StringUtils.isNotBlank(req_enc)) {
			try {
				req.setCharacterEncoding(req_enc);
			} catch (UnsupportedEncodingException ex) {
				log.error("setEncoding fail!", ex);
			}
		}
		if (StringUtils.isNotBlank(resp_enc)) {
			resp.setCharacterEncoding(resp_enc);
			if (resp_enc.toLowerCase().equals("gbk")) {
				resp.setContentType("text/html; charset=GBK");
			} else if (resp_enc.toLowerCase().equals("utf-8")) {
				resp.setContentType("text/html; charset=UTF-8");
			}
		} else {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html; charset=UTF-8");
		}
	}

	/**
	 * 接口 设置编码,APP接口专用 设置contentType = application/json
	 * 
	 * @param req
	 * @param resp
	 */
	public static void setEncoding4App(HttpServletRequest req,
			HttpServletResponse resp) {
		String queryString = req.getQueryString();
		String req_enc = getParameterBeforeSettingCharacterEncoding(
				queryString, "req_enc");
		String resp_enc = getParameterBeforeSettingCharacterEncoding(
				queryString, "resp_enc");
		if (StringUtils.isNotBlank(req_enc)) {
			try {
				req.setCharacterEncoding(req_enc);
			} catch (UnsupportedEncodingException ex) {
				log.error("setEncoding fail!", ex);
			}
		}
		if (StringUtils.isNotBlank(resp_enc)) {
			resp.setCharacterEncoding(resp_enc);
			if (resp_enc.toLowerCase().equals("gbk")) {
				resp.setContentType("application/json; charset=GBK");
			} else if (resp_enc.toLowerCase().equals("utf-8")) {
				resp.setContentType("application/json; charset=UTF-8");
			}
		} else {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=UTF-8");
		}
	}

	private static String getParameterBeforeSettingCharacterEncoding(
			String queryString, String key) {
		if (queryString == null || queryString.length() == 0) {
			return null;
		}
		int reqIdx = queryString.indexOf(key);// req_enc=utf-8&resp_enc=gbk
		if (reqIdx != -1) {
			reqIdx = reqIdx + key.length() + 1;
			int endIdx = reqIdx;
			for (; endIdx < queryString.length()
					&& queryString.charAt(endIdx) != '&'; endIdx++) {
			}
			if (endIdx > reqIdx) {
				return queryString.substring(reqIdx, endIdx);
			}
		}
		return null;
	}

	/**
	 * md5加密，获取短信验证码防刷用到
	 * 
	 * @param text
	 * @return
	 */
	public static String md5(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(text.getBytes("utf-8"));
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < digest.length; i++) {
				String b = Integer.toHexString(0xFF & digest[i]);
				if (b.length() == 1) {
					buf.append('0');
				}
				buf.append(b);
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		return text;
	}

	/**
	 * 处理省市市前面的符号
	 * 
	 * @param request
	 * @param name
	 * @param def
	 */
	public static String getAreaPlaceByReq(HttpServletRequest request,
			String name, String def) {
		String param = param(request, name, def);
		return param.replaceAll("├", "").replaceAll("└", "");
	}

	/**
	 * 校验最大页码是否超出真实页码
	 * 
	 * @param resp
	 * @param pageNo
	 * @param pageCount
	 */
	public static void checkPageMaxNo(HttpServletResponse resp, int pageNo,
			int pageCount) {
		if (pageNo > pageCount) {
			//throw new AutohmException();
		}
	}

	/**
	 * 把换行符替换成成空格
	 * 
	 * @param context
	 * @return
	 */
	public static String replaceNewLine(String context) {
		return context.replaceAll("\\n", " ").replaceAll("\\r", "")
				.replaceAll("\\\\s", " ");
	}
	
	/**  
     *   
     * 基本功能：过滤所有以"<"开头以">"结尾的标签  
     * <p>  
     *   
     * @param str  
     * @return String  
     */  
    public static String filterHtml(String str) {   
        Pattern pattern = Pattern.compile("<([^>]*)>");   
        Matcher matcher = pattern.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result1 = matcher.find();   
        while (result1) {   
            matcher.appendReplacement(sb, "");   
            result1 = matcher.find();   
        }   
        matcher.appendTail(sb);   
        return sb.toString();   
    } 
    /**
     * 去掉空格、回车、换行符、制表符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

	/**
	 * 过滤[图]字
	 * 
	 * @param context
	 * @return
	 */
	public static String filterPicture(String context) {
		return context.replaceAll("\\[图\\]", "");
	}

	public static JSONArray parseArray(HttpServletRequest request, String name) {
		String js = param(request, name, "");
		try {
			return JSONArray.parseArray(js);
		} catch (com.alibaba.fastjson.JSONException e) {
			return null;
		}
	}

	/**
	 * URL转换为链接
	 * 
	 * @param urlText
	 * @return String
	 */
	public static String urlToPCLink(String urlText) {
		String regexp = "(((http|ftp|https|file)://)|((?<!((http|ftp|https|file)://))www\\.))"
				+ ".*?" + "(?=(&nbsp;|\\s|　|<br />|$|[<>]))";
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(urlText);
		StringBuffer stringbuffer = new StringBuffer();
		while (matcher.find()) {
			String url = matcher.group().substring(0, 3).equals("www") ? "http://"
					+ matcher.group()
					: matcher.group();
			String tempString = "<a href=\"" + url + "\" target='_blank'>"
					+ url + "</a>";
			int tempLength = tempString.length();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < tempLength; ++i) {
				char c = tempString.charAt(i);
				if (c == '\\' || c == '$') {
					buffer.append("\\").append(c);
				} else {
					buffer.append(c);
				}
			}
			tempString = buffer.toString();
			matcher.appendReplacement(stringbuffer, tempString);
		}
		matcher.appendTail(stringbuffer);
		return stringbuffer.toString();
	}

	/**
	 * URL转换为WAP链接 wap端链接一般不用打开新窗口的
	 * 
	 * @param urlText
	 * @return String
	 */
	public static String urlToWAPLink(String urlText) {
		String regexp = "(((http|ftp|https|file)://)|((?<!((http|ftp|https|file)://))www\\.))"
				+ ".*?" + "(?=(&nbsp;|\\s|　|<br />|$|[<>]))";
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(urlText);
		StringBuffer stringbuffer = new StringBuffer();
		while (matcher.find()) {
			String url = matcher.group().substring(0, 3).equals("www") ? "http://"
					+ matcher.group()
					: matcher.group();
			String tempString = "<a href=\"" + url + "\">" + url + "</a>";
			int tempLength = tempString.length();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < tempLength; ++i) {
				char c = tempString.charAt(i);
				if (c == '\\' || c == '$') {
					buffer.append("\\").append(c);
				} else {
					buffer.append(c);
				}
			}
			tempString = buffer.toString();
			matcher.appendReplacement(stringbuffer, tempString);
		}
		matcher.appendTail(stringbuffer);
		return stringbuffer.toString();
	}

	/**
	 * 返回错误
	 * 
	 * @param resp
	 * @param code
	 * @param preLog
	 */
	private static void sendError(HttpServletResponse resp, int code,
			String... preLog) {
		try {
			resp.sendError(code);
		} catch (Exception ex) {
			String errorPreLog = "error";
			if (preLog != null) {
				errorPreLog = preLog[0];
			}
			log.error(errorPreLog, ex);
		}
	}

	/**
	 * 返回404
	 * 
	 * @param resp
	 * @param preLog
	 */
	public static void send404(HttpServletResponse resp, String... preLog) {
		sendError(resp, HttpServletResponse.SC_NOT_FOUND, preLog);
	}

	/**
	 * 返回403
	 * 
	 * @param resp
	 * @param preLog
	 */
	public static void send403(HttpServletResponse resp, String... preLog) {
		sendError(resp, HttpServletResponse.SC_FORBIDDEN, preLog);
	}

	/**
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
				request.getParameter("resp_enc"), "UTF-8");
		if (!Charset.isSupported(charsetName)) {
			charsetName = "UTF-8";
		}

		// contentType
		String contentType = StringUtils.defaultIfBlank(
				request.getParameter("content_type"), "json");
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
		return StringUtils.isBlank(callback) ? result : (callback.concat("(")
				.concat(result).concat(")"));
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

	public static void printMsg(HttpServletRequest request,
			HttpServletResponse response, JSONBuilder json, int status) {
		if (json == null) {
			//throw new AutohmException(
			//		"json must no be null when call successMsg()");
		}

		json.put("status", status);
		cbMsg(request, response, json.toString());
	}

	/**
	 * 成功输出
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void successMsg(HttpServletRequest request,
			HttpServletResponse response, JSONBuilder json) {
		printMsg(request, response, json, SystemConstant.SUCCESS);
	}

	/**
	 * 错误输出
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void errorMsg(HttpServletRequest request,
			HttpServletResponse response, JSONBuilder json) {
		printMsg(request, response, json, SystemConstant.ERROR);
	}

	/**
	 * 失败输出
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void failMsg(HttpServletRequest request,
			HttpServletResponse response, JSONBuilder json) {
		printMsg(request, response, json, SystemConstant.FAILED);
	}

	/**
	 * 信息提醒（不关闭当前页面）
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public static void noticeMsg(HttpServletRequest req,
			HttpServletResponse resp, String message, String navTabId,
			int status, int statusCode) {
		JSONBuilder json = new JSONBuilder();
		json.put("message", message);
		json.put("navTabId", navTabId);
		json.put("statusCode", statusCode);
		printMsg(req, resp, json, status);
	}

	/**
	 * 设置跨域头文件
	 * 
	 * @param request
	 * @param response
	 */
	public static void setOriginHeader(HttpServletRequest request,
			HttpServletResponse response) {
		String originHeader = request.getHeader("Origin");
		if (originHeader != null) {
			// 过滤
			// originHeader = EncodeUtils.encodeForHTMLFilterNull(originHeader);
			originHeader = EncodeUtils.filterMHTML(originHeader);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Origin", originHeader);
		}
	}
	
	/** 判断是否外链
	 * @param url
	 * @return
	 */
	public static boolean isExternalUrl(String url) {
        String[] pcWebs = {"pconline.com.cn", "pcauto.com.cn", "pclady.com.cn",
            "pcbaby.com.cn", "pcgames.com.cn", "pchouse.com.cn", "3conline.com"};
        boolean result = true;
        for (String web : pcWebs) {
            if (url.contains(web)) {
                result = false;
                break;
            }
        }
        return result;
    }
	
    public static String removeHttp(String url){
    	if(StringUtils.isBlank(url)){
    		return "";
    	}
    	String regex = ".+?\\.pc(auto|online|baby|lady|house)\\.com\\.cn(/.*)?";
    	if(url.matches(regex)){
    		return url.replace("https:", "").replace("http:", "");
    	}else{
    		return url;
    	}
    }
    
    public static String addHttp(String url){
    	if(StringUtils.isBlank(url)){
    		return "";
    	}
    	if(url.startsWith("//")){
    		url = "http:" + url;
    	}
    	return url;
    }
}
