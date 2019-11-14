package cn.com.pcauto.wenda.util;

import javax.servlet.http.Cookie;

public class CookieUtil {
	
	public static Cookie get(Cookie[] cookies, String key) {
		if(cookies == null)
			return null;
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(key))
				return cookie;
		}
		return null;
	}
}
