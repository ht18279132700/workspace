package cn.com.pcauto.wenda.service.censor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.censor.CensorEntity;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Reply;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.ReplyService;
import cn.com.pcauto.wenda.service.UserReplyService;
import cn.com.pcauto.wenda.util.Const;

public class CensorReplyService extends CensorService{
	
	private static final Logger log = LoggerFactory.getLogger(CensorReplyService.class);
	
	@Autowired
	private ReplyService replyService;
	@Autowired
	private UserReplyService userReplyService;
	
	@Override
	public void callback(String url, int status, long version) {
		long qid = getIdFromUrl(url, "qid");
		long aid = getIdFromUrl(url, "aid");
		long rid = getIdFromUrl(url, "rid");
		Reply reply = replyService.findById(aid, rid);
		if (reply == null) {
			log.info("回复审核回调id不存在或已删除，qid={}，aid={}，rid={}", qid, aid, rid);
			return;
		}
		switch (status) {
		//审核通过
		case CENSORSYSTEM_RESPONSE_PASS:
			replyService.updateReplyStatus(reply, Const.STATUS_PASS);
			break;
		//审核不通过    
		case CENSORSYSTEM_RESPONSE_DENY:
			replyService.updateReplyStatus(reply, Const.STATUS_DELETE);
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 新增回复，回复内容送审
	 */
	public void notifyCensorCreateReply(User user, Question question, Reply reply, String ip){
		sendReply2Censor(user, question, reply, ip, CENSOR_ISNEW);
	}
	
	/**
	 * 修改回复，回复内容送审
	 */
	public void notifyCensorUpdateReply(User user, Question question, Reply reply, String ip){
		sendReply2Censor(user, question, reply, ip, CENSOR_NOTNEW);
	}
	
	public void sendReply2Censor(User user, Question question, Reply reply, String ip, int isNew){
		CensorEntity censorEntity = new CensorEntity();
		long qid = question.getId();
		String uri = systemConfig.getRoot()+"/"+qid+".html?typeId="+Const.CENSOR_TYPEID_REPLY+"&qid="+qid+"&aid="+reply.getBeRepliedAid()+"&rid="+reply.getId();
		censorEntity.setTypeId(Const.CENSOR_TYPEID_REPLY);
		censorEntity.setIp(ip);
		censorEntity.setIsNew(isNew);
		censorEntity.setTitle(systemConfig.getAppName());
		censorEntity.setUri(uri);
		censorEntity.setUserId(user.getUid());
		censorEntity.setUserName(user.getName());
		censorEntity.setContent(reply.getContent());
        censorEntity.setVersion(reply.getCreateAt().getTime());
        sendToCreate(censorEntity);
	}
	
	@Override
	public boolean isCurrentType(String url) {
		return Const.CENSOR_TYPEID_REPLY == getTypeId(url);
	}

}
