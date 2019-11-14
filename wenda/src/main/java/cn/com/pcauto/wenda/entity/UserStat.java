package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

@Entity(tableName="wd_user_stat")
public class UserStat {

	@Id
	@Column(name="uid")
	private long uid;
	
	@Column(name="question_num")
	private int questionNum;
	
	@Column(name="answer_num")
	private int answerNum;
	
	@Column(name="reply_num")
	private int replyNum;
	
	@Column(name="praise_num")
	private int praiseNum;
	
	@Column(name="tread_num")
	private int treadNum;
	
	@Column(name = "last_question_at")
	private Date lastQuestionAt;
	
	@Column(name = "last_answer_at")
	private Date lastAnswerAt;
	
	@Column(name = "last_reply_at")
	private Date lastReplyAt;
	
	@Column(name="create_at")
	private Date createAt;
	
	@Column(name="update_at")
	private Date updateAt;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(int questionNum) {
		this.questionNum = questionNum;
	}

	public int getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(int answerNum) {
		this.answerNum = answerNum;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praseNum) {
		this.praiseNum = praseNum;
	}

	public int getTreadNum() {
		return treadNum;
	}

	public void setTreadNum(int treadNum) {
		this.treadNum = treadNum;
	}

	public Date getLastQuestionAt() {
		return lastQuestionAt;
	}

	public void setLastQuestionAt(Date lastQuestionAt) {
		this.lastQuestionAt = lastQuestionAt;
	}

	public Date getLastAnswerAt() {
		return lastAnswerAt;
	}

	public void setLastAnswerAt(Date lastAnswerAt) {
		this.lastAnswerAt = lastAnswerAt;
	}

	public Date getLastReplyAt() {
		return lastReplyAt;
	}

	public void setLastReplyAt(Date lastReplyAt) {
		this.lastReplyAt = lastReplyAt;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

}
