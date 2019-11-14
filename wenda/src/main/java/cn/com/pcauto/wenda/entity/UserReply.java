package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 用户回复实体类
 */
@Entity(tableName = "wd_user_reply", split = "Mod:createBy:50")
public class UserReply {

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
	
}
