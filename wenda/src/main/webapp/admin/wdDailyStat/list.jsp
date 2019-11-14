<%@page contentType="text/html; charset=UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<%@ taglib prefix="pager" uri="/admin/pager/pager.tld"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UFT-8">
    <meta http-equiv="x-ua-compatible" content="ie=7">
    <link href="${ROOT}/admin/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="/admin/themes/css/pager.css" rel="stylesheet" type="text/css"/>
    <link href="/admin/themes/css/table.css" rel="stylesheet" type="text/css"/>
	<style>
	.query-form-div{margin-top: 5px; margin-bottom: 5px;}
	</style>
</head>
<body>
	<div class="query-form-div">
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/wdDailyStat/list.do">
			<div class="form-group"> 
				<label class="control-label">&nbsp;发布时间：</label> 
				<span class="muted date-label"></span> 
				<input class="input date hasDatepicker form-control" type="text" tabindex="1"
					value="${beginDay}" name="beginDay" id="beginDay"
					style="width: 130px" autocomplete="off"
					onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endDay\')}',dateFmt:'yyyy-MM-dd 00:00:00'});" />
				<em class="muted">—</em> 
				<input class="input date hasDatepicker form-control"
					type="text" tabindex="1" id="endDay"
					value="${endDay}" name="endDay" style="width: 130px" autocomplete="off"
					onclick="WdatePicker({minDate:'#F{$dp.$D(\'beginDay\')}',dateFmt:'yyyy-MM-dd 23:59:59'});" />
			</div>
			<button type="submit" class="btn btn-info btn-sm">查询</button>
			<button type="button" class="btn btn-warning btn-sm" onclick="exportData()">导出</button>
		</form>
	</div>
	<table class="table table-bordered table-hover table-condensed table-striped">
		<thead>
			<tr>
				<th>时间</th>
				<th>SEO提问数</th>
				<th>SEO回答数</th>
				<th>网友提问数</th>
				<th>网友回答数</th>
				<th>网友回复数</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${pager.total > 0}">
					<c:forEach var="s" items="${pager.resultList}">
						<tr>
							<td>${f:joinDay(s.day, '/')}</td>
							<td>${s.seoQuestionNum}</td>
							<td>${s.seoAnswerNum}</td>
							<td>${s.userQuestionNum}</td>
							<td>${s.userAnswerNum}</td>
							<td>${s.userReplyNum}</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
			       		<td colspan="6" class="text-center">暂无符合条件的数据</td>
			   		</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	<c:if test="${!empty pager.resultList}">
       <div class="pager">
		<pager:pager pageNo="${pager.pageNo}" pageSize="${pager.pageSize}" totalSize="${pager.total}" showInput="true">
      		<%@include file="../pager/pagerJs.jsp"%>
		</pager:pager>
       </div>
	</c:if>
<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
<script>
function exportData(){
	window.open("/admin/wdDailyStat/export.do?beginDay="+$("#beginDay").val()+"&endDay="+$("#endDay").val());
}
</script>
</body>
</html>