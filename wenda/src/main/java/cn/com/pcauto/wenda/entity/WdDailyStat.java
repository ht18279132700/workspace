package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 问答数日计
 */
@Entity(tableName="wd_daily_stat")
public class WdDailyStat {

	@Id
	@Column(name="day")
	private long day;
	
	@Column(name="seo_question_num")
	private int seoQuestionNum;
	
	@Column(name="seo_answer_num")
	private int seoAnswerNum;
	
	@Column(name="user_question_num")
	private int userQuestionNum;
	
	@Column(name="user_answer_num")
	private int userAnswerNum;
	
	@Column(name="user_reply_num")
	private int userReplyNum;
	
	@Column(name="create_at")
	private Date createAt;

	public long getDay() {
		return day;
	}

	public void setDay(long day) {
		this.day = day;
	}

	public int getSeoQuestionNum() {
		return seoQuestionNum;
	}

	public void setSeoQuestionNum(int seoQuestionNum) {
		this.seoQuestionNum = seoQuestionNum;
	}

	public int getSeoAnswerNum() {
		return seoAnswerNum;
	}

	public void setSeoAnswerNum(int seoAnswerNum) {
		this.seoAnswerNum = seoAnswerNum;
	}

	public int getUserQuestionNum() {
		return userQuestionNum;
	}

	public void setUserQuestionNum(int userQuestionNum) {
		this.userQuestionNum = userQuestionNum;
	}

	public int getUserAnswerNum() {
		return userAnswerNum;
	}

	public void setUserAnswerNum(int userAnswerNum) {
		this.userAnswerNum = userAnswerNum;
	}

	public int getUserReplyNum() {
		return userReplyNum;
	}

	public void setUserReplyNum(int userReplyNum) {
		this.userReplyNum = userReplyNum;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
}
