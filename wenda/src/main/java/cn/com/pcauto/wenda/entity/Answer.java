package cn.com.pcauto.wenda.entity;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.Column;
import org.gelivable.dao.Entity;
import org.gelivable.dao.Id;
import org.gelivable.dao.Transient;
import org.gelivable.web.EnvUtils;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.pcauto.wenda.service.AnswerContentService;
import cn.com.pcauto.wenda.service.PraiseService;
import cn.com.pcauto.wenda.service.QuestionPhotoService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.ReplyService;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.DateUtils;

/**
 * 回答实体类
 */
@Entity(tableName = "wd_answer",split = "Mod:qid:50")
public class Answer {

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "qid")
	private long qid;
	@Column(name = "content")
	private String content;
	@Column(name = "has_more_content")
	private int hasMoreContent;
	@Column(name = "image_num")
	private int imageNum;
	@Column(name = "status")
	private int status;
	@Column(name = "create_by")
	private long createBy;
	@Column(name = "create_at")
	private Date createAt;
	@Column(name = "update_at")
	private Date updateAt;
	
	@Column(name = "praise_num")
	private int praiseNum;        //点赞数
	
	@Column(name = "tread_num")
	private int treadNum;         //踩数
	
	@Column(name = "reply_num")
	private int replyNum;         //回复数（仅含有对答案的回复）
	
	@Column(name = "total_reply_num")
	private int totalReplyNum;    //总回复数（包含对答案的回复，还包含对回复的回复）
	
	@Column(name = "agent")
	private int agent;
	
	@Transient
	private String moreContent;
	@Transient
	private QuestionPhoto firstPhoto;
	@Transient
	private List<QuestionPhoto> photos;
	@Transient
	private String showCreateAt;
	@Transient
	private User user;
	@Transient
	private Praise praise;
	@Transient
	private List<Reply> listReply;
	@Transient
	private Question question;
	
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
	public int getImageNum() {
		return imageNum;
	}
	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
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
	public int getPraiseNum() {
		return praiseNum;
	}
	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}
	public int getTreadNum() {
		return treadNum;
	}
	public void setTreadNum(int treadNum) {
		this.treadNum = treadNum;
	}
	public int getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}
	public int getTotalReplyNum() {
		return totalReplyNum;
	}
	public void setTotalReplyNum(int totalReplyNum) {
		this.totalReplyNum = totalReplyNum;
	}
	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	
	@JSONField(serialize=false)
	public QuestionPhoto getFirstPhoto(){
		if(id > 0 && qid > 0 && firstPhoto == null){
			if(photos == null){
				QuestionPhotoService questionPhotoService = EnvUtils.getEnv().getBean(QuestionPhotoService.class);
				firstPhoto = questionPhotoService.getFirstPhoto(this);
			}else if(photos.size() > 0){
				firstPhoto = photos.get(0);
			}
		}
		return firstPhoto;
	}
	
	@JSONField(serialize=false)
	public List<QuestionPhoto> getPhotos(){
		if(id > 0 && qid > 0 && photos == null){
			QuestionPhotoService questionPhotoService = EnvUtils.getEnv().getBean(QuestionPhotoService.class);
			photos = questionPhotoService.listAnswerPhotos(this);
		}
		return photos;
	}
	
	@JSONField(serialize=false)
	public Question getQuestion(){
		if(qid > 0 && question == null){
			QuestionService questionService = EnvUtils.getEnv().getBean(QuestionService.class);
			question = questionService.findById(qid);
		}
		return question;
	}
	
	@JSONField(serialize=false)
	public User getUser(){
		if (createBy > 0 && user == null) {
			UserService userService = EnvUtils.getEnv().getBean(UserService.class);
			user = userService.findById(createBy);
		}
		return user;
	}
	
	public String getShowCreateAt(){
		return DateUtils.format(createAt);
	}
	
	/**
	 * 获取登录用户的点赞状态
	 * @return
	 */
	public int getPraiseStatus(){
		HttpServletRequest request = EnvUtils.getEnv().getRequest();
		PraiseService praiseService = EnvUtils.getEnv().getBean(PraiseService.class);
		User user = (User)request.getAttribute("user");
		if(user != null && user.getUid() > 0){
			praise = praiseService.findById(id, user.getUid());
			if (praise != null) {
				return praise.getStatus();
			}
		}
		return 0;
	}
	
	@JSONField(serialize=false)
	public List<Reply> getListReply(){
		if(id > 0 && listReply == null){
			ReplyService replyService = EnvUtils.getEnv().getBean(ReplyService.class);
			listReply = replyService.listReply(id);
		}
		return listReply;
	}
	
}
