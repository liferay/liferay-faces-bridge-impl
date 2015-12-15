<?xml version="1.0" encoding="iso-8859-1" standalone="yes" ?>
<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:f="http://java.sun.com/jsf/core"
		  xmlns:tr="http://myfaces.apache.org/trinidad">
	<jsp:directive.page contentType="text/html;charset=utf-8"/>
	<f:view>
		<tr:document title="JSR 329 Resource Redisplay Test(s)">
			<tr:form>
				<tr:panelPage>
					<tr:outputLabel value="PPR Result: "></tr:outputLabel>
					<tr:outputFormatted id="pprResult"
										partialTriggers="runPPR"
										value="#{test.testResourceResult}"/>
					<tr:commandButton id="runPPR" text="#{test.redisplayCommandName}" partialSubmit="true"
									  partialTriggers="runPPR" actionListener="#{test.runActionListener}"/>
					<tr:goLink id="pprGoLink" text="#{test.redisplayLinkName}"
							   destination="portlet:render?_jsfBridgeViewId=_jsfBridgeCurrentView&amp;org.apache.portlet.faces.tck.redisplay=1"
							   partialTriggers="runPPR"/>
				</tr:panelPage>
			</tr:form>
		</tr:document>
	</f:view>
</jsp:root>
