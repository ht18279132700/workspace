<%@page import="cn.com.pcauto.wenda.util.IpUtils"%>
<%@page import="cn.com.pcauto.wenda.service.QuestionService"%>
<%@page import="cn.com.pcauto.wenda.entity.TagQuestion"%>
<%@page import="cn.com.pcauto.wenda.entity.Tag"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.springframework.jdbc.core.RowCallbackHandler"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="cn.com.pcauto.wenda.entity.Question"%>
<%@page import="org.springframework.jdbc.core.JdbcTemplate"%>
<%@page import="org.gelivable.dao.GeliDao"%>
<%@page import="org.gelivable.web.Env"%>
<%@page import="org.gelivable.web.EnvUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<!-- 
指定id区域的数据进行重新匹配标签
-->
<%
String ip = IpUtils.getIp(request);
if(!"127.0.0.1".equals(ip) && !ip.contains("192.168.")){
	out.print("permisission deny");
	return;
}

String ids = StringUtils.defaultIfBlank(request.getParameter("ids"), "");
String[] idArr = ids.split(",");
if(idArr.length != 2){
	out.print("ids参数错误");
	return;
}
long start = 0;
long end = 0;
try{
	start = Long.parseLong(idArr[0]);
	end = Long.parseLong(idArr[1]);
}catch(Exception e){
	out.print("ids参数错误");
	return;
}
if(end - start < 0){
	out.print("ids范围错误");
	return;
}
if(end - start > 10000){
	out.print("ids范围过大，一次最多处理1万条");
	return;
}
Env env = EnvUtils.getEnv();
QuestionService qs = env.getBean(QuestionService.class);
GeliDao dao = env.getBean(GeliDao.class);
JdbcTemplate jt = dao.getJdbcTemplate();

final Map<Long, String> allTags = new HashMap<Long, String>();
jt.query("select id,keywords from wd_tag", new RowCallbackHandler(){
	public void processRow(ResultSet rs) throws SQLException {
		allTags.put(rs.getLong(1), rs.getString(2));
	}
});

List<Question> list = dao.list(Question.class, "select id from wd_question where agent=-1 and id>=? and id<=?", start, end);

for(Question q : list){
	if(q == null) continue;
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
out.print("匹配完成");
%>