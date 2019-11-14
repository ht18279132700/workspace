package cn.com.pcauto.wenda.service;

import java.util.Date;

import org.gelivable.dao.Mid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.Praise;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.entity.UserAnswer;
import cn.com.pcauto.wenda.entity.UserPraise;
import cn.com.pcauto.wenda.entity.UserStat;
import cn.com.pcauto.wenda.util.Const;

public class PraiseService extends BasicService<Praise> {

	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserAnswerService userAnswerService;
	@Autowired
	private UserPraiseService userPraiseService;
	@Autowired
	private UserStatService userStatService;
	
	public PraiseService(){
		super(Praise.class);
	}
	
	public Praise praise(User user, Answer answer, int action){
		if(action != Const.TREAD){
			action = Const.PRAISE;
		}
		Praise praise = findById(answer.getId(), user.getUid());
		if(praise == null){
			praise = create(user, answer, action);
		}else if(praise.getStatus() == Const.PRAISE){
			if(action == Const.PRAISE){
				praise = cancelPraise(answer, praise);
			}else{
				praise = cancelPraiseAndTread(answer, praise);
			}
		}else if(praise.getStatus() == Const.TREAD){
			if(action == Const.TREAD){
				praise = cancelTread(answer, praise);
			}else{
				praise = cancelTreadAndPraise(answer, praise);
			}
		}else {
			if (action == Const.TREAD) {
				praise = tread(answer, praise);
			}else {
				praise = praise(answer, praise);
			}
		}
		return praise;
	}

	private Praise cancelTreadAndPraise(Answer answer, Praise praise) {
		praise.setStatus(Const.PRAISE);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		
		answerService.incrPraiseAndDecrTread(answer);
		userAnswerService.incrPraiseAndDecrTread(getUserAnswer(answer));
		userStatService.incrPraiseAndDecrTread(praise.getCreateBy());

		return praise;
	}

	private Praise cancelTread(Answer answer, Praise praise) {
		praise.setStatus(0);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		decr(answer, "treadNum");
		decr(getUserAnswer(answer), "treadNum");
		decr(getUserStat(praise), "treadNum");
		return praise;
	}
	
	private Praise tread(Answer answer, Praise praise){
		praise.setStatus(Const.TREAD);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		incr(answer, "treadNum");
		incr(getUserAnswer(answer), "treadNum");
		incr(getUserStat(praise), "treadNum");
		return praise;
	}

	private Praise cancelPraiseAndTread(Answer answer, Praise praise) {
		praise.setStatus(Const.TREAD);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		
		answerService.incrTreadAndDecrPraise(answer);
		userAnswerService.incrTreadAndDecrPraise(getUserAnswer(answer));
		userStatService.incrTreadAndDecrPraise(praise.getCreateBy());
		
		return praise;
	}

	private Praise cancelPraise(Answer answer, Praise praise) {
		praise.setStatus(0);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		decr(answer, "praiseNum");
		decr(getUserAnswer(answer), "praiseNum");
		decr(getUserStat(praise), "praiseNum");
		return praise;
	}
	
	private Praise praise(Answer answer, Praise praise){
		praise.setStatus(Const.PRAISE);
		praise.setUpdateAt(new Date());
		update(praise);
		syncUserPraise(praise);
		incr(answer, "praiseNum");
		incr(getUserAnswer(answer), "praiseNum");
		incr(getUserStat(praise), "praiseNum");
		return praise;
	}

	private Praise create(User user, Answer answer, int action) {
		Praise praise = new Praise();
		praise.setCreateBy(user.getUid());
		praise.setBePraisedAid(answer.getId());
		praise.setBePraisedUid(answer.getCreateBy());
		praise.setQid(answer.getQid());
		praise.setStatus(action);
		praise.setCreateAt(new Date());
		super.create(praise);
		
		UserPraise userPraise = new UserPraise();
		BeanUtils.copyProperties(praise, userPraise);
		userPraiseService.create(userPraise);
		
		if(action == Const.PRAISE){
			incr(answer,"praiseNum");
			incr(getUserAnswer(answer),"praiseNum");
			incr(getUserStat(praise),"praiseNum");
		}else{
			incr(answer,"treadNum");
			incr(getUserAnswer(answer),"treadNum");
			incr(getUserStat(praise),"treadNum");
		}
		return praise;
	}
	
	private void syncUserPraise(Praise praise) {
		UserPraise userPraise = new UserPraise();
		BeanUtils.copyProperties(praise, userPraise);
		userPraiseService.update(userPraise);
	}
	
	private UserAnswer getUserAnswer(Answer answer){
		UserAnswer userAnswer = new UserAnswer();
		userAnswer.setId(answer.getId());
		userAnswer.setCreateBy(answer.getCreateBy());
		return userAnswer;
	}
	
	private UserStat getUserStat(Praise praise){
		UserStat userStat = new UserStat();
		userStat.setUid(praise.getCreateBy());
		return userStat;
	}
	
	public Praise findById(long aid, long createBy){
		return find(aid, new Mid(createBy, aid));
	}

}
