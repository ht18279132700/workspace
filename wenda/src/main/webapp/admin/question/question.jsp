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
	.query-form-div{margin-top: 5px; margin-bottom: 5px;text-align: center;white-space: nowrap;overflow: auto;}
	</style>
</head>
<body>
	<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
	<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
	<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
	<div class="query-form-div">
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/question/question.do">
			<input type="text" hidden="hidden" name="tid" value="${tid}">
			<div class="form-group">
				<label class="control-label">&nbsp;用户ID:</label> 
				<input type="text" class="form-control" name="uid" style="width: 60px;" value="${uid}">
			</div>
			<div class="form-group">
				<label class="control-label">&nbsp;问题ID:</label>
				<input type="text" class="form-control" name="qid" style="width: 60px;" value="${qid}">
			</div>
			<div class="form-group">
				<label class="control-label">&nbsp;问题:</label> 
				<input type="text" class="form-control" name="title" style="width: 130px;" value="${title}">
			</div>
			<div class="form-group"> 
				<label class="control-label">&nbsp;提问时间：</label> 
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
			<button type="button" class="btn btn-warning btn-sm" onclick="deleteQuestion(0)">批量删除</button>
		</form>
	</div>
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th><input type="checkbox" class="checkboxCtrl" group="qids" onclick="checkboxCtrl()">问题ID</th>
	            <th style="width: 35%;">标题</th>
	            <th>提问者昵称</th>
	            <th>提问者ID</th>
	            <th>提问时间</th>
	            <th>回答数</th>
	            <th>访问量</th>
	            <th>操作</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<c:if test="${!empty pager.resultList}">
			<c:forEach var="question" items="${pager.resultList}" varStatus="vs">
			    <tr id="tbody${question.id}">
			        <td><input type="checkbox" id="qids" name="qids" value="${question.id}">${question.id}</td>
			        <td class="text-left"><a target="_blank" href="/${question.id}.html">${fn:escapeXml(question.title)}</a></td>
			        <td class="text-left"><a target="_blank" href="${UCROOT}/${question.createBy}">${question.user.nickName}</a></td>
			        <td>${question.createBy}</td>
			        <td><fmt:formatDate value="${question.createAt}" pattern="yyyy-MM-dd HH:mm"/></td>
			        <td>${question.answerNum}</td>
					<td>${question.pv}</td>
			        <td class="action">
					<a href="javascript:;" onclick="detailQuestion(${question.id})">修改</a>
					<a href="javascript:;"  onclick="deleteQuestion(${question.id})">删除</a>
					<a href="/admin/answer/list.do?qid=${question.id}">查看</a>
					</td>
	            </tr>
	        </c:forEach>
	        </c:if>
			<c:if test="${empty pager.resultList}">
			   <tr>
			       <td colspan="8" class="text-center">暂无符合条件的数据</td>
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
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="questionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="questionModalLabel">
						更新问题
					</h4>
				</div>
				<div class="modal-body" style="width: 620px;">
		        	<form class="form-horizontal" role="form" >
		        		<input type="hidden" name="modalQid" id="modalQid"/>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">标题</label>
							<span class="label label-danger" style="display: none;" id="titleShow">问题不可为空</span>
							<div class="col-sm-10">
							<input type="text" class="form-control" id="modalTitle" placeholder="修改标题" maxlength="50">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 600px;">提问时间:
								<span id="modalCreateAt">2018-12-03 11:22</span>&nbsp;&nbsp;&nbsp;&nbsp;
								<span>格式:yyyy-MM-dd HH:mm</span>&nbsp;&nbsp;&nbsp;&nbsp;
								<span class="label label-danger" style="display: none;" id="timeShow">请按正确格式填写时间</span>
							</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" id="modalTime" placeholder="修改时间">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">内容</label>	
							<div class="col-sm-10">
							<textarea class="form-control" id="modalContent" rows="6" placeholder="修改内容" maxlength="1000"></textarea>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="updateShow">修改成功</span>
					<button type="button" class="btn btn-primary" onclick="updateQuestion()">保存</button>
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

	function deleteQuestion(id){
		var ids = "";
		if(id > 0){
			ids = id;
		}else{
			$("input[name='qids']:checked").each(function(i){
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
				"/admin/question/deleteQuestion.do",
				{ids:ids},
				function(data){
					if(data.code == 0){
						var qids = data.ids;
						for(var i = 0; i < qids.length; i++){
							$("#tbody"+qids[i]).remove();
						}
					}else{
						alert(data.message);
					}
				}
			);
		});
	}
	function detailQuestion(qid){
		hiddenShow();
		$.getJSON(
			"/admin/question/detail.do",
			{qid:qid},
			function(data){
				if(data.code == 0){
					$("#questionModal").modal("show");
					$("#modalQid").val(data.qid);
					$("#modalTitle").val(data.title);
					$("#modalCreateAt").html(data.createAt);
					$("#modalTime").val(data.createAt);
					$("#modalContent").val(data.content);
				}else{
					alert(data.message);
				}
			}
		);
	}
	
	function updateQuestion(){
		hiddenShow();
		var qid = $("#modalQid").val();
		var title = $("#modalTitle").val();
		var time = $("#modalTime").val();
		var content = $("#modalContent").val();
		if(qid == null || qid <=0 ){
			alert("问题ID错误");
			return;
		}
		if(title == null || title.length == 0){
			$("#titleShow").show();
			return;
		}
		if(time == null || time.length == 0){
			$("#timeShow").show();
			return;
		}
		var DATE_FORMAT = /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01]) (0\d{1}|1\d{1}|2[0-3]):([0-5]\d{1})$/;
		if(!DATE_FORMAT.test(time)){
			$("#timeShow").show();
			return;
		}

		$.post(
			"/admin/question/update.do",
			{qid:qid,title:title,time:time,content:content},
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
		$("#titleShow").hide();
		$("#timeShow").hide();
		$("#updateShow").hide();
	}
</script>
</body>
</html>