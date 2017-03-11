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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_1;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#init method works as stated in section 4.2.1. Checks that -
 * getExcludedRequestAttributes has been called and the portlet context attribute, qualified with the portlet name is
 * set up - isPreserveActionParameters has been called and the portlet context attribute, qualified with the portlet
 * name is set up - getDefaultViewIdMap has been called and the portlet context attribute set
 */

public class InitMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	// private static String[] EXCLUDED_ATTRS = {"exclude1", "exclude2"};
	private static String[] VIEW_IDS = { "view", "edit", "help" };

	private boolean bGetBridgeClassNameCalled;

	private boolean bGetExcludedRequestAttributesCalled;
	private List<String> mExcludedRequestAttrs = new ArrayList();

	private boolean bIsPreserveActionParametersCalled;
	private boolean bIsPreserveActionParametersResult;

	private boolean bGetDefaultViewIdMapCalled;
	private Map<String, String> mDefaultViewIdMap = new Hashtable<String, String>();

	private boolean bGetBridgeEventHandlerCalled;
	private BridgeEventHandler mEventHandler = null;

	private boolean bGetBridgePRPHandlerCalled;
	private BridgePublicRenderParameterHandler mPRPhandler;

	private boolean bGetDefaultRenderKitIdCalled;
	private String mDefaultRenderKitId = null;

	private StringBuilder mMsg = new StringBuilder();

	public String getBridgeClassName() {
		bGetBridgeClassNameCalled = true;

		return super.getBridgeClassName();
	}

	public BridgeEventHandler getBridgeEventHandler() throws PortletException {
		bGetBridgeEventHandlerCalled = true;
		mEventHandler = super.getBridgeEventHandler();

		return mEventHandler;
	}

	public BridgePublicRenderParameterHandler getBridgePublicRenderParameterHandler() throws PortletException {
		bGetBridgePRPHandlerCalled = true;
		mPRPhandler = super.getBridgePublicRenderParameterHandler();

		return mPRPhandler;
	}

	public String getDefaultRenderKitId() {
		bGetDefaultRenderKitIdCalled = true;
		mDefaultRenderKitId = super.getDefaultRenderKitId();

		return mDefaultRenderKitId;
	}

	public Map getDefaultViewIdMap() {
		bGetDefaultViewIdMapCalled = true;
		mDefaultViewIdMap = super.getDefaultViewIdMap();

		return mDefaultViewIdMap;
	}

	public List<String> getExcludedRequestAttributes() {
		bGetExcludedRequestAttributesCalled = true;

		List<String> mExcludedRequestAttrs = super.getExcludedRequestAttributes();

		return mExcludedRequestAttrs;
	}

	public void init(PortletConfig portletConfig) throws PortletException {
		super.init(portletConfig);

		boolean bTestPassed = true;

		StringBuilder msg = new StringBuilder();

		// getBridgeClassName test
		if (bGetBridgeClassNameCalled) {
			msg.append("getBridgeClassName() called.");
		}
		else {
			bTestPassed = false;
			msg.append("getBridgeClassName() not called.");
		}

		// Check that the excluded request portlet context attributes correspond
		// to those returned from getExcludedRequestAttributes.
		if (bGetExcludedRequestAttributesCalled) {
			msg.append("  getExcludedRequestAttributes() called.");
		}
		else {
			bTestPassed = false;
			msg.append("  getExcludedRequestAttributes() not called.");
		}

		List<String> attrs = (List<String>) getPortletContext().getAttribute("javax.portlet.faces." + getPortletName() +
				".excludedRequestAttributes");

		for (String expectedAttr : mExcludedRequestAttrs) {

			if (!attrs.contains(expectedAttr)) {
				bTestPassed = false;
				msg.append("  Missing excluded request attribute ").append(expectedAttr).append(".");
			}

		}

		// Check that the preserveActionParams portlet context attribute is set
		// to the same value as isPreserveActionParameters.
		if (bIsPreserveActionParametersCalled) {
			msg.append("  isPreserveActionParameters() called.");
		}
		else {
			bTestPassed = false;
			msg.append("  isPreserveActionParameters() not called.");
		}

		Boolean contextValue = (Boolean) getPortletContext().getAttribute("javax.portlet.faces." + getPortletName() +
				".preserveActionParams");

		if (!Boolean.valueOf(bIsPreserveActionParametersResult).equals(contextValue)) {
			msg.append("  Value of the preserveActionParams context attribute is ").append(contextValue).append(
				", expected value is ").append(Boolean.valueOf(bIsPreserveActionParametersResult)).append(".");
			bTestPassed = false;
		}

		// Check that the associated PortletContext attributes are set according to
		// getDefaultViewIdMap.
		if (bGetDefaultViewIdMapCalled) {
			msg.append("  getDefaultViewIdMapCalled() called.");
		}
		else {
			msg.append("  getDefaultViewIdMapCalled() not called.");
			bTestPassed = false;
		}

		Map viewIdMapAttr = (Map) getPortletContext().getAttribute("javax.portlet.faces." + getPortletName() +
				".defaultViewIdMap");

		if (!mDefaultViewIdMap.isEmpty()) {

			if (viewIdMapAttr == null) {
				msg.append("  Portlet context attribute javax.portlet.faces.").append(getPortletName()).append(
					".defaultViewIdMap has not been set.");
			}
			else {

				for (String mode : mDefaultViewIdMap.keySet()) {

					if (viewIdMapAttr.get(mode) == null) {
						bTestPassed = false;
						msg.append("  Mode, ").append(mode).append(
							", has been set in the map returned by getDefaultViewIdMap but has not been set in javax.portlet.faces.")
							.append(getPortletName()).append(".defaultViewIdMap");
					}
				}
			}
		}
		else {

			if (viewIdMapAttr != null) {
				msg.append("  Portlet context attribute javax.portlet.faces.").append(getPortletName()).append(
					".defaultViewIdMap has been set while the getDefaultViewIdMap returns null.");
				bTestPassed = false;
			}
		}

		// Check that the BridgeEventHandler portlet context attribute is set
		// to the same value as mEventHandler.
		if (bGetBridgeEventHandlerCalled) {
			msg.append("  getBridgeEventHandler() called.");
		}
		else {
			bTestPassed = false;
			msg.append("  getBridgeEventHandler() not called.");
		}

		BridgeEventHandler eventHandler = (BridgeEventHandler) getPortletContext().getAttribute("javax.portlet.faces." +
				getPortletName() + ".bridgeEventHandler");

		if (((eventHandler == null) && (mEventHandler != null)) ||
				((mEventHandler == null) && (eventHandler != null)) || (!mEventHandler.equals(eventHandler))) {
			msg.append("  Value of the bridgeEventHandler context attribute is ").append(eventHandler).append(
				", expected value is ").append(mEventHandler).append(".");
			bTestPassed = false;
		}

		// Check that the BridgePublicRenderParameterHandler portlet context attribute is set
		// to the same value as mPRPHandler.
		if (bGetBridgePRPHandlerCalled) {
			msg.append("  getBridgePublicRenderParameterHandler() called.");
		}
		else {
			bTestPassed = false;
			msg.append("  getBridgePublicRenderParameterHandler() not called.");
		}

		BridgePublicRenderParameterHandler prpHandler = (BridgePublicRenderParameterHandler) getPortletContext()
			.getAttribute("javax.portlet.faces." + getPortletName() + ".bridgePublicRenderParameterHandler");

		if (((prpHandler == null) && (mPRPhandler != null)) || ((mPRPhandler == null) && (prpHandler != null)) ||
				(!mPRPhandler.equals(prpHandler))) {
			msg.append("  Value of the bridgePublicRenderParameterHandler context attribute is ").append(prpHandler)
				.append(", expected value is ").append(mPRPhandler).append(".");
			bTestPassed = false;
		}

		// Check that the defaultRenderKitId portlet context attribute is set
		// to the same value as mDefaultRenderKitId.
		if (bGetDefaultRenderKitIdCalled) {
			msg.append("  getDefaultRenderKitId() called.");
		}
		else {
			bTestPassed = false;
			msg.append("  getDefaultRenderKitId() not called.");
		}

		String defaultRenderKitId = (String) getPortletContext().getAttribute("javax.portlet.faces." +
				getPortletName() + ".defaultRenderKitId");

		if (((defaultRenderKitId == null) && (mDefaultRenderKitId != null)) ||
				((mDefaultRenderKitId == null) && (defaultRenderKitId != null)) ||
				(!mDefaultRenderKitId.equals(defaultRenderKitId))) {
			msg.append("  Value of the defaultRenderKitId context attribute is ").append(defaultRenderKitId).append(
				", expected value is ").append(mDefaultRenderKitId).append(".");
			bTestPassed = false;
		}

		if (bTestPassed) {
			getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(), msg.toString());
		}
		else {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(), msg.toString());
		}
	}

	public boolean isPreserveActionParameters() {
		bIsPreserveActionParametersCalled = true;
		bIsPreserveActionParametersResult = super.isPreserveActionParameters();

		return bIsPreserveActionParametersResult;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if ((String) getPortletContext().getAttribute(TEST_PASS_PREFIX + getPortletName()) != null) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail((String) getPortletContext().getAttribute(TEST_PASS_PREFIX + getPortletName()));
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail((String) getPortletContext().getAttribute(TEST_FAIL_PREFIX + getPortletName()));
		}

		out.println(resultWriter.toString());
	}
}
