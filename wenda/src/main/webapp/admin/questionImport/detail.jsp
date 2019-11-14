<%@page contentType="text/html; charset=UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UFT-8">
    <meta http-equiv="x-ua-compatible" content="ie=7">
    <link href="${ROOT}/admin/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css" />
</head>
<body>
    <div class="panel-heading" style="text-align: center;font-size: 20px;">
    	<span class="label label-default">导入问题ID:</span>
		<span class="label label-success">${questionImport.id}</span>&nbsp;&nbsp;
		<span class="label label-default">创建人:</span>
		<span class="label label-success">${questionImport.nickname}</span>&nbsp;&nbsp;
		<span class="label label-default">创建时间:</span>
		<span class="label label-success">${questionImport.showQuestionTime}</span><br>
		<span class="label label-default">qid:</span>
		<span class="label label-success">${questionImport.qid}</span>&nbsp;&nbsp;
		<span class="label label-default">URL:</span>
		<a href="${questionImport.url}" target="_blank"><span class="label label-success" style="font-size: 16px;">${questionImport.url}</span></a>
		<button type="button" class="btn btn-info btn-xs" onclick="javascript:history.back(-1);">返回</button>
    </div>
    <div class="panel-body">
    	<div class="input-group">
            <span class="input-group-addon">标题:</span>
            <input type="text" class="form-control" id="qiidTitle" style="width: 99%;" value="${questionImport.title}">
        </div><br>
    	<span class="label label-default">主楼:</span>
    	<textarea class="form-control" id="qiidContent" style="overflow: hidden;width: 99%;" rows="4">${questionImport.content}</textarea><br>
    	<div class="btn-group" style="float: right;margin-right: 20px;">
			<button type="button" class="btn btn-info btn-sm" onclick="saveQuestion(${questionImport.id})">保存</button>
		</div>
    </div>
    <c:forEach items="${list}" var="answerImport" varStatus="status" >
    <div class="panel-body" id="aidBody${answerImport.id}">
    	<span class="label label-default">${status.count}楼:</span>
    	<textarea class="form-control" style="overflow: hidden;width: 99%;" id="aid${answerImport.id}" rows="4">${answerImport.content }</textarea><br>
    	<span class="label label-default">回答时间:</span>
		<span class="label label-success">${answerImport.showAnswerTime}</span>&nbsp;&nbsp;&nbsp;&nbsp;
		<span class="label label-default">回答人:</span>
		<span class="label label-success">${answerImport.nickname}</span>
    	<div class="btn-group" style="float: right;margin-right: 20px;">
			<button type="button" class="btn btn-info btn-sm" onclick="saveAnswer(${answerImport.id})">保存</button>
			<button type="button" class="btn btn-danger btn-sm" onclick="deleteAnswer(${answerImport.id})">删除</button>
		</div>
    </div>
    </c:forEach>
<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<jsp:include page="../common/alert.jsp"></jsp:include>
<script type="text/javascript">
	function saveQuestion(qid){
		var qiidTitle = $("#qiidTitle").val();
		var qiidContent = $("#qiidContent").val();
		$.getJSON(
			"/admin/questionImport/update.do",
			{id:qid,title:qiidTitle,content:qiidContent},
			function(data){
				if(data.code == 0){
					alert(data.message);
				}else{
					alert(data.message);
				}
			}
		);
	}
	
	function saveAnswer(aid){
		var aidContent = $("#aid"+aid).val();
		$.getJSON(
			"/admin/answerImport/update.do",
			{id:aid,content:aidContent},
			function(data){
				if(data.code == 0){
					alert(data.message);
				}else{
					alert(data.message);
				}
			}
		);
	}
		
	function deleteAnswer(aid){
		confirm("确定要删除吗？",function(){
			$.getJSON(
				"/admin/answerImport/delete.do",
				{id:aid},
				function(data){
					if(data.code == 0){
						$("#aidBody"+aid).remove();
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