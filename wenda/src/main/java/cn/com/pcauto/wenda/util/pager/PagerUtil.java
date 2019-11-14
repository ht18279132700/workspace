package cn.com.pcauto.wenda.util.pager;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;



/**
 * 分页工具类
 */
public class PagerUtil {

    /**
     * 获取分页的url地址
     * @param request	请求变量
     * @return	jsp请求连接
     */
    public static String getPagerUrl(HttpServletRequest request) {
        String url =  request.getContextPath() + request.getServletPath() + "?";

        //需要把参数加进去，以作为条件进行搜索（不支持参数含有中文）
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            if (!StringUtils.isEmpty(request.getParameter(paraName))) {
                url += "&" + paraName + "=" + filterInput(request.getParameter(paraName));
			}
        }
        url = filterParamScripts(url);
        return url.replaceAll("&*(pageNo)=[^&]*", "").replace("?&", "?");
    }
    public static String filterParamScripts(String param) {
        if (param == null) {
            return null;
        }
        return param.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
	public static String filterInput(String html) {
		if (html == null) {
			return html;
		}
		StringBuilder sb = new StringBuilder(html.length());
		for (int i = 0, c = html.length(); i < c; ++i) {
			char ch = html.charAt(i);
			switch (ch) {
			case '>':
				sb.append("&gt;"); break;
			case '<':
				sb.append("&lt;"); break;
			case '&':
				sb.append("&amp;"); break;
			case '"':
				sb.append("&quot;"); break;
			case '\'':
				sb.append("&#039;"); break;
                        case '\t':
                                sb.append("&nbsp;");break;
			default:
				sb.append(ch); break;
			}
		}
		return sb.toString();
	}
    

}
