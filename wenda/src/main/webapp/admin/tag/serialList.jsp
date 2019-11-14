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
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/tag/serialList.do">
			<input type="text" hidden="hidden" name="brandID" id="brandID" value="${tag.id}">
			<div class="form-group">
				<label class="control-label">车系名称：</label> 
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
			<button type="button" class="btn btn-warning btn-sm" onclick="createSerialTag()">新建车系分类</button>
			<button type="button" class="btn btn-sm" onclick="exportSerial()">导出</button>
			<button type="button" class="btn btn-info btn-sm" onclick="javascript:history.back(-1);">返回</button>
		</form>
	</div>	
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th>车系</th>
	            <th>ID</th>
	            <th>所属品牌</th>
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
					<a href="javascript:;" onclick="updateSerialTag(${tag.id},'${tag.name}')">修改车系分类</a>
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
	<!-- 模态框（创建车系标签） -->
	<div class="modal fade" id="createSerialModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">创建车系标签</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌名称</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" readonly="readonly" value="${tag.name}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">车系名称</label>
							<span class="label label-danger" style="display: none;" id="createSerialNameShow">请填写车系！</span>	
							<div class="col-sm-10">
							<input id="createSerialName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">首字母</label>
							<span class="label label-danger" style="display: none;" id="createSerialLetterShow">请填写首字母！</span>	
							<div class="col-sm-10">
							<input id="createSerialLetter" type="text" class="form-control" maxlength="1" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">车系ID</label>
							<span class="label label-danger" style="display: none;" id="createSerialIDShow">请填写车系ID！</span>	
							<div class="col-sm-10">
							<input id="createSerialID" maxlength="10" type="text" class="form-control" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="createSerialStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="createSerialUpdateShow">创建成功</span>
					<button type="button" class="btn btn-primary" onclick="insertSerialTag()">创建</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态框（修改车系标签） -->
	<div class="modal fade" id="updateSerialModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">修改车系标签</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
		        		<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌名称</label>
							<div class="col-sm-10">
							<input type="text" class="form-control" readonly="readonly" value="${tag.name}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">车系名称</label>
							<span class="label label-danger" style="display: none;" id="updateSerialNameShow">请填写车系！</span>	
							<div class="col-sm-10">
							<input id="updateSerialName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">车系ID</label>
							<div class="col-sm-10">
							<input id="updateSerialID" maxlength="10" type="text" class="form-control" readonly="readonly" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="updateSerialStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="updateSerialShow">保存成功</span>
					<button type="button" class="btn btn-primary" onclick="savaSerialTag()">保存</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>

<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script>
	//创建车系标签
	function createSerialTag(name){
		$("#createSerialModal").modal("show");
		hideCreateSerial();
	}
	function insertSerialTag(){
		hideCreateSerial();
		var name = $("#createSerialName").val();
		if(name.length == 0){
			$("#createSerialNameShow").show();
			return;
		}
		var letter = $("#createSerialLetter").val();
		if(letter.length == 0){
			$("#createSerialLetterShow").show();
			return;
		}
		var serialID = $("#createSerialID").val();
		if(!/^[1-9]+[0-9]*]*$/.test(serialID)){
			$("#createSerialIDShow").show();
			return;
		}
		var brandID = "${tag.id}";
		var status = $("#createSerialStatus").val();
		$.getJSON(
			"/admin/tag/createSerialTag.do",
			{brandID:brandID,name:name,letter:letter,serialID:serialID,status:status},
			function(data){
				if(data.code == 0){
				}else{
					$("#createSerialUpdateShow").val(data.message);
				}
				$("#createSerialUpdateShow").show();
			}
		);
	}
	function hideCreateSerial(){
		$("#createSerialNameShow").hide();
		$("#createSerialUpdateShow").hide();
		$("#createSerialIDShow").hide();
		$("#createSerialLetterShow").hide();
	}
	
	//修改车系标签
	function updateSerialTag(tid,name){
		hideUpdateSerial();
		$("#updateSerialModal").modal("show");
		$("#updateSerialName").val(name);
		$("#updateSerialID").val(tid);
	}
	function savaSerialTag(){
		hideUpdateSerial();
		var name = $("#updateSerialName").val();
		if(name.length == 0){
			$("#updateSerialNameShow").show();
			return;
		}
		var status = $("#updateSerialStatus").val();
		var serialID = $("#updateSerialID").val();
		$.getJSON(
			"/admin/tag/updateTag.do",
			{tagID:serialID,name:name,status:status},
			function(data){
				if(data.code == 0){
					$("#updateSerialShow").show();
					location.reload();
				}else{
					$("#updateSerialShow").val(data.message);
					$("#updateSerialShow").show();
				}
			}
		);
	}
	function hideUpdateSerial(){
		$("#updateSerialNameShow").hide();
		$("#updateSerialShow").hide();
	}
	
	function exportSerial(){
		window.open("/admin/tag/exportSerialOrSecond.do?type=S&tagID="+$("#brandID").val()+"&name="+$("#queryName").val()+"&id="+$("#queryID").val()+"&status="+$("#queryStatus").val(),"_self");
	}
	
</script>
</body>
</html>