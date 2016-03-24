<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" session="true" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="javax.faces.component.*" %>
<%@ page import="javax.portlet.PortletRequest" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.faces.preference.Preference" %>
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
	// App Scope
	pageContext.getServletContext().setAttribute("com.liferay.faces.bridge.TCK.appScopeAttr", Boolean.TRUE);

	// Session scope (app scope)
	pageContext.getSession().setAttribute("com.liferay.faces.bridge.TCK.portletSessionScopeAttr", Boolean.TRUE);

	// Request scope
	pageContext.getRequest().setAttribute("com.liferay.faces.bridge.TCK.requestScopeAttr", Boolean.TRUE);
%>

<%-- Acquire all the elements via EL -- add to page context -- then compare with Java --%>

<%-- applicationScope --%>
<c:set var="tck_applicationScope" value="${applicationScope}" scope="page"/>

<%-- cookie --%>
<c:set var="tck_cookie" value="${cookie}" scope="page"/>

<%-- header --%>
<c:set var="tck_header" value="${header}" scope="page"/>

<%-- headerValues --%>
<c:set var="tck_headerValues" value="${headerValues}" scope="page"/>

<%-- initParam --%>
<c:set var="tck_initParam" value="${initParam}" scope="page"/>

<%-- pageContext --%>
<c:set var="tck_pageContext" value="${pageContext}" scope="page"/>

<%-- pageScope --%>
<c:set var="tck_pageScope" value="${pageScope}" scope="page"/>

<%-- param --%>
<c:set var="tck_param" value="${param}" scope="page"/>

<%-- paramValues --%>
<c:set var="tck_paramValues" value="${paramValues}" scope="page"/>

<%-- requestScope --%>
<c:set var="tck_requestScope" value="${requestScope}" scope="page"/>

<%-- sessionScope --%>
<c:set var="tck_sessionScope" value="${sessionScope}" scope="page"/>


<%--  Extra objects resolved by base JSP resolver if using portlet:defineObjects --%>
<%--  Note: we aren't - hence we expect null here - we do this to test that the 
      Bridge's ELresolver is properly delegating to the JSPResolver for this resolution.
      If it isn't it will resolve these values and we will report an error --%>

<%-- portletConfig --%>
<c:set var="tck_portletConfig" value="${portletConfig}" scope="page"/>

<%-- renderRequest --%>
<c:set var="tck_renderRequest" value="${renderRequest}" scope="page"/>

<%-- renderResponse --%>
<c:set var="tck_renderResponse" value="${renderResponse}" scope="page"/>

<%-- portletSession --%>
<c:set var="tck_portletSession" value="${portletSession}" scope="page"/>

<%-- portletSessionScope --%>
<c:set var="tck_portletSessionScope" value="${portletSessionScope}" scope="page"/>

<%-- portletPreferences --%>
<c:set var="tck_portletPreferences" value="${portletPreferences}" scope="page"/>

<%-- portletPreferencesValues --%>
<c:set var="tck_portletPreferencesValues" value="${portletPreferencesValues}" scope="page"/>


<%-- Objects resolved by Face's JSP ELResolver --%>
<%-- facesContext --%>
<c:set var="tck_facesContext" value="${facesContext}" scope="page"/>

<%-- view --%>
<c:set var="tck_view" value="${view}" scope="page"/>


<%-- Objects resolved by Bridge's JSP ELResolver --%>
<%-- httpSessionScope --%>
<c:set var="tck_httpSessionScope" value="${httpSessionScope}" scope="page"/>

<%-- mutablePortletPreferencesValues --%>
<c:set var="tck_mutablePortletPreferencesValues" value="${mutablePortletPreferencesValues}" scope="page"/>

