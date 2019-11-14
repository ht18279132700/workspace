package cn.com.pcauto.wenda.util;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
	public static String getIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded;
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                ip = realIp + "/" + forwarded.replaceAll(", " + realIp, "");
            }
        }
        return ip;
    }

	/**
	 * 获取request请求协议
	 * @param request
	 * @return
	 */
	
	public static String getScheme(HttpServletRequest request) {

		String scheme = request.getHeader("X-Forwarded-Proto");

		if (scheme == null) {
			
			scheme = request.getScheme();
		} 
	
		return scheme;
	}  

 
	
}
