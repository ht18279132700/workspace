package cn.com.pcauto.wenda.util;

import java.util.Date;

/**
 * MC缓存时间
 * 
 * @author fxr
 *
 */
public class McCacheTime {
	
	/**
	 * 缓存时间5s
	 */
	public final static long MC_CACHE_5S = 5000;
	
	/**
	 * 缓存时间30s
	 */
	public final static long MC_CACHE_30S = 30000;
	
	/**
	 * 缓存时间1M
	 */
	public final static long MC_CACHE_1M = 60000;
	
	/**
	 * 缓存时间5M
	 */
	public final static long MC_CACHE_5M = 300000;
	
	/**
	 * 缓存时间10M
	 */
	public final static long MC_CACHE_10M = 600000;
	
	/**
	 * 缓存时间30M
	 */
	public final static long MC_CACHE_30M = 1800000;
	/**
	 * 缓存时间1h
	 */
	public final static long MC_CACHE_1H = 3600000;
	
	/**
	 * 缓存时间4h
	 */
	public final static long MC_CACHE_4H = 14400000;
	
	/**
	 * 缓存时间15天
	 */
	public final static long MC_CACHE_15D = 1296000000;
	/**
	 * 缓存时间3天
	 */
	public final static long MC_CACHE_3D = 259200000;
	/**
	 * 缓存时间1天
	 */
	public final static long MC_CACHE_1D = 86400000;
	
	
	public static Date getMc5sTimes() {
		return getMcCacheTimes(MC_CACHE_5S);
	}
	
	public static Date getMc30sTimes() {
		return getMcCacheTimes(MC_CACHE_30S);
	}
	
	public static Date getMc1MTimes() {
		return getMcCacheTimes(MC_CACHE_1M);
	}
	
	public static Date getMc5MTimes() {
		return getMcCacheTimes(MC_CACHE_5M);
	}
	
	public static Date getMc10MTimes() {
		return getMcCacheTimes(MC_CACHE_10M);
	}
	
	public static Date getMc30MTimes() {
		return getMcCacheTimes(MC_CACHE_30M);
	}
	
	public static Date getMc1HTimes() {
		return getMcCacheTimes(MC_CACHE_1H);
	}
	
	public static Date getMc4HTimes() {
		return getMcCacheTimes(MC_CACHE_4H);
	}
	
	public static Date getMc15DTimes() {
		return getMcCacheTimes(MC_CACHE_15D);
	}
	public static Date getMc3DTimes() {
		return getMcCacheTimes(MC_CACHE_3D);
	}
	public static Date getMc1DTimes() {
		return getMcCacheTimes(MC_CACHE_1D);
	}
	private static Date getMcCacheTimes(long cacheTime) {
		return new Date(System.currentTimeMillis() + cacheTime);
	}
}
