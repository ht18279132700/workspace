/*
 * Copyright 1997-2013
 *
 * http://www.pconline.com.cn
 *
 */
package cn.com.pcauto.wenda.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gelivable.auth.GeliAuthFacade;
import org.gelivable.auth.entity.GeliSession;
import org.gelivable.auth.entity.GeliUser;
import org.gelivable.web.AbstractAuthFilter;
import org.gelivable.web.Env;
import org.gelivable.web.EnvUtils;
import org.gelivable.web.HttpMethod;

/**
 *
 * @author chenxiaohu
 */
public class AuthFilter extends AbstractAuthFilter {
    Log LOG = LogFactory.getLog(AuthFilter.class);

    static final String CREATE_DO = "create.do";
    static final String UPDATE_DO = "update.do";
    static final String DELETE_DO = "delete.do";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    @Override
    public void sendAuthFail(HttpServletResponse resp, boolean json) throws IOException {
        if (json) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/json");
            resp.getWriter().println("{\"statusCode\":300, \"message\":\"没有权限！\"}");
        } else {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html");
            resp.getWriter().print("<div class=\"pageContent\">"
                    + "<div style='padding-top:200px;text-align:center;"
                    + "font-size:24px;color:red;'>"
                    + "没有权限!</div></div>");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest req = (HttpServletRequest)request;
    	
        super.doFilter(request, response, chain); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int hasRight(HttpServletRequest req) {
    	String uri = req.getRequestURI();
	     Env env = EnvUtils.getEnv();
	     GeliAuthFacade authFacade = env.getBean(GeliAuthFacade.class);
	     String adminPrefix = env.getServletContext().getContextPath() + "/admin";
	    //  String userImgPrefix = env.getServletContext().getContextPath()+"/user/img";
	    
		 if(uri.startsWith("/admin/user/img") || uri.startsWith("/admin/themes")){
				 return HAS_RIGHT;
		 }
		 //权限管理只有是管理员,普通用户返回没权限
		 if(uri.startsWith(adminPrefix)&&(uri.startsWith(adminPrefix+"/geli") || uri.startsWith(adminPrefix+"/dev"))){
			 if (!authFacade.isAdmin()) {
	                return HAS_NOT_RIGHT;
	            }else{
	            	return HAS_RIGHT;
	            }
		 }else if(uri.startsWith(adminPrefix)&&(!uri.startsWith(adminPrefix+"/geli"))){//如果只是访后台并且是后台用户,那么返回有权限
			 GeliUser user = GeliSession.getCurrentUser();
			 if(user != null && authFacade.isApplicationUser(user.getUserId())){
				 return HAS_RIGHT;
			 }
		 }
		return DEFAULT_RIGHT;
    }
    
    
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, 
//            FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest)request;
//        HttpServletResponse resp = (HttpServletResponse)response;
//
//        String uri = req.getRequestURI();
//        if (uri.indexOf("//") > -1) {
//            resp.setStatus(404);
//            resp.getWriter().println("//Not Found");
//            return;
//        }
// 
//        // ensure env filter exectued
//        Env env = EnvUtils.getEnv();
//        if (env.getServletContext() == null) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // ensure url is /admin/**/*.do and user isn't super admin
//        GeliAuthFacade authFacade = env.getBean(GeliAuthFacade.class);
//        String adminPerfix = env.getServletContext().getContextPath() + "/admin/"; 
//        System.out.println(uri.startsWith(adminPerfix) && uri.endsWith(".do"));
//        System.out.println( authFacade.isAdmin());
//        if (!(uri.startsWith(adminPerfix) && uri.endsWith(".do"))
//                || authFacade.isAdmin()) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        HttpMethod method = env.getHttpMethod();
//        switch (hasRight(req)) {
//        case HAS_RIGHT:
//            chain.doFilter(request, response);
//            break;
//        case HAS_NOT_RIGHT:
//            if (method == HttpMethod.POST) {
//                sendAuthFail(resp, true);
//            } else {
//                sendAuthFail(resp, false);
//            }
//            break;
//        case DEFAULT_RIGHT:
//            if (method == HttpMethod.POST) {
//                sendAuthFail(resp, true);
//            } else {
//                GeliUser user = GeliSession.getCurrentUser();
//                if (user != null && authFacade.isApplicationUser(user.getUserId())) {
//                    chain.doFilter(request, response);
//                } else {
//                    sendAuthFail(resp, false);
//                }
//            }
//            break;
//        default:
//            LOG.error("Invalid result by hasRight must one of: -1, 0, 1");
//            sendAuthFail(resp, false);
//        }
//    }
//    
    
    
}

