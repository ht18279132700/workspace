<%@page import="java.net.URLDecoder"%>
<%@page import="org.gelivable.auth.GeliAuthFacade"%>
<%@page import="org.gelivable.web.Env"%>
<%@page import="org.springframework.jdbc.core.JdbcTemplate"%>
<%@page import="org.gelivable.web.EnvUtils"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%
if("exec".equals(request.getParameter("act"))){
	JSONObject json = new JSONObject();
	if(!"POST".equals(request.getMethod())){
		json.put("msg", "your request method is not valid");
		out.print(json);
		return;
	}
	String data = request.getParameter("data");
	if(StringUtils.isBlank(data)){
		json.put("msg", "data must not be blank");
		out.print(json);
		return;
	}
	
	Env env = EnvUtils.getEnv();
	
	GeliAuthFacade auth =env.getBean(GeliAuthFacade.class);
	if(!auth.isAdmin()){
		json.put("msg", "no permisission");
		out.print(json);
		return;
	}
	
	JdbcTemplate jt = env.getBean(JdbcTemplate.class);
	try{
		data = URLDecoder.decode(data, "UTF-8");
		int[] res = jt.batchUpdate(data.split(";"));
		int len = res.length;
		int r = 0;
		for(int i=0; i<len; i++){
			r += res[i];
		}
		json.put("msg", String.format("执行完毕，%s条语句，%s行受影响", len, r));
	}catch(Exception e){
		json.put("msg", e.getMessage());
	}
	out.print(json);
	return;
}
%>
<!doctype html>
<html>

<body>
	<div>
		<textarea style="width:1200px; height:600px" id="Jarea"></textarea>
	</div>
	<div>
		<button id="Jexec" onclick="execs()">执行</button>
	</div>
	<script src="//www1.pcauto.com.cn/bbs/webapp/js/jquery-1.7.1.min.js"></script>
	<script>
	function execs(){
		$("#Jexec").attr("disabled", "disabled");
		var v = $("#Jarea").val();
		$.post("/admin/ext/execsql.jsp?act=exec",{data:encodeURIComponent(v)},function(res){
			res = JSON.parse(res);
			alert(res.msg);
			$("#Jexec").removeAttr("disabled");
		});
	}
	</script>
</body>

</html>