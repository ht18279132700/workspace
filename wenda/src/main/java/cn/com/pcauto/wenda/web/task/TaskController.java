package cn.com.pcauto.wenda.web.task;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.entity.Answer;
import cn.com.pcauto.wenda.entity.AnswerRelation;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.service.AnswerRelationService;
import cn.com.pcauto.wenda.service.AnswerService;
import cn.com.pcauto.wenda.service.QuestionService;
import cn.com.pcauto.wenda.service.UserService;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.IpUtils;


@Controller
@RequestMapping(value = "/task")
public class TaskController {
	
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerRelationService answerRelationService;
	@Autowired
	private UserService userService;
	@Autowired
	private AnswerService answerService;

	@RequestMapping(value = "/answerRelation.do")
	public void AnswerRelation(HttpServletRequest request,HttpServletResponse response){
		String ip = IpUtils.getIp(request);
		if(!"127.0.0.1".equals(ip) && !ip.contains("192.168.")){
			return;
		}
		Date now = DateUtils.getNow();
		Date time = DateUtils.parseDate("2019-10-25 00:00:00");
		Date time13 = DateUtils.getSomeDateStart(time, 13);
		Date time20 = DateUtils.getSomeDateStart(time, 20);
		Date start = DateUtils.parseDate("2018-01-01 00:00:00");
		Date end = null;
		//根据任务执行的时间，判断要匹配某段时间内的问题
		if (now.getTime() > time20.getTime()) {
			start =  DateUtils.getSomeDateStart(now, -7);
			end = DateUtils.getBefore0Day();
		}else if (now.getTime() > time13.getTime() && now.getTime() <= time20.getTime()) {
			start = time;
			end = DateUtils.getBefore0Day();
		}else {
			end = time;
		}
		
		//获取用户表的ID
		List<Long> listUid = userService.listSockpuppet(10000);
		//获取关键词答案表的数据
		List<AnswerRelation> listRelations = answerRelationService.listRelations();
		//获取问题表的ID和标题
		int count = questionService.countRelationAnswerQid(start,end);
		int limit = 10000;
		for (int i = 0; i < count;) {
			Map<Long, String> map = questionService.listRelationAnswerQid(start,end,i,limit);
			for(Entry<Long, String> entry : map.entrySet()){
				String title = entry.getValue().toLowerCase();
				if (title != null) {
					for (AnswerRelation answerRelation : listRelations) {
						long id = answerRelation.getId();
						String keywords = answerRelation.getKeywords().toLowerCase();
						if (title.contains(keywords)) {
							Long qid = entry.getKey();
							Answer answer = new Answer();
							answer.setQid(qid);
							answer.setContent(answerRelation.getContent());
							answer.setAgent(Const.AGENT_SEO);
							answer.setStatus(Const.STATUS_PASS);
							Question question = questionService.findById(qid);
							//随机获取用户ID
							User user = null;
							while (user == null) {
								Long uid = listUid.get((int) (Math.random()*listUid.size()));
								user = userService.findById(uid);
							}
							//创建答案
							answer = answerService.create(user, question, answer);
							//更新问题表,关联答案的字段
							if (answer != null) {
								question.setRelateAnswerID(id);
								questionService.update(question,"relateAnswerID");
							}
							//防止频繁操作数据库。
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;
						}
					}
				}
			}
			i = i + limit; 
		}
		
	}
}
