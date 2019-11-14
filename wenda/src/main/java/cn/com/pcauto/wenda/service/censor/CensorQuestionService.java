package cn.com.pcauto.wenda.service.censor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.censor.CensorEntity;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.util.Const;

public class CensorQuestionService extends CensorService{
	
	private static final Logger log = LoggerFactory.getLogger(CensorQuestionService.class);
	@Autowired
	private QuestionService questionService;
	
	@Override
	public void callback(String url, int status, long version) {
		long qid = getIdFromUrl(url, "qid");
		Question question = questionService.findById(qid);
		if (question == null) {
			log.info("问题审核回调id不存在或已删除，qid={}", qid);
			return;
		}
		switch (status) {
		//审核通过
		case CENSORSYSTEM_RESPONSE_PASS:
			questionService.updateQuestionStatus(question, Const.STATUS_PASS);
			break;
		//审核不通过    
		case CENSORSYSTEM_RESPONSE_DENY:
			questionService.updateQuestionStatus(question, Const.STATUS_DELETE);
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 创建问题，问题标题和内容送审
	 */
	public void notifyCensorCreateQuestion(User user, Question question, String ip){
		sendQuestion2Censor(user, question, ip, CENSOR_ISNEW);
	}
	
	/**
	 * 修改问题，问题标题和内容送审
	 */
	public void notifyCensorUpdateQuestion(User user, Question question, String ip){
		sendQuestion2Censor(user, question, ip, CENSOR_NOTNEW);
	}
	
	public void sendQuestion2Censor(User user, Question question, String ip, int isNew){
		CensorEntity censorEntity = new CensorEntity();
		String uri = systemConfig.getRoot()+"/"+question.getId()+".html?typeId="+Const.CENSOR_TYPEID_QUESTION+"&qid="+question.getId();
		censorEntity.setTypeId(Const.CENSOR_TYPEID_QUESTION);
		censorEntity.setIp(ip);
		censorEntity.setIsNew(isNew);
		censorEntity.setTitle(question.getTitle());
		censorEntity.setUri(uri);
		censorEntity.setUserId(user.getUid());
		censorEntity.setUserName(user.getName());
		StringBuilder content = new StringBuilder();
		if (StringUtils.isBlank(question.getContent())) {
			content.append(question.getTitle());
		}else {
			content.append(question.getContent());
		}
		if (question.getImageNum() > 0) {
			List<QuestionPhoto> photos = question.getPhotos();
			if (photos != null && photos.size() > 0) {
				for (QuestionPhoto questionPhoto : photos) {
					content.append("<img src='"+questionPhoto.getUrl()+"' />");
				}
				censorEntity.setIsHtml(1);
			}
		}
		censorEntity.setContent(content.toString());
        censorEntity.setVersion(question.getCreateAt().getTime());
        sendToCreate(censorEntity);
	}
	
	@Override
	public boolean isCurrentType(String url) {
		return Const.CENSOR_TYPEID_QUESTION == getTypeId(url);
	}

	
}
