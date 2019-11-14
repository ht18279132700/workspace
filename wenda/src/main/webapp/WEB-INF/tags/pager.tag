<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="f" uri="/WEB-INF/tlds/functions.tld" %>
<%@tag pageEncoding="utf-8" %>
<c:set var="url" value='<%=(request.getContextPath() + request.getServletPath() + "?" + (request.getQueryString() != null ? request.getQueryString() : "").replaceAll("&*(pageNo|pageSize)=[^&]*", ""))%>'/>
<c:set var="numbers"><jsp:doBody/></c:set>
<c:set var="numbers" value="${fn:split(numbers,',')}"/>
<c:set var="pageNo" value="${1*numbers[0]}"/>
<c:set var="pageSize" value="${1*numbers[1]}"/>
<c:set var="total" value="${1*numbers[2]}"/>
<c:set var="pageTotal">${(total-1)/pageSize+1}</c:set>
<c:set var="pageTotal" value="${fn:substring(pageTotal,0,fn:indexOf(pageTotal,'.'))}"/>
<c:set var="showPage" value="${8}"/>
<c:if test="${pageTotal>1}">
    <c:choose>
        <c:when test="${pageTotal <= showPage}">
            <c:set var="begin" value="2"/>
            <c:set var="end" value="${pageTotal - 1}"/>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${pageNo >= pageTotal - 3}">
                    <c:set var="begin" value="${pageTotal - showPage + 1}"/>
                    <c:set var="end" value="${pageTotal - 1}"/>
                </c:when>
                <c:when test="${pageNo <= 4}">
                    <c:set var="begin" value="2"/>
                    <c:set var="end" value="${showPage}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="begin" value="${pageNo - 4 <= 1 ? 2 : pageNo - 4}"/>
                    <c:set var="end" value="${pageNo + 3}"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>

    </c:choose>
    <c:if test="${pageNo > 1}"><a class="prev" href="${f:htmlUrl(url,(pageNo > 1 ? pageNo-1 : 1))}">上一页</a></c:if><c:choose><c:when  test="${pageNo > 6 && pageTotal > 9}"><a href="${f:htmlUrl(url,1)}">1...</a></c:when><c:otherwise><c:if test="${1 != pageNo}"><a href="${f:htmlUrl(url,1)}">1</a></c:if><c:if test="${1 == pageNo}"><span>1</span></c:if></c:otherwise></c:choose><c:forEach var="n" begin="${begin}" end="${end}"><c:if test="${n != pageNo}"><a href="${f:htmlUrl(url,n)}" >${n}</a></c:if><c:if test="${n == pageNo}"><span>${n}</span></c:if></c:forEach><c:choose><c:when  test="${pageNo < pageTotal-4 && pageTotal > 9}"><a href="${f:htmlUrl(url,pageTotal)}">...${pageTotal}</a></c:when><c:otherwise><c:if test="${pageTotal != pageNo}"><a href="${f:htmlUrl(url,pageTotal)}">${pageTotal}</a></c:if><c:if test="${pageTotal == pageNo}"><span>${pageTotal}</span></c:if></c:otherwise></c:choose><c:if test="${pageName=='topic'}"><c:if test="${pageTotal>1}"><i class="iNum"><input type="text" class="txt" name="pageNo" style="width:18px;" value="${pageNo}" onkeyup="btnSurePress(this,event);"  title="输入页码，按回车跳转"/> /${pageTotal}页</i></c:if></c:if><c:if test="${pageNo < pageTotal}"><a class="next" href="${f:htmlUrl(url,(pageNo < pageTotal ? pageNo+1 : pageTotal))}">下一页</a></c:if>
        <!-- <script type="text/javascript">
            document.onkeyup=function(e){
                var e = e || window.event;
                var target = e.target || e.srcElement;
                var key = e.keyCode || e.which;
                var tagname = target.tagName;
                if(tagname == 'INPUT' || tagname == 'TEXTAREA') {
                    return;
                }
                if(key==37){
                    location.href="${f:htmlUrl(url, (pageNo>1?pageNo-1:1))}";
                }
                if(key==39){
                    location.href="${f:htmlUrl(url,(pageNo < pageTotal ? pageNo+1 : pageTotal))}";
                }
            };
    </script> -->
</c:if>
