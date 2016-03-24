<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter" %>
<%@ page import="com.liferay.faces.bridge.tck.common.Constants" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>JSR 378 Non Faces Request TCK Test</title>
</head>
<body>

<portlet:defineObjects/>

<!-- Test pass if the response content type has been set to the same value
	 as the request.-->
<!-- Test pass if the jsp has been rendered using a portlet dispatcher.
	 This is indicated by the presence of portlet attributes being set
	 on the request. -->
<%
	String testName = (String) renderRequest.getAttribute(Constants.TEST_NAME);
	BridgeTCKResultWriter writer = new BridgeTCKResultWriter(testName);

	String contentType = renderResponse.getContentType();
	String contentTypeMsg = "Expected response content type is " + renderRequest.getResponseContentType() + ", actual value is " + contentType + ".  ";

	String[] dispatcherAttributes = {"javax.portlet.config",
		"javax.portlet.request",
		"javax.portlet.response"};

	boolean dispatcherPass = true;
	for (String attr : dispatcherAttributes) {
		if (request.getAttribute(attr) == null) {
			dispatcherPass = false;
			break;
		}
	}
	String dispatcherMsg = null;
	if (dispatcherPass == true) {
		dispatcherMsg = "  The request included valid dispatcher attributes.";
	} else {
		dispatcherMsg = "  The request did not include valid attributes indicating that a portlet request dispatcher was not used.";
	}

	writer.setDetail(contentTypeMsg + dispatcherMsg);
	if (contentType.startsWith(renderRequest.getResponseContentType()) && dispatcherPass) {
		writer.setStatus(true);
	} else {
		writer.setStatus(false);
	}

%>
<%=writer.toString()%>
</body>
</html>
