package cn.com.pcauto.wenda.util;

import org.apache.commons.lang.StringUtils;

/**
 * EncodeUtils for encode the data use in browser prevent attack like XSS ...
 *
 * @author cuiyulong@pconline.com.cn
 * @author chenxiaohu@pconline.com.cn
 */
public class EncodeUtils extends org.gelivable.web.EncodeUtils {

    public static String encodeForHTML(String input, int sourceAgent) {
        if (input == null || sourceAgent == 1) {
            return input;
        }
        return encodeForHTML(input);
    }
    
    public static String encodeForHTML(String input, boolean isEnode) {
        if (input == null || !isEnode) {
            return input;
        }
        return encodeForHTML(input);
    }
    
    public static String encodeForHTMLFilterNull(String input) {
        if (input == null) {
            return input;
        }
        return encodeForHTML(input);
    }
    
    /**
     * 过滤MHTML协议注入（破坏协议结构，把换行去掉）
     * @param param
     * @return
     */
    public static String filterMHTML(String param) {
    	if (StringUtils.isBlank(param)) {
    		return param;
    	}
    	return param.replace("%0d%0a", "").replace("\n", "").replace("\r", "");
    }
}
