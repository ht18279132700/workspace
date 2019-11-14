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
    <title>车问答-太平洋汽车网</title>
    <meta name="keywords" content="车问答、汽车问题、汽车知识" />
    <meta name="description" content="太平洋汽车网车问答频道，为用户解决汽车生活中遇到的问题，包括选车、买车、用车养车、知识技巧等，并将关注同一话题的用户聚集在一起-有汽车疑问，就上太平洋汽车网。" />
    <!-- qq传链接抓取信息 -->
    <meta itemprop="image" content="https://www1.pcauto.com.cn/zt/share/share_logo.jpg">
    <meta itemprop="name" content="车问答首页_车问答项目_太平洋汽车网">

	<!-- 项目设计和制作 -->
    <meta name="Author" content="tianjianhui_gz liyichun_gz">
    
    <meta name="applicable-device" content="pc" />

	<meta name="mobile-agent" content="format=xhtml; url=${WAPROOT}/" />

	<meta name="mobile-agent" content="format=html5; url=${WAPROOT}/">

	<link rel="alternate" media="only screen and(max-width: 640px)" href="${WAPROOT}/" />

    <!-- 设备跳转模块 S -->
    <script>
    <!--#include virtual="/global_ssi/pcauto/jump/index.html" -->
    window.deviceJump && deviceJump.init({
                "main": "${ROOT}/",//这里需要自己手动写入pc版连接,如没有可为空
                "ipad": "",//这里需要自己手动写入ipad版连接,如没有可为空
                "wap": "${WAPROOT}/",//这里需要自己手动写入简版连接,如没有可为空
                "wap_3g": "${WAPROOT}/"//这里需要自己手动写入炫版连接,如没有可为空
            });
    </script>
    <!-- 设备跳转模块 E -->
	<link href="//js.3conline.com/pcautonew1/pc/2019/wenda/css/index.css" rel="stylesheet">
</head>
<body id="Jimg_load">
    <!-- 广告头 -->
    <script>if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'><\/scr" + "ipt>");</script>

    <!--计数器　SSI 上线生效 S-->
    <script>
        window._common_counter_code_ = "channel=9961";
        window._common_counter_uuid_ = "";
        window._common_counter_from_ = "";// 如果是cms页面则填cms，非cms页面可为空或者忽略该参数
        <!--#include virtual="/global_ssi/pcauto/count/index.html" -->
    </script>
    <!--计数器　SSI 上线生效 E-->

    <!--公共头部 SSI 上线生效 S-->
    <!--#include virtual="/global_ssi/pcauto/utf8/navibar/index.html" -->
    <!--公共头部 SSI 上线生效 E-->

    <!-- 项目内容区域 S -->
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
    <div class="section mt50">
        <div class="layAB">
            <!-- 热门话题 -->
            <div class="content-fl-topic">
                <div class="title">
                    <span class="mark">热门话题</span>
                    <a href="${ROOT}/topic/" class="gjcmore" target="_blank">查看更多标签>></a>
                </div>
                 <ul class="list clearfix">
                 <c:forEach items="${tags}" var="tag">
                    <li><a href="${ROOT}/topic/${tag.id}.html#ad=12705" target="_blank">${tag.name}</a></li>
                 </c:forEach>
                </ul>
            </div>
            <!-- 最新问答 -->
            <div class="content-fl-result mt40">
               <div class="title">
                    <span class="mark">最新问答</span>
                </div>
                <ul class="fl-newque-list">
                <c:forEach items="${pager.resultList}" var="question">
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
                <c:if test="${pager.pageCount > 1}">
                <div class="pcauto_page"><tags:pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:pager></div>
                </c:if>
            </div>
        </div>
        <div class="layC">
        <div id="JscrollFixed">
            <!-- 热门问答 -->
            <div class="title">
                <span class="mark">热门问答</span>
            </div>
            <ul class="list3">
            <c:forEach items="${hotQuestions}" var="question">
                <li class="list3-item">
                    <a href="${ROOT}/${question.id}.html#ad=12706" target="_blank">
                        <div class="list3-detail">${fn:escapeXml(question.title)}</div>
                        <ul class="listinfo">
                           <li class="listinfo-reply">
                               <span class="icon iconfont iconxiaoxi"></span>
                               <c:if test="${question.answerNum eq 0}"><span class="num num0">${question.answerNum}</span></c:if>
                               <c:if test="${question.answerNum ne 0}"><span class="num">${question.answerNum}</span></c:if>
                               <span class="text">回答</span>
                           </li>
                        </ul>
                    </a>
                </li>
            </c:forEach>
            </ul>
        </div>
        </div>
    </div>
    <!-- 项目内容区域 E -->
    
    <!--车问答增加友情链接和热点标签20190422 S-->
	<div class="section tab2 group-recommend" id="JgroupTab">
    <div class="thA">
    <div class="pannelx tab-ctrl">
    <span class="tab-toc cur">友情链接</span>
    <span class="tab-toc">热点标签</span>
    </div>
    </div>
    <div class="tabwrap">
    <div class="tabcon" style="display: block;">
    <div class="tbody dText dText3">
	  <plugin:rclient url="http://www.pcauto.com.cn/06fz/seonq/1904/intf12843.html"/>
    </div>
    </div>	    
    <div class="tabcon" style="display: none;">
    <div class="tbody dText dText3">
	  <plugin:rclient url="http://www.pcauto.com.cn/06fz/seonq/1904/intf12844.html"/>
    </div>
    </div>		
    </div>
    </div>
    <!--车问答增加友情链接和热点标签20190422 E-->
    
    <!-- 公共底部 SSI 上线生效 S -->
    <!--#include virtual="/global_ssi/pcauto/utf8/footer/index.html" -->
    <!-- 公共底部 SSI 上线生效 E -->
    <script>_submitIvyID();</script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/runtime.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/vendors.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/index.bundle.js"></script>
</body>
</html>