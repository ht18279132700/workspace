package cn.com.pcauto.wenda.censor; 

import java.util.HashMap;
import java.util.Map;

/** 
 * @author 作者 guoqiang
 * @version 2016-7-28
 */
public class CensorEntity {
	/** 标题 **/
    private String title;
    /** 分网标识 2:汽车网**/
    private int site;
    /** 应用名称　**/
    private String app;
    /** 类型ID **/
    private long typeId;
    /** 是滞新建　**/
    private int isNew;

    private String uri;
    
    private String ip;
    /** 版本　**/
    private long version;
    /** 用户ID**/
    private long userId;
    /** 用户名称 **/
    private String userName;
    /** 审核内容 **/
    private String content;
    /** 是否有图片  1：有图片**/
    private int isHtml;

    public String getTitle() {
        return title;
    }

    public int getSite() {
        return site;
    }

    public String getApp() {
        return app;
    }

    public long getTypeId() {
        return typeId;
    }

    public int getIsNew() {
        return isNew;
    }

    public String getUri() {
        return uri;
    }

    public String getIp() {
        return ip;
    }

    public long getVersion() {
        return version;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public int getIsHtml() {
		return isHtml;
	}

	public void setIsHtml(int isHtml) {
		this.isHtml = isHtml;
	}

	public Map<String,Object> getMap(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("title", title);
        map.put("app", app);
        map.put("site", site);
        map.put("typeId", typeId);
        map.put("isNew", isNew);
        map.put("uri", uri);
        map.put("ip", ip);
        map.put("version", version);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("content", content);
        if (1 == isHtml) {
        	map.put("isHtml", isHtml);
		}
        return map;
    }
}
