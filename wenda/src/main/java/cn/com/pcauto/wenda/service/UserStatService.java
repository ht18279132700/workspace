package cn.com.pcauto.wenda.service;

import java.util.Date;

import cn.com.pcauto.wenda.entity.UserStat;

public class UserStatService extends BasicService<UserStat> {
	
	public UserStatService(){
		super(UserStat.class);
	}
	
	public int updateQuestionNumAndTime(long uid, Date lastQuestionAt){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET question_num=question_num+1, last_question_at=IF(last_question_at > ?, last_question_at, ?)");
		sb.append(" , update_at=? WHERE uid=?");
		int rows = geliDao.getJdbcTemplate().update(sb.toString(), lastQuestionAt, lastQuestionAt, new Date(), uid);
		if(rows > 0){
			removeFromCache(uid);
		}
		return rows;
	}
	
	public int updateAnswerNumAndTime(long uid, Date lastAnswerAt){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET answer_num=answer_num+1, last_answer_at=IF(last_answer_at > ?, last_answer_at, ?)");
		sb.append(" , update_at=? WHERE uid=?");
		int rows = geliDao.getJdbcTemplate().update(sb.toString(), lastAnswerAt, lastAnswerAt, new Date(), uid);
		if(rows > 0){
			removeFromCache(uid);
		}
		return rows;
	}
	
	public int updateReplyNumAndTime(long uid, Date lastReplyAt){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET reply_num=reply_num+1, last_reply_at=IF(last_reply_at > ?, last_reply_at, ?)");
		sb.append(" , update_at=? WHERE uid=?");
		int rows = geliDao.getJdbcTemplate().update(sb.toString(), lastReplyAt, lastReplyAt, new Date(), uid);
		if(rows > 0){
			removeFromCache(uid);
		}
		return rows;
	}
	
	public int incrTreadAndDecrPraise(long uid){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET tread_num=tread_num+1, praise_num=praise_num-1, update_at=? WHERE uid=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), new Date(), uid);
		if(i > 0){
			removeFromCache(uid);
		}
		return i;
	}
	
	public int incrPraiseAndDecrTread(long uid){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET praise_num=praise_num+1, tread_num=tread_num-1, update_at=? WHERE uid=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), new Date(), uid);
		if(i > 0){
			removeFromCache(uid);
		}
		return i;
	}

}
