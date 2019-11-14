<%@page contentType="text/html; charset=UTF-8" session="false"%>
<%@include file="/WEB-INF/jspf/import.jspf"%>
<div class="content_wrap" id="keywordTreeContent" style="width: 14%;" >
	<div class="zTreeTagBackground left" style="width: 100%;">
		<ul class="ztree" id="keywordTreeBrand" >
		</ul>
	</div>
</div>
<iframe id="keywordQuestion" name="keywordQuestion" width="86%"></iframe>
<script>
	$("#keywordTreeContent").height(document.documentElement.clientHeight - 90);
	$("#keywordTreeBrand").height(document.documentElement.clientHeight - 100);
	$("#keywordQuestion").height(document.documentElement.clientHeight - 90);

	var setting = {
		async: {
			enable: true,
			url:"/admin/tag/getTags.do",
			autoParam:["id", "name=n", "level=lv"],
			otherParam:{"tagStatus":"Keyword"},
			dataFilter: filter
		}
	};

	function filter(treeId, parentNode, childNodes) {
		if (!childNodes) return null;
		for (var i=0, l=childNodes.length; i<l; i++) {
			childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
		}
		return childNodes;
	}

	$(document).ready(function(){
		$.fn.zTree.init($("#keywordTreeBrand"), setting);
	});
</script>