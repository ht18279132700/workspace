<%@page contentType="text/html" pageEncoding="utf-8" session="false"%><%@include
	file="/WEB-INF/jspf/import.jspf"%>
<%
	GeliUser geliUser = GeliSession.getCurrentUser();
	if (geliUser == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	pageContext.setAttribute("_USER_", geliUser);

	Env env = EnvUtils.getEnv();
	GeliAuthFacade authFacade = env.getBean(GeliAuthFacade.class);
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=7" />
<title>wenda - pcgroups</title>

<link href="themes/green/style.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="themes/css/core.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="themes/css/print.css" rel="stylesheet" type="text/css"
	media="print" />
<link href="themes/css/zTreeTag.css" rel="stylesheet" type="text/css" />
<link href="themes/css/zTreeStyle.css" rel="stylesheet" type="text/css" />
<link href="/admin/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css" />
<!--[if IE]>
<link href="themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->
<script src="js/speedup.js" type="text/javascript"></script>
<script src="js/jquery-1.9.1.js" type="text/javascript"></script>
<script src="js/jquery.cookie.js" type="text/javascript"></script>
<script src="js/jquery.validate.js" type="text/javascript"></script>
<script src="js/jquery.bgiframe.js" type="text/javascript"></script>
<script src="/admin/js/UPC.js"></script>
<script src="js/dwz.core.js" type="text/javascript"></script>
<script src="js/dwz.util.date.js" type="text/javascript"></script>
<script src="js/dwz.validate.method.js" type="text/javascript"></script>
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="js/dwz.barDrag.js" type="text/javascript"></script>
<script src="js/dwz.drag.js" type="text/javascript"></script>
<script src="js/dwz.tree.js" type="text/javascript"></script>
<script src="js/dwz.accordion.js" type="text/javascript"></script>
<script src="js/dwz.ui.js" type="text/javascript"></script>
<script src="js/dwz.theme.js" type="text/javascript"></script>
<script src="js/dwz.switchEnv.js" type="text/javascript"></script>
<script src="js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="js/dwz.contextmenu.js" type="text/javascript"></script>
<script src="js/dwz.navTab.js" type="text/javascript"></script>
<script src="js/dwz.tab.js" type="text/javascript"></script>
<script src="js/dwz.resize.js" type="text/javascript"></script>
<script src="js/dwz.dialog.js" type="text/javascript"></script>
<script src="js/dwz.dialogDrag.js" type="text/javascript"></script>
<script src="js/dwz.sortDrag.js" type="text/javascript"></script>
<script src="js/dwz.cssTable.js" type="text/javascript"></script>
<script src="js/dwz.stable.js" type="text/javascript"></script>
<script src="js/dwz.taskBar.js" type="text/javascript"></script>
<script src="js/dwz.ajax.js" type="text/javascript"></script>
<script src="js/dwz.pagination.js" type="text/javascript"></script>
<script src="js/dwz.database.js" type="text/javascript"></script>
<script src="js/dwz.datepicker.js" type="text/javascript"></script>
<script src="js/dwz.effects.js" type="text/javascript"></script>
<script src="js/dwz.panel.js" type="text/javascript"></script>
<script src="js/dwz.checkbox.js" type="text/javascript"></script>
<script src="js/dwz.history.js" type="text/javascript"></script>
<script src="js/dwz.combox.js" type="text/javascript"></script>
<script src="js/dwz.print.js" type="text/javascript"></script>
<script src="js/jquery.ztree.core.js" type="text/javascript"></script>
<!--
<script src="bin/dwz.min.js" type="text/javascript"></script>
-->
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="/admin/bootstrap/js/bootstrap.js" type="text/javascript"></script>
<jsp:include page="common/alert.jsp" />
<style>
h2{
    font-size: 1rem;
}
</style>
<script type="text/javascript">
	$(function() {
		DWZ.init("dwz.frag.xml", {
			loginUrl : "login.jsp",
			statusCode : {
				ok : 200,
				error : 300,
				timeout : 301
			},
			pageInfo : {
				pageNum : "pageNum",
				numPerPage : "numPerPage",
				orderField : "orderField",
				orderDirection : "orderDirection"
			},
			debug : false,
			callback : function() {
				initEnv();
				$("#themeList").theme({
					themeBase : "themes"
				});
			}
		});
	});
</script>
</head>
<body scroll="no">
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<a class="logo" href="${ctx}/admin/index.jsp" style="width: 100px;">logo</a>
				<div
					style="float: left; color: white; font-size: 28px; padding-top: 15px;">车问答</div>
				<ul class="nav">
					<li><a href="javascript:;">欢迎您，${_USER_.name}</a></li>
					<li><a href="${ctx}/admin/login.jsp?action=logout">退出系统</a></li>
				</ul>
				<ul class="themeList" id="themeList">
					<li theme="default"><div></div></li>
					<li theme="green"><div class="selected"></div></li>
					<li theme="purple"><div></div></li>
					<li theme="silver"><div></div></li>
					<li theme="azure"><div></div></li>
				</ul>
			</div>
		</div>

		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse">
						<div></div>
					</div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse">
					<h2>主菜单</h2>
					<div>-</div>
				</div>

				<div class="accordion" fillSpace="sidebar">
					<div class="accordionHeader">
						<h2>
							<span>Folder</span>话题标签
						</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a href="${ctx}/admin/tag/brandList.do" target="navTab" external="true"
								rel="list-brand">品牌/车系</a></li>
							<li><a href="${ctx}/admin/tag/firstList.do" target="navTab" external="true"
								rel="list-keyword">关键词</a></li>
							<li><a href="${ctx}/admin/tag/hotTag.do" target="navTab" external="true"
								rel="list-hotTag">标签热度</a></li>
							<li><a href="javascript:;" type="button" onclick="deleteTagCache()"
								rel="list-keyword" style="color:red">清理标签缓存</a></li>
						</ul>
					</div>
					<div class="accordionHeader">
                        <h2>
                            <span>Folder</span>问题列表
                        </h2>
                    </div>
                    <div class="accordionContent">
                        <ul class="tree treeFolder">
                            <li><a href="${ctx}/admin/question/brandQuestion.do?" target="navTab"
                                rel="question-brand">品牌/车系问题</a></li>
                            <li><a href="${ctx}/admin/question/keywordQuestion.do?" target="navTab"
                                rel="question-keyword">关键词问题</a></li>
                            <li><a href="${ctx}/admin/question/question.do?tid=0" target="navTab" external="true"
                                rel="question-other">其他</a></li>
                            <li><a href="${ctx}/admin/questionImport/index.do" target="navTab"
                                rel="question-import">SEO导入</a></li>
                        </ul>
                    </div>
                    <div class="accordionHeader">
                        <h2>
                            <span>Folder</span>查询统计
                        </h2>
                    </div>
                    <div class="accordionContent">
                        <ul class="tree treeFolder">
                          <li>
                            <a href="${ctx}/admin/wdDailyStat/list.do" target="navTab" external="true" rel="count-wd-daily">问答数日计</a>
                          </li>
                        </ul>
                    </div>
					<div class="accordionHeader">
						<h2>
							<span>Folder</span>权限设置
						</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a href="${ctx}/admin/gelifunction/list.do"
								target="navTab" rel="list-gelifunction">功能列表</a></li>
							<li><a href="${ctx}/admin/gelirole/list.do" target="navTab"
								rel="list-gelirole">角色列表</a></li>
							<li><a href="${ctx}/admin/gelirolefunction/list.do"
								target="navTab" rel="list-gelirolefunction">角色功能</a></li>
							<li><a href="${ctx}/admin/geliacl/list.do" target="navTab"
								rel="list-geliacl">访问控制</a></li>
							<li><a href="${ctx}/admin/geliuser/list.do" target="navTab"
								rel="list-geliuser">用户列表</a></li>
							<li><a
								href="${ctx}/admin/gelilog/list.do?orderField=logId&orderDirection=desc"
								target="navTab" rel="list-gelilog">操作日志</a></li>
							<li><a
								href="${ctx}/admin/gelilogdetail/list.do?orderField=logDetailId&orderDirection=desc"
								target="navTab" rel="list-gelilogdetail">日志数据</a></li>
							<li><a href="${ctx}/admin/gelitool/list.do" target="navTab"
								rel="list-gelitool">代码定制</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>

		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent">
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span
										class="home_icon">我的主页</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div>
					<div class="tabsRight">right</div>
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">我的主页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						<div class="pageFormContent" layoutH="80"
							style="margin-right: 230px">欢迎使用车问答后台！</div>
					</div>
				</div>
			</div>
		</div>

	</div>
<script>
	function deleteTagCache(){
		confirm("频繁使用此功能会降低系统性能，确认要继续清理吗？", function(){
			$.getJSON("/admin/tag/deleteTagCache.do", {}, function(data) {
				if (data.code == 0) {
					alert("清理成功");
				}
			});
		});
	}
</script>
</body>
</html>