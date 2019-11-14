package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

import cn.com.pcauto.wenda.util.DateUtils;

@Entity(tableName = "wd_question_import")
public class QuestionImport {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "qid")
	private long qid;
	@Column(name = "uid")
	private long uid;
	@Column(name = "status")
	private int status;
	@Column(name = "tags")
	private String tags;
	@Column(name = "url")
	private String url;
	@Column(name = "title")
	private String title;
	@Column(name = "content")
	private String content;
	@Column(name = "question_time")
	private Date questionTime;
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
	@Column(name = "photos")
	private String photos;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
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
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getQuestionTime() {
		return questionTime;
	}
	public void setQuestionTime(Date questionTime) {
		this.questionTime = questionTime;
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
	public String getShowQuestionTime(){
		return DateUtils.formatDetail(questionTime);
	}
	public String getPhotos() {
		return photos;
	}
	public void setPhotos(String photos) {
		this.photos = photos;
	}
	
	
}
