<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/common/import.jsp"%>
<!doctype HTML>
<html>
<head>
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0,user-scalable=no,viewport-fit=cover"/>
    <meta content="black" name="apple-mobile-web-app-status-bar-style" />
    <meta content="telephone=no" name="format-detection" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf8" />
    <title>车问答话题汇总_车问答_太平洋汽车网</title>
    <meta name="keywords" content="汽车问答，汽车热门话题，热门话题" />
    <meta name="description" content="太平洋汽车网为您提供热门汽车问答热门话题等相关内容。" />
    <meta name="author" content="yangronghan_gz none" />
    <meta content="always" name="referrer" />

<link href="//js.3conline.com/pcautonew1/pc/2019/cwdgjc/css/index.css" rel="stylesheet"></head>
<body id="Jlazy_img">
<!--如有广告 则需要有广告头引入 如-->
<script>
if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'></scr" + "ipt>");
</script>
<!-- 栏目计数器 -->
<!-- 注意：更新正确的计数器 channel 值并且删除注析即可。如无计数器请删除以下代码 -->
<script>
	window._common_counter_code_ = "";
	window._common_counter_uuid_ = "";
	window._common_counter_from_ = "";
	<!--#include virtual="/global_ssi/pcauto/count/index.html" -->
</script>

<!-- 公共导航条 -->
	<!--#include virtual="/global_ssi/pcauto/utf8/navibar/index.html" -->
<!-- 公共头部 -->
	<!--#include virtual="/global_ssi/pcauto/utf8/c_header/index.html" -->

