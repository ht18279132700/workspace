package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 点赞实体类
 */
@Entity(tableName = "wd_praise", split = "Mod:bePraisedAid:50")
public class Praise {

	@Id
	@Column(name = "create_by")
	private long createBy;
	
	@Id
	@Column(name = "be_praised_aid")
	private long bePraisedAid;
	
	@Column(name = "be_praised_uid")
	private long bePraisedUid;
	
	@Column(name = "qid")
	private long qid;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "create_at")
	private Date createAt;
	
	@Column(name = "update_at")
	private Date updateAt;
	
	
	public long getBePraisedAid() {
		return bePraisedAid;
	}
	public void setBePraisedAid(long bePraisedAid) {
		this.bePraisedAid = bePraisedAid;
	}
	public long getBePraisedUid() {
		return bePraisedUid;
	}
	public void setBePraisedUid(long bePraisedUid) {
		this.bePraisedUid = bePraisedUid;
	}
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
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
}
