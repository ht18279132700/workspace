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
    <title>${fn:escapeXml(question.title)}-太平洋汽车网</title>
    <meta name="keywords" content="${fn:escapeXml(question.title)}" />
    <meta name="description" content="${fn:escapeXml(descript)}" />
    <!-- qq传链接抓取信息 -->
    <meta itemprop="image" content="https://www1.pcauto.com.cn/zt/share/share_logo.jpg">
    <meta itemprop="name" content="车问答首页_车问答项目_太平洋汽车网">
    <!-- 项目设计和制作 -->
    <meta name="Author" content="guoxuemin_gz liyichun_gz">
    
    <meta name="applicable-device" content="pc" />

	<meta name="mobile-agent" content="format=xhtml; url=${WAPROOT}/${question.id}.html" />

	<meta name="mobile-agent" content="format=html5; url=${WAPROOT}/${question.id}.html">

	<link rel="alternate" media="only screen and(max-width: 640px)" href="${WAPROOT}/${question.id}.html" />
    
    <!-- 设备跳转模块 S -->
    <script>
        <!--#include virtual="/global_ssi/pcauto/jump/index.html" -->
        window.deviceJump && deviceJump.init({
            "main": "${ROOT}/${question.id}.html",//这里需要自己手动写入pc版连接,如没有可为空
            "ipad": "",//这里需要自己手动写入ipad版连接,如没有可为空
            "wap": "${WAPROOT}/${question.id}.html",//这里需要自己手动写入简版连接,如没有可为空
            "wap_3g": "${WAPROOT}/${question.id}.html"//这里需要自己手动写入炫版连接,如没有可为空
        });
    </script>
    <!-- 设备跳转模块 E -->
	<link href="//js.3conline.com/pcautonew1/pc/2019/wenda/css/detail.css" rel="stylesheet">
</head>

