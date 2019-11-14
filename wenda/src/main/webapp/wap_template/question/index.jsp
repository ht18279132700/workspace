<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ include file="/common/import.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta content="telephone=no" name="format-detection" />
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0,user-scalable=no,viewport-fit=cover" />
    <meta content="always" name="referrer">
    <!-- seo 信息 -->
    <title>车问答-太平洋汽车网</title>
    <meta name="keywords" content="车问答、汽车问题、汽车知识" />
    <meta name="description" content="太平洋汽车网车问答频道，为用户解决汽车生活中遇到的问题，包括选车、买车、用车养车、知识技巧等，并将关注同一话题的用户聚集在一起-有汽车疑问，就上太平洋汽车网。" />
    <!-- qq传链接抓取信息 -->
    <meta itemprop="image" content="https://www1.pcauto.com.cn/zt/share/share_logo.jpg">
    <meta itemprop="name" content="车问答_车问答项目_太平洋汽车网">
    <!-- 项目设计和制作 -->
    <meta name="Author" content="tianjianhui_gz tianjianhui_gz">
    <link rel="canonical" href="${ROOT}/">
    <!-- 页面适配js -->
    <script type="text/javascript">
        (function (e, t) { var i = document, n = window; var l = i.documentElement; var a, r; var d, s = document.createElement("style"); var o; function m() { var i = l.getBoundingClientRect().width; if (!t) { t = 540 } if (i > t) { i = t } var n = i * 100 / e; s.innerHTML = "html{font-size:" + n + "px;}" } a = i.querySelector('meta[name="viewport"]'); r = "width=device-width,initial-scale=1,maximum-scale=1.0,user-scalable=no,viewport-fit=cover"; if (a) { a.setAttribute("content", r) } else { a = i.createElement("meta"); a.setAttribute("name", "viewport"); a.setAttribute("content", r); if (l.firstElementChild) { l.firstElementChild.appendChild(a) } else { var c = i.createElement("div"); c.appendChild(a); i.write(c.innerHTML); c = null } } m(); if (l.firstElementChild) { l.firstElementChild.appendChild(s) } else { var c = i.createElement("div"); c.appendChild(s); i.write(c.innerHTML); c = null } n.addEventListener("resize", function () { clearTimeout(o); o = setTimeout(m, 300) }, false); n.addEventListener("pageshow", function (e) { if (e.persisted) { clearTimeout(o); o = setTimeout(m, 300) } }, false); if (i.readyState === "complete") { i.body.style.fontSize = "16px" } else { i.addEventListener("DOMContentLoaded", function (e) { i.body.style.fontSize = "16px" }, false) } })(750, 750);
    </script>
	<link href="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/css/index.css" rel="stylesheet">
</head>

<body>
    <!-- 广告头 -->
    <script>if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'><\/scr" + "ipt>");</script>

    <!--计数器　SSI 上线生效 S-->
    <script>
        window._common_counter_code_ = "channel=9966";
        window._common_counter_uuid_ = "";
        window._common_counter_from_ = "";// 如果是cms页面则填cms，非cms页面可为空或者忽略该参数
	<!--#include virtual = "/global_ssi/pcauto/count/index.html"-->
    </script>
    <!--计数器　SSI 上线生效 E-->



    <div class="g-doc" id="Jimg_load">
        <!-- 项目内容区域 S -->

        <!-- 公共头部 SSI 上线生效 S -->
        <!--#include virtual="/global_ssi/pcauto/utf8/wap_header/v2/index.html" -->
        <!-- 公共头部 SSI 上线生效 E -->
        <div class="m-crumb">
            <span class="crumb">
                <a href="javascript:history.back();" class="goBack"></a>
                <a href="//m.pcauto.com.cn/">首页</a>&nbsp;&gt;&nbsp;
                <a href="${WAPROOT}/">车问答</a>
            </span>
        </div>
        <div class="m-content" id="Jscrolladd">
            <div class="header">
                <div class="search">
                    <div class="search-left">
                        <div class="left-pic"></div>
                        <input type="text" id="serchText" class="left-ipt" placeholder="请输入您的问题">
                    </div>
                    <div class="search-right" id="serchQue">搜索</div>
                </div>
                <div class="topic clearfix">
                    <div class="title">热门话题</div>
                    <ul class="list clearfix">
                    <c:forEach items="${tags}" var="tag">
                        <li><a href="${WAPROOT}/topic/${tag.id}.html#ad=12711" class="text">${tag.name}</a></li>
                    </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="section clearfix smargin">
                <div class="title">最新问题</div>
                <ul class="list2" id="Jajaxdata">
                <c:forEach items="${pager.resultList}" var="question">
                    <li>
                        <a href="${WAPROOT}/${question.id}.html">
                            <div class="arttitle">${fn:escapeXml(question.title)}</div>
                            <ul class="list3 clearfix">
                                <li><span class="mespic"></span>
                                	<c:if test="${question.answerNum eq 0}"><span class="num0">${question.answerNum}</span></c:if>
                                	<c:if test="${question.answerNum ne 0}"><span class="num">${question.answerNum}</span></c:if>
                                	<span>回答</span>
                                </li>
                                <li>提问者: ${question.user.nickName}</li>
                                <li>${question.showCreateAt}</li>
                            </ul>
                            <div class="artcont">
                            	<c:if test="${question.mostPraiseAnswer.imageNum > 0 }">
                                	<img src="//www1.pcauto.com.cn/images/blank.gif"  #src="${f:getImgSize(question.mostPraiseAnswer.firstPhoto.url, '240x160')}" alt="" class="img">
                            	</c:if>
                                <div class="cont">${fn:escapeXml(question.mostPraiseAnswer.content) }</div>
                            </div>
                        </a>
                    </li>
                </c:forEach>
                </ul>
            </div>
        </div>
        <div class="center marginb30" id="center"></div>
        <div class="loading"></div>
    </div>
    <!-- 公共底部 SSI 上线生效 S -->
    <!--#include virtual="/global_ssi/pcauto/utf8/wap_footer/index.html" -->
    <!-- 公共底部 SSI 上线生效 E -->
    <!-- 项目内容区域 E -->
    <!--页面脚本区S-->
    <script type="text/javascript">
    	var pageParam = {pageCount:"${pageCount}",userId:"${user.uid}",wdRoot:"${oldRoot}",WAPROOT:"${WAPROOT}"};
    </script>
    <!--所有页面用到的js脚本都必须放到此位置，包括外链js和内嵌js-->
    <script src="//js.3conline.com/min/temp/v1/lib-jquery1.10.2.js"></script>
    <script src="//js.3conline.com/min2/temp/v2/plugin-lazy.js" charset="utf-8"></script>
    <!--页面脚本区E-->
    <!-- 广告尾 -->
    <script>_submitIvyID();</script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/runtime.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/vendors.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/index.bundle.js"></script>
</body>
</html>