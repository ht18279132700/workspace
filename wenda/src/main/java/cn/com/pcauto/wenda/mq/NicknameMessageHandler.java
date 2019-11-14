package cn.com.pcauto.wenda.mq;

import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.UserService;

import com.alibaba.fastjson.JSONObject;

public class NicknameMessageHandler implements MqMessageHandler {

	@Autowired
	private UserService userService;
	
	@Override
	public void handleJsonMessage(JSONObject object) {
		long uid = object.getLongValue("userId");
		String nickName = object.getString("nickName");
		User user = userService.findById(uid);
		if(user != null){
			user.setNickName(nickName);
			userService.update(user, "nickName");
		}
	}

}