<div class="doc">
	<div class="chaNav">
	    <div class="chaTop">
	        <div class="chaTit">车问答话题</div>
	    </div>
	</div>
	<div class="search" id="kuaisou">
	    <form target="_blank" method="post" action="//ks.pcauto.com.cn/" id="mainSNSSearchFromId" name="mainSNSSearchFrom">
	        <input autocomplete="off" name="q" id="ksWord" class="inpTxt" type="text" value="">
	        <input name="Submit" onClick="javascript:document.getElementById('mainSNSSearchFromId').submit();" class="inpBtn" type="button" value="搜 索">
	    </form>
	</div>

	<div class="brandindex cle">
	  <div class="list">
	    <a href="${ROOT}/topic/" <c:if test="${letter eq 'hot' }">class="cur"</c:if> >热门</a>
	    <a href="${ROOT}/topic/A.html" <c:if test="${letter eq 'A' }">class="cur"</c:if> >A</a>
	    <a href="${ROOT}/topic/B.html" <c:if test="${letter eq 'B' }">class="cur"</c:if> >B</a>
	    <a href="${ROOT}/topic/C.html" <c:if test="${letter eq 'C' }">class="cur"</c:if> >C</a>
	    <a href="${ROOT}/topic/D.html" <c:if test="${letter eq 'D' }">class="cur"</c:if> >D</a>
	    <a href="${ROOT}/topic/E.html" <c:if test="${letter eq 'E' }">class="cur"</c:if> >E</a>
	    <a href="${ROOT}/topic/F.html" <c:if test="${letter eq 'F' }">class="cur"</c:if> >F</a>
	    <a href="${ROOT}/topic/G.html" <c:if test="${letter eq 'G' }">class="cur"</c:if> >G</a>
	    <a href="${ROOT}/topic/H.html" <c:if test="${letter eq 'H' }">class="cur"</c:if> >H</a>
	    <a href="${ROOT}/topic/I.html" <c:if test="${letter eq 'I' }">class="cur"</c:if> >I</a>
	    <a href="${ROOT}/topic/J.html" <c:if test="${letter eq 'J' }">class="cur"</c:if> >J</a>
	    <a href="${ROOT}/topic/K.html" <c:if test="${letter eq 'K' }">class="cur"</c:if> >K</a>
	    <a href="${ROOT}/topic/L.html" <c:if test="${letter eq 'L' }">class="cur"</c:if> >L</a>
	    <a href="${ROOT}/topic/M.html" <c:if test="${letter eq 'M' }">class="cur"</c:if> >M</a>
	    <a href="${ROOT}/topic/N.html" <c:if test="${letter eq 'N' }">class="cur"</c:if> >N</a>
	    <a href="${ROOT}/topic/O.html" <c:if test="${letter eq 'O' }">class="cur"</c:if> >O</a>
	    <a href="${ROOT}/topic/P.html" <c:if test="${letter eq 'P' }">class="cur"</c:if> >P</a>
	    <a href="${ROOT}/topic/Q.html" <c:if test="${letter eq 'Q' }">class="cur"</c:if> >Q</a>
	    <a href="${ROOT}/topic/R.html" <c:if test="${letter eq 'R' }">class="cur"</c:if> >R</a>
	    <a href="${ROOT}/topic/S.html" <c:if test="${letter eq 'S' }">class="cur"</c:if> >S</a>
	    <a href="${ROOT}/topic/T.html" <c:if test="${letter eq 'T' }">class="cur"</c:if> >T</a>
	    <a href="${ROOT}/topic/U.html" <c:if test="${letter eq 'U' }">class="cur"</c:if> >U</a>
	    <a href="${ROOT}/topic/V.html" <c:if test="${letter eq 'V' }">class="cur"</c:if> >V</a>
	    <a href="${ROOT}/topic/W.html" <c:if test="${letter eq 'W' }">class="cur"</c:if> >W</a>
	    <a href="${ROOT}/topic/X.html" <c:if test="${letter eq 'X' }">class="cur"</c:if> >X</a>
	    <a href="${ROOT}/topic/Y.html" <c:if test="${letter eq 'Y' }">class="cur"</c:if> >Y</a>
	    <a href="${ROOT}/topic/Z.html" <c:if test="${letter eq 'Z' }">class="cur"</c:if> >Z</a>
	  </div>
	</div>
	<c:if test="${ letter ne 'hot' }">
	<div class="content">
		<div class="tag-box">
			<c:forEach items="${pager.resultList}" var="tag" varStatus="status">
			    <span>
			        <a href="${ROOT}/topic/${tag.id}.html" target="_blank">${tag.name}</a>
			    </span>
			    <c:if test="${status.count % 60 == 0}"><div class="tag-item"></div></c:if>
			</c:forEach>
		</div>
		<!-- 页码 -->
        <c:if test="${pager.pageCount > 1}">
        <div class="pcauto_page"><tags:pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:pager></div>
        </c:if>
	</div>
	</c:if>
	<c:if test="${ letter eq 'hot' }">
	<div class="content">
		<div class="tag-box">
			<c:forEach items="${list}" var="tag" varStatus="status">
		    <span>
		        <a href="${ROOT}/topic/${tag.id}.html" target="_blank">${tag.name}</a>
		    </span>
			<c:if test="${status.count % 60 == 0}"><div class="tag-item"></div></c:if>
			</c:forEach>
		</div>
	</div>
	</c:if>
</div>

<!-- 公共底部 -->
	<!--#include virtual="/global_ssi/pcauto/utf8/footer/index.html" -->

<!--页面脚本区S-->
<script src="//www.pcauto.com.cn/price/000056330/1306/intf1868.js" class=" defer" charset="gbk"></script>
<script src="//js.3conline.com/pcauto/common/js/search_v3.js" class=" defer" charset="gbk"></script>
<script src="//js.3conline.com/pcauto/common/js/brandSelect.m.js" class=" defer" charset="gbk"></script>
<script src="//js.3conline.com/wap/pcauto/common/auto_wxShare.js"></script>


<!-- 开发写的js -->

<!--页面脚本区E-->

<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/cwdgjc/js/runtime.bundle.js"></script>
<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/cwdgjc/js/vendors.bundle.js"></script>
<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/cwdgjc/js/index.bundle.js"></script>
</body>
</html>