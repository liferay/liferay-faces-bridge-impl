/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;

import com.liferay.faces.bridge.tck.common.portlet.RenderViewDispatchCompatPortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class ManualBridgeInvokePortlet extends RenderViewDispatchCompatPortlet {

	private static final String EXCEPTIONTHROWN_NODEFAULTVIEWID_TEST = "exceptionThrownWhenNoDefaultViewIdTest";
	private static final String VIEWIDWITHPARAM_TEST = "viewIdWithParam_1_Test";
	private static final String FACESCONTEXTRELEASED_ACTION_TEST = "facesContextReleasedActionTest";
	private static final String PORTLETPHASEREMOVED_ACTION_TEST = "portletPhaseRemovedActionTest";
	private static final String FACESCONTEXTRELEASED_EVENT_TEST = "facesContextReleasedEventTest";
	private static final String PORTLETPHASEREMOVED_EVENT_TEST = "portletPhaseRemovedEventTest";
	private static final String FACESCONTEXTRELEASED_RENDER_TEST = "facesContextReleasedRenderTest";
	private static final String PORTLETPHASEREMOVED_RENDER_TEST = "portletPhaseRemovedRenderTest";
	private static final String FACESCONTEXTRELEASED_RESOURCE_TEST = "facesContextReleasedResourceTest";
	private static final String PORTLETPHASEREMOVED_RESOURCE_TEST = "portletPhaseRemovedResourceTest";
	private static final String BRIDGESETSCONTENTTYPE_TEST = "bridgeSetsContentTypeTest";

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException,
		PortletSecurityException, IOException {
		super.processAction(request, response);

		if (getTestName().equals(FACESCONTEXTRELEASED_ACTION_TEST)) {

			if (request.getPortletSession(true).getAttribute("org.apache.portlet.faces.tck.facesContextReleased") !=
					null) {
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.TRUE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Correctly released FacesContext at end of doFacesRequest (action).");
				request.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.FALSE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Didn't release FacesContext at end of doFacesRequest (action).");
			}

		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_ACTION_TEST)) {

			if (request.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				request.getPortletSession(true).setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.TRUE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Correctly removed PortletPhase attrbiute at end of doFacesRequest (action).");
			}
			else {
				request.getPortletSession(true).setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.FALSE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Didn't removed PortletPhase attrbiute at end of doFacesRequest (action).");
			}
		}
	}

	@Override
	public void processEvent(EventRequest request, EventResponse response) throws PortletException,
		PortletSecurityException, IOException {
		super.processEvent(request, response);

		if (getTestName().equals(FACESCONTEXTRELEASED_EVENT_TEST)) {

			if (request.getPortletSession(true).getAttribute("org.apache.portlet.faces.tck.facesContextReleased") !=
					null) {
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.TRUE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Correctly released FacesContext at end of doFacesRequest (event).");
				request.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.FALSE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Didn't release FacesContext at end of doFacesRequest (event).");
			}

		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_EVENT_TEST)) {

			if (request.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				request.getPortletSession(true).setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.TRUE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Correctly removed PortletPhase attrbiute at end of doFacesRequest (event).");
			}
			else {
				request.getPortletSession(true).setAttribute("org.apache.portlet.faces.tck.testResult", Boolean.FALSE);
				request.getPortletSession().setAttribute("org.apache.portlet.faces.tck.testDetail",
					"Didn't removed PortletPhase attrbiute at end of doFacesRequest (event).");
			}
		}
	}

	@Override
	public void renderView(RenderRequest renderRequest, MimeResponse mimeResponse) throws PortletException,
		IOException {

		if (getTestName().equals(EXCEPTIONTHROWN_NODEFAULTVIEWID_TEST)) {

			Bridge bridge = super.getFacesBridge(renderRequest, mimeResponse);

			try {
				doBridgeFacesRequest(bridge, renderRequest, mimeResponse);
			}
			catch (BridgeDefaultViewNotSpecifiedException e) {
				outputTestResult(renderRequest, mimeResponse, Boolean.TRUE,
					"Correctly threw BridgeDefaultViewNotSpecifiedException when no default defined.");
			}
			catch (Exception e) {
				outputTestResult(renderRequest, mimeResponse, Boolean.FALSE,
					"Didn't throw BridgeDefaultViewNotSpecifiedException when no default defined.");
			}
		}
		else if (getTestName().equals(BRIDGESETSCONTENTTYPE_TEST)) {

			// By invoking the bridge directly (and not setting the contentType)
			// we force the bridge to have to do the work
			Bridge bridge = super.getFacesBridge(renderRequest, mimeResponse);
			doBridgeFacesRequest(bridge, renderRequest, mimeResponse);
		}
		else if (getTestName().equals(VIEWIDWITHPARAM_TEST)) {
			renderRequest.setAttribute(Bridge.VIEW_ID, "/tests/singleRequestTest.xhtml?param1=testValue");
			dispatchToView(renderRequest, mimeResponse);
		}
		else if (getTestName().equals(FACESCONTEXTRELEASED_ACTION_TEST) ||
				getTestName().equals(PORTLETPHASEREMOVED_ACTION_TEST) ||
				getTestName().equals(FACESCONTEXTRELEASED_EVENT_TEST) ||
				getTestName().equals(PORTLETPHASEREMOVED_EVENT_TEST)) {

			// Output in the Test method fort his test
			dispatchToView(renderRequest, mimeResponse);
		}
		else if (getTestName().equals(FACESCONTEXTRELEASED_RENDER_TEST)) {

			dispatchToView(renderRequest, mimeResponse);

			if (
				renderRequest.getPortletSession(true).getAttribute(
						"org.apache.portlet.faces.tck.facesContextReleased") != null) {
				outputTestResult(renderRequest, mimeResponse, Boolean.TRUE,
					"Correctly release FacesContext at end of render.");
				renderRequest.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				outputTestResult(renderRequest, mimeResponse, Boolean.FALSE,
					"FacesContext not release at end of render.");
			}
		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_RENDER_TEST)) {
			dispatchToView(renderRequest, mimeResponse);

			if (renderRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				outputTestResult(renderRequest, mimeResponse, Boolean.TRUE,
					"Correctly removed Portlet Phase attribute at end of render.");
			}
			else {
				outputTestResult(renderRequest, mimeResponse, Boolean.FALSE,
					"Didn't remove Portlet Phase attribute at end of render.");
			}
		}
		else {
			dispatchToView(renderRequest, mimeResponse);
		}
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		if (getTestName().equals(FACESCONTEXTRELEASED_RESOURCE_TEST)) {
			super.serveResource(request, response);

			if (request.getPortletSession(true).getAttribute("org.apache.portlet.faces.tck.facesContextReleased") !=
					null) {
				outputTestResult(request, response, Boolean.TRUE,
					"Correctly release FacesContext at end of resource request.");
				request.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				outputTestResult(request, response, Boolean.FALSE,
					"FacesContext not release at end of resource request.");
			}
		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_RESOURCE_TEST)) {
			super.serveResource(request, response);

			if (request.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				outputTestResult(request, response, Boolean.TRUE,
					"Correctly removed Portlet Phase attribute at end of resource request.");
			}
			else {
				outputTestResult(request, response, Boolean.FALSE,
					"Didn't remove Portlet Phase attribute at end of resource request.");
			}
		}
	}

	protected void outputTestResult(PortletRequest portletRequest, MimeResponse mimeResponse, Boolean pass,
		String detail) throws IOException {

		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if (pass.booleanValue()) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(detail);
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(detail);
		}

		if ((mimeResponse instanceof RenderResponse) || (mimeResponse instanceof ResourceResponse)) {

			mimeResponse.setContentType("text/html");

			PrintWriter out = mimeResponse.getWriter();
			out.println(resultWriter.toString());
		}
		else {
			portletRequest.setAttribute(BridgeTCKResultWriter.class.getName(), resultWriter);
		}
	}
}
