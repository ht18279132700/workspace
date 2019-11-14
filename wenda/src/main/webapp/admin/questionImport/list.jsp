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
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/questionImport/listQuestionImport.do">
			<div class="form-group">
				<label class="control-label">&nbsp;类型：</label> 
			    <select name="status" class="form-control" id="queryStatus">
			    	<option value="0">第三方数据</option>
			    </select>
			</div>
			<div class="form-group">
				<label class="control-label">&nbsp;创建者：</label> 
				<input type="text" class="form-control" name="createBy" style="width: 60px;" value="${createBy==0?'':createBy}">
			</div>
			<div class="form-group"> 
				<label class="control-label">&nbsp;提问时间：</label> 
				<span class="muted date-label"></span> 
				<input class="input date hasDatepicker form-control" type="text" tabindex="1" id="start"
					value="${createBegin}" name="createBegin" id="createBegin"
					style="width: 130px" autocomplete="off"
					onclick="WdatePicker({maxDate:'#F{$dp.$D(\'end\')}',dateFmt:'yyyy-MM-dd 00:00:00'});" />
				<em class="muted">—</em> 
				<input class="input date hasDatepicker form-control"
					type="text" tabindex="1" id="end" id="createEnd"
					value="${createEnd}" name="createEnd" style="width: 130px" autocomplete="off"
					onclick="WdatePicker({minDate:'#F{$dp.$D(\'start\')}',dateFmt:'yyyy-MM-dd 23:59:59'});" />
			</div>
			<button type="submit" class="btn btn-info btn-sm">查询</button>
		</form>
	</div>
	<table class="table table-bordered table-hover table-condensed table-striped">
		<thead>
			<tr>
				<th>问答ID</th>
				<th>qid</th>
				<th style="width: 40%;">标题</th>
				<th>发表者</th>
				<th>发表时间</th>
				<th>创建者</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${pager.total > 0}">
					<c:forEach var="questionImport" items="${pager.resultList}">
						<tr id="qidBody${questionImport.id}">
							<td>${questionImport.id}</td>
							<td>${questionImport.qid}</td>
							<td class="text-left"><a target="_blank" href="${sys.root}/${questionImport.qid}.html">${fn:escapeXml(questionImport.title)}</a></td>
							<td>${questionImport.nickname}</td>
							<td><fmt:formatDate value="${questionImport.questionTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td>${questionImport.createBy}</td>
							<td><fmt:formatDate value="${questionImport.createAt}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td class="action">
								<a href="javascript:;"  
									rel="list-question-import" onclick="deleteQueston(${questionImport.id})">删除</a>
								<a href="${sys.root}/admin/questionImport/detail.do?id=${questionImport.id}" 
									rel="list-question-import">修改</a>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
			       		<td colspan="8" class="text-center">暂无符合条件的数据</td>
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
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<jsp:include page="../common/alert.jsp"></jsp:include>
<script>
function deleteQueston(qid){
	confirm("确定要删除吗？",function(){
		$.getJSON(
			"/admin/questionImport/delete.do",
			{id:qid},
			function(data){
				if(data.code == 0){
					$("#qidBody"+qid).remove();
				}else{
					alert(data.message);
				}
			}
		);
	});
}
</script>
</body>
</html>