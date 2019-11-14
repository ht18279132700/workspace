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
    .label-div{padding: 10px 5px;}
    .label-div label{margin: 0;}
    .label-div p{display: inline-block;margin: 0;}
    .query-form-div{margin-top: 5px;text-align: center;white-space: nowrap;overflow: auto;}
    </style>
</head>
<body>
	<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
	<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
	<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
	<div class="query-form-div">
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/answer/list.do" style="text-align: left;">
			<input type="text" name="qid" value="${question.id}" hidden="hidden">
			<div class="form-group">
				<label class="control-label">&nbsp;用户ID:</label> 
				<input type="text" class="form-control" name="uid" style="width: 50px;" value="${uid}">
			</div>
			<div class="form-group">
				<label class="control-label">&nbsp;回答ID:</label>
				<input type="text" class="form-control" name="aid" style="width: 50px;" value="${aid}">
			</div>
			<div class="form-group">
				<label class="control-label">&nbsp;回答内容:</label> 
				<input type="text" class="form-control" name="content" style="width: 80px;" value="${content}">
			</div>
			<div class="form-group"> 
				<label class="control-label">&nbsp;回答时间：</label> 
				<span class="muted date-label"></span> 
				<input class="input date hasDatepicker form-control" type="text" tabindex="1" id="start"
					value="${createBegin}" name="createBegin"
					style="width: 130px" autocomplete="off"
					onclick="WdatePicker({maxDate:'#F{$dp.$D(\'end\')}',dateFmt:'yyyy-MM-dd 00:00:00'});" />
				<em class="muted">—</em> 
				<input class="input date hasDatepicker form-control"
					type="text" tabindex="1" id="end"
					value="${createEnd}" name="createEnd" style="width: 130px" autocomplete="off"
					onclick="WdatePicker({minDate:'#F{$dp.$D(\'start\')}',dateFmt:'yyyy-MM-dd 23:59:59'});" />
			</div>
			<button type="submit" class="btn btn-info btn-sm">查询</button>
			<button type="button" class="btn btn-warning btn-sm" onclick="deleteAnswer(${question.id},0)">批量删除</button>
			<button type="button" class="btn btn-sm" onclick="javascript:history.back(-1);">返回</button>
		</form>
	</div>
	<div class="label-div">
		<label>标题：</label>
		<p>${question.title}</p>
	</div>
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th><input type="checkbox" class="checkboxCtrl" group="aids" onclick="checkboxCtrl()">回答ID</th>
	            <th style="width: 40%;">回答内容</th>
	            <th>回答者</th>
	            <th>回答时间</th>
	            <th>操作</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<c:if test="${!empty pager.resultList}">
			<c:forEach var="answer" items="${pager.resultList}" varStatus="vs">
			    <tr id="tbody${answer.id}">
			        <td><input type="checkbox" id="aids" name="aids" value="${answer.id}">${answer.id}</td>
			        <td class="text-left">${fn:escapeXml(answer.content)}</td>
			        <td>${answer.createBy}/<a target="_blank" href="${UCROOT}/${answer.createBy}">${answer.user.nickName}</a></td>
			        <td><fmt:formatDate value="${answer.createAt}" pattern="yyyy-MM-dd HH:mm"/></td>
			        <td class="action">
					<a href="javascript:;" onclick="detailAnswer(${question.id},${answer.id})">修改</a>
					<a href="javascript:;" onclick="deleteAnswer(${question.id},${answer.id})">删除</a>
					</td>
	            </tr>
	        </c:forEach>
	        </c:if>
			<c:if test="${empty pager.resultList}">
			   <tr>
			       <td colspan="5">暂无符合条件的数据</td>
			   </tr>
			</c:if>
	    </tbody>
	</table>
	<c:if test="${!empty pager.resultList}">
		<div class="pager">
			<pager:pager pageNo="${pager.pageNo}" pageSize="${pager.pageSize}"
				totalSize="${pager.total}" showInput="true">
				<%@include file="../pager/pagerJs.jsp"%>
			</pager:pager>
		</div>
	</c:if>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="answerModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="answerModalLabel">
						更新回答
					</h4>
				</div>
				<div class="modal-body" style="width: 620px;">
		        	<form class="form-horizontal" role="form" >
		        		<input type="hidden" name="modalAid" id="modalAid"/>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">问题</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" readonly="readonly" value="${question.title}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">内容</label>
							<span class="label label-danger" style="display: none;" id="contentShow">回答内容不能为空</span>	
							<div class="col-sm-10">
							<textarea class="form-control" id="modalAnswerContent" rows="8" maxlength="5000" placeholder="修改内容"></textarea>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="updateShow">修改成功</span>
					<button type="button" class="btn btn-primary" onclick="updateAnswer(${question.id})">保存</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
<jsp:include page="../common/alert.jsp"></jsp:include>
<script>
	function checkboxCtrl(){
		if($(".checkboxCtrl").is(':checked')){
			$("input:checkbox").prop("checked","checked");
			$("input:checkbox").attr("checked","checked");
		}else{
			$("input:checkbox").removeAttr("checked");
		}
	}

	function deleteAnswer(qid,id){
		var ids = "";
		if(id > 0){
			ids = id;
		}else{
			$("input[name='aids']:checked").each(function(i){
				if(0 == i){
					ids += $(this).val();
				}else{
					ids = ids + "," + $(this).val();
				}
			});
		}
		if(ids.length == 0){
			alert("未选择问题ID");
			return;
		}
		confirm("确定要删除吗？",function(){
			$.getJSON(
				"/admin/answer/deleteAll.do",
				{ids:ids,qid:qid},
				function(data){
					if(data.code == 0){
						var aids = data.ids;
						for(var i = 0; i < aids.length; i++){
							$("#tbody"+aids[i]).remove();
						}
					}else{
						alert(data.message);
					}
				}
			);
		});
	}
	function detailAnswer(qid,aid){
		hiddenShow();
		$.getJSON(
			"/admin/answer/detail.do",
			{qid:qid,aid:aid},
			function(data){
				if(data.code == 0){
					$("#answerModal").modal("show");
					$("#modalAnswerContent").val(data.content);
					$("#modalAid").val(data.aid);
				}else{
					alert(data.message);
				}
			}
		);
	}
	
	function updateAnswer(qid){
		hiddenShow();
		var content = $("#modalAnswerContent").val();
		var aid = $("#modalAid").val();
		if(content == null || content.length == 0){
			$("#contentShow").show();
			return;
		}
		$.post(
			"/admin/answer/update.do",
			{qid:qid,aid:aid,content:content},
			function(data){
				if(data.code == 0){
					$("#updateShow").show();
					location.reload();
				}else{
					$("#updateShow").html(data.message);
					$("#updateShow").show();
				}
			}
		);
	}
	function hiddenShow(){
		$("#contentShow").hide();
		$("#updateShow").hide();
	}
</script>
</body>
</html>