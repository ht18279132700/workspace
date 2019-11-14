<%@page contentType="text/html; charset=UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<link rel="stylesheet" type="text/css" href="/admin/themes/css/pure-min.css"/>
<link rel="stylesheet" type="text/css" href="/admin/themes/css/pure-main.css"/>
<style>
div.upload-tip-div{
	width:96%;
    margin: 0 auto;
    color: #3a87ad;
    background-color: #d9edf7;
    border-color: #bce8f1;
    padding: 10px;
    border: 1px solid transparent;
    border-radius: 4px;
    word-spacing: 0.1em;
    letter-spacing: 0.01em;
    line-height: normal;
}
</style>
	<div class="pure-u-1" id="main">
		<div class="container" layoutH="20">
			<div class="upload-tip-div">要求：上传的Excel必须包含以下列名${thirdDataHeaders}</div>
			<form class="pure-form pure-form-aligned" method="post" onsubmit="return iframeCallback(this,importAjaxResult);"
				enctype="multipart/form-data" action="/admin/questionImport/importThirdData.do">
				<fieldset>
					<div class="pure-control-group">
						<label for="foo">Excel问答文件上传</label>
						<input name="thirdDataExcel" type="file">
						<span class="label label-danger">一次最多上传1万条数据</span>
					</div>
					<div class="pure-controls">
						<button type="submit" class="pure-button pure-button-primary pure-button-small">导入问答数据</button>
						<button type="button" onclick="getImportAjax('/admin/questionImport/taskStatus.do')"
							class="pure-button pure-button-primary pure-button-small">查询任务状态</button>
						<a target="navTab" external="true" href="/admin/questionImport/listQuestionImport.do"
							class="pure-button pure-button-primary pure-button-small" rel="list-importQuestion">查看导入数据</a>
						<button type="button" onclick="getImportAjax('/admin/questionImport/publish.do')"
							class="pure-button pure-button-primary pure-button-small">发布问答</button>
					</div>
				</fieldset>
			</form>
			<hr>
			<form class="pure-form pure-form-aligned" method="post" 
				enctype="multipart/form-data" action="/admin/questionImport/questionExport.do">
				<fieldset>
					<div class="pure-control-group">
						<label for="foo">请输入日期：</label>
						<input name="beginDate" type="text" datefmt="yyyy-MM-dd 00:00:00" class="date" readonly="true">-
						<input name="endDate" type="text"  datefmt="yyyy-MM-dd 23:59:59" class="date" readonly="true">
					</div>
					<div class="pure-controls">
						<button type="submit" class="pure-button pure-button-primary pure-button-small">导出问答数据</button>
						<span class="label label-info">一次最多导出100万条数据</span>
					</div>
				</fieldset>
			</form>
			<hr>
			<form class="pure-form pure-form-aligned" method="post" onsubmit="return iframeCallback(this,importAjaxResult);"
				enctype="multipart/form-data" action="/admin/photoImport/importPhotoData.do">
				<fieldset>
					<div class="pure-control-group">
						<label for="foo">Excel图片文件上传</label>
						<input name="PhotoDataExcel" type="file">
						<span class="label label-danger">一次最多上传5万条数据</span>
					</div>
					<div class="pure-controls">
						<button type="submit" class="pure-button pure-button-primary pure-button-small">导入图片数据</button>
						<button type="button" onclick="getImportAjax('/admin/photoImport/taskStatus.do')"
							class="pure-button pure-button-primary pure-button-small">查询任务状态</button>
						<a type="button" href="/admin/photoImport/listPhotoImport.do" target="navTab" rel="list-importPhoto"
							class="pure-button pure-button-primary pure-button-small" >查看导入图片</a>
						<button type="button" onclick="getImportAjax('/admin/photoImport/publish.do')"
							class="pure-button pure-button-primary pure-button-small">切图</button>
					</div>
				</fieldset>
			</form>
			<hr>
			<form class="pure-form pure-form-aligned" method="post" 
				enctype="multipart/form-data" action="/admin/photoImport/photoExport.do">
				<fieldset>
					<div class="pure-control-group">
						<label for="foo">请输入日期：</label>
						<input name="beginDate" type="text" datefmt="yyyy-MM-dd 00:00:00" class="date" readonly="readonly">-
						<input name="endDate" type="text"  datefmt="yyyy-MM-dd 23:59:59" class="date" readonly="readonly">
					</div>
					<div class="pure-controls">
						<button type="submit" class="pure-button pure-button-primary pure-button-small">导出图片数据</button>
						<span class="label label-info">一次最多导出100万条数据</span>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
	<script>
		function importAjaxResult(data){
			if(data.code == 0){
				alert(data.title,data.result);
			}
		}
	
		function getImportAjax(url){
			$.ajax({
				url:url,
				type:"get",
				typeData:"json",
				success:function(data){
					if(data.code == 0){
						alert(data.title,data.result);
					}else{
						alert(data.message);
					}
				},
				error:function(data){
					alert(data.message);
				}
			});
		}
	</script>
