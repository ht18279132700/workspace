package cn.com.pcauto.wenda.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;

public class QuestionPhotoService extends BasicService<QuestionPhoto> {

	public QuestionPhotoService(){
		super(QuestionPhoto.class);
	}
	
	public void create(List<QuestionPhoto> photoList, long qid){
		create(photoList, qid, 0);
	}
	
	public void create(List<QuestionPhoto> photoList, long qid, long aid){
		if(photoList == null || photoList.isEmpty()){
			return;
		}
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName(qid));
		sql.append("(qid,aid,seq,url,width,height,size,status,create_at) ");
		sql.append("values(?,?,?,?,?,?,?,?,?)");
		
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i=0; i < photoList.size(); i++) {
			QuestionPhoto photo = photoList.get(i);
			List<Object> list = new ArrayList<Object>();
			list.add(qid);
			list.add(aid);
			list.add(i+1);
			list.add(photo.getUrl());
			list.add(photo.getWidth());
			list.add(photo.getHeight());
			list.add(photo.getSize());
			list.add(photo.getStatus());
			list.add(photo.getCreateAt() != null ? photo.getCreateAt() : new Date());
			params.add(list.toArray());
		}
		geliDao.getJdbcTemplate().batchUpdate(sql.toString(), params);
	}
	
	public QuestionPhoto getFirstPhoto(Question question){
		List<QuestionPhoto> photos = listQuestionPhotos(question, 1);
		if(photos != null && photos.size() > 0){
			return photos.get(0);
		}
		return null;
	}
	
	public List<QuestionPhoto> listQuestionPhotos(Question question){
		return listQuestionPhotos(question, 0);
	}
	
	public List<QuestionPhoto> listQuestionPhotos(Question question, int limit){
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("SELECT qid,aid,seq FROM ");
		sb.append(getTableName(question.getId())).append(" WHERE qid=? AND aid=0 ORDER BY seq");
		params.add(question.getId());
		if(limit > 0){
			sb.append(" LIMIT ?");
			params.add(limit);
		}
		return list(question.getId(), sb.toString(), params.toArray());
	}
	
	public QuestionPhoto getFirstPhoto(Answer answer){
		List<QuestionPhoto> photos = listAnswerPhotos(answer, 1);
		if(photos != null && photos.size() > 0){
			return photos.get(0);
		}
		return null;
	}
	
	public List<QuestionPhoto> listAnswerPhotos(Answer answer){
		return listAnswerPhotos(answer, 0);
	}
	
	public List<QuestionPhoto> listAnswerPhotos(Answer answer, int limit){
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("SELECT qid,aid,seq FROM ");
		sb.append(getTableName(answer.getQid())).append(" WHERE qid=? AND aid=? ORDER BY seq");
		params.add(answer.getQid());
		params.add(answer.getId());
		if(limit > 0){
			sb.append(" LIMIT ?");
			params.add(limit);
		}
		return list(answer.getQid(),sb.toString(), params.toArray());
	}
}
