package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;
import org.gelivable.dao.Transient;
import org.gelivable.web.EnvUtils;

import cn.com.pcauto.wenda.service.AnswerContentService;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.DateUtils;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 回复实体类
 */
@Entity(tableName = "wd_reply", split = "Mod:beRepliedAid:50")
public class Reply {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "be_replied_aid")
	private long beRepliedAid;
	@Column(name = "be_replied_rid")
	private long beRepliedRid;
	@Column(name = "be_replied_uid")
	private long beRepliedUid;
	@Column(name = "qid")
	private long qid;
	@Column(name = "content")
	private String content;
	@Column(name = "has_more_content")
	private int hasMoreContent;
	@Column(name = "status")
	private int status;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_at")
	private Date updateAt;
	@Column(name = "agent")
	private int agent;
	
	@Transient
	private String moreContent;
	@Transient
	private User user;
	@Transient
	private User beRepliedUser;
	@Transient
	private Answer answer;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBeRepliedAid() {
		return beRepliedAid;
	}
	public void setBeRepliedAid(long beRepliedAid) {
		this.beRepliedAid = beRepliedAid;
	}
	public long getBeRepliedRid() {
		return beRepliedRid;
	}
	public void setBeRepliedRid(long beRepliedRid) {
		this.beRepliedRid = beRepliedRid;
	}
	public long getBeRepliedUid() {
		return beRepliedUid;
	}
	public void setBeRepliedUid(long beRepliedUid) {
		this.beRepliedUid = beRepliedUid;
	}
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public String getContent() {
		if(id > 0 && hasMoreContent == 1 && StringUtils.isBlank(moreContent)){
			AnswerContentService answerContentService = EnvUtils.getEnv().getBean(AnswerContentService.class);
			moreContent = answerContentService.getContent(id);
			content += moreContent;
		}
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getHasMoreContent() {
		return hasMoreContent;
	}
	public void setHasMoreContent(int hasMoreContent) {
		this.hasMoreContent = hasMoreContent;
	}
	public String getMoreContent() {
		return moreContent;
	}
	public void setMoreContent(String moreContent) {
		this.moreContent = moreContent;
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
	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	public String getShowCreateAt(){
		return DateUtils.format(createAt);
	}
	
	@JSONField(serialize=false)
	public User getUser(){
		if (createBy > 0 && user == null) {
			UserService userService = EnvUtils.getEnv().getBean(UserService.class);
			user = userService.findById(createBy);
		}
		return user;
	}
	
	@JSONField(serialize=false)
	public User getBeRepliedUser(){
		if (beRepliedUid > 0) {
			UserService userService = EnvUtils.getEnv().getBean(UserService.class);
			beRepliedUser = userService.findById(beRepliedUid);
		}
		return beRepliedUser;
	}
	
	@JSONField(serialize=false)
	public Answer getAnswer(){
		if(beRepliedAid > 0 && qid > 0 && answer == null){
			AnswerService answerService = EnvUtils.getEnv().getBean(AnswerService.class);
			answer = answerService.findById(qid, beRepliedAid);
		}
		return answer;
	}
}
