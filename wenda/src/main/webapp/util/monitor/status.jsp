<%@page session="false" language="java" contentType="text/html;charset=GBK" pageEncoding="GBK"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if(request.getHeader("Host") == null){
        out.println("�Ƿ�����");
        return;
    }else{
    	if(request.getHeader("Host").startsWith("192.168.") || request.getHeader("Host").startsWith("127.0.0.1")){
    		//todo
    	}else{
    		out.println("�Ƿ�����");
            return;
    	}
    }
%>
<textarea cols="200" rows="40" style="font-size: 12px">
  <c:forEach items="${_envSet}" var="e">${e.logString}</c:forEach>
</textarea>
