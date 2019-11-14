package cn.com.pcauto.wenda.service;

import cn.com.pcauto.wenda.entity.UserAnswer;

public class UserAnswerService extends BasicService<UserAnswer> {

	public UserAnswerService(){
		super(UserAnswer.class);
	}
	
	public int incrTreadAndDecrPraise(UserAnswer userAnswer){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName(userAnswer.getCreateBy()));
		sb.append(" SET tread_num=tread_num+1, praise_num=praise_num-1 WHERE id=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), userAnswer.getId());
		if(i > 0){
			removeFromCache(userAnswer.getId());
		}
		return i;
	}
	
	public int incrPraiseAndDecrTread(UserAnswer userAnswer){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName(userAnswer.getCreateBy()));
		sb.append(" SET praise_num=praise_num+1, tread_num=tread_num-1 WHERE id=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), userAnswer.getId());
		if(i > 0){
			removeFromCache(userAnswer.getId());
		}
		return i;
	}
	
	public UserAnswer findById(long createBy,long id){
		return find(createBy, id);
	}
	
}
