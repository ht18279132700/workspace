package cn.com.pcauto.wenda.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.Functions;
import cn.pconline.passport3.account.entity.Session;
import cn.pconline.passport3.client.Passport;

public class UserInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserService userService;
	
    @Autowired
    private Passport passport;
	
	private List<String> whitelist;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		if(whitelist != null && whitelist.contains(request.getServletPath())){
			return super.preHandle(request, response, handler);
		}
		
		User user = (User)request.getAttribute("user");
		if(user == null){
			long accountId = 0;
			Session session = passport.recognize(request, response);
			if(session != null && (accountId = session.getAccountId()) > 0){
				user = userService.findById(accountId);
				if(user == null){
					user = Functions.getRemoteUser(accountId);
					userService.create(user);
				}
			}
			if(user == null){
				user = new User();
			}
			request.setAttribute("user", user);
		}
		return super.preHandle(request, response, handler);
	}

	public void setWhitelist(List<String> whitelist) {
		this.whitelist = whitelist;
	}
	
}
