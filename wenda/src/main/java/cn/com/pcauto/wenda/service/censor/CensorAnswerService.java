package cn.com.pcauto.wenda.service.censor;

import java.util.List;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.censor.CensorEntity;
import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.UserAnswerService;
import cn.com.pcauto.wenda.util.Const;

public class CensorAnswerService extends CensorService{
	
	private static final Logger log = LoggerFactory.getLogger(CensorAnswerService.class);
	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserAnswerService userAnswerService;
	
	@Override
	public void callback(String url, int status, long version) {
		long qid = getIdFromUrl(url, "qid");
		long aid = getIdFromUrl(url, "aid");
		Answer answer = answerService.findById(qid, aid);
		if (answer == null) {
			log.info("回答审核回调id不存在或已删除，qid={}，aid={}", qid, aid);
			return;
		}
		switch (status) {
		//审核通过
		case CENSORSYSTEM_RESPONSE_PASS:
			answerService.updateAnswerStatus(answer, Const.STATUS_PASS);
			break;
		//审核不通过
		case CENSORSYSTEM_RESPONSE_DENY:
			answerService.updateAnswerStatus(answer, Const.STATUS_DELETE);
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 创建回答，回答内容送审
	 */
	public void notifyCensorCreateAnswer(User user, Question question, Answer answer, String ip){
		sendAnswer2Censor(user, question, answer, ip, CENSOR_ISNEW);
	}
	
	/**
	 * 修改回答，回答内容送审
	 */
	public void notifyCensorUpdateAnswer(User user, Question question, Answer answer, String ip){
		sendAnswer2Censor(user, question, answer, ip, CENSOR_NOTNEW);
	}
	
	public void sendAnswer2Censor(User user, Question question, Answer answer, String ip, int isNew){
		CensorEntity censorEntity = new CensorEntity();
		String uri = systemConfig.getRoot()+"/"+answer.getQid()+".html?typeId="+Const.CENSOR_TYPEID_ANSWER+"&qid="+answer.getQid()+"&aid="+answer.getId();
		censorEntity.setTypeId(Const.CENSOR_TYPEID_ANSWER);
		censorEntity.setIp(ip);
		censorEntity.setIsNew(isNew);
		censorEntity.setTitle(systemConfig.getAppName());
		censorEntity.setUri(uri);
		censorEntity.setUserId(user.getUid());
		censorEntity.setUserName(user.getName());
		StringBuilder content = new StringBuilder(answer.getContent());
		if (answer.getImageNum() > 0) {
			List<QuestionPhoto> photos = answer.getPhotos();
			if (photos != null && photos.size() > 0) {
				for (QuestionPhoto questionPhoto : photos) {
					content.append("<img src='"+questionPhoto.getUrl()+"' />");
				}
				censorEntity.setIsHtml(1);
			}
		}
		censorEntity.setContent(content.toString());
        censorEntity.setVersion(answer.getCreateAt().getTime());
        sendToCreate(censorEntity);
	}
	
	@Override
	public boolean isCurrentType(String url) {
		return Const.CENSOR_TYPEID_ANSWER == getTypeId(url);
	}

}
