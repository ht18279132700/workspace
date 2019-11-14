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
    <title>${fn:escapeXml(question.title)}-太平洋汽车网</title>
    <meta name="keywords" content="${fn:escapeXml(question.title)}" />
    <meta name="description" content="${fn:escapeXml(descript)}" />
    <!-- qq传链接抓取信息 -->
    <meta itemprop="image" content="https://www1.pcauto.com.cn/zt/share/share_logo.jpg"/>
    <meta itemprop="name" content="车问答_车问答项目_太平洋汽车网"/>
    <!-- 项目设计和制作 -->
    <meta name="Author" content="tianjianhui_gz tianjianhui_gz"/>
    <link rel="canonical" href="${ROOT}/${question.id}.html">
    <!-- 页面适配js -->
    <script type="text/javascript">
        (function (e, t) { var i = document, n = window; var l = i.documentElement; var a, r; var d, s = document.createElement("style"); var o; function m() { var i = l.getBoundingClientRect().width; if (!t) { t = 540 } if (i > t) { i = t } var n = i * 100 / e; s.innerHTML = "html{font-size:" + n + "px;}" } a = i.querySelector('meta[name="viewport"]'); r = "width=device-width,initial-scale=1,maximum-scale=1.0,user-scalable=no,viewport-fit=cover"; if (a) { a.setAttribute("content", r) } else { a = i.createElement("meta"); a.setAttribute("name", "viewport"); a.setAttribute("content", r); if (l.firstElementChild) { l.firstElementChild.appendChild(a) } else { var c = i.createElement("div"); c.appendChild(a); i.write(c.innerHTML); c = null } } m(); if (l.firstElementChild) { l.firstElementChild.appendChild(s) } else { var c = i.createElement("div"); c.appendChild(s); i.write(c.innerHTML); c = null } n.addEventListener("resize", function () { clearTimeout(o); o = setTimeout(m, 300) }, false); n.addEventListener("pageshow", function (e) { if (e.persisted) { clearTimeout(o); o = setTimeout(m, 300) } }, false); if (i.readyState === "complete") { i.body.style.fontSize = "16px" } else { i.addEventListener("DOMContentLoaded", function (e) { i.body.style.fontSize = "16px" }, false) } })(750, 750);
    </script>
	<link href="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/css/detail.css" rel="stylesheet"></head>

