<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf" %>
<%
    Env env = EnvUtils.getEnv();
    String st = env.param("st", "");
    if ("-1".equals(st)) {
        response.sendRedirect("msg.jsp?code=login_fail");
        return;
    }
    GeliAuthFacade authFacade = env.getBean(GeliAuthFacade.class);
    Map<String, Object> user = authFacade.getAuthResult(st);
    if (st.length() > 5) {
        user = authFacade.getAuthResult(st);
    }
    if (user != null) {
        GeliDao dao = GeliUtils.getDao();
        Date now = new Date();
        Long userId = (Long) user.get("userId");

        // ensure user is for this application
        try {
            GeliUser u = dao.find(GeliUser.class, userId);
            u.setLoginAt(now);
            dao.update(u, "loginAt");
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            if (authFacade.isAdmin(userId)) {
                GeliUser u = new GeliUser();
                u.setUserId(userId);
                u.setAccount((String)user.get("account"));
                u.setName((String)user.get("name"));
                u.setCreateAt(now);
                u.setLoginAt(now);
                dao.create(u);
            } else {
                response.sendRedirect("msg.jsp?code=unauthorized");
                return;
            }
        }

        if (!authFacade.isApplicationUser(userId)) {
            response.sendRedirect("msg.jsp?code=unauthorized");
            return;
        }
        // create and save session
        GeliSession rSession = new GeliSession();
        rSession.setSessionId(st);
        rSession.setUserId(userId);
        rSession.setCreateAt(new Date());
        GeliSession.saveSession(rSession, response);

        response.sendRedirect("index.jsp");
        return;
    }

    // logout system
    String action = env.param("action");
    if ("logout".equals(action)) {
        GeliSession.clearSession(request, response);
        response.sendRedirect("login.jsp");
        return;
    }

    pageContext.setAttribute("authUri", authFacade.getAuthUri());

%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>车问答后台登录</title>
<link href="/admin/themes/css/login.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.info{font-size: 12px;color: red;margin-left: 80px;}
.login_logo a{text-decoration: none;}
.login_logo a span{font-size: 26px;vertical-align: middle;}
.login_logo a img{vertical-align: middle;}
</style>
</head>
<body onload='document.getElementById("return").value = location.href.replace(/http:\/\/(.*)\/(.*)/, "http://$1/login.jsp");'>
	<div id="login">
		<div id="login_header">
			<h1 class="login_logo">
				<!-- <a href="#"><img src="statics/dwz/themes/default/images/login_logo.gif" /></a> -->
				<a href="//www.pcauto.com.cn/" target="_blank">
					<img alt="" src="/admin/themes/green/images/inc_logo.png"/>
				</a>
				&nbsp;
				<a href="//wenda.pcauto.com.cn/" target="_blank"><span>车问答</span></a>
			</h1>
			<div class="login_headerContent">
				<div class="navList">
					<ul>
						<li><a href="javascript:;" target="_blank"></a></li>
					</ul>
				</div>
				<h2 class="login_title" style="font-size:16px">用户登录</h2>
			</div>
		</div>
		<div id="login_content">
			<div class="loginForm">
				<form id="login" action="${authUri}" method="post">
					<p>
						<label>用户名：</label>
						<input id="username" name="username" type="text" style="width:140px;height:20px;" class="login_input" placeholder="员工帐号" autofocus required/>
						<br/>
						<span class="info">${userNoMsg}</span>
					</p>
					<p>
						<label>密&nbsp;&nbsp;&nbsp;码：</label>
						<input id="password" name="password" type="password" style="width:140px;height:20px;" class="login_input" placeholder="密码" required/>
						<br/>
						<span class="info">${userPwdMsg}</span>
					</p>
					<p><br/><span class="info">${errorMsg}</span></p>
					<div class="login_bar" style="margin-left:10px;">
						<input type="hidden" id="return" name="return" value="">
						<input class="sub" type="submit" id="submit" value="" />
					</div>
				</form>
			</div>
			<div class="login_banner"><img src="/admin/themes/green/images/login_banner.jpg" /></div>
			<div class="login_main">
			  <c:if test="${!(pageContext.request.getRequestURL().toString().contains('//wenda.pcauto.com.cn'))}">
				<ul class="helpList">
					<li><a href="javascript:;">测试帐号：admin</a></li>
					<li><a href="javascript:;">默认密码：123456</a></li>
				</ul>  
			  </c:if>
			</div>
		</div>
	</div>
</body>
</html>