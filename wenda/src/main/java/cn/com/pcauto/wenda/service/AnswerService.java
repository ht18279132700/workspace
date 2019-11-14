package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.List;

import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.TagQuestion;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.entity.UserAnswer;
import cn.com.pcauto.wenda.entity.UserStat;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.Pager;

public class AnswerService extends BasicService<Answer> {
	
	@Autowired
	private QuestionService questionService;
	@Autowired
	private TagQuestionService tagQuestionService;
	@Autowired
	private AnswerContentService answerContentService;
	@Autowired
	private QuestionPhotoService questionPhotoService;
	@Autowired
	private UserAnswerService userAnswerService;
	@Autowired
	private UserStatService userStatService;
	@Autowired
	private SystemConfig systemConfig;
	
	public AnswerService(){
		super(Answer.class);
	}
	
	public Answer create(User user, Question question, Answer answer){
		return create(user, question, answer, null);
	}
	
	public Answer create(User user, Question question, Answer answer, List<QuestionPhoto> photoList){
		answer.setQid(question.getId());
		answer.setCreateBy(user.getUid());
		
		String content = answer.getContent();
		if(content.length() > 128){
			answer.setContent(content.substring(0, 128));
			answer.setHasMoreContent(1);
		}
		if(photoList != null){
			answer.setImageNum(photoList.size());
		}
		if(answer.getCreateAt() == null){
			answer.setCreateAt(new Date());
		}
		
		long aid = super.create(answer);
		answer.setId(aid);
		
		UserAnswer userAnswer = new UserAnswer();
		BeanUtils.copyProperties(answer, userAnswer);
		userAnswerService.create(userAnswer);
		
		if(answer.getHasMoreContent() == 1){
			answerContentService.create(aid, content.substring(128));
		}
		
		if(answer.getImageNum() > 0){
			questionPhotoService.create(photoList, answer.getQid(), aid);
		}
		
		if(systemConfig.getCensorType() == 0 || answer.getStatus() == Const.STATUS_PASS){
			questionService.updateAnswerNumAndTime(question, answer.getCreateAt());
			tagQuestionService.updateAnswerNumAndTime(question.getTagQuestion(question.getTagList()), answer.getCreateAt());
			userStatService.updateAnswerNumAndTime(user.getUid(), answer.getCreateAt());
		}
		
		return answer;
	}
	
	public Answer getMostPraiseAnswer(Question question){
		long qid = question.getId();
		String key = "MostPraiseAnswer-" + qid;
		
		String json = (String)mcc.get(key);
		if(json != null){
			return JSON.parseObject(json, Answer.class);
		}
		
		Answer answer = null;
		QueryParam param = new QueryParam();
		param.and("qid", qid);
		param.orderBy("praise_num");
		param.orderBy("create_at");
		String sql = getAnswerSql(param, qid);
		List<Answer> list = list(qid, 1, 1, sql, param.getParams().toArray());
		if(list != null && list.size() > 0){
			answer = list.get(0);
		}
		if(answer != null){
			mcc.set(key, JSON.toJSONString(answer), McCacheTime.getMc10MTimes());
		}
		return answer;
	}
	
	public int incrTreadAndDecrPraise(Answer answer){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName(answer.getQid()));
		sb.append(" SET tread_num=tread_num+1, praise_num=praise_num-1 WHERE id=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), answer.getId());
		if(i > 0){
			removeFromCache(answer.getId());
		}
		return i;
	}
	