<%
	/* Now test that we got the right stuff */
  
  /* For implicit objects handled by the base JSP resolver -- merely verify they were resolved */

	boolean status = true;
	String detail = "";

	if (pageContext.getAttribute("tck_applicationScope") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'applicationScope' failed<br>";
	}

	if (pageContext.getAttribute("tck_cookie") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'cookie' failed<br>";
	}

	if (pageContext.getAttribute("tck_header") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'header' failed<br>";
	}

	if (pageContext.getAttribute("tck_headerValues") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'headerValues' failed<br>";
	}

	if (pageContext.getAttribute("tck_initParam") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'initParam' failed<br>";
	}

	if (pageContext.getAttribute("tck_pageContext") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'pageContext' failed<br>";
	}

	if (pageContext.getAttribute("tck_pageScope") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'pageScope' failed<br>";
	}

	if (pageContext.getAttribute("tck_param") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'param' failed<br>";
	}

	if (pageContext.getAttribute("tck_paramValues") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'paramValues' failed<br>";
	}

	if (pageContext.getAttribute("tck_requestScope") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'requestScope' failed<br>";
	}

	if (pageContext.getAttribute("tck_sessionScope") == null) {
		status = false;
		detail += "JSF EL resolution of implict variable 'sessionScope' failed<br>";
	}
  
  /* verify we didn't resolve the portlet implicit objects tied to portlet:defineObjects -- as
     we didn't use this construct in the page in order to prove the bridge properly delegates */

	if (pageContext.getAttribute("tck_portletConfig") != null) {
		status = false;
		detail += "'portletConfig' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_renderRequest") != null) {
		status = false;
		detail += "'renderRequest' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_renderResponse") != null) {
		status = false;
		detail += "'renderResponse' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_portletSession") != null) {
		status = false;
		detail += "'portletSession' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_portletSessionScope") != null) {
		status = false;
		detail += "'portletSessionScope' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_portletPreferences") != null) {
		status = false;
		detail += "'portletPreferences' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}

	if (pageContext.getAttribute("tck_portletPreferencesValues") != null) {
		status = false;
		detail += "'portletPreferencesValues' improperly resolved.  'portlet:defineObjects' isn't used in this JSP meaning the EL shouldn't have resolved, but it did.  This implies the Bridge's EL resolver handled the resolution which is wrong.<br>";
	}
  
  
  /* Verify that the Faces specific implicit objects resolved correctly - test
   * values to ensure the Bridge didn't perturb */
	FacesContext elFacesContext = (FacesContext) pageContext.getAttribute("tck_facesContext");
	if (elFacesContext == null || !FacesContext.getCurrentInstance().equals(elFacesContext)) {
		status = false;
		detail += "JSF EL resolution of implict variable 'facesContext' failed<br>";
		elFacesContext = FacesContext.getCurrentInstance();
	}

	UIViewRoot elView = (UIViewRoot) pageContext.getAttribute("tck_view");
	if (elView == null || !elFacesContext.getViewRoot().equals(elView)) {
		status = false;
		detail += "JSF EL resolution of implict variable 'view' failed<br>";
	}
  
  /* Verify that the Bridge specific implicit objects resolved correctly - test
   * values to ensure the Bridge got them right */
	Map<String, Object> elHttpSessionScope = (Map<String, Object>) pageContext.getAttribute("tck_httpSessionScope");
	if (elHttpSessionScope == null || !testHttpSessionScope(elHttpSessionScope, (PortletSession) elFacesContext.getExternalContext().getSession(true))) {
		status = false;
		detail += "JSF EL resolution of implict variable 'httpSessionScope' failed<br>";
	}

	Map<String, Preference> elMutablePortletPreferencesValues = (Map<String, Preference>) pageContext.getAttribute("tck_mutablePortletPreferencesValues");
	if (elMutablePortletPreferencesValues == null || !testMutablePortletPreferencesValues(elMutablePortletPreferencesValues, ((PortletRequest) elFacesContext.getExternalContext().getRequest()).getPreferences().getMap())) {
		status = false;
		detail += "JSF EL resolution of implict variable 'mutablePortletPreferencesValues' failed<br>";
	}

	if (status)
		detail = "All JSP EL expressions resolved correctly";

	elFacesContext.getExternalContext().getRequestMap().put("com.liferay.faces.bridge.TCK.status", Boolean.valueOf(status));
	elFacesContext.getExternalContext().getRequestMap().put("com.liferay.faces.bridge.TCK.detail", detail);

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


<%-- JSP methods we rely on to do validation above --%>
<%!
	public boolean testImplicitObject(Object implicitObject, Object compareTo) {
		return true;
	}

	private boolean testImplicitObjectArrayMaps(Map<String, String[]> implicitObject, Map<String, String[]> compareTo) {
		if (!arrayMapsEquals(implicitObject, compareTo)) {
			return false;
		} else {
			return true;
		}
	}

	private boolean arrayMapsEquals(Map<String, String[]> a, Map<String, String[]> b) {
		if (a == b) return true;
		if (a.size() != b.size()) return false;
		for (Iterator<Map.Entry<String, String[]>> i = a.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, String[]> e = i.next();
			if (!Arrays.equals(e.getValue(), b.get(e.getKey())))
				return false;
		}
		return true;
	}

	private boolean testHttpSessionScope(Map<String, Object> implicitObject, PortletSession session) {
		// iterate over the Map and make sure each is in the portlet session app scope
		for (Iterator<Map.Entry<String, Object>> i = implicitObject.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, Object> e = i.next();
			Object compareTo = session.getAttribute(e.getKey(), PortletSession.APPLICATION_SCOPE);
			if (compareTo == null) {
				return false;
			} else if (!e.getValue().equals(compareTo)) {
				return false;
			}
		}

		// Now verify that the Map contained the correct number of entries
		Enumeration en = session.getAttributeNames(PortletSession.APPLICATION_SCOPE);
		int count = 0;
		while (en.hasMoreElements()) {
			en.nextElement();
			count++;
		}

		if (count != implicitObject.size()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean testMutablePortletPreferencesValues(Map<String, Preference> implicitObject, Map<String, String[]> prefMap) {
		if (implicitObject.size() != prefMap.size()) {
			return false;
		}

		// Now test that the Map contains the same entries as the immutable preference Map
		for (Iterator<Map.Entry<String, Preference>> i = implicitObject.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, Preference> e = i.next();
			List<String> portletPrefValues = Arrays.asList(prefMap.get(e.getKey()));
			if (portletPrefValues == null) {
				return false;
			} else if (!e.getValue().getValues().equals(portletPrefValues)) {
				return false;
			}
		}
		return true;

	}
%>