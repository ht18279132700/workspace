<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="cn.com.pcauto.wenda.util.monitor.Monitor"%>
<%@page contentType="text/html;charset=UTF-8" session="false" %>

<%
    if(request.getHeader("Host") == null || !request.getHeader("Host").startsWith("192.168.")){
        out.println("非法请求！");
        return;
    }

	request.setAttribute("serverArrayStr", JSON.toJSONString(Monitor.serverArray));
%>

<table border="1"
	style="font-size: 13; line-height: 13px; text-align: center; vertical-align: middle;">
	<tr>
		<th width="160px;">IP</th>
		<th width="70px;">负载</th>
		<th width="100px;">Used Memory</th>
		<th width="100px;">Free Memory</th>
		<th width="100px;">Total Memory</th>
		<th width="100px;">Max Memory</th>
		<th width="50px;">YGC</th>
		<th width="60px;">YGCT(ms)</th>
		<th width="50px;">FGC</th>
		<th width="60px;">FGCT(ms)</th>
		<th width="100px;">Total Threads</th>
		<th width="150px;">Dead Lock Threads</th>
		<th width="200px;">操作</th>
	</tr>

</table>

<p class="line-height: 1pt"></p>

<script type="text/javascript" src="http://js.3conline.com/js/jquery-1.4.2.min.js"></script>
<script type="text/javascript">
	var ipAddress = JSON.parse('${serverArrayStr}');

	for(var i = 0; i < ipAddress.length; i++){
		$.getScript("//" + ipAddress[i] + "/util/monitor/jvm.jsp?callback=func");
	}

	function func(ip, port, val, val2, val3, val4, val5, val6, val7, val8,
			val9, val10, val11, val12) {
		var value = '';
		if (val <= 4) {
			value = '<font color="green">' + val + '</font>';
		} else if (val > 4 && val <= 8) {
			value = '<font color="orange" style="font-weight: bold">' + val
					+ '</font>';
		} else {
			value = '<font color="red" style="font-weight: bold">' + val
					+ '</font>';
		}
		var shtml = '<tr><td>'
				+ ip
				+ ":"
				+ port
				+ '</td>'
				+ '<td>'
				+ value
				+ '</td>'
				+ '<td>'
				+ val2
				+ '</td>'
				+ '<td>'
				+ val3
				+ '</td>'
				+ '<td>'
				+ val4
				+ '</td>'
				+ '<td>'
				+ val5
				+ '</td>'
				+ '<td>'
				+ val6
				+ '</td>'
				+ '<td>'
				+ val7
				+ '</td>'
				+ '<td>'
				+ val8
				+ '</td>'
				+ '<td>'
				+ val9
				+ '</td>'
				+ '<td>'
				+ val10
				+ '</td>'
				+ '<td>'
				+ val11
				+ '  '
				+ val12
				+ '</td>'
				+ '<td>'
				+ '<a href="http://' + ip + ":" + port +  '/util/monitor/threads.jsp' + '" target="_blank">查看线程</a>&nbsp;&nbsp;'
				+ '<a href="http://' + ip + ":" + port +  '/util/monitor/status.jsp' + '" target="_blank">查看状态</a>&nbsp;&nbsp;'
				+ '<a href="http://' + ip + ":" + port +  '/util/monitor/monitor.jsp' + '" target="_blank">monitor</a>&nbsp;&nbsp;'
				+ '</td></tr>';
		$("tbody").append(shtml);
	}

</script>