	public int incrPraiseAndDecrTread(Answer answer){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName(answer.getQid()));
		sb.append(" SET praise_num=praise_num+1, tread_num=tread_num-1 WHERE id=?");
		int i = geliDao.getJdbcTemplate().update(sb.toString(), answer.getId());
		if(i > 0){
			removeFromCache(answer.getId());
		}
		return i;
	}

	public Pager<Answer> pager(int pageNo, int pageSize, long qid){
		QueryParam param = new QueryParam();
		param.and("qid", qid);
		param.orderBy("create_at");
		String sql = getAnswerSql(param, qid);
		
		return pager(qid, pageNo, pageSize, sql, param.getParams().toArray());
	}
	public Pager<Answer> pager(int pageNo, int pageSize, long qid, QueryParam queryParam){
		String sql = getAnswerSql(queryParam, qid);
		return pager(qid, pageNo, pageSize, sql, queryParam.getParams().toArray());
	}
	
	private String getAnswerSql(QueryParam param, long qid) {
		if(systemConfig.getCensorType() == 0){ //先出后审
			param.and("status", Relation.GEQ, 0);
		}else{ //先审后出
			param.and("status", 1);
		}
		StringBuilder sb = new StringBuilder("SELECT id FROM ").append(getTableName(qid));
		sb.append(param.getWhereSql()).append(param.getOrderBy());
		return sb.toString();
	}
	
	public Answer findById(long qid,long aid){
		return find(qid, aid);
	}
	
	/**
	 * 更新审核状态
	 * @param answer
	 * @param status
	 */
	public void updateAnswerStatus(Answer answer, int status){
		if(answer == null)return;
		String key = new StringBuilder("updateAnswerStatus").append(answer.getId()).toString();
		if(!mcc.add(key, true, McCacheTime.getMc30sTimes()))return;
		
		int oldStatus = answer.getStatus();
		Date date = new Date();
		answer.setStatus(status);
		answer.setUpdateAt(date);
		update(answer, "status,updateAt");
		
		UserAnswer userAnswer = userAnswerService.findById(answer.getCreateBy(), answer.getId());
		if(userAnswer != null){
			userAnswer.setStatus(status);
			userAnswer.setUpdateAt(date);
			userAnswerService.update(userAnswer, "status,updateAt");
		}
		Question q = answer.getQuestion();
		if(systemConfig.getCensorType() == 0 
				&& status == Const.STATUS_DELETE 
				&& oldStatus == Const.STATUS_PENDING 
				|| oldStatus == Const.STATUS_PASS 
				&& status == Const.STATUS_DELETE){
			UserStat userStat = new UserStat();
			userStat.setUid(answer.getCreateBy());
			decr(userStat, "answerNum");
			if(q != null){
				decr(q, "answerNum");
				for (TagQuestion tq : q.getTagQuestion(q.getTagList())) {
					decr(tq, "answerNum");
				}
			}
		}
		if(systemConfig.getCensorType() == 1 
				&& status == Const.STATUS_PASS
				&& oldStatus == Const.STATUS_PENDING
				|| oldStatus == Const.STATUS_DELETE
				&& status == Const.STATUS_PASS){
			userStatService.updateAnswerNumAndTime(answer.getCreateBy(), answer.getCreateAt());
			if(q != null){
				questionService.updateAnswerNumAndTime(q, answer.getCreateAt());
				tagQuestionService.updateAnswerNumAndTime(q.getTagQuestion(q.getTagList()), answer.getCreateAt());
			}
		}
		mcc.delete(key);
	}

	public void updateAnswer(Answer answer, String content) {
		int oldHasMoreContent = answer.getHasMoreContent();
		if(content.length() > 128){
			answer.setContent(content.substring(0, 128));
			answer.setHasMoreContent(1);
		}else {
			answer.setContent(content);
			answer.setHasMoreContent(0);
		}
		answer.setUpdateAt(new Date());
		update(answer);
		UserAnswer userAnswer = userAnswerService.findById(answer.getCreateBy(), answer.getId());
		if(userAnswer != null){
			userAnswer.setHasMoreContent(answer.getHasMoreContent());
			userAnswer.setContent(answer.getContent());
			userAnswer.setUpdateAt(answer.getUpdateAt());
			userAnswerService.update(userAnswer);
		}
		if(oldHasMoreContent == 1){
			answerContentService.deleteContent(answer.getId());
		}
		if(answer.getHasMoreContent() == 1){
			answerContentService.create(answer.getId(), content.substring(128));
		}
	}
	
}
