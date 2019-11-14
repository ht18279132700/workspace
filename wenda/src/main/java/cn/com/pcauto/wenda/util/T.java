package cn.com.pcauto.wenda.util;

import java.util.Calendar;
import java.util.Date;

public class T {
	public static long longValue(String v, long def) {
		if (v == null || v.length() == 0)
			return def;
		try {
			return Long.parseLong(v.trim());
		} catch (Exception e) {
			return def;
		}
	}

	public static Date getYesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
    
}