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
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/tag/secondList.do">
			<input type="text" hidden="hidden" name="firstID" id="firstID" value="${tag.id}">
			<div class="form-group">
				<label class="control-label">关键词名称：</label> 
				<input type="text" class="form-control" name="name" id="queryName" style="height: 20px; width: 100px;" value="${name}">
			</div>
			<div class="form-group">
				<label class="control-label">ID:</label>
				<input type="text" class="form-control" name="tid" id="queryID" style="height: 20px; width: 60px;" value="${tid}">
			</div>
			<div class="form-group">
			    <select name="status" class="form-control" id="queryStatus">
			    	<option value="99">全部</option>
			    	<option value="0" <c:if test="${status == 0}">selected="selected"</c:if>>正常</option>
			    	<option value="-1" <c:if test="${status == -1}">selected="selected"</c:if>>禁用</option>
			    </select>
			</div>
			<button type="submit" class="btn btn-info btn-sm">查询</button>
			<button type="button" class="btn btn-warning btn-sm" onclick="createSecondTag()">新建二级级分类</button>
			<button type="button" class="btn btn-sm" onclick="exportSecond()">导出</button>
			<button type="button" class="btn btn-info btn-sm" onclick="javascript:history.back(-1);">返回</button>
		</form>
	</div>	
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th>关键词二级分类</th>
	            <th>ID</th>
	            <th>所属一级分类</th>
	            <th>状态</th>
	            <th>操作</th>
	        </tr>
	    </thead>
	    <tbody>
	    	<c:if test="${!empty pager.resultList}">
			<c:forEach var="tag" items="${pager.resultList}" varStatus="vs">
			    <tr>
			        <td>${tag.name}</td>
			        <td>${tag.id}</td>
			        <td>${tag.parentTag.name}</td>
			        <td><c:if test="${tag.status == 0}">正常</c:if><c:if test="${tag.status == -1}">禁用</c:if></td>
			        <td class="action">
					<a href="javascript:;" onclick="updateSecondTag(${tag.id},'${tag.name}')">修改二级分类</a>
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
			<pager:pager pageNo="${pager.pageNo}" pageSize="${pager.pageSize}" totalSize="${pager.total}" showInput="true">
				<%@include file="../pager/pagerJs.jsp"%>
			</pager:pager>
		</div>
	</c:if>
	<!-- 模态框（创建二级分类） -->
	<div class="modal fade" id="createSecondModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">创建二级分类</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">关键词一级分类</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" readonly="readonly" value="${tag.name}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">二级分类名称</label>
							<span class="label label-danger" style="display: none;" id="createSecondNameShow">请填写二级分类名称</span>	
							<div class="col-sm-10">
							<input id="createSecondName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="createSecondStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="createSecondUpdateShow">创建成功</span>
					<button type="button" class="btn btn-primary" onclick="insertSecondTag()">创建</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态框（修改二级分类） -->
	<div class="modal fade" id="updateSecondModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">修改二级分类</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
		        		<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">关键词一级分类</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" readonly="readonly" value="${tag.name}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">关键词二级分类</label>
							<span class="label label-danger" style="display: none;" id="updateSecondNameShow">二级分类不可为空！</span>	
							<div class="col-sm-10">
							<input id="updateSecondName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">ID</label>
							<div class="col-sm-10">
							<input id="updateSecondID" maxlength="10" type="text" class="form-control" readonly="readonly" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="updateSecondStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="updateSecondShow">保存成功</span>
					<button type="button" class="btn btn-primary" onclick="savaSecondTag()">保存</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>

<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script>
	//创建二级标签
	function createSecondTag(name){
		$("#createSecondModal").modal("show");
		hideCreateSecond();
	}
	function insertSecondTag(){
		hideCreateSecond();
		var name = $("#createSecondName").val();
		if(name.length == 0){
			$("#createSecondNameShow").show();
			return;
		}
		var pid = "${tag.id}";
		var status = $("#createSecondStatus").val();
		$.getJSON(
			"/admin/tag/createSecondTag.do",
			{pid:pid,name:name,status:status},
			function(data){
				if(data.code == 0){
				}else{
					$("#createSecondUpdateShow").val(data.message);
				}
				$("#createSecondUpdateShow").show();
			}
		);
	}
	function hideCreateSecond(){
		$("#createSecondNameShow").hide();
		$("#createSecondUpdateShow").hide();
	}
	
	//修改二级标签
	function updateSecondTag(tid,name){
		hideUpdateSecond();
		$("#updateSecondModal").modal("show");
		$("#updateSecondName").val(name);
		$("#updateSecondID").val(tid);
	}
	function savaSecondTag(){
		hideUpdateSecond();
		var name = $("#updateSecondName").val();
		if(name.length == 0){
			$("#updateSecondNameShow").show();
			return;
		}
		var status = $("#updateSecondStatus").val();
		var secondID = $("#updateSecondID").val();
		$.getJSON(
			"/admin/tag/updateTag.do",
			{tagID:secondID,name:name,status:status},
			function(data){
				if(data.code == 0){
					$("#updateSecondShow").show();
					location.reload();
				}else{
					$("#updateSecondShow").val(data.message);
					$("#updateSecondShow").show();
				}
			}
		);
	}
	function hideUpdateSecond(){
		$("#updateSecondNameShow").hide();
		$("#updateSecondShow").hide();
	}
	
	function exportSecond(){
		window.open("/admin/tag/exportSerialOrSecond.do?type=2&tagID="+$("#firstID").val()+"&name="+$("#queryName").val()+"&id="+$("#queryID").val()+"&status="+$("#queryStatus").val(),"_self");
	}
	
</script>
</body>
</html>