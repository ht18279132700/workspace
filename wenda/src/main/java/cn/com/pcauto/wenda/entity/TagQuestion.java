package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 问题标签实体类
 */
@Entity(tableName = "wd_tag_question",split = "Mod:tid:20")
public class TagQuestion implements Cloneable {

	@Id
	@Column(name = "tid")
	private long tid;
	@Id
	@Column(name = "qid")
	private long qid;
	@Column(name = "title")
	private String title;
	@Column(name = "status")
	private int status;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_at")
	private Date updateAt;
	@Column(name = "last_answer_at")
	private Date lastAnswerAt;
	@Column(name = "answer_num")
	private int answerNum;
	@Column(name = "has_content")
	private int hasContent;
	@Column(name = "image_num")
	private int imageNum;
	@Column(name = "tag_auto")
	private int tagAuto;
	@Column(name = "agent")
	private int agent;
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public Date getLastAnswerAt() {
		return lastAnswerAt;
	}
	public void setLastAnswerAt(Date lastAnswerAt) {
		this.lastAnswerAt = lastAnswerAt;
	}
	public int getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(int answerNum) {
		this.answerNum = answerNum;
	}
	public int getHasContent() {
		return hasContent;
	}
	public void setHasContent(int hasContent) {
		this.hasContent = hasContent;
	}
	public int getImageNum() {
		return imageNum;
	}
	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}
	public int getTagAuto() {
		return tagAuto;
	}
	public void setTagAuto(int tagAuto) {
		this.tagAuto = tagAuto;
	}
	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}else if(obj == this){
			return true;
		}else if(obj instanceof TagQuestion){
			TagQuestion tq = (TagQuestion)obj;
			return tq.getTid() == this.tid && tq.getQid() == this.qid && tq.getTagAuto() == this.tagAuto;
		}
		return false;
	}
	
	@Override
	public TagQuestion clone(){
		TagQuestion tagQuestion = null;
		try {
			tagQuestion = (TagQuestion)super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return tagQuestion;
	}
}
