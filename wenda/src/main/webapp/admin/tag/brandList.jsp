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
		<form class="form-inline" role="form" id="queryForm" method="post" action="/admin/tag/brandList.do">
			<div class="form-group">
				<label class="control-label">品牌名称：</label> 
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
			<button type="button" class="btn btn-warning btn-sm" onclick="createBrandTag()">新建品牌分类</button>
			<button type="button" class="btn btn-sm" onclick="brandExport()">导出</button>
		</form>
	</div>
	<table class="table table-bordered table-hover table-condensed table-striped">
	    <thead>
	        <tr>
	            <th>品牌</th>
	            <th>ID</th>
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
			        <td><c:if test="${tag.status == 0}">正常</c:if><c:if test="${tag.status == -1}">禁用</c:if></td>
			        <td class="action">
			        <a href="/admin/tag/serialList.do?brandID=${tag.id}">查看车系分类</a>
					<a href="javascript:;" onclick="createSerialTag(${tag.id},'${tag.name}')">创建车系分类 </a>
					<a href="javascript:;" onclick="updateBrandTag(${tag.id},'${tag.name}')">修改品牌分类</a>
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
	<!-- 模态框（创建品牌标签） -->
	<div class="modal fade" id="createBrandModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">创建品牌标签</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌名称</label>
							<span class="label label-danger" style="display: none;" id="createBrandNameShow">请填写品牌！</span>	
							<div class="col-sm-10">
							<input id="createBrandName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">首字母</label>
							<span class="label label-danger" style="display: none;" id="createBrandLetterShow">请填写首字母！</span>	
							<div class="col-sm-10">
							<input id="createBrandLetter" type="text" class="form-control" maxlength="1" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌ID</label>
							<span class="label label-danger" style="display: none;" id="createBrandIDShow">请填写品牌ID！</span>	
							<div class="col-sm-10">
							<input id="createBrandID" maxlength="10" type="text" class="form-control" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="createBrandStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="createBrandUpdateShow">创建成功</span>
					<button type="button" class="btn btn-primary" onclick="insertBrandTag()">创建</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态框（创建车系标签） -->
	<div class="modal fade" id="createSerialModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">创建车系标签</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
		        		<input type="hidden" name="createSerialBrandID" id="createSerialBrandID"/>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌名称</label>
							<div class="col-sm-10">
							<input id="createSerialBrandName" type="text" class="form-control" readonly="readonly" value="">
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
					<button type="button" class="btn btn-primary" onclick="saveSerialTag()">创建</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态框（修改品牌标签） -->
	<div class="modal fade" id="updateBrandModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 400px;">
				<div class="modal-header" style="width: 370px;">
					<h4 class="modal-title">修改品牌标签</h4>
				</div>
				<div class="modal-body" style="width: 370px;">
		        	<form class="form-horizontal" role="form" >
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌名称</label>
							<span class="label label-danger" style="display: none;" id="updateBrandNameShow">品牌不可为空！</span>	
							<div class="col-sm-10">
							<input id="updateBrandName" type="text" class="form-control" maxlength="20" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;width: 160px;">品牌ID</label>
							<div class="col-sm-10">
							<input id="updateBrandID" maxlength="10" type="text" class="form-control" readonly="readonly" value="">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" style="text-align:left;">状态</label>
							<div class="col-sm-10">
							    <select id="updateBrandStatus" name="status" class="form-control" style="width: 100px;">
							    	<option value="0">正常</option>
							    	<option value="-1">禁用</option>
							    </select>
						    </div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<span class="label label-danger" style="display: none;" id="updateBrandShow">保存成功</span>
					<button type="button" class="btn btn-primary" onclick="savaBrandTag()">保存</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>

<script src="${ROOT}/admin/js/jquery-1.9.1.js" type="text/javascript"></script>
<script type="text/javascript" src="//www1.pcauto.com.cn/bbs/2016/js/WdatePicker.js"></script>
<script src="${ROOT}/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<script>
	//创建品牌标签
	function createBrandTag(){
		$("#createBrandModal").modal("show");
		hideCreateBrand();
	}
	function insertBrandTag(){
		hideCreateBrand();
		var name = $("#createBrandName").val();
		if(name.length == 0){
			$("#createBrandNameShow").show();
			return;
		}
		var letter = $("#createBrandLetter").val();
		if(letter.length == 0){
			$("#createBrandLetterShow").show();
			return;
		}
		var brandID = $("#createBrandID").val();
		if(!/^[1-9]+[0-9]*]*$/.test(brandID)){
			$("#createBrandIDShow").show();
			return;
		}
		var status = $("#createBrandStatus").val();
		$.getJSON(
			"/admin/tag/createBrandTag.do",
			{brandID:brandID,name:name,letter:letter,status:status},
			function(data){
				if(data.code == 0){
				}else{
					$("#createBrandUpdateShow").val(data.message);
				}
				$("#createBrandUpdateShow").show();
			}
		);
	}
	function hideCreateBrand(){
		$("#createBrandNameShow").hide();
		$("#createBrandLetterShow").hide();
		$("#createBrandIDShow").hide();
		$("#createBrandUpdateShow").hide();
	}
	
	//创建车系标签
	function createSerialTag(tid,name){
		$("#createSerialModal").modal("show");
		$("#createSerialBrandName").val(name);
		$("#createSerialBrandID").val(tid);
		hideCreateSerial();
	}
	function saveSerialTag(){
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
		var brandID = $("#createSerialBrandID").val();
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
	
	//修改品牌标签
	function updateBrandTag(tid,name){
		hideUpdateBrand();
		$("#updateBrandModal").modal("show");
		$("#updateBrandName").val(name);
		$("#updateBrandID").val(tid);
	}
	function savaBrandTag(){
		hideUpdateBrand();
		var name = $("#updateBrandName").val();
		if(name.length == 0){
			$("#updateBrandNameShow").show();
			return;
		}
		var status = $("#updateBrandStatus").val();
		var brandID = $("#updateBrandID").val();
		$.getJSON(
			"/admin/tag/updateTag.do",
			{tagID:brandID,name:name,status:status},
			function(data){
				if(data.code == 0){
					$("#updateBrandShow").show();
					location.reload();
				}else{
					$("#updateBrandShow").val(data.message);
					$("#updateBrandShow").show();
				}
			}
		);
	}
	function hideUpdateBrand(){
		$("#updateBrandNameShow").hide();
		$("#updateBrandShow").hide();
	}
	
	function brandExport(){
		window.open("/admin/tag/brandOrFirstExport.do?type=B&name="+$("#queryName").val()+"&id="+$("#queryID").val()+"&status="+$("#queryStatus").val(),"_self");
	}
</script>
</body>
</html>