<body>
    <!-- 广告头 -->
    <script>if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'><\/scr" + "ipt>");</script>

    <!--计数器　SSI 上线生效 S-->
    <script>
        window._common_counter_code_ = "channel=9968";
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
                <a href="${WAPROOT}/">车问答</a>&nbsp;&gt;&nbsp;
                <a href="${WAPROOT}/${question.id}.html">详情</a>
            </span>
        </div>
        <div class="m-content" id="Jscrolladd" data-qid="${question.id}">
            <div class="header">
                <div class="topic clearfix">
                    <div class="title padding15">${fn:escapeXml(question.title)}</div>
                    <ul class="list clearfix">
                    <c:forEach items="${tagList}" var="tag" begin="0" end="4">
                        <li><a href="${WAPROOT}/topic/${tag.id}.html#ad=12708" class="text">${tag.name}</a></li>
                    </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="wrapper clearfix">
                <div class="info">
                    <div class="img"><img src="${f:getUserIcon(question.createBy,50)}" alt=""></div>
                    <span class="name">${question.user.nickName }</span>
                    <span class="date">${question.showCreateAt}</span>
                </div>
                <div class="detail">
                    <div>${fn:escapeXml(question.content)}</div>
                    <c:forEach items="${question.photos}" var="photo">
                    <img src="${f:getImgSize(photo.url, '789x789')}" alt="">
                    </c:forEach>
                </div>
            </div>
            <div class="linkque clearfix">
                <div class="title">相关问答</div>
                <div class="answerbtn" id="JanswerQue">回答</div>
                <c:forEach items="${relateQuestion}" var="question" begin="0" end="4">
                <a href="${WAPROOT}/${question.id}.html#ad=12709"><p>${fn:escapeXml(question.title)}</p></a>
                </c:forEach>
            </div>
            <div class="allanswer" id="Jajaxdata">
            	<div class="title">所有回答</div>
                <c:forEach items="${pager.resultList }" var="answer">
                <div class="answerinfo clearfix" id="aid${answer.id}">
                    <div class="headpic"><img src="${f:getUserIcon(answer.createBy,50)}" alt=""></div>
                    <div class="other" >
                        <div class="name" id="uid${answer.createBy}">${answer.user.nickName}</div>
                        <div>${fn:escapeXml(answer.content)}</div>
                        <c:forEach items="${answer.photos }" var="photo">
                        <img src="${f:getImgSize(photo.url, '789x789')}" alt="">
                        </c:forEach>
                        <ul class="list4 clearfix">
                            <li class="date">${answer.showCreateAt}</li>
                            <li class="huifu" data-rid="" data-aid="${answer.id}" data-uid="${answer.createBy}"><span class="icon1" data-rid="" data-aid="${answer.id}" data-uid="${answer.createBy}"></span>回复</li>
                            <li class="cai" id="tread-${answer.id}" onclick="praiseAndTread(-1,${answer.id})">
                            <c:if test="${answer.praiseStatus eq -1 }"><span class="icon2alive"></span></c:if>
                            <c:if test="${answer.praiseStatus ne -1 }"><span class="icon2"></span></c:if>
                            <i>${answer.treadNum}</i>踩</li>
                            <li class="zan" id="praise-${answer.id}" onclick="praiseAndTread(1,${answer.id})">
                            <c:if test="${answer.praiseStatus eq 1 }"><span class="icon3alive"></span></c:if>
                            <c:if test="${answer.praiseStatus ne 1 }"><span class="icon3"></span></c:if>
                            <i>${answer.praiseNum}</i>有用</li>
                        </ul>
                    </div>
                    <c:forEach items="${answer.listReply }" var="reply">
                    <div class="others" data-rid="${reply.id}">
                        <div class="hfdetail"><span class="name" id="uid${reply.createBy}">${reply.user.nickName}</span>回复<span class="name">${reply.beRepliedUser.nickName}：</span>${fn:escapeXml(reply.content)}</div>
                        <ul class="list4 clearfix">
                            <li class="date">${reply.showCreateAt}</li>
                            <li class="huifu" data-rid="${reply.id }" data-aid="${answer.id }" data-uid="${reply.createBy }"><span class="icon1" data-rid="${reply.id }" data-aid="${answer.id }" data-uid="${reply.createBy }"></span>回复</li>
                        </ul>
                    </div>
                    </c:forEach>
                </div>
                </c:forEach>
            </div>
        </div>
        <div class="center marginb30" id="center"></div>
        <div class="loading"></div>
    </div>
    <div class="m-cmt-post-wrap displaynone" id="JcmtPostWrap" style="position:absolute;top:0;">
	    <div class="m-cmt-post" id="JcmtPost">
	        <div class="m-cmt-post-th">
	            <div class="m-cmt-post-th-l" id="JcmtPostClose">取消</div>
	            <div class="m-cmt-post-th-r"><input type="submit" name="" value="发送" id="JcmtSubmit"></div>
	            <div class="m-cmt-post-th-m" id="Jtitle">回复</div>
	        </div>
	        <div class="m-cmt-post-tb">
	        	<textarea rows="" cols="" id="JcmtCon"  placeholder="请输入内容..."></textarea>
	        </div>
	        <div class="m-cmt-post-identify" id="JcmtCaptchaWrap" style="display:none;">
	        	<span><input type="text" placeholder="" id="JcmtCaptcha"></span>
	        	<img src="//www.pcauto.com.cn/blank.gif" id="JcmtCaptchaImg">
	        </div>
	        <div class="m-cmt-post-tf">
	            <div class="m-cmt-post-tf-r" id="JcmtUser">
	            	<a class="m-cmt-post-tf-r-avt" href="${WAPUCROOT}">
	            		<img src="${f:getUserIcon(user.uid,50)}">
	            	</a>
	            </div>
	        </div>
	        <div class="m-cmt-post-emo-list disn" id="JcmtFaceList"></div>
	    </div>
	</div>
    <!-- 公共底部 SSI 上线生效 S -->
    <!--#include virtual="/global_ssi/pcauto/utf8/wap_footer/index.html" -->
    <!-- 公共底部 SSI 上线生效 E -->
    <!-- 项目内容区域 E -->
    <script type="text/javascript">
    	var pageParam = {pageCount:"${pageCount}",userId:"${user.uid}",wdRoot:"${oldRoot}",qid:"${question.id}",WAPUCROOT:"${WAPUCROOT}",WAPROOT:"${WAPROOT}"};
    </script>
    <!--页面脚本区S-->
    <!--所有页面用到的js脚本都必须放到此位置，包括外链js和内嵌js-->
    <script src="//js.3conline.com/min/temp/v1/lib-jquery1.10.2.js"></script>
    <script src="//js.3conline.com/min/temp/v1/lib-jquery.cookie.js"></script>
    <script src="//js.3conline.com/min2/temp/v2/plugin-lazy.js" charset="utf-8"></script>
    <!--页面脚本区E-->
    <!-- 广告尾 -->
    <script>_submitIvyID();</script>
    <script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/runtime.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/vendors.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/wap/2019/seocwdwap/js/detail.bundle.js"></script>
</body>
</html>