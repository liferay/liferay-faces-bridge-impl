<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>JSR 378 Dispatch Test</title>
</head>
<body>
<%
	if (request.getAttribute("javax.servlet.forward.servlet_path") != null)
		request.setAttribute("com.liferay.faces.bridge.tck.dispatchForward", Boolean.TRUE);
	else
		request.setAttribute("com.liferay.faces.bridge.tck.dispatchForward", Boolean.FALSE);
%>
<jsp:include page="SingleRequestTest.jsp"/>
</body>
</html>
