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
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object implements PhaseListener, BridgePublicRenderParameterHandler {

	public void afterPhase(PhaseEvent event) {

		// Do nothing
		return;
	}

	public void beforePhase(PhaseEvent event) {
		FacesContext ctx = event.getFacesContext();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> m = ctx.getExternalContext().getRequestMap();
		String testname = (String) m.get(Constants.TEST_NAME);

		PhaseId phase = event.getPhaseId();

		if ((!testname.equals("processPRPInRestoreViewPhaseTest")) ||
				(!Boolean.TRUE.equals(
						(Boolean) extCtx.getSessionMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet"))))
			return;

		// Marked as true  so command link name would change.
		extCtx.getSessionMap().put("com.liferay.faces.bridge.tck.testCompleted", Boolean.TRUE);

		// verify not set in RestoreView
		if (phase == PhaseId.RESTORE_VIEW) {

			if (m.get("modelPRP") == null) {
				extCtx.getRequestMap().put("tck.notSetBeforeRestoreView", Boolean.TRUE);
			}
		}
		else {

			if (m.get("modelPRP") != null) {
				extCtx.getRequestMap().put("tck.setAfterRestoreView", Boolean.TRUE);
			}
		}

	}

	// Test is SingleRequest -- Render
	// Test #5.43
	@BridgeTest(test = "checkViewHistoryTest")
	public String checkViewHistoryTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> m = extCtx.getSessionMap();
		ELResolver facesResolver = ctx.getELContext().getELResolver();
		PortletConfig config = (PortletConfig) facesResolver.getValue(ctx.getELContext(), null, "portletConfig");

		testRunner.setTestComplete(true);

		String view = (String) m.get("javax.portlet.faces.viewIdHistory.view");
		String edit = (String) m.get("javax.portlet.faces.viewIdHistory.edit");
		String help = (String) m.get("javax.portlet.faces.viewIdHistory.help");

		Map<String, String> defaultViewIdMap = (Map<String, String>) ((PortletContext) extCtx.getContext())
			.getAttribute(Bridge.BRIDGE_PACKAGE_PREFIX + config.getPortletName() + "." + Bridge.DEFAULT_VIEWID_MAP);

		if (view == null) {
			testRunner.setTestResult(false, "javax.portlet.faces.viewIdHistory.view session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (edit == null) {
			testRunner.setTestResult(false, "javax.portlet.faces.viewIdHistory.edit session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (help == null) {
			testRunner.setTestResult(false, "javax.portlet.faces.viewIdHistory.help session attribute doesn't exist.");

			return Constants.TEST_FAILED;
		}

		if (!view.startsWith(defaultViewIdMap.get("view"))) {
			testRunner.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.view contains unexpected value. Expected: " +
				defaultViewIdMap.get("view") + " but value was: " + view);

			return Constants.TEST_FAILED;
		}

		if (!edit.startsWith(defaultViewIdMap.get("edit"))) {
			testRunner.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.edit contains unexpected value. Expected: " +
				defaultViewIdMap.get("edit") + " but value was: " + edit);

			return Constants.TEST_FAILED;
		}

		if (!help.startsWith(defaultViewIdMap.get("help"))) {
			testRunner.setTestResult(false,
				"javax.portlet.faces.viewIdHistory.help contains unexpected value. Expected: " +
				defaultViewIdMap.get("help") + " but value was: " + help);

			return Constants.TEST_FAILED;
		}

		testRunner.setTestResult(true, "Correctly contained the viewId history session attributes.");

		return Constants.TEST_SUCCESS;
	}

	// PhaseListener tests
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.46
	@BridgeTest(test = "navigateToLastViewTest")
	public String navigateToLastViewTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		PortletMode mode = (PortletMode) ((PortletRequest) extCtx.getRequest()).getPortletMode();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		String theView = ctx.getViewRoot().getViewId();
		String theHistoryView = (String) extCtx.getSessionMap().get("javax.portlet.faces.viewIdHistory.view");
		String theHistoryEdit = (String) extCtx.getSessionMap().get("javax.portlet.faces.viewIdHistory.view");
		String renderParam = (String) extCtx.getRequestParameterMap().get("com.liferay.faces.bridge.tck.testAttr");

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			String viewId = ctx.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTest.xhtml")) {

				if (mode.toString().equals("view")) {

					// First page -- navigate to the second page
					return "navigateToLastViewTest1"; // action Navigation result
				}
				else
				// (mode.toString().equals("edit"))
				{

					// Third page -- mark the test as complete so can test/render the result
					// Done on session attribute because we cross the request scope boundary (mode change)
					// and hence the current test bean is lost
					extCtx.getSessionMap().put("com.liferay.faces.bridge.tck.testCompleted", Boolean.TRUE);

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
						(Boolean) extCtx.getSessionMap().get("com.liferay.faces.bridge.tck.testCompleted"))) {
				testRunner.setTestComplete(true);
				extCtx.getSessionMap().remove("com.liferay.faces.bridge.tck.testCompleted");

				if (extCtx.getRequestParameterMap().get("com.liferay.faces.bridge.tck.testAttr") != null) {
					testRunner.setTestResult(true,
						"Successfully returned from edit mode to last view with its existing render parameters.");

					return Constants.TEST_SUCCESS;
				}
				else {
					testRunner.setTestResult(false,
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
	public String noViewStateParamOnModeChangeTest(TestRunnerBean testRunner) {

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "noViewStateParamOnModeChangeTest"; // action Navigation result
		}
		else {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extCtx = ctx.getExternalContext();

			testRunner.setTestComplete(true);

			if (extCtx.getRequestParameterMap().get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testRunner.setTestResult(true,
					"Render after mode change properly doesn't expose the ResponseStateManager.VIEW_STATE_PARAM.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"Render after mode change incorrectly exposes the ResponseStateManager.VIEW_STATE_PARAM.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.69
	@BridgeTest(test = "processPRPInRestoreViewPhaseTest")
	public String processPRPInRestoreViewPhaseTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			if (extCtx.getSessionMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet") == null) {
				extCtx.getRequestMap().put("modelPRP", testRunner.getTestName());
				extCtx.getRequestMap().put("modelPRP2", testRunner.getTestName());

				// Put it on the request Map -- to carry forward to the render -- which will set it on the session --
				// this avoids having the render that follows this action from thinking the test is done.
				extCtx.getRequestMap().put("tck.processPRPInRestoreViewPhaseTest.modelPRPSet", Boolean.TRUE);

				return "processPRPInRestoreViewPhaseTest"; // returns from edit mode
			}
			else {
				return "processPRPInRestoreViewPhaseTestResult";
			}

		}
		else {

			if (Boolean.TRUE.equals(
						(Boolean) extCtx.getSessionMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet"))) {
				testRunner.setTestComplete(true);

				extCtx.getSessionMap().remove("tck.processPRPInRestoreViewPhaseTest.modelPRPSet");

				// Marked as true in the phaseListener so command link name would change.
				extCtx.getSessionMap().remove("com.liferay.faces.bridge.tck.testCompleted");

				if (extCtx.getRequestMap().get("tck.notSetBeforeRestoreView") == null) {
					testRunner.setTestResult(false,
						"PRP set before RestoreView phase. It should be set after the RestoreView phase and before other phases.");

					return Constants.TEST_FAILED;
				}
				else if (extCtx.getRequestMap().get("tck.setAfterRestoreView") == null) {
					testRunner.setTestResult(false,
						"PRP not set in the after RestoreView phase. It wasn't set in any of the other phases before phase.");

					return Constants.TEST_FAILED;
				}
				else {
					testRunner.setTestResult(true,
						"PRP correctly updated the model after the RestoreView phase and before other phases.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {

				if (extCtx.getRequestMap().get("tck.processPRPInRestoreViewPhaseTest.modelPRPSet") != null) {

					// Place on session so next action triggers running the test.
					extCtx.getSessionMap().put("tck.processPRPInRestoreViewPhaseTest.modelPRPSet", Boolean.TRUE);
				}

				return "Test is still being run.";
			}
		}
	}

	public void processUpdates(FacesContext ctx) {
		ctx.getExternalContext().getRequestMap().put("tck.prpProcessUpdatesCalled", Boolean.TRUE);
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.70 and 5.71
	@BridgeTest(test = "prpModelUpdateTest")
	public String prpModelUpdateTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			if (extCtx.getSessionMap().get("tck.prpModelUpdateTest.modelPRPSet") == null) {
				extCtx.getRequestMap().put("modelPRP", testRunner.getTestName());
				extCtx.getRequestMap().put("modelPRP2", testRunner.getTestName());

				// Put it on the request Map -- to carry forward to the render -- which will set it on the session --
				// this avoids having the render that follows this action from thinking the test is done.
				extCtx.getRequestMap().put("tck.prpModelUpdateTest.modelPRPSet", Boolean.TRUE);

				return "prpModelUpdateTest"; // returns from edit mode
			}
			else {
				return "prpModelUpdateTest";
			}

		}
		else {

			if (Boolean.TRUE.equals((Boolean) extCtx.getSessionMap().get("tck.prpModelUpdateTest.modelPRPSet"))) {
				testRunner.setTestComplete(true);

				extCtx.getSessionMap().remove("tck.prpModelUpdateTest.modelPRPSet");

				String modelPRP = (String) extCtx.getRequestMap().get("modelPRP");
				String modelPRP2 = (String) extCtx.getRequestMap().get("modelPRP2");

				if (modelPRP == null) {
					testRunner.setTestResult(false, "Expect 'modelPRP' PRP not set.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testRunner.getTestName())) {
					testRunner.setTestResult(false,
						"Expect 'modelPRP' doesn't have expected value.  Expected: " + testRunner.getTestName() +
						" but PRP has a value of: " + modelPRP);

					return Constants.TEST_FAILED;
				}
				else if (modelPRP2 == null) {
					testRunner.setTestResult(false, "Expect 'modelPRP2' PRP not set.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP2.equals(testRunner.getTestName())) {
					testRunner.setTestResult(false,
						"Expect 'modelPRP2' doesn't have expected value.  Expected: " + testRunner.getTestName() +
						" but PRP has a value of: " + modelPRP2);

					return Constants.TEST_FAILED;
				}
				else if (extCtx.getRequestMap().get("tck.prpProcessUpdatesCalled") == null) {
					testRunner.setTestResult(false,
						"Though incoming PRPs updated their models, the registered processUpdates handler wasn't called.");

					return Constants.TEST_FAILED;
				}
				else {
					testRunner.setTestResult(true,
						"Both PRPs correctly updated their models with expected values and the processUpdates handler was called.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {

				if (extCtx.getRequestMap().get("tck.prpModelUpdateTest.modelPRPSet") != null) {

					// Place on session so next action triggers running the test.
					extCtx.getSessionMap().put("tck.prpModelUpdateTest.modelPRPSet", Boolean.TRUE);
				}

				return "Test is still being run.";
			}
		}
	}

}
