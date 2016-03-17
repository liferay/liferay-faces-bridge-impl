<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"
		 import="javax.faces.context.*, javax.faces.application.*, java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
	<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>JSR 329 NonJSFResource Test</title>
	</head>
	<body>
	<h:form>
		<h:panelGrid columns="1">
			<h:outputText value="Test result in iFrame."/>
		</h:panelGrid>
	</h:form>
	<%
		ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
		String resource = extCtx.encodeActionURL("portlet:resource?_jsfBridgeViewId=/tests/nonJSFViewTest.xhtml");
		out.println("<iframe src=\"" + resource + "\" width=\"100%\" height=\"400px\" name=\"tck-iframe\"/>");
	%>
	</body>
	</html>
</f:view>
