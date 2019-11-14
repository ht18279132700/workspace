package cn.com.pcauto.wenda.entity;

import java.util.Date;

import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;
import org.gelivable.dao.Transient;
import org.gelivable.web.EnvUtils;

import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.TagType;

@Entity(tableName = "wd_tag")
public class Tag implements Comparable<Tag> {
	
	public static final int STATUS_PASS = 0;     // 正常状态
	public static final int STATUS_DELETE = -1;  // 禁用状态
	
	public static final long OTHER = 3364;       // 标签【其他】的标签ID
	
	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "tag_type")
	private String tagType;
	@Column(name = "name")
	private String name;
	@Column(name = "keywords")
	private String keywords;
	@Column(name = "pid")
	private long pid;
	@Column(name = "seq")
	private int seq;
	@Column(name = "letter")
	private String letter;
	@Column(name = "brand_id")
	private long brandId;
	@Column(name = "serial_id")
	private long serialId;
	@Column(name = "status")
	private int status;
	@Column(name = "question_num")
	private int questionNum;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_by")
	private long updateBy;
	@Column(name = "update_at")
	private Date updateAt;
	
	/*
	 * 2019-07-26 新增标签相关性字段，用定时任务匹配标签之间的相关性，然后存入此字段
	 */
	@Column(name = "correlation")
	private String correlation;
	
	@Transient
	private Tag parentTag;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTagType() {
		return tagType;
	}
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public long getBrandId() {
		return brandId;
	}
	public void setBrandId(long brandId) {
		this.brandId = brandId;
	}
	public long getSerialId() {
		return serialId;
	}
	public void setSerialId(long serialId) {
		this.serialId = serialId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getQuestionNum() {
		return questionNum;
	}
	public void setQuestionNum(int questionNum) {
		this.questionNum = questionNum;
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
	
	public String getCorrelation() {
		return correlation;
	}
	
	public void setCorrelation(String correlation) {
		this.correlation = correlation;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}else if(obj == this){
			return true;
		}else if(obj instanceof Tag){
			Tag tag = (Tag)obj;
			return tag.getId() == this.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
	
	public TagType getTagTypeEnum(){
		TagType[] values = TagType.values();
		for (TagType tagType : values) {
			if(this.getTagType().equals(tagType.getName())){
				return tagType;
			}
		}
		return null;
	}
	
	public Tag getParentTag(){
		if(pid > 0 && parentTag == null){
			TagService tagService = EnvUtils.getEnv().getBean(TagService.class);
			parentTag = tagService.findById(pid);
		}
		return parentTag;
	}
	
//	@Override
//	public int compareTo(Tag t) {
//		if(t == null){
//			return -1;
//		}
//		int result = 0;
//		TagType type1 = this.getTagTypeEnum();
//		TagType type2 = t.getTagTypeEnum();
//		if(type1 != null && type2 != null){
//			result = type1.getPriority() - type2.getPriority();
//		}
//		if(result == 0){
//			result = this.getSeq() - t.getSeq();
//		}
//		return result;
//	}
	
	/**
	 * 2019-07-16 标签排序规则修改
	 * 1、标签名称字数多的优先
	 * 2、字数相同时，标签下的问答数量多的优先
	 */
	@Override
	public int compareTo(Tag t) {
		if(t == null){
			return -1;
		}
		int result = t.name.length() - name.length();
		if(result == 0){
			result = t.questionNum - questionNum;
		}
		return result;
	}
}