<body id="Jimg_load">
    <!-- 广告头 -->
    <script>if (!window._addIvyID) document.write("<scr" + "ipt src='//www.pconline.com.cn/_hux_/auto/default/index.js'><\/scr" + "ipt>");</script>
    <!--计数器　SSI 上线生效 S-->
    <script>
        window._common_counter_code_ = "channel=9963";
        window._common_counter_uuid_ = "";
        window._common_counter_from_ = "";// 如果是cms页面则填cms，非cms页面可为空或者忽略该参数

    <!--#include virtual="/global_ssi/pcauto/count/index.html" -->

    </script>
    <!--计数器　SSI 上线生效 E-->
    <!--公共头部 SSI 上线生效 S-->
    <!--#include virtual="/global_ssi/pcauto/utf8/navibar/index.html" -->
    <!--公共头部 SSI 上线生效 E-->
    <!-- 项目内容区域 S -->
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
    <div class="section mt40">
        <div class="layAB">
            <ul class="list clearfix">
            <c:forEach items="${tagList}" var="tag" begin="0" end="4">
                <li><a href="${ROOT}/topic/${tag.id}.html#ad=12701" target="_blank">${tag.name}</a></li>
            </c:forEach>
            </ul>
            <div class="topic" data-qid="${question.id}">
                <div class="topic-title">${fn:escapeXml(question.title)}</div>
                <div class="topic-info clearfix">
                    <img class="item avata" src="${f:getUserIcon(question.createBy,50)}" alt="" />
                    <span class="item author">${question.user.nickName }</span>
                    <span class="item date">${question.showCreateAt}</span>
                </div>
            </div>
            <div class="topic-content">
                <p>${fn:escapeXml(question.content)}</p>
                <c:forEach items="${question.photos}" var="photo">
                <p class="imgbox"><img src="//www1.pcauto.com.cn/images/blank.gif" #src="${f:getImgSize(photo.url, '790x790')}" alt=""></p>
                </c:forEach>
            </div>
            <div id="Janswerbox-tag" class="mt40"></div>
            <div class="answer mt40">
                <div class="answer-title">${question.answerNum}个回答</div>
                <ul id="JcmtList" class="cmt-list answer-list">
                <c:forEach items="${pager.resultList }" var="answer">
                    <li class="cmt-item" data-aid="${answer.id}">
                        <div class="cmt-info">
                            <img src="${f:getUserIcon(answer.createBy,50)}" alt="" class="cmt-avata" />
                            <span class="cmt-user">${answer.user.nickName}</span>
                            <span class="cmt-date">${answer.showCreateAt}</span>
                        </div>
                        <div class="cmt-content">
                            <p>${fn:escapeXml(answer.content)}</p>
                            <c:forEach items="${answer.photos }" var="photo">
                            <p><img src="//www1.pcauto.com.cn/images/blank.gif" #src="${f:getImgSize(photo.url, '790x790')}" alt="" /></p>
                            </c:forEach>
                        </div>
                        <div class="cmt-bar">
                        <c:if test="${answer.praiseStatus eq 1 }">
                            <span data-type="1" class="Jpraise icon iconfont iconzan active" id="praise-${answer.id }"><span class="num">${answer.praiseNum}</span>有用</span>
                        </c:if>
                        <c:if test="${answer.praiseStatus ne 1 }">
                        	<span data-type="1" class="Jpraise icon iconfont iconzan" id="praise-${answer.id }"><span class="num">${answer.praiseNum}</span>有用</span>
                        </c:if>
                        <c:if test="${answer.praiseStatus eq -1 }">
                        	<span data-type="-1" class="Jtread icon iconfont iconcai active" id="tread-${answer.id }"><span class="num">${answer.treadNum}</span>踩</span>
                        </c:if>
                        <c:if test="${answer.praiseStatus ne -1 }">
                            <span data-type="-1" class="Jtread icon iconfont iconcai" id="tread-${answer.id }"><span class="num">${answer.treadNum}</span>踩</span>
                        </c:if>
                            <span class="Jreply icon iconfont iconxiaoxi">回复</span>
                        </div>
	                    <ul class="cmt-list reply-list">
						<c:forEach items="${answer.listReply }" var="reply">
							<li class="cmt-item"  data-rid="${reply.id}">
							    <div class="cmt-info">
							        <img src="//www1.pcauto.com.cn/images/blank.gif" #src="${f:getUserIcon(reply.createBy,50)}" alt="" class="cmt-avata"/>
							        <span class="cmt-user">${reply.user.nickName}</span>
							        <span class="cmt-date">${reply.showCreateAt}</span>
							    </div>
							    <div class="cmt-content">
							        <p><span class="cmt-content-to">回复 ${reply.beRepliedUser.nickName}：</span>${fn:escapeXml(reply.content)}</p>
							    </div>
							    <div class="cmt-bar">
							        <span class="Jreply icon iconfont iconxiaoxi">回复</span>
							    </div>
							</li>
						</c:forEach>
						</ul>
					</li>
                </c:forEach>
                </ul>
            </div>
            <!-- 页码 -->
            <c:if test="${pager.pageCount > 1}">
            <div class="pcauto_page mt30"><tags:pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:pager></div>
            </c:if>
        </div>
        <div class="layC">
	        <div>
	            <!-- 相关话题 -->
				<jsp:include page="/template/common/relate_tag.jsp"></jsp:include>
	        </div>
        	<div id="JscrollFixed">
	            <!-- 相关问答 -->
	            <jsp:include page="/template/common/relate_question.jsp"></jsp:include>
        	</div>
        </div>
    </div>
    <script>
    	//问题id
    	window.QUESTIONID = "${question.id}";
    </script>
    <!-- 项目内容区域 E -->
    <!-- 公共底部 SSI 上线生效 S -->
    <!--#include virtual="/global_ssi/pcauto/utf8/footer/index.html" -->
    <!-- 公共底部 SSI 上线生效 E -->
    <script>
    _submitIvyID();
    </script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/runtime.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/vendors.bundle.js"></script>
	<script type="text/javascript" src="//js.3conline.com/pcautonew1/pc/2019/wenda/js/detail.bundle.js"></script>
</body>
</html>