<%@page import="java.util.*"%>
<%@page import="cn.com.pcauto.wenda.util.IpUtils"%>
<%@page import="cn.com.pcauto.wenda.util.DateUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="cn.com.pcauto.wenda.entity.Tag"%>
<%@page import="cn.com.pcauto.wenda.entity.Question"%>
<%@page import="cn.com.pcauto.wenda.entity.TagDaily"%>
<%@page import="cn.com.pcauto.wenda.service.TagDailyService"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.springframework.jdbc.core.JdbcTemplate"%>
<%@page import="org.gelivable.dao.GeliDao"%>
<%@page import="org.gelivable.web.Env"%>
<%@page import="org.gelivable.web.EnvUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<!-- 
统计每日发布问题标签的总数
-->
<%
String ip = IpUtils.getIp(request);
if(!"127.0.0.1".equals(ip) && !ip.contains("192.168.")){
	out.print("permisission deny");
	return;
}
Env env = EnvUtils.getEnv();
GeliDao dao = env.getBean(GeliDao.class);
TagDailyService tagDailyService = env.getBean(TagDailyService.class);
JdbcTemplate jdbc = dao.getJdbcTemplate();

Date start = DateUtils.get22hourTime(-2);
Date end = DateUtils.get22hourTime(-1);

Map<String,Integer> map = new HashMap<String,Integer>();
List<Question> list = dao.list(Question.class, "SELECT id FROM wd_question WHERE create_at > ? AND create_at <= ? AND tags IS NOT NULL AND tags != '' ", start, end);
if(list != null && list.size() > 0){
	for(Question question : list){
		String[] tagArr = question.getTags().split(",");
		for(int i = 0; i < tagArr.length; i++){
			if(map.get(tagArr[i]) != null){
				Integer num = map.get(tagArr[i]);
				num++;
				map.put(tagArr[i], num);
			}else{
				map.put(tagArr[i], 1);
			}
		} 
	}
	
	Set<Entry<String,Integer>> entrySet = map.entrySet();
	if(entrySet.size() > 0){
		for (Entry<String, Integer> entry : entrySet) {
			long tid = Long.parseLong(entry.getKey().trim());
			Tag tag = dao.find(Tag.class,tid);
			jdbc.update("INSERT INTO wd_tag_daily VALUES(?,?,?,?,?)", tid, end, tag.getName(),tag.getQuestionNum(),entry.getValue());
		}
	}
	
}
tagDailyService.listHotTag(10);

out.print("统计完成");
%>