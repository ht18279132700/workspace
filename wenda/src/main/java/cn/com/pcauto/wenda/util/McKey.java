package cn.com.pcauto.wenda.util;

/**
 * MC缓存key，由该类统一管理，其他地方慢慢迁移过来
 * 
 * @author fxr
 *
 */
public class McKey {
	/**APP接口强制缓存头key*/
	public final static String APPAPI_CACHE_HEADER_KEY = "appapi_cache_header";
	
	/**文章pv浏览数key*/
	public final static String ARTICLEPVLOG_ARTICLEPC_KEY = "articlepv_artivle_%s_time_%s";
	
	/**作者最新两篇文章key*/
	public final static String AUTHOR_NEW_ARTICLE_KEY = "author_new_article_%s";
	
	/**作者标签*/
	public final static String AUTHOR_TAG_KEY = "author_tag";
}
