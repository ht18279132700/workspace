/*
 */
package cn.com.pcauto.wenda.service.censor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.censor.CensorEntity;
import cn.com.pcauto.wenda.config.SystemConfig;
import cn.pconline.r.client.EntityBuilder;
import cn.pconline.r.client.ResponseExtractor;
import cn.pconline.r.client.SimpleHttpTemplate;

/**
 * @author xulin
 * Date:2015-11-19 10:26:02 
 */
public class CensorService {
	private static final Logger log = LoggerFactory.getLogger(CensorService.class);
    // CHANGE STATUS
	public static final int CENSORSYSTEM_RESPONSE_PASS = 2;
	public static final int CENSORSYSTEM_RESPONSE_DENY = 3;
	
	public static final int CENSOR_STATUS_PENDING  = 0; //0待审
	public static final int CENSOR_STATUS_APPROVED = 1; //1已审
	public static final int CENSOR_STATUS_REJECTED = -1; //-1被扣留
	public static final int CENSOR_STATUS_DELETE = -2; //-2被删除
	/** 新建审核**/
    public static final int CENSOR_ISNEW= 1;
    /** 修改审核 **/
    public static final int CENSOR_NOTNEW= 0;
    /**site，汽车网为2**/
    public static final int SITE = 2;
	    
    @Autowired
    private SimpleHttpTemplate simpleHttpTemplate4Driver;
    @Autowired
    public SystemConfig systemConfig;
    
    @Resource(name="censorThreadPool")
    private ExecutorService executorService;

	/**
	 * interface ResponseExtractor comes from R, not Spring
	 */
	class CensorResponseExtractor implements ResponseExtractor<CensorResult> {
	
	    @Override
	    public CensorResult extractData(HttpResponse paramHttpResponse)
	            throws IOException {
	        StatusLine status = paramHttpResponse.getStatusLine();
	        CensorResult result = null;
	        int sc = status.getStatusCode();
	        if ((sc >= 200) && (sc <= 300)) {
	            HttpEntity responseEntity = paramHttpResponse.getEntity();
	            result = null;
	            if (responseEntity != null) {
	                result = new CensorResult();
	                result.setResult(EntityUtils
	                        .toString(responseEntity, "GBK"));
	            }
	        } else {
	            log.error("censor post error : " + sc);
	            log.error("censor post error1 : " + status.getReasonPhrase());
	            log.error("censor post error1 : " + status.getProtocolVersion());
	            HttpEntity responseEntity = paramHttpResponse.getEntity();
	            if (responseEntity != null) {
	                log.error(EntityUtils.toString(responseEntity, "GBK"));
	            }
	        }
	        return result;
	    }
	}
	
	class CensorResult {
	
	    String result = "";
	
	    public String getResult() {
	        return result;
	    }
	
	    public void setResult(String result) {
	        this.result = result;
	    }
	}

	class CensorCall implements Callable<Object> {
	    private String url;
	    private Map<String, Object> params;
	
	    public CensorCall(String url, Map<String, Object> params) {
	        this.url = url;
	        this.params = params;
	    }
	
	    public Object call() throws Exception {
	        String result = post(params, url);
	        if (result != null && result.length() < 50) {
	            result = result.replaceAll("\r\n", "");
	        }
	        if (result == null || "".equals(result)) {
	        } else {
	            log.error(result);
	        }
	
	        return 0;
	    }
	    
	        /**
	     * 以r包的SimpleHttpTemplate的post方式发送
	     *
	     * @param params
	     * @param url
	     * @return
	     */
	    private String post(Map<String, Object> params, String url) {
	        EntityBuilder builder = EntityBuilder.entity("GBK");
	        builder = builder.params(params);
	        HttpEntity entity = builder.build();
	        CensorResult result = simpleHttpTemplate4Driver.post(url, null,
	                new CensorResponseExtractor(), entity);
	        String jsonString = result.getResult();
	        return jsonString;
	    }


    }
    /**
     * 发送审核
     * @param entity 
     */
	protected void sendToCreate(CensorEntity entity){
        entity.setApp(systemConfig.getAppName());
        entity.setSite(SITE);
        String receiveHome = systemConfig.getCensorDoMain()+ "/receive.jsp";
        executorService.submit(new CensorCall(receiveHome, entity.getMap()));
    }
    /**
     * 获取审核回调中的typeId
     * @param url
     * @return 
     */
    public long getTypeId(String url){
        return getIdFromUrl(url, "typeId");
    }
    protected long getIdFromUrl(String url,String typeName){
        if(StringUtils.isNotBlank(url) && StringUtils.isNotBlank(typeName)){
            Pattern pattern = Pattern.compile(typeName+"=(\\d+)");
            Matcher matcherr = pattern.matcher(url);
            if(matcherr.find()){
                return Long.parseLong(matcherr.group(1));
            }
        }
        return 0;
    }
    /**
    * 数据库的更新日期为datetime类型，比较时间戳的话，不精确
    *
    * @param time1
    * @param time2
    * @return
    */
   protected boolean isTheSameVersion(long time1, long time2) {
       Calendar cal1 = Calendar.getInstance();
       cal1.setTimeInMillis(time1);
       Calendar cal2 = Calendar.getInstance();
       cal2.setTimeInMillis(time2);
       return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
               && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
               && cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
               && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)
               && cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND);
   }
    public  void callback(String url, int status,long version){
    };
    /** 是否匹配当前类型 **/
    public  boolean isCurrentType(String url){
        return false;
    };
}
