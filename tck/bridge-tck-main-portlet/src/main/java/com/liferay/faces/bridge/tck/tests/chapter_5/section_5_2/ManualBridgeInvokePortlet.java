/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class ManualBridgeInvokePortlet extends ManualBridgeInvokeCompatPortlet {

	private static final String FACESCONTEXTRELEASED_ACTION_TEST = "facesContextReleasedActionTest";
	private static final String PORTLETPHASEREMOVED_ACTION_TEST = "portletPhaseRemovedActionTest";
	private static final String FACESCONTEXTRELEASED_EVENT_TEST = "facesContextReleasedEventTest";
	private static final String PORTLETPHASEREMOVED_EVENT_TEST = "portletPhaseRemovedEventTest";
	private static final String FACESCONTEXTRELEASED_RENDER_TEST = "facesContextReleasedRenderTest";
	private static final String PORTLETPHASEREMOVED_RENDER_TEST = "portletPhaseRemovedRenderTest";
	private static final String FACESCONTEXTRELEASED_RESOURCE_TEST = "facesContextReleasedResourceTest";
	private static final String PORTLETPHASEREMOVED_RESOURCE_TEST = "portletPhaseRemovedResourceTest";
	private static final String BRIDGESETSCONTENTTYPE_TEST = "bridgeSetsContentTypeTest";

	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		if (getTestName().equals(BRIDGESETSCONTENTTYPE_TEST)) {

			// By invoking the bridge directly (and not setting the contentType)
			// we force the bridge to have to do the work
			Bridge bridge = super.getFacesBridge(renderRequest, renderResponse);
			bridge.doFacesRequest(renderRequest, renderResponse);
		}
		else if (getTestName().equals(FACESCONTEXTRELEASED_ACTION_TEST) ||
				getTestName().equals(PORTLETPHASEREMOVED_ACTION_TEST) ||
				getTestName().equals(FACESCONTEXTRELEASED_EVENT_TEST) ||
				getTestName().equals(PORTLETPHASEREMOVED_EVENT_TEST)) {

			// Output in the Test method for this test
			super.doDispatch(renderRequest, renderResponse);
		}
		else if (getTestName().equals(FACESCONTEXTRELEASED_RENDER_TEST)) {
			super.doDispatch(renderRequest, renderResponse);

			if (
				renderRequest.getPortletSession(true).getAttribute(
						"org.apache.portlet.faces.tck.facesContextReleased") != null) {
				outputTestResult(renderResponse, Boolean.TRUE, "Correctly release FacesContext at end of render.");
				renderRequest.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				outputTestResult(renderResponse, Boolean.FALSE, "FacesContext not release at end of render.");
			}
		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_RENDER_TEST)) {
			super.doDispatch(renderRequest, renderResponse);

			if (renderRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				outputTestResult(renderResponse, Boolean.TRUE,
					"Correctly removed Portlet Phase attribute at end of render.");
			}
			else {
				outputTestResult(renderResponse, Boolean.FALSE,
					"Didn't remove Portlet Phase attribute at end of render.");
			}
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	public void init(PortletConfig config) throws PortletException {
		super.init(config);

	}

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

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		if (getTestName().equals(FACESCONTEXTRELEASED_RESOURCE_TEST)) {
			super.serveResource(request, response);

			if (request.getPortletSession(true).getAttribute("org.apache.portlet.faces.tck.facesContextReleased") !=
					null) {
				outputTestResult(response, Boolean.TRUE, "Correctly release FacesContext at end of resource request.");
				request.getPortletSession().removeAttribute("org.apache.portlet.faces.tck.facesContextReleased");
			}
			else {
				outputTestResult(response, Boolean.FALSE, "FacesContext not release at end of resource request.");
			}
		}
		else if (getTestName().equals(PORTLETPHASEREMOVED_RESOURCE_TEST)) {
			super.serveResource(request, response);

			if (request.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) == null) {
				outputTestResult(response, Boolean.TRUE,
					"Correctly removed Portlet Phase attribute at end of resource request.");
			}
			else {
				outputTestResult(response, Boolean.FALSE,
					"Didn't remove Portlet Phase attribute at end of resource request.");
			}
		}
	}

	@Override
	protected void outputTestResult(MimeResponse response, Boolean pass, String detail) throws IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if (pass.booleanValue()) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(detail);
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(detail);
		}

		out.println(resultWriter.toString());
	}
}
