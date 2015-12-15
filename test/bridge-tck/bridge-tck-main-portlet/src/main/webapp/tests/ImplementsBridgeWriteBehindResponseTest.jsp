<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" session="true"
		 import="java.util.*,
		         javax.faces.context.*,
		         javax.faces.component.*,
		         javax.servlet.*,
		         javax.portlet.*,
		         javax.portlet.faces.*,
		         javax.portlet.faces.preference.*" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Chapter 6_5 Tests</title>
</head>
<body>

<%
	// Note: JSP code runs before the JSF EL is executed

	FacesContext elFacesContext = FacesContext.getCurrentInstance();
	ExternalContext elExternalContext = elFacesContext.getExternalContext();
	RenderResponse renderResponse = (RenderResponse) elExternalContext.getResponse();
	Map m = elExternalContext.getRequestMap();

	String detail = null;

	if (renderResponse instanceof BridgeWriteBehindResponse) {
		m.put("com.liferay.faces.bridge.TCK.status", Boolean.TRUE);
		m.put("com.liferay.faces.bridge.TCK.detail", "ExternalContext correctly returns a response object that implements BridgeWriteBehindResponseWrapper during a render (dispatch).");
	} else {
		m.put("com.liferay.faces.bridge.TCK.status", Boolean.FALSE);
		m.put("com.liferay.faces.bridge.TCK.detail", "ExternalContext incorrectly returns a response object that doesn't implement BridgeWriteBehindResponseWrapper during a render (dispatch).");
	}

%>

<f:view>
	<h:form>
		<h:panelGrid columns="1">
			<h:outputText escape="false" value="#{test.renderTestResult}"/>
		</h:panelGrid>
	</h:form>
</f:view>
</body>
</html>


