/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_3;

import java.util.Map;

import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.ResponseStateManager;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object implements PhaseListener, BridgePublicRenderParameterHandler {

	// Java 1.6+ @Override
	public void afterPhase(PhaseEvent phaseEvent) {

		// Do nothing
		return;
	}

	// Java 1.6+ @Override
	public void beforePhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
		String testname = (String) requestMap.get(Constants.TEST_NAME);

		PhaseId phase = phaseEvent.getPhaseId();

		if ((!testname.equals("processPRPInRestoreViewPhaseTest")) ||
				(!Boolean.TRUE.equals(
						(Boolean) externalContext.getSessionMap().get(
							"tck.processPRPInRestoreViewPhaseTest.modelPRPSet"))))
			return;

		// Marked as true  so command link name would change.
		externalContext.getSessionMap().put("com.liferay.faces.bridge.tck.testCompleted", Boolean.TRUE);

		// verify not set in RestoreView
		if (phase == PhaseId.RESTORE_VIEW) {

			if (requestMap.get("modelPRP") == null) {
				externalContext.getRequestMap().put("tck.notSetBeforeRestoreView", Boolean.TRUE);
			}
		}
		else {

			if (requestMap.get("modelPRP") != null) {
				externalContext.getRequestMap().put("tck.setAfterRestoreView", Boolean.TRUE);
			}
		}

	}

	// Test is SingleRequest -- Render
	// Test #5.43
	@BridgeTest(test = "checkViewHistoryTest")
	public String checkViewHistoryTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		ELResolver facesResolver = facesContext.getELContext().getELResolver();
		PortletConfig portletConfig = (PortletConfig) facesResolver.getValue(facesContext.getELContext(), null,
				"portletConfig");

		testBean.setTestComplete(true);

		String view = (String) sessionMap.get("javax.portlet.faces.viewIdHistory.view");
		String edit = (String) sessionMap.get("javax.portlet.faces.viewIdHistory.edit");
		String help = (String) sessionMap.get("javax.portlet.faces.viewIdHistory.help");

		Map<String, String> defaultViewIdMap = (Map<String, String>) ((PortletContext) externalContext.getContext())
			.getAttribute(Bridge.BRIDGE_PACKAGE_PREFIX + portletConfig.getPortletName() + "." +
				Bridge.DEFAULT_VIEWID_MAP);

		if (view == null) {
			testBean.setTestResult(false, "javax.portlet.faces.viewIdHistory.view session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (edit == null) {
			testBean.setTestResult(false, "javax.portlet.faces.viewIdHistory.edit session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (help == null) {
			testBean.setTestResult(false, "javax.portlet.faces.viewIdHistory.help session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (!view.startsWith(defaultViewIdMap.get("view"))) {
			testBean.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.view contains unexpected value. Expected: " +
				defaultViewIdMap.get("view") + " but value was: " + view);

			return Constants.TEST_FAILED;
		}

		if (!edit.startsWith(defaultViewIdMap.get("edit"))) {
			testBean.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.edit contains unexpected value. Expected: " +
				defaultViewIdMap.get("edit") + " but value was: " + edit);

			return Constants.TEST_FAILED;
		}

		if (!help.startsWith(defaultViewIdMap.get("help"))) {
			testBean.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.help contains unexpected value. Expected: " +
				defaultViewIdMap.get("help") + " but value was: " + help);

			return Constants.TEST_FAILED;
		}

		testBean.setTestResult(true, "Correctly contained the viewId history session attributes.");

		return Constants.TEST_SUCCESS;
	}

	// PhaseListener tests
	// Java 1.6+ @Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.46
	@BridgeTest(test = "navigateToLastViewTest")
	public String navigateToLastViewTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletMode portletMode = (PortletMode) ((PortletRequest) externalContext.getRequest()).getPortletMode();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		String theView = facesContext.getViewRoot().getViewId();
		String theHistoryView = (String) externalContext.getSessionMap().get("javax.portlet.faces.viewIdHistory.view");
		String theHistoryEdit = (String) externalContext.getSessionMap().get("javax.portlet.faces.viewIdHistory.view");
		String renderParam = (String) externalContext.getRequestParameterMap().get(
				"com.liferay.faces.bridge.tck.testAttr");

		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/MultiRequestTest.jsp")) {

				if (portletMode.toString().equals("view")) {

					// First page -- navigate to the second page
					return "navigateToLastViewTest1"; // action Navigation result
				}
				else
				// (portletMode.toString().equals("edit"))
				{

					// Third page -- mark the test as complete so can test/render the result
					// Done on session attribute because we cross the request scope boundary (mode change)
					// and hence the current test bean is lost
					externalContext.getSessionMap().put("com.liferay.faces.bridge.tck.testCompleted", Boolean.TRUE);

					return "navigateToLastViewTest3"; // returns from edit mode
				}
			}
			else
			// (viewId.equals("/tests/RedsiplayActionRequestTest.jsp"))
			{

				// Coming from the first page
				// Second page -- navigate to the edit mode
				// Render parameter added in the navigation
				return "navigateToLastViewTest2"; // action Navigation result -- goes to edit mode
			}
		}
		else {

			// Should only get here from
			if (Boolean.TRUE.equals(
						(Boolean) externalContext.getSessionMap().get("com.liferay.faces.bridge.tck.testCompleted"))) {
				testBean.setTestComplete(true);
				externalContext.getSessionMap().remove("com.liferay.faces.bridge.tck.testCompleted");

				if (externalContext.getRequestParameterMap().get("com.liferay.faces.bridge.tck.testAttr") != null) {
					testBean.setTestResult(true,
						"Successfully returned from edit mode to last view with its existing render parameters.");

					return Constants.TEST_SUCCESS;
				}
				else {
					testBean.setTestResult(false,
						"Though we returned from edit mode to last view it was without its existing render parameters.");

					return Constants.TEST_FAILED;
				}
			}
			else {
				return "Test is still being run.";
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.41
	@BridgeTest(test = "noViewStateParamOnModeChangeTest")
	public String noViewStateParamOnModeChangeTest(TestBean testBean) {

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "noViewStateParamOnModeChangeTest"; // action Navigation result
		}
		else {
			ExternalContext externalContext = facesContext.getExternalContext();

			testBean.setTestComplete(true);

			if (externalContext.getRequestParameterMap().get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testBean.setTestResult(true,
					"Render after mode change properly doesn't expose the ResponseStateManager.VIEW_STATE_PARAM.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Render after mode change incorrectly exposes the ResponseStateManager.VIEW_STATE_PARAM.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.69
	@BridgeTest(test = "processPRPInRestoreViewPhaseTest")
	public String processPRPInRestoreViewPhaseTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {

			if (externalContext.getSessionMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet") == null) {
				externalContext.getRequestMap().put("modelPRP", testBean.getTestName());
				externalContext.getRequestMap().put("modelPRP2", testBean.getTestName());

				// Put it on the request Map -- to carry forward to the render -- which will set it on the session --
				// this avoids having the render that follows this action from thinking the test is done.
				externalContext.getRequestMap().put("tck.processPRPInRestoreViewPhaseTest.modelPRPSet", Boolean.TRUE);

				return "processPRPInRestoreViewPhaseTest"; // returns from edit mode
			}
			else {
				return "processPRPInRestoreViewPhaseTestResult";
			}

		}
		else {

			if (Boolean.TRUE.equals(
						(Boolean) externalContext.getSessionMap().get(
							"tck.processPRPInRestoreViewPhaseTest.modelPRPSet"))) {
				testBean.setTestComplete(true);

				externalContext.getSessionMap().remove("tck.processPRPInRestoreViewPhaseTest.modelPRPSet");

				// Marked as true in the phaseListener so command link name would change.
				externalContext.getSessionMap().remove("com.liferay.faces.bridge.tck.testCompleted");

				if (externalContext.getRequestMap().get("tck.notSetBeforeRestoreView") == null) {
					testBean.setTestResult(false,
						"PRP set before RestoreView phase. It should be set after the RestoreView phase and before other phases.");

					return Constants.TEST_FAILED;
				}
				else if (externalContext.getRequestMap().get("tck.setAfterRestoreView") == null) {
					testBean.setTestResult(false,
						"PRP not set in the after RestoreView phase. It wasn't set in any of the other phases before phase.");

					return Constants.TEST_FAILED;
				}
				else {
					testBean.setTestResult(true,
						"PRP correctly updated the model after the RestoreView phase and before other phases.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {

				if (externalContext.getRequestMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet") != null) {

					// Place on session so next action triggers running the test.
					externalContext.getSessionMap().put("tck.processPRPInRestoreViewPhaseTest.modelPRPSet",
						Boolean.TRUE);
				}

				return "Test is still being run.";
			}
		}
	}

	// Java 1.6+ @Override
	public void processUpdates(FacesContext facesContext) {
		facesContext.getExternalContext().getRequestMap().put("tck.prpProcessUpdatesCalled", Boolean.TRUE);
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.70 and 5.71
	@BridgeTest(test = "prpModelUpdateTest")
	public String prpModelUpdateTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {

			if (externalContext.getSessionMap().get("tck.prpModelUpdateTest.modelPRPSet") == null) {
				externalContext.getRequestMap().put("modelPRP", testBean.getTestName());
				externalContext.getRequestMap().put("modelPRP2", testBean.getTestName());

				// Put it on the request Map -- to carry forward to the render -- which will set it on the session --
				// this avoids having the render that follows this action from thinking the test is done.
				externalContext.getRequestMap().put("tck.prpModelUpdateTest.modelPRPSet", Boolean.TRUE);

				return "prpModelUpdateTest"; // returns from edit mode
			}
			else {
				return "prpModelUpdateTest";
			}

		}
		else {

			if (Boolean.TRUE.equals(
						(Boolean) externalContext.getSessionMap().get("tck.prpModelUpdateTest.modelPRPSet"))) {
				testBean.setTestComplete(true);

				externalContext.getSessionMap().remove("tck.prpModelUpdateTest.modelPRPSet");

				String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");
				String modelPRP2 = (String) externalContext.getRequestMap().get("modelPRP2");

				if (modelPRP == null) {
					testBean.setTestResult(false, "Expect 'modelPRP' PRP not set.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testBean.getTestName())) {
					testBean.setTestResult(false,
						"Expect 'modelPRP' doesn't have expected value.  Expected: " + testBean.getTestName() +
						" but PRP has a value of: " + modelPRP);

					return Constants.TEST_FAILED;
				}
				else if (modelPRP2 == null) {
					testBean.setTestResult(false, "Expect 'modelPRP2' PRP not set.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP2.equals(testBean.getTestName())) {
					testBean.setTestResult(false,
						"Expect 'modelPRP2' doesn't have expected value.  Expected: " + testBean.getTestName() +
						" but PRP has a value of: " + modelPRP2);

					return Constants.TEST_FAILED;
				}
				else if (externalContext.getRequestMap().get("tck.prpProcessUpdatesCalled") == null) {
					testBean.setTestResult(false,
						"Though incoming PRPs updated their models, the registered processUpdates handler wasn't called.");

					return Constants.TEST_FAILED;
				}
				else {
					testBean.setTestResult(true,
						"Both PRPs correctly updated their models with expected values and the processUpdates handler was called.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {

				if (externalContext.getRequestMap().get("tck.prpModelUpdateTest.modelPRPSet") != null) {

					// Place on session so next action triggers running the test.
					externalContext.getSessionMap().put("tck.prpModelUpdateTest.modelPRPSet", Boolean.TRUE);
				}

				return "Test is still being run.";
			}
		}
	}

}
