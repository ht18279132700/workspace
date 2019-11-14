package cn.com.pcauto.wenda.config;

/**
 * 系统配置
 */
public class SystemConfig {

    private String root;	//系统前缀
    private String oldRoot;	//旧的系统前缀
    private String appName; //系统名称
    private String censorDoMain;//审核地址
    private int censorType;//审核类型(0,先出后审,1先审后出)
    private String cmtRoot;//评论系统域名
    
    private String upcRoot;//upc图片上传路径
    private String ucRoot;//个人中心应用路径
    private String msgFeedUrl; //消息队列URl
    private String passportUrl;//passport路径
    private String userSessionName;
    private String proxyHost;//代理ip
    private int proxyPort;//代理端
    
    private String wapRoot;//wap首页路径
    private String wapUcRoot;//wap个人中心应用路径
    private String priceRoot;//报价库域名
    private String ksRoot;//快搜的域名
    private String uploadvideoRoot;//上传视频域名

	private int forbid;   // 禁止发言，等于1时整个系统禁言，不允许发表内容，默认为0
	public final String forbidTips = "系统升级维护中，暂不支持此操作";   // 禁言时的提示内容


	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getOldRoot() {
		return oldRoot;
	}

	public void setOldRoot(String oldRoot) {
		this.oldRoot = oldRoot;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCensorDoMain() {
		return censorDoMain;
	}

	public void setCensorDoMain(String censorDoMain) {
		this.censorDoMain = censorDoMain;
	}

	public int getCensorType() {
		return censorType;
	}

	public void setCensorType(int censorType) {
		this.censorType = censorType;
	}

	public String getCmtRoot() {
		return cmtRoot;
	}

	public void setCmtRoot(String cmtRoot) {
		this.cmtRoot = cmtRoot;
	}

	public String getUpcRoot() {
		return upcRoot;
	}

	public void setUpcRoot(String upcRoot) {
		this.upcRoot = upcRoot;
	}

	public String getUcRoot() {
		return ucRoot;
	}

	public void setUcRoot(String ucRoot) {
		this.ucRoot = ucRoot;
	}

	public String getMsgFeedUrl() {
		return msgFeedUrl;
	}

	public void setMsgFeedUrl(String msgFeedUrl) {
		this.msgFeedUrl = msgFeedUrl;
	}

	public String getPassportUrl() {
		return passportUrl;
	}

	public void setPassportUrl(String passportUrl) {
		this.passportUrl = passportUrl;
	}

	public String getUserSessionName() {
		return userSessionName;
	}

	public void setUserSessionName(String userSessionName) {
		this.userSessionName = userSessionName;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getWapRoot() {
		return wapRoot;
	}

	public void setWapRoot(String wapRoot) {
		this.wapRoot = wapRoot;
	}

	public String getWapUcRoot() {
		return wapUcRoot;
	}

	public void setWapUcRoot(String wapUcRoot) {
		this.wapUcRoot = wapUcRoot;
	}

	public String getPriceRoot() {
		return priceRoot;
	}

	public void setPriceRoot(String priceRoot) {
		this.priceRoot = priceRoot;
	}

	public String getUploadvideoRoot() {
		return uploadvideoRoot;
	}

	public void setUploadvideoRoot(String uploadvideoRoot) {
		this.uploadvideoRoot = uploadvideoRoot;
	}
	
    public String getRootMoveHttp() {
        return root.replace("http:", "").replace("https:", "");
    }

	public String getKsRoot() {
		return ksRoot;
	}

	public void setKsRoot(String ksRoot) {
		this.ksRoot = ksRoot;
	}

	public int getForbid() {
		return forbid;
	}

	public void setForbid(int forbid) {
		this.forbid = forbid;
	}
}