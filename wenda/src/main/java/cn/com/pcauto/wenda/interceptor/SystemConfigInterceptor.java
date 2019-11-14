package cn.com.pcauto.wenda.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.com.pcauto.wenda.config.SystemConfig;


/**
 * 读取系统配置拦截器
 */
public class SystemConfigInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        if (null == request.getAttribute("cfg")) {
            String ROOT = systemConfig.getRoot();
            String oldRoot = systemConfig.getOldRoot();
            request.setAttribute("cfg", systemConfig);
            request.setAttribute("ROOT", ROOT.replace("https:", "").replace("http:", "")); 
            request.setAttribute("oldRoot", oldRoot.replace("https:", "").replace("http:", "")); 
            request.setAttribute("THIS", ROOT.replace("https:", "").replace("http:", "") + request.getServletPath());
            request.setAttribute("appName", systemConfig.getAppName());
            request.setAttribute("UCROOT", systemConfig.getUcRoot().replace("https:", "").replace("http:", "")); 
            request.setAttribute("WAPROOT", systemConfig.getWapRoot().replace("https:", "").replace("http:", "")); 
            request.setAttribute("WAPUCROOT", systemConfig.getWapUcRoot().replace("https:", "").replace("http:", "")); 
            request.setAttribute("UPC", systemConfig.getUpcRoot().replace("https:", "").replace("http:", "")); 
            request.setAttribute("passportRoot", systemConfig.getPassportUrl().replace("https:", "").replace("http:", "")); 
            request.setAttribute("cmtRoot", systemConfig.getCmtRoot().replace("https:", "").replace("http:", "")); 
            request.setAttribute("uploadvideoRoot", systemConfig.getUploadvideoRoot().replace("https:", "").replace("http:", ""));
            request.setAttribute("commonSessionName", systemConfig.getUserSessionName()); 
        }
        return super.preHandle(request, response, handler);
    }
}
