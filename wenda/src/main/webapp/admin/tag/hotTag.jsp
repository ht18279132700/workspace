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
	.query-form-div{margin-top: 5px; margin-bottom: 5px;text-align: center;}
	</style>
</head>
<body>
	<div class="query-form-div">
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/tag/hotTag.do">
			<div class="form-group">
				<label class="control-label">标签名称：</label> 
				<input type="text" class="form-control" id="queryName" name="name" style="height: 20px; width: 100px;" value="${name}">
			</div>
			<div class="form-group">
				<label class="control-label">ID:</label>
				<input type="text" class="form-control" id="queryID" name="tid" style="height: 20px; width: 60px;" value="${tid}">
			</div>
			<div class="form-group">
			    <select name="status" class="form-control" id="queryStatus">
			    	<option value="99">全部</option>
			    	<option value="0" <c:if test="${status == 0}">selected="selected"</c:if>>正常</option>
			    	<option value="-1" <c:if test="${status == -1}">selected="selected"</c:if>>禁用</option>
			    </select>
			</div>
			<button type="submit" class="btn btn-info btn-sm">查询</button>
			<button type="button" class="btn btn-sm" onclick="_export()">导出</button>
		</form>
	</div>	
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th>标签名称</th>
	            <th>ID</th>
	            <th>状态</th>
	            <th>热度值</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<c:if test="${!empty pager.resultList}">
			<c:forEach var="tag" items="${pager.resultList}" varStatus="vs">
			    <tr>
			        <td>${tag.name}</td>
			        <td>${tag.id}</td>
			        <td><c:if test="${tag.status == 0}">正常</c:if><c:if test="${tag.status == -1}">禁用</c:if></td>
			        <td>${tag.questionNum}</td>
	            </tr>
	        </c:forEach>
	        </c:if>
			<c:if test="${empty pager.resultList}">
			   <tr>
			       <td colspan="4">暂无符合条件的数据</td>
			   </tr>
			</c:if>
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
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script>
	function _export(){
		window.open("/admin/tag/exportHotTag.do?name="+$("#queryName").val()+"&id="+$("#queryID").val()+"&status="+$("#queryStatus").val(),"_self");
	}
</script>
</body>
</html>