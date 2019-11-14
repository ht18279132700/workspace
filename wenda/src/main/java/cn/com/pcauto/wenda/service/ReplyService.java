package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.List;

import org.gelivable.param.OrderBy;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Reply;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.entity.UserAnswer;
import cn.com.pcauto.wenda.entity.UserReply;
import cn.com.pcauto.wenda.entity.UserStat;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.McCacheTime;

public class ReplyService extends BasicService<Reply> {

	@Autowired
	private ReplyContentService replyContentService;
	@Autowired
	private UserReplyService userReplyService;
	@Autowired
	private UserStatService userStatService;
	@Autowired
	private SystemConfig systemConfig;
	
	public ReplyService(){
		super(Reply.class);
	}
	
	public Reply create(User user, Answer answer, Reply newReply){
		return create(user, answer, null, newReply);
	}
	
	public Reply create(User user, Answer answer, Reply persistReply, Reply newReply){
		newReply.setCreateBy(user.getUid());
		newReply.setBeRepliedAid(answer.getId());
		if(persistReply != null){
			newReply.setBeRepliedRid(persistReply.getId());
			newReply.setBeRepliedUid(persistReply.getCreateBy());
		}else{
			newReply.setBeRepliedUid(answer.getCreateBy());
		}
		if(newReply.getCreateAt() == null){
			newReply.setCreateAt(new Date());
		}
		String content = newReply.getContent();
		if(content.length() > 128){
			newReply.setContent(content.substring(0, 128));
			newReply.setHasMoreContent(1);
		}
		long rid = super.create(newReply);
		newReply.setId(rid);
		
		UserReply userReply = new UserReply();
		BeanUtils.copyProperties(newReply, userReply);
		userReplyService.create(userReply);
		
		replyContentService.create(rid, content);
		
		if(systemConfig.getCensorType() == 0 || newReply.getStatus() == Const.STATUS_PASS){
			UserAnswer userAnswer = getUserAnswer(answer);
			if(persistReply != null){ //对回复进行回复，只对总数加1
				incr(answer, "totalReplyNum");
				incr(userAnswer, "totalReplyNum");
			}else{ //对答案回复，两个计数都加1
				incr(answer, "replyNum", "totalReplyNum");
				incr(userAnswer, "replyNum", "totalReplyNum");
			}
			userStatService.updateReplyNumAndTime(user.getUid(), newReply.getCreateAt());
		}
		
		return newReply;
		
	}
	
	private String getAnswerSql(QueryParam param, long aid) {
		if(systemConfig.getCensorType() == 0){ //先出后审
			param.and("status", Relation.GEQ, 0);
		}else{ //先审后出
			param.and("status", 1);
		}
		StringBuilder sb = new StringBuilder("SELECT id FROM ").append(getTableName(aid));
		sb.append(param.getWhereSql()).append(param.getOrderBy());
		return sb.toString();
	}
	
	public List<Reply> listReply(long aid){
		QueryParam param = new QueryParam();
		param.and("be_replied_aid", aid);
		param.orderBy("create_at", OrderBy.ASC);
		String sql = getAnswerSql(param, aid);
		return list(aid, sql, param.getParams().toArray());
	}
	
	public Reply findById(long aid,long rid){
		return find(aid, rid);
	}
	
	public void updateReplyStatus(Reply reply, int status){
		if(reply == null)return;
		String key = new StringBuilder("updateReplyStatus").append(reply.getId()).toString();
		if(!mcc.add(key, true, McCacheTime.getMc30sTimes()))return;
		
		int oldStatus = reply.getStatus();
		Date date = new Date();
		reply.setStatus(status);
		reply.setUpdateAt(date);
		update(reply, "status,updateAt");
		
		UserReply userReply = userReplyService.find(reply.getCreateBy(), reply.getId());
		if(userReply != null){
			userReply.setStatus(status);
			userReply.setUpdateAt(date);;
			userReplyService.update(userReply, "status,updateAt");
		}
		Answer answer = reply.getAnswer();
		if(systemConfig.getCensorType() == 0 
				&& status == Const.STATUS_DELETE 
				&& oldStatus == Const.STATUS_PENDING 
				|| oldStatus == Const.STATUS_PASS 
				&& status == Const.STATUS_DELETE){
			UserStat userStat = new UserStat();
			userStat.setUid(reply.getCreateBy());
			decr(userStat, "replyNum");
			if(answer != null){
				UserAnswer userAnswer = getUserAnswer(answer);
				if(reply.getBeRepliedRid() <= 0){ //回复答案
					decr(answer, "replyNum", "totalReplyNum");
					decr(userAnswer, "replyNum", "totalReplyNum");
				}else{ //回复回复
					decr(answer, "totalReplyNum");
					decr(userAnswer, "totalReplyNum");
				}
			}
		}
		if(systemConfig.getCensorType() == 1 
				&& status == Const.STATUS_PASS
				&& oldStatus == Const.STATUS_PENDING
				|| oldStatus == Const.STATUS_DELETE
				&& status == Const.STATUS_PASS){
			userStatService.updateReplyNumAndTime(reply.getCreateBy(), reply.getCreateAt());
			if(answer != null){
				UserAnswer userAnswer = getUserAnswer(answer);
				if(reply.getBeRepliedRid() <= 0){ //回复答案
					incr(answer, "replyNum", "totalReplyNum");
					incr(userAnswer, "replyNum", "totalReplyNum");
				}else{ //回复回复
					incr(answer, "totalReplyNum");
					incr(userAnswer, "totalReplyNum");
				}
			}
		}
		mcc.delete(key);
	}
	
	private UserAnswer getUserAnswer(Answer answer) {
		//准备一个临时的UserAnswer对象，对它的回复数执行加1减1操作，只要有主键和分表键即可实现
		UserAnswer userAnswer = new UserAnswer();
		userAnswer.setId(answer.getId()); //主键
		userAnswer.setCreateBy(answer.getCreateBy()); //分表键
		return userAnswer;
	}
	
}
