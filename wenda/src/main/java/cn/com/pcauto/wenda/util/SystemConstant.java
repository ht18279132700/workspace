package cn.com.pcauto.wenda.util;

/**
 * 应用常量接口
 * 
 * @author fxr
 * 
 */
public interface SystemConstant {
	
	/** 成功状态码 */
	int SUCCESS = 0;
	/** 失败状态码 */
	int FAILED = 1;
	/** 错误状态码 */
	int ERROR = -1;

	/** 权限问题 */
	int STATUS_PERMISSIONS_ERROR = 1001;
	/** 数据错误 */
	int STATUS_DATA_ERROR = 1002;
	/** 未登录 */
	int STATUS_NOLOGIN_ERROR = 1003;

	// APP客户端版本号
	String APP_VERSION = "version";
	String V1_VERSION = "v1.0";// 当前接口版本号1.0
	String V1_VERSION_LATEST = "v1.0(latest)";// 当前接口版本号1.0为最新的版本


	String CHARSET_UTF8 = "UTF-8";
	String CONTENTTYPE_JSON = "json";
	String CONTENTTYPE_HTML = "html";
	
	String PAGE_NO = "pageNo";
	String PAGE_SIZE = "pageSize";
	String PAGER_PAGECOUNT = "pageCount";// 总页数
	String PAGER_TOTAL = "total";// 返回数据总数
	int PAGE_NO_1 = 1;
	int PAGE_SIZE_1 = 1; // 一般最小是1
	int PAGE_SIZE_5 = 5; // 
	int PAGE_SIZE_10 = 10; // wap一般默认是10
	int PAGE_SIZE_15 = 15;
	int PAGE_SIZE_20 = 20; // pc一般默认是20
	int PAGE_SIZE_25 = 25; // 默认是25
	int PAGE_SIZE_30 = 30;
	int PAGE_SIZE_100 = 100; // 一般默认最大是100

	// 参数常量
	String PARAM_XSSENCODE = "xssEncode";// 0 原内容 1 过滤xss
	String WINDOW_NAME = "windowname"; // windowname 跨域参数
	String PARAM_CALLBACK = "callback";// 回调方法

	int PARAM_XSSENCODE_DEF = 0;// 默认返回原内容，不过滤xss
	int PARAM_XSSENCODE_1 = 1;// 过滤xss
	int PARAM_XSSENCODE_2 = 2;//
	int IS_WINDOW_NAME_CROSS = 1; // WINDOWNAME跨域
	
}
