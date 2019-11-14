<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/common/import.jsp"%>
<c:if test="${!empty relateTags}">
	<div class="title">
	    <span class="mark">相关话题</span>
	    <a href="${ROOT}/topic/" class="gjcmore" target="_blank">查看更多标签>></a>
	</div>
	<ul class="list clearfix">
	<c:forEach items="${relateTags}" var="tag">
	    <li><a href="${ROOT}/topic/${tag.id}.html#ad=12704" target="_blank">${tag.name}</a></li>
	</c:forEach>
	</ul>
</c:if>