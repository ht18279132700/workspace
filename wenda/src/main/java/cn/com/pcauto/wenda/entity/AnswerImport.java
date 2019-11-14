package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

import cn.com.pcauto.wenda.util.DateUtils;

@Entity(tableName = "wd_answer_import")
public class AnswerImport {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "qiid")
	private long qiid;
	@Column(name = "aid")
	private long aid;
	@Column(name = "uid")
	private long uid;
	@Column(name = "status")
	private int status;
	@Column(name = "content")
	private String content;
	@Column(name = "answer_time")
	private Date answerTime;
	@Column(name = "nickname")
	private String nickname;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_by")
	private long updateBy;
	@Column(name = "update_at")
	private Date updateAt;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getQiid() {
		return qiid;
	}
	public void setQiid(long qiid) {
		this.qiid = qiid;
	}
	public long getAid() {
		return aid;
	}
	public void setAid(long aid) {
		this.aid = aid;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getAnswerTime() {
		return answerTime;
	}
	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public long getCreateBy() {
		return createBy;
	}
	public void setCreateBy(long createBy) {
		this.createBy = createBy;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public long getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(long updateBy) {
		this.updateBy = updateBy;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public String getShowAnswerTime(){
		return DateUtils.formatDetail(answerTime);
	}
	
}
