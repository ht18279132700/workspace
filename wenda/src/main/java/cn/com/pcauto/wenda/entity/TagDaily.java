package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;


@Entity(tableName = "wd_tag_daily")
public class TagDaily {
	
	@Id
	@Column(name = "id")
	private long id;
	@Id
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "name")
	private String name;
	@Column(name = "question_num")
	private int questionNum;
	@Column(name = "daily_num")
	private int dailyNum;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuestionNum() {
		return questionNum;
	}
	public void setQuestionNum(int questionNum) {
		this.questionNum = questionNum;
	}
	public int getDailyNum() {
		return dailyNum;
	}
	public void setDailyNum(int dailyNum) {
		this.dailyNum = dailyNum;
	}
	
	
}

