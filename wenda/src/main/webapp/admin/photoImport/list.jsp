<%@page contentType="text/html; charset=UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<div class="pageContent">
	<form onsubmit="return navTabSearch(this);" id="pagerForm" name="searchForm" method="post"
	action="/admin/photoImport/listPhotoImport.do">
		<input type="hidden" name="pageNum" value="${pager.pageNo}" /> 
		<input type="hidden" name="numPerPage" value="${pager.pageSize}" />
		<div class="panelBar">
			<ul class="toolBar">
				<li><a class="icon" href="javascript:" onclick="getPhotosJson(0)" rel="photoIds"><span>批量生成</span></a></li>
			</ul>
		</div>
	</form>
	<table class="table" style="width: 100%" layoutH="75">
		<thead>
			<tr>
				<th width="2%" align="center"></th>
				<th align="center" width="125">图片</th>
				<th align="center" style="width: 200px;">问答图片url</th>
				<th align="center">图片宽</th>
				<th align="center">图片高</th>
				<th align="center">图片大小</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${pager.total > 0}">
					<c:forEach var="photoImport" items="${pager.resultList}">
						<tr ondblclick="getPhotosJson(${photoImport.id})">
							<td align="center"><input type="checkbox" id="photoIds" name="photoIds" value="${photoImport.id}" onclick="Count()"></td>
							<td width="125" height="90"><a href="${photoImport.wdUrl}" target="_blank"><img src="${f:getImgSize(photoImport.wdUrl, '240x160')}" width="120" height="80"></a></td>
							<td style="width: 200px;">${photoImport.wdUrl}</td>
							<td>${photoImport.width}</td>
							<td>${photoImport.height}</td>
							<td>${photoImport.size}</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="5" style="font-weight: bold; font-size: 18px; color: red; line-height: 70px; height: 70px;">
							暂无符合条件的数据
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>
<div class="panelBar">
	<div class="pages">
		<span>共<font style="font-weight: bold; color: red;">${pager.total}</font>条记录，每页</span>
		<select class="combox" name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
			<option value="20" ${numPerPage == 20 ? 'selected' : ''}>20</option>
			<option value="50" ${numPerPage == 50 ? 'selected' : ''}>50</option>
			<option value="100" ${numPerPage == 100 ? 'selected' : ''}>100</option>
			<option value="200" ${numPerPage == 200 ? 'selected' : ''}>200</option>
		</select>
		<span>条记录，共<font style="font-weight: bold; color: red;">${pager.pageCount}</font>页</span>
	</div>
	<div class="pagination" targetType="navTab" totalCount="${pager.total}" numPerPage="${pager.pageSize}" pageNumShown="10" currentPage="${pager.pageNo}"></div>
</div>
<!-- 模态框（Modal） -->
<div class="modal fade" id="photoModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="photoModalLabel">
					图片Json数据
				</h4>
			</div>
			<textarea class="modal-body" id="photoModelBody" style="position: absolute;left: -9999px;"></textarea>
			<div class="modal-body" id="photoModelBody1"></div>
			<div class="modal-footer">
				<span class="label label-success" style="float: left;" id="copySuccess"></span>
				<button type="button" class="btn btn-primary" name="copy_input" onclick="copyPhoto()">复制</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>
<script>
	function Count(){
		var count = 0;
		$("input[name='photoIds']:checked").each(function() {
			count++;
		});
		if(count < 5) {
			$(":checkbox:not(:checked)").attr("disabled",false);
		}  else {
			$(":checkbox:not(:checked)").attr("disabled",true);
		}

	}

	function getPhotosJson(id){
		$("#copySuccess").text("");
		var ids = "";
		if(id > 0){
			ids = id;
		}else{
			$("input[name='photoIds']:checked").each(function(i){
				if(0 == i){
					ids += $(this).val();
				}else{
					ids = ids + "," + $(this).val();
				}
			});
		}
		if(ids.length < 1){
			alert("未选择图片");
			return;
		}
		
		$.ajax({
			url:"/admin/photoImport/getJsonPhoto.do?ids="+ids,
			type:"get",
			dateType:"json",
			success:function(data){
				if(data.code == 0){
					$("#photoModelBody").html(data.photos);
					$("#photoModelBody1").html(data.photos);
					$("#photoModal").modal("show");
				}else{
					alert(data.message);
				}
			},
			error:function(data){
				alert(data.message);
			}
			
		});
	}
  	function copyPhoto(){
		var modelBody=$('#photoModelBody');
		modelBody.select(); // 选择对象  
	    var isCopy = document.execCommand("Copy"); // 执行浏览器复制命令
	    if(isCopy){
	    	$("#copySuccess").text("复制成功");
	    }
	} 
</script>