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
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.Event;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.xml.namespace.QName;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends HeaderTests implements PhaseListener {

	@Override
	public void afterPhase(PhaseEvent event) {

		PhaseId phase = event.getPhaseId();

		// Do nothing if in render phase
		if (phase == PhaseId.RENDER_RESPONSE) {
			return;
		}

		FacesContext facesContext = event.getFacesContext();
		Map<String, Object> m = facesContext.getExternalContext().getRequestMap();
		String testname = (String) m.get(Constants.TEST_NAME);
		Bridge.PortletPhase portletPhase = (Bridge.PortletPhase) m.get(Bridge.PORTLET_LIFECYCLE_PHASE);

		if (testname.equals("headerPhaseListenerTest") && (Bridge.PortletPhase.HEADER_PHASE.equals(portletPhase))) {
			m.put("org.apache.portlet.faces.tck.lastAfterPhase", phase);
		}
		else if (testname.equals("eventPhaseListenerTest") && (portletPhase.equals(Bridge.PortletPhase.EVENT_PHASE))) {
			m.put("org.apache.portlet.faces.tck.lastAfterPhase", phase);
		}

		if ((Bridge.PortletPhase.HEADER_PHASE.equals(portletPhase) &&
					(testname.equals("facesContextReleasedRenderTest") ||
						testname.equals("portletPhaseRemovedRenderTest"))) ||
				(portletPhase.equals(Bridge.PortletPhase.RESOURCE_PHASE) &&
					(testname.equals("facesContextReleasedResourceTest") ||
						testname.equals("portletPhaseRemovedResourceTest")))) {

			// prematurely prevent render from happening as the Portlet needs to write to the response once it verifies
			// that the corresponding elements were cleaned up
			facesContext.responseComplete();
		}
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		PhaseId phase = event.getPhaseId();

		// Do nothing if in render phase
		if (phase == PhaseId.RENDER_RESPONSE) {
			return;
		}

		FacesContext facesContext = event.getFacesContext();
		Map<String, Object> m = facesContext.getExternalContext().getRequestMap();
		String testname = (String) m.get(Constants.TEST_NAME);
		Bridge.PortletPhase portletPhase = (Bridge.PortletPhase) m.get(Bridge.PORTLET_LIFECYCLE_PHASE);

		if (testname.equals("headerPhaseListenerTest") && (Bridge.PortletPhase.HEADER_PHASE.equals(portletPhase))) {
			m.put("org.apache.portlet.faces.tck.lastBeforePhase", phase);
		}
		else if (testname.equals("eventPhaseListenerTest") && (portletPhase.equals(Bridge.PortletPhase.EVENT_PHASE))) {
			m.put("org.apache.portlet.faces.tck.lastBeforePhase", phase);
		}
	}

	// Test is Render test
	// Test #5.29
	@BridgeTest(test = "bridgeSetsContentTypeTest")
	public String bridgeSetsContentTypeTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		testBean.setTestComplete(true);

		// Parameter encoded in the faces-config.xml target
		String responseCT = externalContext.getResponseContentType();
		String requestedCT = ((RenderRequest) externalContext.getRequest()).getResponseContentType();

		if ((responseCT != null) && (requestedCT != null) && responseCT.equals(requestedCT)) {
			testBean.setTestResult(true,
				"Bridge correctly set the proper (default) content type when not set by portlet.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Bridge didn't set the proper (default) content type when not set by portlet.  Current: " + responseCT +
				" expected: " + requestedCT);

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.54, 5.55, 5.56, 5.57. 5.58
	@BridgeTest(test = "eventControllerTest")
	public String eventControllerTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Clear settings from previous run of the test: Done here because this render doesn't happen in scope so
			// second rendition doesn't keep the test result -- rather rerenders
			externalContext.getSessionMap().put(TestEventHandler.EVENT_RECEIVED, null);
			externalContext.getSessionMap().put(TestEventHandler.EVENT_TEST_FAILED, null);

			// Set a render parameter so we can verify its there after the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setRenderParameter("tck.renderParam", "value");

			// Create and raise the event
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventControllerTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			Boolean eventPhaseCheck = (Boolean) externalContext.getRequestMap().get("tck.eventPhaseCheck");
			String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");
			String checkPRP = (String) externalContext.getRequestMap().get("tck.compareModelPRPValue");

			// Verify the render parameter set in action is still there
			String param = (String) externalContext.getRequestParameterMap().get("tck.renderParam");

			if (eventPhaseCheck == null) {
				testBean.setTestResult(false, "Registered Event handler wasn't called to handle raised event.");

				return Constants.TEST_FAILED;
			}
			else if (!eventPhaseCheck.booleanValue()) {
				testBean.setTestResult(false,
					"Expected EVENT_PHASE request attribute not set during event processing.");

				return Constants.TEST_FAILED;
			}
			else if (param == null) {
				testBean.setTestResult(false,
					"Render parameter set in action phase not carried forward through the event phase into the header/render phase.");

				return Constants.TEST_FAILED;
			}
			else if (!param.equals("value")) {
				testBean.setTestResult(false,
					"(Private) Render parameter set in action phase carried forward through the event phase into the header/render phase but with an unexpected value.  Received: " +
					param + " but expected: value");

				return Constants.TEST_FAILED;
			}
			else if (modelPRP == null) {
				testBean.setTestResult(false,
					"(Public) Render parameter set in event phase wasn't received/value pushed to its model in the header/render phase.");

				return Constants.TEST_FAILED;
			}
			else if ((checkPRP == null) || !modelPRP.equals(checkPRP)) {
				testBean.setTestResult(false,
					"(Public) Render parameter value set in event phase isn't set in the model in the render.  PRP model value: " +
					modelPRP + " but expected: " + checkPRP);

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"Event controller tests passed.<br>Registered event handler called and its navigational result processed corectly.");
				testBean.appendTestDetail("EVENT_PHASE attribute correctly set during event processing.");
				testBean.appendTestDetail(
					"(Private) Render parameter set during action phase was properly carried forward from the event phase.");
				testBean.appendTestDetail(
					"(Public) Render parameter whose underlying model value was set during event phase was properly carried forward into the render.");

				return Constants.TEST_SUCCESS;

			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.58
	@BridgeTest(test = "eventNoHandlerPRPPreservedTest")
	public String eventNoHandlerPRPPreservedTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Set the value into the model underneath the public render parameter
			externalContext.getRequestMap().put("modelPRP", testBean.getTestName());

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventNoHandlerPRPPreservedTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// ensure that both the public render paramter and the model are there and have the same value
			RenderRequest request = (RenderRequest) externalContext.getRequest();
			String[] prpArray = request.getPublicParameterMap().get("testPRP");
			String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");

			if (prpArray == null) {
				testBean.setTestResult(false,
					"event raised without a registered handler didn't carry forward the public render parameter.");

				return Constants.TEST_FAILED;
			}
			else if (modelPRP == null) {
				testBean.setTestResult(false,
					"event raised without a registered handler didn't update the model from the passed public render parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!modelPRP.equals(prpArray[0])) {
				testBean.setTestResult(false,
					"event raised without a registered handler:  passed public render parameter value doesn't match underlying one.");

				return Constants.TEST_FAILED;
			}
			else if (!modelPRP.equals(testBean.getTestName())) {
				testBean.setTestResult(false,
					"event raised without a registered handler:  public render parameter didn't contain expected value.  PRP value: " +
					modelPRP + " but expected: " + testBean.getTestName());

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"event raised without a registered handler worked correctly as the public render parameter was maintained.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- Render/Action
	// Test #5.33 --
	@BridgeTest(test = "eventPhaseListenerTest")
	public String eventPhaseListenerTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> m = externalContext.getRequestMap();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventPhaseListenerTest"; // action Navigation result
		}
		else {

			testBean.setTestComplete(true);

			// Phase Listener (below) has set these attributes
			PhaseId lastBeforePhaseId = (PhaseId) m.get("org.apache.portlet.faces.tck.lastBeforePhase");
			PhaseId lastAfterPhaseId = (PhaseId) m.get("org.apache.portlet.faces.tck.lastAfterPhase");

			if ((lastBeforePhaseId == null) || (lastAfterPhaseId == null)) {
				testBean.setTestResult(false,
					"Event incorrectly didn't invoke either or both the RESTORE_VIEW before/after listener.  Its also possible the event wasn't received.");

				return Constants.TEST_FAILED;
			}
			else if ((lastBeforePhaseId == PhaseId.RESTORE_VIEW) && (lastAfterPhaseId == PhaseId.RESTORE_VIEW)) {
				testBean.setTestResult(true,
					"Event properly invoked the RESTORE_VIEW phase including calling its before/after listeners and didnt' execute any other action phases.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Event incorrectly executed an action phase/listener post RESTORE_VIEW: lastBeforePhase: " +
					lastBeforePhaseId.toString() + " lastAfterPhase: " + lastAfterPhaseId.toString());

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.50
	@BridgeTest(test = "eventScopeNotRestoredModeChangedTest")
	public String eventScopeNotRestoredModeChangedTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Clear settings from previous run of the test
			externalContext.getSessionMap().put(TestEventHandler.EVENT_RECEIVED, null);
			externalContext.getSessionMap().put(TestEventHandler.EVENT_TEST_FAILED, null);

			// Place a request attr in scope so we can make sure its not there later
			externalContext.getRequestMap().put(TestEventHandler.EVENTATTR, testBean.getTestName());

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventScopeNotRestoredModeChangedTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			Event event = (Event) externalContext.getSessionMap().get(TestEventHandler.EVENT_RECEIVED);
			String failedMsg = (String) externalContext.getSessionMap().get(TestEventHandler.EVENT_TEST_FAILED);
			String payload = (String) externalContext.getRequestMap().get(TestEventHandler.EVENTATTR);

			if (event == null) {
				testBean.setTestResult(false, "Raised event wasn't received.");

				return Constants.TEST_FAILED;
			}
			else if (failedMsg != null) {
				testBean.setTestResult(false, failedMsg);

				return Constants.TEST_FAILED;
			}
			else if (payload != null) {
				testBean.setTestResult(false,
					"Event navigation indicated a mode change but the request scope was preserved.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"Request scope not preserved after event navigation indicated a mode change.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.49
	@BridgeTest(test = "eventScopeNotRestoredRedirectTest")
	public String eventScopeNotRestoredRedirectTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Clear settings from previous run of the test
			externalContext.getSessionMap().put(TestEventHandler.EVENT_RECEIVED, null);
			externalContext.getSessionMap().put(TestEventHandler.EVENT_TEST_FAILED, null);

			// Place a request attr in scope so we can make sure its not there later
			externalContext.getRequestMap().put(TestEventHandler.EVENTATTR, testBean.getTestName());

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventScopeNotRestoredRedirectTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			Event event = (Event) externalContext.getSessionMap().get(TestEventHandler.EVENT_RECEIVED);
			String failedMsg = (String) externalContext.getSessionMap().get(TestEventHandler.EVENT_TEST_FAILED);
			String payload = (String) externalContext.getRequestMap().get(TestEventHandler.EVENTATTR);

			if (event == null) {
				testBean.setTestResult(false, "Raised event wasn't received.");

				return Constants.TEST_FAILED;
			}
			else if (failedMsg != null) {
				testBean.setTestResult(false, failedMsg);

				return Constants.TEST_FAILED;
			}
			else if (payload != null) {
				testBean.setTestResult(false,
					"Event navigation issued a redirect but the request scope was preserved.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true, "Request scope not preserved after event navigation issued a redirect.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.48
	@BridgeTest(test = "eventScopeRestoredTest")
	public String eventScopeRestoredTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Clear settings from previous run of the test: Done here because this render doesn't happen in scope so
			// second rendition doesn't keep the test result -- rather rerenders
			externalContext.getSessionMap().put(TestEventHandler.EVENT_RECEIVED, null);
			externalContext.getSessionMap().put(TestEventHandler.EVENT_TEST_FAILED, null);

			// Place a request attr in scope so we can make sure its still there later
			externalContext.getRequestMap().put(TestEventHandler.EVENTATTR, testBean.getTestName());

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "eventScopeRestoredTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			Event event = (Event) externalContext.getSessionMap().get(TestEventHandler.EVENT_RECEIVED);
			String failedMsg = (String) externalContext.getSessionMap().get(TestEventHandler.EVENT_TEST_FAILED);
			String payload = (String) externalContext.getRequestMap().get(TestEventHandler.EVENTATTR);

			if (event == null) {
				testBean.setTestResult(false, "Raised event wasn't received.");

				return Constants.TEST_FAILED;
			}
			else if (failedMsg != null) {
				testBean.setTestResult(false, failedMsg);

				return Constants.TEST_FAILED;
			}
			else if ((payload == null) || !payload.equals(testBean.getTestName())) {
				testBean.setTestResult(false,
					"Event received and request scope restored but that scope wasn't carried forward into the render");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true, "Event received and request scope restored.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.20
	@BridgeTest(test = "exceptionThrownWhenNoDefaultViewIdTest")
	public String exceptionThrownWhenNoDefaultViewIdTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "exceptionThrownWhenNoDefaultViewIdTest"; // action Navigation result
		}
		else {

			// If redisplay hasn't been invoked yet -- merely return
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") == null) {
				return "exceptionThrownWhenNoDefaultViewIdTest";
			}

			// Note we should never get here because the default viewId isn't defined.
			// We should now be in the default view for edit Mode
			testBean.setTestComplete(true);

			testBean.setTestResult(false,
				"Unexpectedly ended up in render -- should of had a BridgeDefaultViewNotSpecifiedException thrown.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.25
	@BridgeTest(test = "facesContextReleasedActionTest")
	public String facesContextReleasedActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "facesContextReleasedActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			boolean result = ((Boolean) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testResult"))
				.booleanValue();
			testBean.setTestResult(result,
				(String) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testDetail"));

			if (result) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.59
	@BridgeTest(test = "facesContextReleasedEventTest")
	public String facesContextReleasedEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "facesContextReleasedEventTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			boolean result = ((Boolean) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testResult"))
				.booleanValue();
			testBean.setTestResult(result,
				(String) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testDetail"));

			if (result) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.19
	@BridgeTest(test = "ignoreCurrentViewIdModeChangeTest")
	public String ignoreCurrentViewIdModeChangeTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "ignoreCurrentViewIdModeChangeTest"; // action Navigation result
		}
		else {

			// If redisplay hasn't been invoked yet -- merely return
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") == null) {
				return "ignoreCurrentViewIdModeChangeTest";
			}

			// We should now be in the default view for edit Mode
			String viewId = facesContext.getViewRoot().getViewId();

			testBean.setTestComplete(true);

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {
				testBean.setTestResult(true,
					"Second render correctly used the default view because there was a mode change.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Second render incorrectly rerendered existing view though there was a view change.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.31
	@BridgeTest(test = "isPostbackTest")
	public String isPostbackTest(TestBean testBean) {

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "isPostbackTest"; // action Navigation result
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();

			testBean.setTestComplete(true);

			if (facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext)) {
				testBean.setTestResult(true,
					"Render after action properly encoded so ResponseStateManager.isPostback is true.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Render after action isn't properly encoded in that ResponseStateManager.isPostback is false.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test # 5.64
	@BridgeTest(test = "nonFacesResourceTest")
	public String nonFacesResourceTest(TestBean testBean) {
		// Test renders a page containing an in-protocol resource (image) and a button When the image is rendered our
		// portlet wraps the request so it can substitute its own RequestDispatcher and verify forward is called on the
		// targeted resource -- a flag is set in the session to indicate success/fail The button is pushed -- in the
		// action handler (here) we navigate to the result page which reads the session attr and renders result.

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "nonFacesResourceTest";
		}

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (Bridge.PortletPhase.HEADER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {
			testBean.setTestComplete(true);

			PortletSession portletSession = ((PortletRequest) externalContext.getRequest()).getPortletSession(true);
			Boolean b = (Boolean) portletSession.getAttribute("com.liferay.faces.bridge.tck.NonFacesResourceInvokedInForward",
					PortletSession.APPLICATION_SCOPE);

			if (b == null) {
				testBean.setTestResult(false,
					"NonFaces resource not invoked implying the bridge didn't handle/forward as required.");

				return Constants.TEST_FAILED;
			}
			else {
				portletSession.removeAttribute("com.liferay.faces.bridge.tck.NonFacesResourceInvokedInForward",
					PortletSession.APPLICATION_SCOPE);
			}

			if (b.equals(Boolean.TRUE)) {
				testBean.setTestResult(true, "NonFaces resource correctly dispatched to by bridge via a forward.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "NonFaces resource called by bridge but not in a forward.");

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}

	}

	// Test is MultiRequest -- Render/Action
	// Test #5.26
	@BridgeTest(test = "portletPhaseRemovedActionTest")
	public String portletPhaseRemovedActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "portletPhaseRemovedActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			boolean result = ((Boolean) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testResult"))
				.booleanValue();
			testBean.setTestResult(result,
				(String) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testDetail"));

			if (result) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.60
	@BridgeTest(test = "portletPhaseRemovedEventTest")
	public String portletPhaseRemovedEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			StateAwareResponse response = (StateAwareResponse) externalContext.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testBean.getTestName());

			return "portletPhaseRemovedEventTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			boolean result = ((Boolean) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testResult"))
				.booleanValue();
			testBean.setTestResult(result,
				(String) externalContext.getSessionMap().get("org.apache.portlet.faces.tck.testDetail"));

			if (result) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.74
	@BridgeTest(test = "prpUpdatedFromActionTest")
	public String prpUpdatedFromActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Now verify that a change to a public render parameter is carried forward
			String currentValue = (String) requestMap.get("modelPRP");

			if (currentValue == null) {
				currentValue = "1";
			}
			else {
				currentValue = currentValue.concat("1");
			}

			// Config is setup to exclude this value from bridge request scope -- so only get carried forward
			// if received in render request
			requestMap.put("modelPRP", currentValue);

			// Stash copy of value in an attr that is carried forward to compare.
			requestMap.put("tck.compareModelPRPValue", currentValue);

			return "prpUpdatedFromActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");
			String checkPRP = (String) externalContext.getRequestMap().get("tck.compareModelPRPValue");

			if (modelPRP == null) {
				testBean.setTestResult(false,
					"(Public) Render parameter set in action phase wasn't received/value pushed to its model in the header/render phase.");

				return Constants.TEST_FAILED;
			}
			else if ((checkPRP == null) || !modelPRP.equals(checkPRP)) {
				testBean.setTestResult(false,
					"(Public) Render parameter value set in action phase isn't set in the model in the render.  PRP model value: " +
					modelPRP + " but expected: " + checkPRP);

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"(Public) Render parameter whose underlying model value was set during action phase was properly carried forward into the render.");

				return Constants.TEST_SUCCESS;

			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.18
	@BridgeTest(test = "renderRedirectTest")
	public String renderRedirectTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			return "renderRedirectTest"; // action Navigation result
		}
		else {
			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {

				// issue the redirect
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

				try {
					externalContext.redirect(viewHandler.getActionURL(facesContext, "/tests/redisplayRenderRequestTest.xhtml"));
				}
				catch (IOException e) {
					testBean.setTestComplete(true);
					testBean.setTestResult(false, "Call to render redirect threw an IOException: " + e.getMessage());

					return Constants.TEST_FAILED;
				}

				return "renderRedirectTest";
			}
			else if (viewId.equals("/tests/redisplayRenderRequestTest.xhtml")) {

				// If redisplay hasn't been invoked yet -- merely return
				if (externalContext.getSessionMap().get("org.apache.portlet.faces.tck.redisplay") == null) {

					// because this is a redirect during render -- we can't pass new/additional parameters on
					// the redisplay -- since the bridge ignores them (it uses the ones from the original redirect
					// so instead mark that we have done the redisplay in the session
					externalContext.getSessionMap().put("org.apache.portlet.faces.tck.redisplay", Boolean.TRUE);

					return "renderRedirectTest";
				}

				testBean.setTestComplete(true);
				externalContext.getSessionMap().remove("org.apache.portlet.faces.tck.redisplay");

				testBean.setTestResult(true, "Redisplay after redirect correctly rendered the redirected view.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestComplete(true);
				externalContext.getSessionMap().remove("org.apache.portlet.faces.tck.redisplay");
				testBean.setTestResult(false, "Ended up in an unexpected view during renderRedirect test:");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.66
	@BridgeTest(test = "scopeAfterRedisplayResourcePPRTest")
	public String scopeAfterRedisplayResourcePPRTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			m.put("testAttr", testBean.getTestName());

			return "";
		}
		else if (Bridge.PortletPhase.HEADER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext)) &&
				(m.get("com.liferay.faces.bridge.tck.pprSubmitted") != null)) {
			testBean.setTestComplete(true);

			m.remove("com.liferay.faces.bridge.tck.pprSubmitted");

			if ((m.get("testAttr") != null) && ((String) m.get("testAttr")).equals(testBean.getTestName())) {
				testBean.setTestResult(true,
					"Redisplay after resource request correctly restored scope including attr added during resource execution.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Redisplay after resource request didn't restored scope including the attr added during resource execution.");

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		return "";
	}

	// ActionListener
	@BridgeTest(test = "scopeAfterRedisplayResourcePPRTestActionListener")
	public void scopeAfterRedisplayResourcePPRTestListener(TestBean testBean, ActionEvent action) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.65
	@BridgeTest(test = "scopeNotRestoredResourceTest")
	public String scopeNotRestoredResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// set a request scoped attr and then continue
			m.put("testAttr", testBean.getTestName());

			return "scopeNotRestoredResourceTest";
		}

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			String s = (String) m.get("testAttr");

			if (s == null) {
				testBean.setTestResult(true, "Resource request correctly executed without restoring request scope.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "Resource request incorrectly has restored the request scope.");

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test #5.13 (also by proxy verifies #5.12
	@BridgeTest(test = "verifyPortletObjectsTest")
	public String verifyPortletObjectsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		testBean.setTestComplete(true);

		// Are we in the rightview???
		String s = (String) m.get("javax.portlet.faces.tck.verifyPortletObjectsPass");

		if (s != null) {
			testBean.setTestResult(true, s);

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false, (String) m.get("javax.portlet.faces.tck.verifyPortletObjectsFail"));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.14
	@BridgeTest(test = "verifyPortletPhaseTest")
	public String verifyPortletPhaseTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			return "verifyPortletPhaseTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Now verify that what should have been carried forward has and what shouldn't hasn't.

			String s = (String) m.get("javax.portlet.faces.tck.verifyPortletPhaseDuringActionPass");
			String s1 = (String) m.get("javax.portlet.faces.tck.verifyPortletPhaseDuringRenderPass");

			if ((s != null) && (s1 != null)) {
				testBean.setTestResult(true, s + s1);

				return Constants.TEST_SUCCESS;
			}

			if (s == null) {
				s = (String) m.get("javax.portlet.faces.tck.verifyPortletPhaseDuringActionFail");
			}

			if (s1 == null) {
				s1 = (String) m.get("javax.portlet.faces.tck.verifyPortletPhaseDuringRenderFail");
			}

			testBean.setTestResult(false, s + s1);

			return Constants.TEST_FAILED;

		}
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.63
	@BridgeTest(test = "verifyResourcePhaseTest")
	public String verifyResourcePhaseTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map m = externalContext.getRequestMap();

		testBean.setTestComplete(true);

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			testBean.setTestResult(true, "ResourcePhase attribute correctly set during resource processing.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false, "ResourcePhase attribute not set during resource processing.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.20 (first test of 2) -- viewId set directly by portlet using request attribute
	@BridgeTest(test = "viewIdWithParam_1_Test")
	public String viewIdWithParam_1_Test(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		testBean.setTestComplete(true);

		// Parameter encoded in the faces-config.xml target
		String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

		if ((paramValue != null) && paramValue.equals("testValue")) {
			testBean.setTestResult(true,
				"Bridge correctly included parameter from viewId querystring when viewId set explicitly.");

			return Constants.TEST_SUCCESS;
		}
		else {

			if (paramValue == null) {
				testBean.setTestResult(false,
					"Bridge didn't included parameter from viewId querystring when viewId set explicitly.");
			}
			else {
				testBean.setTestResult(false,
					"Bridge didn't properly include parameter from viewId querystring when viewId set explicitly.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
		}

		return Constants.TEST_FAILED;
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.20 (second test of 2) -- viewId set in the default viewId (initparam)
	@BridgeTest(test = "viewIdWithParam_2_Test")
	public String viewIdWithParam_2_Test(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		testBean.setTestComplete(true);

		// Parameter encoded in the faces-config.xml target
		String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

		if ((paramValue != null) && paramValue.equals("testValue")) {
			testBean.setTestResult(true,
				"Bridge correctly included parameter from viewId querystring when using default viewId.");

			return Constants.TEST_SUCCESS;
		}
		else {

			if (paramValue == null) {
				testBean.setTestResult(false,
					"Bridge didn't included parameter from viewId querystring when using default viewId.");
			}
			else {
				testBean.setTestResult(false,
					"Bridge didn't properly include parameter from viewId querystring when using default viewId.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
		}

		return Constants.TEST_FAILED;
	}

}
