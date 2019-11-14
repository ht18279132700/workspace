<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.springframework.jdbc.core.RowCallbackHandler"%>
<%@page import="cn.com.pcauto.wenda.util.IpUtils"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="cn.com.pcauto.wenda.util.Functions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.danga.MemCached.MemCachedClient"%>
<%@page import="org.springframework.jdbc.core.JdbcTemplate"%>
<%@page import="org.gelivable.web.Env"%>
<%@page import="org.gelivable.web.EnvUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<!--
标签相关性匹配
-->
<%
String ip = IpUtils.getIp(request);
if(!"127.0.0.1".equals(ip) && !ip.contains("192.168.")){
	out.print("permisission deny");
	return;
}

Env env = EnvUtils.getEnv();
MemCachedClient mcc = env.getBean(MemCachedClient.class);
JdbcTemplate jt = env.getBean(JdbcTemplate.class);

final Map<Long, String> allTags = new HashMap<Long, String>();
String tagSql = "select id,keywords from wd_tag where question_num > 0";
jt.query(tagSql, new RowCallbackHandler(){
	public void processRow(ResultSet rs) throws SQLException {
		allTags.put(rs.getLong(1), rs.getString(2));
	}
});

int maxSize = 12;

String matSql = "select id from wd_tag where question_num > 0 and id<>? and locate(?,keywords)>0";

for(long id : allTags.keySet()){
	Set<Long> ids = new HashSet<Long>();
	String keywords = allTags.get(id);
	keywords = keywords == null ? "" : keywords;
	String[] ksp = keywords.split(",");
	for(String k : ksp){
		k = k.trim();
		if(k.length() <= 1) continue;
		ids.addAll(jt.queryForList(matSql, Long.class, id, ","+k+","));
		if(ids.size() >= maxSize){
			break;
		}
	}
	
	List<Long> idList = new ArrayList<Long>(ids);
	idList = idList.subList(0, Math.min(idList.size(), maxSize));
	
	
	int i = jt.update("update wd_tag set correlation=? where id=?", Functions.listToString(idList), id);
	if(i > 0){
		mcc.delete("Tag-"+id);
	}
}

out.print("OK");
%>