/*
 */
package cn.com.pcauto.wenda.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gelivable.webmvc.JSONBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author xulin
 * Date:2015-8-26 15:55:34 
 */
public class WebKit {
    protected static final Logger log = LoggerFactory.getLogger(WebKit.class);
    private static final int STATUS_OK = 0;
    public static void errorMsg(String msg, HttpServletResponse resp) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(
                    new JSONBuilder()
                    .put("statusCode", 300)
                    .put("message", msg)
                    .toString());
        } catch (IOException ex) {
            log.error("set errorMsg fail!", ex);
        }
    }

     public static void successMsg(String msg, String navTabId, HttpServletResponse resp, String callbackType) {
        try {
            String rs = StringUtils.isEmpty(msg) ? "操作成功" : msg;
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(new JSONBuilder()
                    .put("statusCode", 200)
                    .put("message", rs)
                    .put("navTabId", navTabId)
                    .put("callbackType", callbackType)
                    .toString());
        } catch (IOException ex) {
            log.error("set successMsg fail!", ex);
        }
    }
    private static void printlnMsg(HttpServletResponse response,JSONObject obj){
        try {
            PrintWriter print = response.getWriter();
            print.println(obj.toJSONString());
            print.flush();
            print.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WebKit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void printlnSuccessMsg(HttpServletResponse response,JSONArray o,String msg){
        JSONObject obj = new JSONObject();
        if(StringUtils.isNotBlank(msg)){
            obj.put("message", msg);
        }
        obj.put("status", STATUS_OK);
        obj.put("data", o);
        printlnMsg(response, obj);
    }
    
    public static void setAccessControlAllowHeader(HttpServletRequest request,
			HttpServletResponse response) {
		String originHeader = request.getHeader("Origin");
		if (StringUtils.isNotBlank(originHeader)) {
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Origin", originHeader);
		}
	}
}
