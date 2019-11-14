<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/common/import.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="renderer" content="webkit">
    <meta content="always" name="referrer">
    <!-- seo 信息 -->
    <title>${fn:escapeXml(keywords)}-太平洋汽车网</title>
    <meta name="keywords" content="${keywords}" />
    <meta name="description" content="" />
    <!-- qq传链接抓取信息 -->
    <meta itemprop="image" content="https://www1.pcauto.com.cn/zt/share/share_logo.jpg">
    <meta itemprop="name" content="车问答首页_车问答专题_太平洋汽车网">
    <!-- 专题设计和制作 -->
    <meta name="Author" content="tianjianhui_gz tianjianhui_gz">
    <!-- 设备跳转模块 S -->
    <script>
    <!--#include virtual="/global_ssi/pcauto/jump/index.html" --> 
    window.deviceJump && deviceJump.init({
        "main": "${ROOT}/search/${urlKeywords}", //这里需要自己手动写入pc版连接,如没有可为空
        "ipad": "", //这里需要自己手动写入ipad版连接,如没有可为空
        "wap": "${WAPROOT}/search/${urlKeywords}", //这里需要自己手动写入简版连接,如没有可为空
        "wap_3g": "${WAPROOT}/search/${urlKeywords}" //这里需要自己手动写入炫版连接,如没有可为空
    });
    </script>
    <!-- 设备跳转模块 E -->
	<link href="//js.3conline.com/pcautonew1/pc/2019/wenda/css/searchList.css" rel="stylesheet">
</head>

<body id="Jimg_load">
    <!-- 广告头 -->
    <script>
    if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'></scr" + "ipt>");
    </script>
    <!--计数器　SSI 上线生效 S-->
    <script>
    window._common_counter_code_ = "channel=9964";
    window._common_counter_uuid_ = "";
    window._common_counter_from_ = ""; // 如果是cms页面则填cms，非cms页面可为空或者忽略该参数
    <!--#include virtual="/global_ssi/pcauto/count/index.html" -->  
    </script>
    <!--计数器　SSI 上线生效 E-->
    <!--公共头部 SSI 上线生效 S-->
    <!--#include virtual="/global_ssi/pcauto/utf8/navibar/index.html" -->
    <!--公共头部 SSI 上线生效 E-->
    <!-- 专题内容区域 S -->
<div class="topheader">
    <div class="header">
	    <div class="section">
	        <div class="header-logo">
	            <a href="//www.pcauto.com.cn/">
	                <div class="header-logo-index"></div>
	            </a>
	            <a href="${ROOT}/">
	                <div class="header-logo-cwd"></div>
	            </a>
	        </div>
	         <div class="header-serch">
	            <div class="header-serch-fr" id="JmyQue">我要提问</div>
	            <div class="header-serch-fl">
	                <form id="Jsearch" action="" target="_blank">
	                	<input type="hidden" name="ad" value="1321" />
	                    <div class="serch-fl-l">
	                        <span class="fl-l-img"></span>
	                        <input id="Jsearch-input" type="text" class="search-input" placeholder="请输入您的问题" />
	                    </div>
	                    <div class="button-search" id="Jbutton-search">搜问题</div>
	                </form>
	            </div>
	        </div>
	    </div>
	</div>
</div>

<div class="section mt50">
    <div class="layAB">
        <!-- 搜索回答  -->
	<div class="title ">
	    <span class="mark"><span class="seach-rs" title="${fn:escapeXml(keywords)}"><span class="item keyword high">“${fn:escapeXml(keywords)}”</span><span class="item">相关结果，共<span class="high">${pageTotal}条</span></span></span></span>
	</div>
	<ul class="fl-newque-list">
	<c:forEach items="${SearchQuestions}" var="question">
        <li class="list2">
            <a href="${ROOT}/${question.id}.html" target="_blank">
                <div class="listtitle">${fn:escapeXml(question.title)}</div>
                <ul class="listinfo">
                    <li class="listinfo-reply">
                        <span class="icon iconfont iconxiaoxi"></span>
                        <c:if test="${question.answerNum eq 0}"><span class="num num0">${question.answerNum}</span></c:if>
                        <c:if test="${question.answerNum ne 0}"><span class="num">${question.answerNum}</span></c:if>
                        <span class="text">回答</span>
                    </li>
                    <li class="listinfo-time">
                        <span class="icon iconfont icondate"></span>
                        <span class="text">${question.showCreateAt}</span>
                    </li>
                    <li class="listinfo-user">
                        <span class="icon iconfont iconyonghu"></span>
                        <span class="text">提问者: ${question.user.nickName}</span>
                    </li>
                </ul>
                <c:if test="${question.mostPraiseAnswer.imageNum > 0 }">
                <div class="picText">
                    <img src="//www1.pcauto.com.cn/images/blank.gif" #src="${f:getImgSize(question.mostPraiseAnswer.firstPhoto.url, '240x160')}" alt=""
                        class="picText-pic">
                    <div class="picText-txt">${fn:escapeXml(question.mostPraiseAnswer.content) }</div>
                </div>
                </c:if>
                <c:if test="${question.mostPraiseAnswer.imageNum == 0 }">
                <div class="picText picText-nopic">
                    <div class="picText-txt">${fn:escapeXml(question.mostPraiseAnswer.content) }</div>
                </div>
                </c:if>
            </a>
        </li>
    </c:forEach>    
	</ul>
        <!-- 页码 -->
        <c:if test="${pageCount > 1}">
        <div class="pcauto_page"><tags:pager>${pageNo},${pageSize},${pageTotal}</tags:pager></div>
        </c:if>
    </div>
    <div class="layC">
    <div id="JscrollFixed">
		<div class="title ">
		    <span class="mark">推荐话题</span>
		</div>
	    <ul class="list clearfix">
	    <c:forEach items="${searchTags}" var="tag">
		    <li><a href="${ROOT}/topic/${tag.id}.html#ad=12707" target="_blank">${tag.name}</a></li>
	    </c:forEach>
		</ul>
    </div>
    </div>
</div>
	<!--#include virtual="/global_ssi/pcauto/utf8/footer/index.html" -->   
	<script>
	_submitIvyID();
	</script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/runtime.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/vendors.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/searchList.bundle.js"></script>
</body>
</html>