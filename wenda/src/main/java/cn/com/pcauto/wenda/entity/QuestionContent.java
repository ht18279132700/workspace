package cn.com.pcauto.wenda.entity;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 问题内容实体类
 */
@Entity(tableName = "wd_question_content",split = "Mod:qid:20")
public class QuestionContent {

	@Id
	@Column(name = "qid")
	private long qid;
	@Id
	@Column(name = "seq")
	private int seq;
	@Column(name = "content")
	private String content;
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
