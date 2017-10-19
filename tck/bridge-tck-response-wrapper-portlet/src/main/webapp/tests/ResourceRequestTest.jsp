<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"
		 import="javax.faces.context.*, javax.faces.application.*, java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
	<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>JSR 378 Multi-Request TCK Test</title>
	</head>
	<body>
	<h:form>
		<h:panelGrid columns="1">
			<h:outputText value="Test result in iFrame."/>
		</h:panelGrid>
	</h:form>
	<%
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String resource = externalContext.encodeActionURL("portlet:resource?_jsfBridgeViewId=/tests/UsesConfiguredResourceResponseWrapperTest.jsp");
		out.println("<iframe name=\"tck-iframe\" src=\"" + resource + "\" width=\"100%\" height=\"400px\"/>");
	%>
	</body>
	</html>
</f:view>
