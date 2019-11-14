package cn.com.pcauto.wenda.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;
import org.gelivable.dao.Transient;
import org.gelivable.web.EnvUtils;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.CounterService;
import cn.com.pcauto.wenda.service.QuestionContentService;
import cn.com.pcauto.wenda.service.QuestionPhotoService;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.Functions;

/**
 * 问题实体类
 */
@Entity(tableName = "wd_question")
public class Question {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "title")
	private String title;
	@Column(name = "status")
	private int status;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_at")
	private Date updateAt;
	@Column(name = "last_answer_at")
	private Date lastAnswerAt;
	@Column(name = "answer_num")
	private int answerNum;
	@Column(name = "has_content")
	private int hasContent;
	@Column(name = "image_num")
	private int imageNum;
	@Column(name = "tags")
	private String tags;
	@Column(name = "agent")
	private int agent;
	@Column(name = "relate_answer")
	private long relateAnswerID;
	
	@Transient
	private String content;
	@Transient
	private Answer mostPraiseAnswer;
	@Transient
	private List<QuestionPhoto> photos;
	@Transient
	private String showCreateAt;
	@Transient
	private User user;
	@Transient
	private int pv;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public Date getLastAnswerAt() {
		return lastAnswerAt;
	}
	public void setLastAnswerAt(Date lastAnswerAt) {
		this.lastAnswerAt = lastAnswerAt;
	}
	public int getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(int answerNum) {
		this.answerNum = answerNum;
	}
	public int getHasContent() {
		return hasContent;
	}
	public void setHasContent(int hasContent) {
		this.hasContent = hasContent;
	}
	public int getImageNum() {
		return imageNum;
	}
	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	public long getRelateAnswerID() {
		return relateAnswerID;
	}
	public void setRelateAnswerID(long relateAnswerID) {
		this.relateAnswerID = relateAnswerID;
	}
	
	public String getContent() {
		if(id > 0 && hasContent == 1 && StringUtils.isBlank(content)){
			QuestionContentService questionContentService = EnvUtils.getEnv().getBean(QuestionContentService.class);
			content = questionContentService.getContent(id);
		}
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@JSONField(serialize=false)
	public Answer getMostPraiseAnswer(){
		if(id > 0 && mostPraiseAnswer == null){
			AnswerService answerService = EnvUtils.getEnv().getBean(AnswerService.class);
			mostPraiseAnswer = answerService.getMostPraiseAnswer(this);
		}
		return mostPraiseAnswer;
	}
	public String getShowCreateAt(){
		return DateUtils.format(createAt);
	}
	public String getShowUpdateAt(){
		return DateUtils.format(updateAt);
	}
	public String getShowLastAnswerAt(){
		return DateUtils.format(lastAnswerAt);
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
	public int getPv(){
		CounterService counterService = EnvUtils.getEnv().getBean(CounterService.class);
		return counterService.getPv(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}else if(obj == this){
			return true;
		}else if(obj instanceof Question){
			Question q = (Question)obj;
			return q.getId() == this.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
	
	public List<Tag> getTagList(){
		ArrayList<Tag> list = new ArrayList<Tag>();
		String tags = this.getTags();
		if(StringUtils.isBlank(tags)){
			return list;
		}
		TagService tagService = EnvUtils.getEnv().getBean(TagService.class);
		String[] tagArr = tags.split(",");
		List<Long> tagIdList = Functions.stringArr2LongList(tagArr);
		for (Long tagId : tagIdList) {
			Tag tag = tagService.findById(tagId);
			if(tag == null){
				continue;
			}
			list.add(tag);
		}
		return list;
	}
	
	public List<TagQuestion> getTagQuestion(List<Tag> tagList){
		ArrayList<TagQuestion> list = new ArrayList<TagQuestion>();
		if(tagList == null || tagList.isEmpty()){
			return list;
		}
		
		TagQuestion tagQuestion = new TagQuestion();
		tagQuestion.setAgent(this.getAgent());
		tagQuestion.setAnswerNum(this.getAnswerNum());
		tagQuestion.setCreateAt(this.getCreateAt());
		tagQuestion.setCreateBy(this.getCreateBy());
		tagQuestion.setHasContent(this.getHasContent());
		tagQuestion.setImageNum(this.getImageNum());
		tagQuestion.setLastAnswerAt(this.getLastAnswerAt());
		tagQuestion.setQid(this.getId());
		tagQuestion.setStatus(this.getStatus());
		tagQuestion.setTitle(this.getTitle());
		tagQuestion.setUpdateAt(this.getUpdateAt());
		
		for (Tag tag : tagList) {
			if(tag == null){
				continue;
			}
			tagQuestion.setTid(tag.getId());
			tagQuestion.setTagAuto(0);
			list.add(tagQuestion);
			
			Tag parentTag = tag.getParentTag();
			if(parentTag != null){
				tagQuestion = tagQuestion.clone();
				tagQuestion.setTid(parentTag.getId());
				tagQuestion.setTagAuto(1);
				list.add(tagQuestion);
			}
			tagQuestion = tagQuestion.clone();
		}
		unRepeat(list);
		return list;
	}
	
	private void unRepeat(List<TagQuestion> list) {
		List<TagQuestion> removeTq = new ArrayList<TagQuestion>();
		for (int i = 0; i < list.size(); i++) {
			TagQuestion tq = list.get(i);
			for (int j = i+1; j < list.size(); j++) {
				TagQuestion tq2 = list.get(j);
				boolean b = tq.getTid() == tq2.getTid() && tq.getQid() == tq2.getQid();
				if(b){
					if(tq.getTagAuto() == 1){
						removeTq.add(tq);
					}else{
						removeTq.add(tq2);
					}
				}
			}
		}
		list.removeAll(removeTq);
	}
	
	@JSONField(serialize=false)
	public List<QuestionPhoto> getPhotos(){
		if(id > 0 && photos == null){
			QuestionPhotoService questionPhotoService = EnvUtils.getEnv().getBean(QuestionPhotoService.class);
			photos = questionPhotoService.listQuestionPhotos(this);
		}
		return photos;
	}
}
