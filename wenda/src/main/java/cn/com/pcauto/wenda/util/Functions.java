package cn.com.pcauto.wenda.util;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.gelivable.web.Env;
import org.gelivable.web.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.entity.User;
import cn.pconline.passport3.account.entity.Account;
import cn.pconline.passport3.client.Passport;
import cn.pconline.r.client.RClient;

import com.alibaba.fastjson.JSONObject;


public class Functions {

	private static final Logger LOG = LoggerFactory.getLogger(Functions.class);
	static DecimalFormat decimalFormat = new DecimalFormat(".#");
	
	/**
     * 将动态链接静态化并加上分页页数
     *
     * @param object
     * @param pageNo
     * @return
     */
    public static String htmlUrl(String url, int pageNo) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        Env env = EnvUtils.getEnv();
        SystemConfig systemConfig = (SystemConfig) env.getBean(SystemConfig.class);
        String root = systemConfig.getRootMoveHttp();
        String resultPrefix = "";
        String resultSuffix = "";
        if (pageNo > 1) {
            resultSuffix = "-" + pageNo;
        }
        int splitSign = url.indexOf("?");
        if (splitSign >= 0) {
            String paramUrl = url.substring(splitSign + 1);
            String pathUrl = url.substring(0, splitSign);
            String pageName = pathUrl.substring(pathUrl.lastIndexOf("/") + 1);
            Map<String, String> paramMap = getParamsMap(paramUrl);
            if ("index.jsp".equals(pageName)) {
				return root + "/list-" + pageNo + ".html";
			}else if ("topic.jsp".equals(pageName)) {
				return root + "/topic/" + paramMap.get("tid") + "-" + pageNo + ".html";
			} else if ("detail.jsp".equals(pageName) || "detail_pager.jsp".equals(pageName)){
				return root + "/" + paramMap.get("qid") + "-" + pageNo + ".html";
			} else if ("search.jsp".equals(pageName)) {
				String keywords = paramMap.get("keywords");
				try {
					keywords = URLEncoder.encode(keywords, "UTF-8");
				} catch (Exception e) {
					LOG.error("url encode error, keywords="+keywords, e);
				}
				return root + "/search/" + keywords  + "&pageNo=" + pageNo;
			} else if ("sort.jsp".equals(pageName)) {
				return root + "/topic/" + paramMap.get("letter") + "-" + pageNo + ".html";
			} else {
				return url + "&pageNo=" + pageNo; 
			}
        }
        return root + resultPrefix + resultSuffix + ".html";
    }
    public static Map<String, String> getParamsMap(String str) {
        String[] args = str.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String arg : args) {
            String[] params = arg.split("=");
            if (params.length > 1) {
                map.put(params[0], params[1]);
            }
        }
        return map;
    }
    
	public static String escapeUrlPath(String s) {
		try {
			StringBuilder sb = new StringBuilder();
			char[] cs = s.toCharArray();
			for (char c : cs) {
				if (CharUtils.isAscii(c)) {
					sb.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
				} else {
					String es = StringEscapeUtils.escapeJava(String.valueOf(c));
					sb.append(es.replace('\\', '%'));
				}
			}
			return URLEncoder.encode(sb.toString(), "UTF-8");
		} catch (Exception e) {
			return s;
		}
	}
    
    public static List<Long> stringArr2LongList(String[] arr) {
    	return stringArr2LongList(arr, true);
    }
    
	public static List<Long> stringArr2LongList(String[] arr, boolean filterUnResolved) {
		List<Long> list = new ArrayList<Long>();
		if(arr == null || arr.length == 0){
			return list;
		}
		for (int i = 0; i < arr.length; i++) {
			try {
				list.add(Long.valueOf(arr[i]));
			} catch (Exception e) {
				if(!filterUnResolved){
					list.add(Long.valueOf(0));
				}
			}
		}
		return list;
	}
	
	public static String getTagIdStr(List<Tag> tagList) {
		StringBuilder sb = new StringBuilder();
		for (Tag tag : tagList) {
			if(tag == null){
				continue;
			}
			sb.append(",").append(tag.getId());
		}
		if(sb.length() > 0){
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
	
	public static List<Long> getTagIdList(List<Tag> tagList) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (Tag tag : tagList) {
			if(tag == null){
				continue;
			}
			list.add(tag.getId());
		}
		return list;
	}
	
	public static List<Long> getQuestionIdList(List<Question> questionList) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (Question q : questionList) {
			if(q == null){
				continue;
			}
			list.add(q.getId());
		}
		return list;
	}

	public static String getUserIcon(long uid,int size){
		if (uid > 0) {
			String picPath = "//i6.3conline.com/images/upload/upc/face/";
			String uidPath = uid + "";
			for(int i=0,s=uidPath.length();i<s;i++){    
			    int k = (i+2)<=s?(i+2):(i+1);
			    picPath += uidPath.substring(i, k) +"/";
			    i++;
			  }
			return picPath + uidPath + "_"+size+"x"+size;
		}else {
			return "";
		}
	}
	
	public static String getImgSize(String url,String size){
		if(StringUtils.isBlank(url)){
			return "";
		}
		url = url.replaceAll("http://(.*?)(.pconline.com.cn|.pcauto.com.cn)(.*?)/(pcautowenda)/(.*?)(\\.jpg|\\.bmp|\\.gif|\\.png|\\.jpeg?)", "//$1$2$3/$4/$5_"+size+"$6");
		return url;
	
	}
	
    public static String removeHttp(String url){
    	if(StringUtils.isBlank(url)){
    		return "";
    	}
    	String regex = ".+?\\.pc(auto|online|baby|lady|house)\\.com\\.cn(/.*)?";
    	if(url.matches(regex)){
    		return url.replace("https:", "").replace("http:", "");
    	}else{
    		return url;
    	}
    }
	
	public static User getRemoteUser(long uid) {
		if(uid <= 0){
			return null;
		}
		User user = getCenterUser(uid);
		if(user == null){
			user = getPassportUser(uid);
		}
		return user;
	}

	private static User getCenterUser(long accountId) {
		Env env = EnvUtils.getEnv();
		SystemConfig systemConfig = env.getBean(SystemConfig.class);
		RClient rClient = env.getBean(RClient.class);
		String url = new StringBuilder().append(systemConfig.getUcRoot())
                .append("/intf/client/getUser.jsp?id=")
                .append(accountId).append("&date=")
                .append(new Date().getTime()).toString();
        String userMsg = rClient.get(url, null, 1500, TimeUnit.MILLISECONDS);
        JSONObject json = null;
        if (StringUtils.isNotBlank(userMsg)) {
        	try {
        		json = JSONObject.parseObject(userMsg);
			} catch (Exception e) {
				LOG.error("解析JSON失败，userMsg="+userMsg, e);
			}
        }
        if (json != null) {
        	User user = new User();
        	user.setUid(accountId);
        	user.setName(StringUtils.defaultIfBlank(json.getString("userName"), ""));
        	user.setNickName(StringUtils.defaultIfBlank(json.getString("nickName"), ""));
        	long longCreateAt = json.getLongValue("createAt");
        	Date createAt = longCreateAt > 0 ? new Date(longCreateAt) : new Date();
        	user.setCreateAt(createAt);
        	return user;
        }
		return null;
	}
	
	private static User getPassportUser(long accountId) {
		Account account = getPassportAccount(accountId);
		if(account != null){
			User user = new User();
			user.setUid(accountId);
			user.setName(account.getAccountName());
			user.setNickName(account.getAccountName());
			user.setCreateAt(new Date(account.getCreateAt()));
			return user;
		}
		return null;
	}
	
	public static Account getPassportAccount(long accountId){
		Env env = EnvUtils.getEnv();
		HttpServletRequest request = env.getRequest();
		Account account = (Account)request.getAttribute("passportAccount");
		if(account == null || account.getAccountId() != accountId){
			account = env.getBean(Passport.class).getAccount(accountId);
			request.setAttribute("passportAccount", account);
		}
		return account;
	}
	
	/**
	 * 数字大于10000，改成1.0万
	 * @param number
	 * @return
	 */
	public static String getFormatStr(int number) {
		String formatStr = "";
		//大于10000,改成1.0万
		if(number >= 10000) {
			String c = decimalFormat.format((float)number/10000) ;
			formatStr = c + "万";
		}else{
			formatStr = number + "";
		}
		return formatStr;//用户要求的规则;
	}
	
    public static void setAllowCredentialHeader(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (StringUtils.isNotBlank(origin) && !"null".equals(origin)) {
        	response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }else{
        	response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE, HEAD");
    }
    
    public static void setCacheHeader(HttpServletResponse response, int seconds) {
    	long now = System.currentTimeMillis();
    	response.setHeader("Cache-Control", String.format("max-age=%s, public", seconds));
    	response.setDateHeader("Last-Modified", now);
    	response.setDateHeader("Expires", now + seconds * 1000L);
    }
    
    /**
     * 给纯数字的年月日加上分隔符
     * @param day
     * @param joinStr
     * @return
     */
    public static String joinDay(long day, String joinStr){
    	String dayStr = String.valueOf(day);
    	if(dayStr.length() == 8){ //年月日，正确应该是8位
    		return dayStr.substring(0, 4)
    				+ joinStr + dayStr.substring(4, 6)
    				+ joinStr + dayStr.substring(6, 8);
    	}
    	return dayStr;
    }
    
    /**
     * 在List集合中随机取出指定个数的元素
     * @param list
     * @param limit
     * @return
     */
    @SuppressWarnings("hiding")
	public static <T> List<T> randomInList(List<T> list, int limit){
    	if(limit <= 0){
    		throw new IllegalArgumentException();
    	}
    	if(list == null || list.size() <= limit){
    		return list;
    	}
    	List<T> resultList = new ArrayList<T>(limit);
    	boolean[] b = new boolean[list.size()];
    	Random random = new Random();
    	int index = 0;
    	for(int i=0; i<limit; i++){
    		do{
    			index = random.nextInt(b.length);
    		}while(b[index]);
    		resultList.add(list.get(index));
    		b[index] = true;
    	}
    	return resultList;
    }
    
    public static List<Long> string2LongList(String str){
    	return string2LongList(str, ",");
    }
    
    public static List<Long> string2LongList(String str, String separator){
    	List<Long> list = new ArrayList<Long>();
    	if(StringUtils.isBlank(str)){
    		return list;
    	}
    	
    	if(separator == null || separator.length() == 0){
    		try {
    			list.add(Long.parseLong(str.trim()));
			} catch (Exception e) {}
    	}else{
    		for(String split : str.split(separator)){
    			try {
        			list.add(Long.parseLong(split.trim()));
    			} catch (Exception e) {}
    		}
    	}
    	return list;
    }
    
    public static List<String> string2List(String str){
    	return string2List(str, ",");
    }
    
    public static List<String> string2List(String str, String separator){
    	List<String> list = new ArrayList<String>();
    	if(StringUtils.isBlank(str)){
    		return list;
    	}
    	
    	if(separator == null || separator.length() == 0){
    		list.add(str);
    	}else{
    		for(String split : str.split(separator)){
    			list.add(split);
    		}
    	}
    	return list;
    }
    
    public static String list2String(List<String> list){
    	if(list == null || list.isEmpty()){
    		return "";
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	for (String string : list) {
    		sb.append(",").append(string);
		}
    	sb.deleteCharAt(0);
    	return sb.toString();
    }
    
    public static String listToString(List<?> list){
    	if(list == null || list.isEmpty()){
    		return "";
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	for (Object id : list) {
    		sb.append(",").append(id);
		}
    	sb.deleteCharAt(0);
    	return sb.toString();
    }
}
