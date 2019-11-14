package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;

/**
 * 问题人气值实体类
 *
 */
@Entity(tableName = "wd_counter")
public class Counter {

	@Id
	@Column(name = "qid")
	private long qid;
	@Column(name = "pv")
	private int pv;
	@Column(name = "update_at")
	private Date updateAt;
	
	public Counter(){}
	public Counter(long qid, int pv){
		this.qid = qid;
		this.pv = pv;
		this.updateAt = new Date();
	}
	public long getQid() {
		return qid;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public int getPv() {
		return pv;
	}
	public void setPv(int pv) {
		this.pv = pv;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	
}
