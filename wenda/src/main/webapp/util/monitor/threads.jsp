<%@page contentType="text/html; charset=UTF-8" import="java.util.*" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if(request.getHeader("Host") == null || !request.getHeader("Host").startsWith("192.168.")){
        out.println("非法请求！");
        return;
    }
%>
<%
	Map threads=new TreeMap(); request.setAttribute("threads",threads);
	for(Map.Entry<Thread, StackTraceElement[]> me: Thread.getAllStackTraces().entrySet()) {
		//if(!"RUNNABLE".equals(me.getKey().getState())) continue;
		String s=me.getKey().getName();
		for(StackTraceElement ste: me.getValue()){
		    if(ste.getClassName().startsWith("_jsp")) { s+=" --- <b>"+ste+"</b>"; break; }
		}
		threads.put(s,me);
	}
%>
<title><%=threads.size()%> threads@<%=java.net.InetAddress.getLocalHost()%>:<%=request.getServerPort()%></title>
<style>body{font-size:9pt; white-space:nowrap; }</style>
<c:forEach var='t' items='${threads}'>
  <c:if test="${t.value.key.state=='RUNNABLE'}">
    <div id="b${t.value.key.id}"
	     onmouseover="style.textDecoration='underline'"
	     onmouseout="style.textDecoration=''"
	     onclick="var d=this.nextElementSibling.style;d.display=d.display!='none'?'none':''" >
	  ${t.key}
    </div>
    <div id="div${t.value.key.id}" style="display: none;">
      <c:forEach var="s" items="${t.value.value}">&nbsp;&nbsp;&nbsp;&nbsp;${s}<br></c:forEach>
    </div>
  </c:if>
</c:forEach>

<hr>

<div style="color:gray">
  <c:forEach var='t' items='${threads}'>
    <c:if test="${t.value.key.state!='RUNNABLE'}">
      <div id="b${t.value.key.id}"
	       onmouseover="style.textDecoration='underline'"
	       onmouseout="style.textDecoration=''"
	       onclick="var d=this.nextElementSibling.style;d.display=d.display!='none'?'none':''">
	    ${t.key}
      </div>
      <div id="div${t.value.key.id}" style="display: none;">
        <c:forEach var="s" items="${t.value.value}">&nbsp;&nbsp;&nbsp;&nbsp;${s}<br></c:forEach>
      </div>
    </c:if>
  </c:forEach>
</div>    