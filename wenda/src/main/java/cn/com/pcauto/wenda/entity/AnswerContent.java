package cn.com.pcauto.wenda.entity;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 回答内容实体类
 */
@Entity(tableName = "wd_answer_content", split = "Mod:aid:50")
public class AnswerContent {

	@Id
	@Column(name = "aid")
	private long aid;
	@Id
	@Column(name = "seq")
	private int seq;
	@Column(name = "content")
	private String content;
	public long getAid() {
		return aid;
	}
	public void setAid(long aid) {
		this.aid = aid;
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
