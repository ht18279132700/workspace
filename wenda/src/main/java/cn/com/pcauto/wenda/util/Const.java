package cn.com.pcauto.wenda.util;

public abstract class Const {

	public static final int PRAISE = 1;  // 赞
	public static final int TREAD = -1;  // 踩
	
	public static final int AGENT_SEO = -1;  // 来源：SEO
	public static final int AGENT_PC = 0;  // 来源：PC
	public static final int AGENT_WAP = 1;  // 来源：WAP
	
	public static final int STATUS_DELETE = -1;  // 状态：删除
	public static final int STATUS_PENDING = 0;  // 状态：待审
	public static final int STATUS_PASS = 1;  // 状态：通过
	
	public static final int USER_TYPE_MJ = -1;  // 用户类型：马甲
	public static final int USER_TYPE_NORMAL = 0;  // 用户类型：正常
	
	public static final int CENSOR_TYPEID_QUESTION = 494001;//问题-审核的类型ID
	public static final int CENSOR_TYPEID_ANSWER = 494002;//回答-审核的类型ID
	public static final int CENSOR_TYPEID_REPLY = 494003;//回复-审核的类型ID
	
	public static final int NOT_TAG_QUESTION = 200;//没有标签的问题，缓存的数量
	
	
}
