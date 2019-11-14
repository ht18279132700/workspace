<%@page contentType="text/html; charset=UTF-8" session="false"%>
<script src="/admin/bootstrap/js/bootbox.js" type="text/javascript"></script>
<script src="/admin/bootstrap/js/bootbox.locales.js" type="text/javascript"></script>
<script>
bootbox.setDefaults("locale","zh_CN");bootbox.setDefaults("locale","zh_CN");
window.alert = function(){
	var title = "提示信息";
	var content = "";
	if(arguments.length >= 2){
		title = arguments[0];
		content = arguments[1];
	}else if(arguments.length == 1){
		content = arguments[0];
	}
	bootbox.alert({
		size : "small",
		title : title,
		message : content
	});
};
window.confirm = function(){
	var title = "确认提示";
	var content = "";
	var callback = null;
	if(arguments.length >= 3){
		title = arguments[0];
		content = arguments[1];
		callback = arguments[2];
	}else if(arguments.length == 2){
		content = arguments[0];
		callback = arguments[1];
	}else if(arguments.length == 1){
		content = arguments[0];
	}
	bootbox.confirm({
		size : "small",
		title : title,
		message : content,
		callback : function(result) {
			if(result && typeof(callback) == "function"){
				callback();
			}
		}
	});
};
</script>