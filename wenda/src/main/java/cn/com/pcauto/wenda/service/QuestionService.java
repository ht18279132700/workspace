package cn.com.pcauto.wenda.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlBuilder;
import org.gelivable.dao.SqlPageBuilder;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Counter;
import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.entity.TagQuestion;
import cn.com.pcauto.wenda.entity.User;
import cn.com.pcauto.wenda.entity.UserStat;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.DateUtils;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.McUtils;
import cn.com.pcauto.wenda.util.Pager;
import cn.pconline.r.client.RClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class QuestionService extends BasicService<Question> {

	@Autowired
	private TagQuestionService tagQuestionService;
	@Autowired
	private QuestionContentService QuestionContentService;
	@Autowired
	private QuestionPhotoService questionPhotoService;
	@Autowired
	private UserStatService userStatService;
	@Autowired
	private TagService tagService;
	@Autowired
	private CounterService counterService;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private SqlPageBuilder sqlPageBuilder;
	@Autowired
	private RClient rClient;
	
	private static final String HotQuestion = "HotQuestion-";
	private static final String EqTagQuestion = "EqTagQuestion-";
	private static final String RelateQuestion = "RelateQuestion-";
	private static final String NotTagQuestion = "NotTagQuestion";
	private static final String CountRandomQuestion = "CountRandomQuestion";
	
	public QuestionService(){
		super(Question.class);
	}
	
	public Question create(User user, Question question){
		return create(user, question, null);
	}
	
	public Question create(User user, Question question, List<QuestionPhoto> photoList){
		String content = question.getContent();
		if(StringUtils.isNotBlank(content)){
			question.setHasContent(1);
		}
		if(photoList != null){
			question.setImageNum(photoList.size());
		}
		if(question.getCreateAt() == null){
			question.setCreateAt(new Date());
		}
		List<Tag> tagList = question.getTagList();
		Collections.sort(tagList);
		question.setTags(Functions.getTagIdStr(tagList));
		question.setCreateBy(user.getUid());
		
		long qid = super.create(question);
		question.setId(qid);
		
		List<TagQuestion> tagQuestionList = question.getTagQuestion(tagList);
		tagQuestionService.create(tagQuestionList);
		
		if(question.getHasContent() == 1){
			QuestionContentService.create(question.getId(), question.getContent());
		}
		if(question.getImageNum() > 0){
			questionPhotoService.create(photoList, qid);
		}
		counterService.create(new Counter(qid, 0));
		
		if(systemConfig.getCensorType() == 0 || question.getStatus() == Const.STATUS_PASS){
			tagService.incrQuestionNum(tagList);
			userStatService.updateQuestionNumAndTime(user.getUid(), question.getCreateAt());
		}
		
		return question;
	}
	
	public int updateAnswerNumAndTime(Question question, Date lastAnswerAt){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET answer_num=answer_num+1, last_answer_at=IF(last_answer_at > ?, last_answer_at, ?)");
		sb.append(" WHERE id=?");
		int rows = geliDao.getJdbcTemplate().update(sb.toString(), lastAnswerAt, lastAnswerAt, question.getId());
		if(rows > 0){
			removeFromCache(question.getId());
		}
		return rows;
	}
	
	
	private String getTagQuestionSql(QueryParam param, long tagId) {
		if(systemConfig.getCensorType() == 0){ //先出后审
			param.and("status", Relation.GEQ, Const.STATUS_PENDING);
		}else{ //先审后出
			param.and("status", Const.STATUS_PASS);
		}
		StringBuilder sb = new StringBuilder("SELECT qid FROM ");
		sb.append(geliOrm.getTableName(TagQuestion.class, tagId));
		sb.append(param.getWhereSql()).append(param.getOrderBy());
		return sb.toString();
	}
	
	private String getQuestionSql(QueryParam param) {
		if(systemConfig.getCensorType() == 0){ //先出后审
			param.and("status", Relation.GEQ, Const.STATUS_PENDING);
		}else{ //先审后出
			param.and("status", Const.STATUS_PASS);
		}
		StringBuilder sb = new StringBuilder("SELECT id FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		return sb.toString();
	}
	
	private String getQuestionCount(QueryParam param) {
		if(systemConfig.getCensorType() == 0){ //先出后审
			param.and("status", Relation.GEQ, Const.STATUS_PENDING);
		}else{ //先审后出
			param.and("status", Const.STATUS_PASS);
		}
		StringBuilder sb = new StringBuilder("SELECT count(1) FROM ");
		sb.append(getTableName()).append(param.getWhereSql());
		return sb.toString();
	}
	
	public Pager<Question> pager(QueryParam param, int pageNo, int pageSize){
		String sql = getQuestionSql(param);
		return pager(pageNo, pageSize, sql, param.getParams().toArray());
	}
	
	public List<Question> list(QueryParam param){
		return list(param, 0); //传0，查询所有
	}
	
	public List<Question> list(QueryParam param, int limit){
		return list(param, 1, limit);
	}
	
	public List<Question> list(QueryParam param, int pageNo, int pageSize){
		String sql = getQuestionSql(param);
		if(pageNo <= 0){
			pageNo = 1;
		}
		if(pageSize > 0){
			sql = sqlPageBuilder.buildPageSql(sql, pageNo, pageSize);
		}
		return list(sql, param.getParams().toArray());
	}
	
	/**
	 * 按问题发布时间倒序显示回答量大于等于 answerNum 个的问答
	 * @param pageNo
	 * @param pageSize
	 * @param answerNum
	 * @return
	 */
	public Pager<Question> pagerForANum(int pageNo, int pageSize, int answerNum){
		QueryParam param = new QueryParam();
		param.and("answer_num", Relation.GEQ, answerNum);
		param.orderBy("create_at");
		return pager(param, pageNo, pageSize);
	}
	/**
	 * 随机显示浏览量前 sum 且有答案的 limit 个问答
	 * @param limit
	 * @return
	 */
	public List<Question> listForPV(int sum, int limit){
		McUtils hotMC = new McUtils(mcc, HotQuestion);
		List<Question> list = hotMC.getList(Question.class);
		if (list == null) {
			Set<Question> set = new HashSet<Question>();
			int i = 1;
			while (set.size() < sum) {
				List<Question> hotQuestions = listHotQuestions(i, sum*2);
				if(hotQuestions == null || hotQuestions.isEmpty()){
					break;
				}
				for (Question question : hotQuestions) {
					if (question.getAnswerNum() > 0 && question.getStatus() == 1) {
						set.add(question);
					}
					if (set.size() >= sum) {
						break;
					}
				}
				i++;
			}
			if(set.size() > 0){
				list = new ArrayList<Question>();
				list.addAll(set);
				hotMC.set(list, McCacheTime.getMc1HTimes());
			}
		}
		if(list == null){
			return Collections.emptyList();
		}
		list = Functions.randomInList(list, limit);
		sortQuestions(list);
		return list;
	}
	/**
	 * 获取热门问题
	 * @param sum
	 * @return
	 */
	private List<Question> listHotQuestions(int pageNo, int pageSize){
		StringBuilder sql = new StringBuilder("SELECT qid FROM ");
		sql.append(geliOrm.getTableName(Counter.class)).append(" ORDER BY pv DESC");
		return list(pageNo, pageSize, sql.toString());
	}
	
	private void sortQuestions(List<Question> list){
		Collections.sort(list, new Comparator<Question>(){
			@Override
			public int compare(Question o1, Question o2) {
				long result = o2.getCreateAt().getTime() - o1.getCreateAt().getTime();
				return (int)(result/1000);
			}
		});
	}
	private void sortQuestionNum(List<Question> list){
		Collections.sort(list, new Comparator<Question>(){
			@Override
			public int compare(Question o1, Question o2) {
				int  result = o2.getAnswerNum() - o1.getAnswerNum();
				return result;
			}
		});
	}
	
	/**
	 * 按问题发布时间倒序显示有该话题标签的所有问题
	 * @param pageNo
	 * @param pageSize
	 * @param tagId
	 * @return
	 */
	public Pager<Question> pagerTagQuestion(int pageNo, int pageSize, long tagId){
		QueryParam param = new QueryParam();
		param.and("tid", tagId);
		param.orderBy("create_at");
		String sql = getTagQuestionSql(param, tagId);
		return pager(tagId, pageNo, pageSize, sql, param.getParams().toArray());
	}
	/**
	 * 后台查询标签下的问题页面
	 * @param param
	 * @param tid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pager<Question> pagerTagQuestion(QueryParam param, long tid, int pageNo, int pageSize) {
		String sql = getTagQuestionSql(param, tid);
		return pager(tid, pageNo, pageSize, sql, param.getParams().toArray());
	}
	
	public List<Question> listSearchQuestion(List<Long> qids){
		QueryParam param = new QueryParam();
		param.andIn("id", qids);
		return list(param);
	}
	
	/**
	 * 获取指定tag的num个问答
	 * @param tag
	 * @param limit
	 * @return
	 */
	public List<Question> listRelevantQuestionByTag(Tag tag, int num){
		QueryParam param = new QueryParam();
		param.and("tid", tag.getId());
		param.and("status", Const.STATUS_PASS);
		param.orderBy("last_answer_at");
		param.orderBy("create_at");
		String sql = getTagQuestionSql(param, tag.getId());
		return list(1, num, sql, param.getParams().toArray());

	}
	
	/**
	 * 问题详情页的相关问题
	 * @param question
	 * @param limit
	 * @return
	 */
	public List<Question> listRelateQuestion(Question question,int limit){
		List<Long> qidTags = listQidsForTags(question,limit);
		Set<Long> set = new HashSet<Long>();//相关Qids问题的结果
		set.addAll(qidTags);
		if (set.size() < limit) {
			List<Long> qidTag = listQidsForTag(question,limit,qidTags);
			List<Long> randomQidTag = Functions.randomInList(qidTag, limit - set.size());
			set.addAll(randomQidTag);
			if (set.size() < limit) {
				List<Long> notTagQids = limitQuestionNotTag(question);
				List<Long> randomnotTagQids = Functions.randomInList(notTagQids, limit - set.size());
				set.addAll(randomnotTagQids);
			}
			
		}
		List<Question> list = list(set.toArray());
		sortQuestionNum(list);
		return list;
	}
	/**
	 * 查询tags相同的问题id,并缓存1小时的数据
	 * @param question
	 * @param limit
	 * @return
	 */
	public List<Long> listQidsForTags(Question question,int limit){
		if (StringUtils.isBlank(question.getTags())) {
			return new ArrayList<Long>();
		}
		McUtils mcUtils = new McUtils(mcc,EqTagQuestion + question.getId());
		List<Long> qidList = mcUtils.getList(Long.class);
		if (qidList == null) {
			qidList = new ArrayList<Long>();
			List<Question> list = listQuestionForTags(question, limit);
			qidList = Functions.getQuestionIdList(list);
			mcUtils.set(qidList, McCacheTime.getMc1HTimes());
		}
		return qidList;
	}
	/**
	 * 获取该问题的相关问题ids,个数为limit*该问题的标签数量
	 * @param question
	 * @param limit
	 * @return
	 */
	public List<Long> listQidsForTag(Question question, int limit,List<Long> notQid){
		McUtils mcUtils = new McUtils(mcc, RelateQuestion + question.getId());
		List<Long> qidList = mcUtils.getList(Long.class);
		if (qidList == null) {
			qidList = new ArrayList<Long>();
			List<Tag> tagList = question.getTagList();//该问题的标签
			List<Question> listTagQuestion = new ArrayList<Question>();//存储该问题的标签问题
			
			List<Long> notQids = new ArrayList<Long>();//排除上面符合要求的问题
			notQids.add(question.getId());
			if (notQid != null) {
				notQids.addAll(notQid);
			}
			for (Tag tag : tagList) {
				listTagQuestion.addAll(listTagQuestion(tag, limit, notQids));
			}
			qidList = Functions.getQuestionIdList(listTagQuestion);
			mcUtils.set(qidList, McCacheTime.getMc1DTimes());
		}
		return qidList;
	}
	
	/**
	 *获取200个没有标签的问题id
	 * @return
	 */
	public List<Long> limitQuestionNotTag(Question question){
		McUtils mcUtils = new McUtils(mcc, NotTagQuestion);
		List<Long> qidList = mcUtils.getList(Long.class);
		if (qidList == null) {
			qidList = new ArrayList<Long>();
			List<Question> listQuestionNotTag = listQuestionNotTag(question);
			qidList = Functions.getQuestionIdList(listQuestionNotTag);
			mcUtils.set(qidList, McCacheTime.getMc1DTimes());
		}
		return qidList;
	}
	
	/**
	 * 获取没有问题标签的前200个问题
	 * @return
	 */
	public List<Question> listQuestionNotTag(Question question){
		QueryParam param = new QueryParam();
		param.and("id", Relation.NEQ, question.getId());
		param.and("tags","");
		param.and("status",Const.STATUS_PASS);
		param.and("answer_num", Relation.GEQ, 1);
		param.orderBy("create_at");
		return list(param, Const.NOT_TAG_QUESTION);
	}
	/**
	 * 查询tags相同的问题
	 * @param question
	 * @param limit
	 * @return
	 */
	public List<Question> listQuestionForTags(Question question,int limit){
		QueryParam param = new QueryParam();
		param.and("tags", question.getTags());
		param.and("status",Const.STATUS_PASS);
		param.and("id", Relation.NEQ, question.getId());
		param.orderBy("answer_num");
		return list(param, limit);
	}
	/**
	 * 查询有一个标签相同的相关问题
	 * @param tag
	 * @param limit
	 * @param ids 排除qid=ids的问题
	 * @return
	 */
	public List<Question> listTagQuestion(Tag tag, int limit, List<Long> qids){
		QueryParam param = new QueryParam();
		param.and("tid", tag.getId());
		param.andNotIn("qid", qids);
		param.and("status",Const.STATUS_PASS);
		param.orderBy("answer_num");
		String sql = getTagQuestionSql(param, tag.getId());
		return list(tag.getId(), 1, limit, sql, param.getParams().toArray());
	}
	
	public void updateQuestionStatus(Question question, int status){
		if(question == null)return;
		String key = new StringBuilder("updateQuestionStatus").append(question.getId()).toString();
		if(!mcc.add(key, true, McCacheTime.getMc30sTimes()))return;
		
		int oldStatus = question.getStatus();
		question.setStatus(status);
		question.setUpdateAt(new Date());
		update(question);
		
		List<Tag> tagList = question.getTagList();
		for(TagQuestion tq : question.getTagQuestion(tagList)){
			tagQuestionService.update(tq);
		}
		if(systemConfig.getCensorType() == 0 
				&& status == Const.STATUS_DELETE
				&& oldStatus == Const.STATUS_PENDING 
				|| oldStatus == Const.STATUS_PASS 
				&& status == Const.STATUS_DELETE){
			tagService.decrQuestionNum(tagList);
			UserStat userStat = new UserStat();
			userStat.setUid(question.getCreateBy());
			decr(userStat, "questionNum");
		}
		if(systemConfig.getCensorType() == 1 
				&& status == Const.STATUS_PASS
				&& oldStatus == Const.STATUS_PENDING
				|| oldStatus == Const.STATUS_DELETE
				&& status == Const.STATUS_PASS){
			tagService.incrQuestionNum(tagList);
			userStatService.updateQuestionNumAndTime(question.getCreateBy(), question.getCreateAt());
		}
		mcc.delete(key);
	}
	
	/**
	 * 更新问题，如果修改了问题的标签，请不要使用此方法，因为此方法无法更新tagQuestion
	 * @param question
	 * @return
	 */
	public boolean updateQuestion(Question question){
		long qid = question.getId();
		String content = question.getContent();
		
		if (StringUtils.isBlank(content)) {
			question.setHasContent(0);
		}else {
			question.setHasContent(1);
		}
		
		List<Tag> tagList = question.getTagList();
		Collections.sort(tagList);
		
		question.setTags(Functions.getTagIdStr(tagList));
		question.setUpdateAt(new Date());
		
		int flag = update(question);
		if (flag <= 0) {
			return false;
		}
		
		QuestionContentService.updateContent(qid, content);
		
		return true;
	}
	
	/**
	 * 更新question和tagQuestion
	 * 所谓TagQuestion的更新，其实就是要先把旧的删除，再创建新的
	 * @param question        待更新的question，该对象中的数据会覆盖数据库中question表的数据
	 * @param oldTags         question之前的旧标签，用于将旧标签的问答计数减一
	 * @param oldTagQuestions 旧的tagQuestion，需要被删除掉
	 * @return
	 */
	public boolean updateQuestionAndTagQuestion(Question question, List<Tag> oldTags, List<TagQuestion> oldTagQuestions){
		long qid = question.getId();
		String content = question.getContent();
		
		if (StringUtils.isBlank(content)) {
			question.setHasContent(0);
		}else {
			question.setHasContent(1);
		}
		
		List<Tag> tagList = question.getTagList();
		Collections.sort(tagList);
		
		question.setTags(Functions.getTagIdStr(tagList));
		question.setUpdateAt(new Date());
		
		int flag = update(question);
		if (flag <= 0) {
			return false;
		}
		
		QuestionContentService.updateContent(qid, content);
		
		tagService.decrQuestionNum(oldTags);  // 旧标签问答计数减一
		tagService.incrQuestionNum(tagList);  // 新标签问答计数加一
		
		List<TagQuestion> list = question.getTagQuestion(tagList);
		tagQuestionService.update(list, oldTagQuestions);
		
		return true;
	}
	
	public Pager<Question> pagerForKS(String url,int pageNo, int pageSize){
		String result = rClient.get(url, "wenda.pcauto.com.cn", 3000, TimeUnit.MILLISECONDS);
		int pageCount = 0;
    	int total = 0;
    	JSONArray dataArr = null;
    	List<Long> qids = new ArrayList<Long>();
    	
    	if(StringUtils.isNotBlank(result)){
    		try {
    			JSONObject jsonObject = JSONObject.parseObject(result);
    			total = jsonObject.getIntValue("total");
    			dataArr = jsonObject.getJSONArray("list");
    			pageCount = jsonObject.getIntValue("pageCount");
    			if (pageNo > pageCount) {
					pageNo = pageCount;
				}
    			if (dataArr != null && dataArr.size() > 0) {
					for (int i = 0; i < dataArr.size(); i++) {
						JSONObject object = (JSONObject) dataArr.get(i);
						Long id = object.getLong("id");
						qids.add(id);
					}
				}
			} catch (Exception e) {
				log.error("解析快搜接口返回结果失败，result="+result, e);
			}
    	}
    	
    	Pager<Question> pager = new Pager<Question>();
    	if (qids.size() > 0) {
    		List<Question> listSearchQuestion = listSearchQuestion(qids);
    		pager.setResultList(listSearchQuestion);
		}
    	pager.setPageNo(pageNo);
    	pager.setPageSize(pageSize);
    	pager.setTotal(total);
    	
    	return pager;
	}
	/**
	 * 在 random 个问题中，随机获取 limit 个问题
	 * @param random
	 * @param limit
	 * @return
	 */
	public List<Question> listRandomQuestion(Date start,Date end,int random,int limit){
		QueryParam param = new QueryParam();
		param.and("status",Const.STATUS_PASS);
		param.and("create_at", Relation.GEQ, start);
		param.and("create_at", Relation.LEQ, end);
		String sql = getQuestionSql(param);
		if (random >=0 && limit > 0) {
			sql += " LIMIT " + random + "," + limit;
		}
		return list(sql, param.getParams().toArray());
	}
	/**
	 * 获取一个星期内，问题的数量
	 * @return
	 */
	public int countRandomQuestionCache(Date start,Date end){
		Object object = mcc.get(CountRandomQuestion);
		if (object == null) {
			int count = countRandomQuestion(start,end);
			mcc.set(CountRandomQuestion, count, DateUtils.getSomeDateEnd(new Date(),0));
			return count;
		}else {
			return (Integer) object;
		}
	}
	
	private int countRandomQuestion(Date start,Date end){
		QueryParam param = new QueryParam();
		param.and("status",Const.STATUS_PASS);
		param.and("create_at", Relation.GEQ, start);
		param.and("create_at", Relation.LEQ, end);
		String sql = getQuestionCount(param);
		return geliDao.count(sql, param.getParams().toArray());
	}
	/**
	 * 每隔一段时间，将 关联答案表与问题表进行匹配。
	 * @param id
	 * @param start
	 * @param end
	 * @param keywords
	 * @param limit
	 * @return
	 */
	public Map<Long, String> listRelationAnswerQid(Date start, Date end, int first,int last){
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("SELECT id,title FROM ").appendSql(getTableName())
		.appendSql(" WHERE relate_answer = 0 AND create_at >= ").appendValue(start)
		.appendSql(" AND create_at<= ").appendValue(end)
		.appendSql(" LIMIT " + first + "," + last);
		return geliDao.getJdbcTemplate().query(sql.getSql(), sql.getValues(),new ResultSetExtractor<Map<Long,String>>(){

			@Override
			public Map<Long, String> extractData(ResultSet rs)
					throws SQLException, DataAccessException {
				Map<Long, String> map = new HashMap<Long,String>();
				while (rs.next()) {
					map.put(rs.getLong("id"), rs.getString("title"));
				}
				return map;
			}
		});
		
	}
	
	public int countRelationAnswerQid(Date start, Date end){
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("SELECT count(1) FROM ").appendSql(getTableName())
		.appendSql(" WHERE relate_answer = 0 AND create_at >= ").appendValue(start)
		.appendSql(" AND create_at<= ").appendValue(end);
		return geliDao.count(sql.getSql(), sql.getValues());
	}
	
}
