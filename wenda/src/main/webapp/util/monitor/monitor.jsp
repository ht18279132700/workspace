<%@page contentType="text/html; charset=GBK" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.*"%>
<%@page import="cn.com.pcauto.wenda.util.monitor.*"%>
<%!
	double linerScore(double start, double end, double score, double value) {
		if (value <= start) return score;
		return score * (end - value) / (end - start);
	}
%>
<%
	if(request.getHeader("Host") == null){
        out.println("�Ƿ�����");
        return;
    }else{
    	if(request.getHeader("Host").startsWith("192.168.") || request.getHeader("Host").startsWith("127.0.0.1")){
    		//todo
    	}else{
    		out.println("�Ƿ�����");
            return;
    	}
    }
%>
<%
	String reset = request.getParameter("reset");
	if (reset != null) {
	    Monitor.reset();
	    response.sendRedirect("monitor.jsp");
	    return;
	}
	
	/* ϵͳ����״�����۹�ʽ
	 5����ƽ����Ӧʱ��(��)    1 - 4      40 - 0
	 5���ӳ������ >         1% - 20%	  50 - 0 // �����Ǳ��ʣ�Ҫע���ĸ����̫С
	 ��ǰ����10����������   5 - 20      10 - 0
	 */

	List samples = MovingAverageMonitor.getReport();

	double minScore = 100.0;
	for (int i = 0; i < samples.size(); i ++) {
		double score = 0.0;
		MovingAverageCounter counter = (MovingAverageCounter)samples.get(i);
		double avg = counter.getFiveMinuteAverage();
		double errorRate = 0.0;

		// ������ 10 �����ϵ�����ͳ�Ʊ��ʲŹ�ƽ
		if (counter.getFiveMinuteCount() > 10 && counter.getFiveMinuteError() > 0) {
			errorRate = counter.getFiveMinuteError() * 1.0 / counter.getFiveMinuteCount();
		}
		score += linerScore(1000.0, 4000.0, 50.0, avg);
		score += linerScore(0.01,   0.20,   50.0, errorRate);
		if (minScore > score) minScore = score;
	}

	if ("monitor".equals(request.getParameter("method"))) {
		if (minScore < 60) {
			response.sendError(response.SC_INTERNAL_SERVER_ERROR, "ϵͳ�÷֣�" + minScore);
		} else {
			out.println("ϵͳ�÷֣�" + minScore);
		}
		return;
	}

	long startTime = Monitor.getStartTime();
	pageContext.setAttribute("startTime", Monitor.dateFormat.format(new java.util.Date(startTime)));
	pageContext.setAttribute("items", Monitor.getReport());
	pageContext.setAttribute("minScore", new Double(minScore));
%><html>
<head>
<style>
<!--
table {
    border-collapse:collapse;border-width:1px;border-color:silver;
}
td {
    font-size: 9pt;height: 20px;
}
th {
    font-size: 9pt;background-color:lightblue;height:20px;
}
.even {background-color:#EEEEEE;}
.odd {background-color:white;}
-->
</style>
<script src="//js.3conline.com/min/temp/v1/lib-jquery1.10.2.js"></script>
<script>
	$(document).ready(function() {
		$("#panel").hover(
			function(){},
			function(){ $(this).hide(); }
		);
	});
	function showInfo(obj, info) {
		var _this = $(obj);
		var pos = _this.offset();
		var panel = $("#panel");
		panel.val($(info).val());
		panel.css("top", pos.top).css("left", pos.left - 920).show();
	}
</script>
</head>
<body>
<textarea style="position:absolute;display:none;border:solid black 1px;top:0;left:0;font-size:12px;background-color:#ffffe8;"
 cols="150" rows="12" id="panel"></textarea>
<table border="1" width="100%">
<tr>
	<th>����ʱ��ͳ�Ʊ���</th>
	<th colspan="2">ͳ�ƿ�ʼʱ��(${startTime})</th>
	<td colspan="7" align="right">ϵͳ�ۺ����ۣ�<font color='${minScore < 60.0?"red":"green"}'>${minScore}</font></td>
	<td align="right">
		<a href="monitor.jsp?reset" onclick="return confirm('��ȷ��Ҫ����ͳ��������ͳ�ƻ���ȫ������ڵ�ͳ�����ݣ�\nҲ������Ҫ����һ��ҳ���������ͳ�ơ�');">����ͳ��</a>
	</td>
</tr>
<tr>
	<th>URI</th>
	<th>����/��</th>
	<th>�ܴ���</th>
	<th>�����</th>
	<th>0-1��</th>
	<th>1-3</th>
	<th>3-5</th>
	<th>5-10</th>
	<th>10-20</th>
	<th>20+</th>
	<th>����10������</th>
</tr>
<c:forEach var="item" items="${items}" varStatus="st">
<tr class="${st.count%2==0?'even':'odd'}">
	<td>${item.uri}</td>
	<td>${item.perMinute}</td>
	<td>${item.count}</td>
	<td ${item.error==0?'':'bgcolor=red'}>${item.error}</td>
	<td>${item.count1}</td>
	<td>${item.count3}</td>
	<td ${item.count5==0?'':'bgcolor=#FFFF99'}>${item.count5}</td>
	<td ${item.count10==0?'':'bgcolor=yellow'} >${item.count10}</td>
	<td ${item.count20==0?'':'bgcolor=#FF9999'} >${item.count20}</td>
	<td ${item.count20x==0?'':'bgcolor=red'} >${item.count20x}</td>
	<td align="right" width="100">${item.maxDuration}
		<textarea style="display:none;" id="ta_${st.count}"><c:forEach var="bottom" items="${item.bottoms}"><c:if test="${!empty bottom[0]}">${bottom[0]},${bottom[1]},${bottom[2]},${bottom[3]},${bottom[4]}<%="\n"%></c:if></c:forEach></textarea>
		<input style="height: 16px;font-size: 12px;color: red" type="text" size="1" value="��" readonly="readonly" onmouseover="showInfo(this, '#ta_${st.count}');">
	</td>
</tr>
</c:forEach>
</table>
</body>
</html>