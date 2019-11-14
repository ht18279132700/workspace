<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="f" uri="/WEB-INF/tlds/functions.tld" %>
<%@taglib prefix="plugin" uri="/WEB-INF/tlds/plugin.tld"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@page import="cn.pc.util.*
,cn.pconline.passport3.client.Passport
,cn.pconline.passport3.account.entity.Account
,cn.pconline.passport3.account.LoginException
,cn.pconline.passport3.account.entity.Session
"%>