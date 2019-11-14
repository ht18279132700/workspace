package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.List;

import org.gelivable.dao.Mid;

import cn.com.pcauto.wenda.entity.TagQuestion;

public class TagQuestionService extends BasicService<TagQuestion> {

	public TagQuestionService(){
		super(TagQuestion.class);
	}
	
	public void create(List<TagQuestion> tagQuestionList){
		for (TagQuestion tagQuestion : tagQuestionList) {
			super.create(tagQuestion);
		}
	}
	
	public int updateAnswerNumAndTime(List<TagQuestion> tagQuestionList, Date lastAnswerAt){
		int rows = 0;
		for (TagQuestion tagQuestion : tagQuestionList) {
			StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName(tagQuestion.getTid()));
			sb.append(" SET answer_num=answer_num+1, last_answer_at=IF(last_answer_at > ?, last_answer_at, ?)");
			sb.append(" WHERE tid=? AND qid=?");
			int i = geliDao.getJdbcTemplate().update(sb.toString(), lastAnswerAt, lastAnswerAt, tagQuestion.getTid(), tagQuestion.getQid());
			if(i > 0){
				removeFromCache(new Mid(tagQuestion.getTid(), tagQuestion.getQid()));
			}
			rows += i;
		}
		return rows;
	}

	public void update(List<TagQuestion> tagQuestions, List<TagQuestion> oldTagQuestions) {
		delete(oldTagQuestions);
		create(tagQuestions);
	}
	
	private void delete(List<TagQuestion> tagQuestionList){
		for (TagQuestion tq : tagQuestionList) {
			geliDao.delete(tq, new Mid(tq.getTid(), tq.getQid()));
		}
	}
}
