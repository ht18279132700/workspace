<%@page import="java.util.Map"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page contentType="text/html;charset=UTF-8" session="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.net.InetAddress"%>
<%@page import="java.lang.management.*"%>
<%@page import="java.io.File"%>
<%
//     if(request.getHeader("Host") == null || !request.getHeader("Host").startsWith("192.168.")){
//         out.println("非法请求！");
//         return;
//     }
%>
<%
    int mb = 1024*1024;
    Runtime runtime = Runtime.getRuntime();
%>
<%
	Logger LOG = LoggerFactory.getLogger(getClass());
    long totalMinorGcCount = 0;
    long totalMinorGcTime = 0;
    long totalFullGcCount = 0;
    long totalFullGcTime = 0;
    for(GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
    	com.sun.management.GarbageCollectorMXBean gcm = (com.sun.management.GarbageCollectorMXBean)gc;
    	com.sun.management.GcInfo info = gcm.getLastGcInfo();
    	Map<String, MemoryUsage> mub = info.getMemoryUsageBeforeGc();
    	Map<String, MemoryUsage> mua = info.getMemoryUsageAfterGc();
    	
    	LOG.info("*******print gc info start*******");
    	LOG.info("gc name: {}", gc.getName());
    	
    	LOG.info("------MemoryUsage BeforeGc------");
    	for(String s : mub.keySet()){
    		MemoryUsage mu = mub.get(s);
    		LOG.info("{}: used={}, committed={}", s, (mu.getUsed() >> 20) + "M", (mu.getCommitted() >> 20) + "M");
    	}
    	
    	LOG.info("------MemoryUsage AfterGc-------");
    	for(String s : mua.keySet()){
    		MemoryUsage mu = mua.get(s);
    		LOG.info("{}: used={}, committed={}", s, (mu.getUsed() >> 20) + "M", (mu.getCommitted() >> 20) + "M");
    	}
    	
    	LOG.info("*******print gc info end*******\n");
    	
        long count = gc.getCollectionCount(); 
        long time = gc.getCollectionTime();
        if(gc.getName().contains("Young") || 
           gc.getName().contains("Copy") ||
           gc.getName().contains("ParNew") ||
           gc.getName().contains("PS Scavenge")){
            totalMinorGcCount += count>0?count:0;
            totalMinorGcTime += time>0?time:0;
        }else{
            totalFullGcCount += count>0?count:0;
            totalFullGcTime += time>0?time:0;
        }
    }
%>
<%
    ThreadMXBean tb = ManagementFactory.getThreadMXBean();
    long[] deadIds = tb.findDeadlockedThreads();
    int deadThreadCount = (deadIds==null?0:deadIds.length);
    StringBuilder dts = new StringBuilder();
    if(deadThreadCount > 0){
    	for(long id:deadIds){
    		dts.append(id).append(",");
        }
    }
%>
<c:choose>
  <c:when test="${empty param.callback }">
  '<%=InetAddress.getLocalHost().getHostAddress() %>',
  '<%=request.getServerPort()%>',
  '<%=ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() %>',
  '<%=(runtime.totalMemory() - runtime.freeMemory()) / mb %>',
  '<%=runtime.freeMemory() / mb%>',
  '<%=runtime.totalMemory() / mb%>',
  '<%=runtime.maxMemory() / mb%>',
  '<%=totalMinorGcCount%>',
  '<%=totalMinorGcTime%>',
  '<%=totalFullGcCount%>',
  '<%=totalFullGcTime%>',
  '<%=tb.getThreadCount()%>',
  '<%=deadThreadCount%>',
  '<%=dts.toString()%>'
  </c:when>
  <c:otherwise>
  ${param.callback }('<%=InetAddress.getLocalHost().getHostAddress() %>',
                     '<%=request.getServerPort()%>',
                     '<%=ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() %>',
                     '<%=(runtime.totalMemory() - runtime.freeMemory()) / mb %>',
                     '<%=runtime.freeMemory() / mb%>',
                     '<%=runtime.totalMemory() / mb%>',
                     '<%=runtime.maxMemory() / mb%>',
                     '<%=totalMinorGcCount%>',
                     '<%=totalMinorGcTime%>',
                     '<%=totalFullGcCount%>',
                     '<%=totalFullGcTime%>',
                     '<%=tb.getThreadCount()%>',
                     '<%=deadThreadCount%>',
                     '<%=dts.toString()%>');
  </c:otherwise>
</c:choose>