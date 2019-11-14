<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/common/import.jsp"%>
<c:if test="${!empty relateQuestion }">
	<div class="title">
	    <span class="mark">相关问答</span>
	</div>
	<ul class="list3">
	<c:forEach items="${relateQuestion}" var="question" begin="0" end="4">
	    <li class="list3-item">
	        <a href="${ROOT}/${question.id}.html#ad=12702" target="_blank">
	            <div class="list3-detail">${fn:escapeXml(question.title)}</div>
	            <ul class="listinfo">
	                <li class="listinfo-reply">
	                    <span class="icon iconfont iconxiaoxi"></span>
	                    <c:if test="${question.answerNum eq 0}"><span class="num num0">${question.answerNum}</span></c:if>
                        <c:if test="${question.answerNum ne 0}"><span class="num">${question.answerNum}</span></c:if>
	                    <span class="text">回答</span>
	                </li>
	            </ul>
	        </a>
	    </li>
	</c:forEach>
	</ul>
</c:if>