package cn.com.pcauto.wenda.entity;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 回复内容实体类
 */
@Entity(tableName = "wd_reply_content", split = "Mod:rid:50")
public class ReplyContent {

	@Id
	@Column(name = "rid")
	private long rid;
	@Id
	@Column(name = "seq")
	private int seq;
	@Column(name = "content")
	private String content;
	
	public long getRid() {
		return rid;
	}
	public void setRid(long rid) {
		this.rid = rid;
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
