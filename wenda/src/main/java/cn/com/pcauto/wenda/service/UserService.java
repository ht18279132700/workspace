package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.entity.UserStat;


public class UserService extends BasicService<User> {
	
	@Autowired
	private UserStatService userStatService;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	public UserService(){
		super(User.class);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public long create(User user){
		if(user == null || user.getUid() <= 0){
			return 0L;
		}
		
		long uid = user.getUid();
		
		try {
			super.create(user);
		} catch (Exception e) {
			LOG.error("create user fail, uid is " + uid, e);
		}
		
		UserStat userStat = new UserStat();
		userStat.setUid(uid);
		userStat.setCreateAt(new Date());
		
		try {
			userStatService.create(userStat);
		} catch (Exception e) {
			LOG.error("create userStat fail, uid is " + uid, e);
		}
		
		return uid;
	}
	/**
	 * 获取用户表的ID集合
	 * @return
	 */
	public List<Long> listUID(){
		String sql = "SELECT uid FROM wd_user";
		return geliDao.getJdbcTemplate().queryForList(sql, Long.class);
	}
	
	public List<Long> listSockpuppet(int limit){
		String sql = "select distinct uid from wd_sockpuppet where status=0 order by last_publish_at asc limit " + limit;
		return geliDao.getJdbcTemplate().queryForList(sql, Long.class);
	}
}
