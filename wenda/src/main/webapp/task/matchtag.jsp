<%@page import="cn.com.pcauto.wenda.util.T"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="org.springframework.jdbc.core.RowCallbackHandler"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="cn.com.pcauto.wenda.util.Const"%>
<%@page import="cn.com.pcauto.wenda.entity.TagQuestion"%>
<%@page import="cn.com.pcauto.wenda.service.QuestionService"%>
<%@page import="cn.com.pcauto.wenda.entity.Tag"%>
<%@page import="java.util.List"%>
<%@page import="cn.com.pcauto.wenda.entity.Question"%>
<%@page import="org.gelivable.dao.GeliDao"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.gelivable.param.OrderBy"%>
<%@page import="org.gelivable.param.Relation"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.gelivable.param.QueryParam"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.springframework.jdbc.core.JdbcTemplate"%>
<%@page import="cn.com.pcauto.wenda.util.IpUtils"%>
<%@page import="org.gelivable.web.Env"%>
<%@page import="org.gelivable.web.EnvUtils"%>
<%@page contentType="text/html; charset=UTF-8" session="false"%>
<!-- 
请求参数：
	tag（问题标签维度）：
	  all              匹配所有问题，不管这些问题之前是否已有标签
	  blank            只给没有标签的问题匹配标签（默认值）
	  
	timeAfter（问题发布时间维度）：
	  all              匹配所有问题，不管何时发布的
	  yesterday        匹配昨天发布的问题（默认值）
	  yyyyMMdd格式的日期     匹配发布时间在指定日期之后的
	  
	agent（提问来源维度）
	  all              匹配所有来源的问题
	  -1                                       只匹配SEO来源的（默认值）
	   1                                       只匹配非SEO来源的
-->
<%
String ip = IpUtils.getIp(request);
if(!"127.0.0.1".equals(ip) && !ip.contains("192.168.")){
	out.print("permisission deny");
	return;
}

String tag = StringUtils.defaultIfBlank(request.getParameter("tag"), "blank");
String timeAfter = StringUtils.defaultIfBlank(request.getParameter("timeAfter"), "yesterday");
String agent = StringUtils.defaultIfBlank(request.getParameter("agent"), "-1");

final QueryParam qp = new QueryParam();
if("blank".equals(tag)){
	qp.and("tags", "");
}
if("yesterday".equals(timeAfter)){
	qp.and("create_at", Relation.GEQ, T.getYesterday());
}else if(!"all".equals(timeAfter)){
	try{
		Date d = new SimpleDateFormat("yyyyMMdd").parse(timeAfter);
		qp.and("create_at", Relation.GEQ, d);
	}catch(Exception e){
		out.print("<b>timeAfter参数错误，可能是格式不正确，请输入yyyyMMdd格式的日期</b><br>");
		out.print(e.getMessage());
		return;
	}
}
if("-1".equals(agent)){
	qp.and("agent", -1);
}else if("1".equals(agent)){
	qp.and("agent", Relation.NEQ, -1);
}

qp.and("status", Const.STATUS_PASS);

Logger log = LoggerFactory.getLogger(this.getClass());
log.info("匹配标签任务开始执行...");

StringBuilder ctSql = new StringBuilder("select count(1) from wd_question").append(qp.getWhereSql());

Env env = EnvUtils.getEnv();
QuestionService qs = env.getBean(QuestionService.class);
GeliDao dao = env.getBean(GeliDao.class);
JdbcTemplate jt = dao.getJdbcTemplate();

long total = jt.queryForLong(ctSql.toString(), qp.getParams().toArray());
log.info("待处理数据总数是: {}", total);
if(total == 0){
	log.info("没有待处理数据，程序退出！");
	out.print("没有待处理数据!");
	return;
}

final Map<Long, String> allTags = new HashMap<Long, String>();
String tagSql = "select id,keywords from wd_tag";
jt.query(tagSql, new RowCallbackHandler(){
	public void processRow(ResultSet rs) throws SQLException {
		allTags.put(rs.getLong(1), rs.getString(2));
	}
});

int perSize = 10000;
long fenpian = total/perSize + (total%perSize == 0 ? 0 : 1);

for(long j = 0; j < fenpian; j++){
	StringBuilder sb = new StringBuilder("select id from wd_question")
	.append(qp.getWhereSql()).append(" limit ")
	.append(j*perSize).append(",").append(perSize);
	List<Question> list = dao.list(Question.class, sb.toString(), qp.getParams().toArray());
	for(Question q : list){
		StringBuilder tagIds = new StringBuilder();
		for(long tid : allTags.keySet()){
			String keywords = allTags.get(tid);
			if(StringUtils.isBlank(keywords)) continue;
			
			int mat = 0;
			String[] ksp = keywords.split(",");
			for(String k : ksp){
				if(q.getTitle().toUpperCase().contains(k.toUpperCase())) mat++;
			}
			if(mat == ksp.length) tagIds.append(tid).append(",");
		}
		if(tagIds.length() > 0){
			tagIds.deleteCharAt(tagIds.length() - 1);
		}
		
		List<Tag> oldTags = q.getTagList();
		List<TagQuestion> oldTagQuestions = q.getTagQuestion(oldTags);
		
		q.setTags(tagIds.toString());
		qs.updateQuestionAndTagQuestion(q, oldTags, oldTagQuestions);
	}
}

log.info("匹配标签任务执行结束。。。");
out.print("匹配标签任务执行结束。。。");
%>