package cn.com.pcauto.wenda.entity;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 手动导入改表数据，并且自动根据key关联问题表的回答（content）
 * @author user
 *
 */

@Entity(tableName = "wd_answer_relation")
public class AnswerRelation {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "keywords")
	private String keywords;
	@Column(name = "content")
	private String content;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
