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
package com.liferay.faces.bridge.tck.tests.chapter_3;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUninitializedException;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class BridgeDestroyTestPortlet extends BridgeDestroyTestCompatPortlet {

	static final String NULLREQUEST_ACTION_TEST = "nullRequestActionTest";

	private String mActionResult = null;
	private boolean mActionResultOutput = false;

	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		if (getTestName().equals(DESTROY_RENDER_TEST)) {
			runRenderDestroyTest(renderRequest, renderResponse);
		}
		else if (getTestName().equals(DESTROY_DOUBLE_TEST)) {
			runDoubleDestroyTest(renderRequest, renderResponse);
		}
		else if ((mActionResult != null) &&
				(getTestName().equals(DESTROY_ACTION_TEST) || getTestName().equals(DESTROY_EVENT_TEST) ||
					getTestName().equals(DESTROY_RESOURCE_TEST))) {
			outputActionResult(renderRequest, renderResponse);

			// This test can't be rerun until the portlet is reloaded
			// So set a new ActionResult that indicates this
			if (!mActionResultOutput) {
				encodeDestroyTestAlreadyRun(getTestName());
				mActionResultOutput = true;
			}
		}
		else if (getTestName().equals(NULLREQUEST_RENDER_TEST)) {
			runNullRequestRenderTest(renderRequest, renderResponse);
		}
		else if (getTestName().equals(NULLREQUEST_ACTION_TEST) && (mActionResult != null)) {
			outputActionResult(renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	public void init(PortletConfig config) throws PortletException {
		super.init(config);

	}

	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
		IOException {

		if (getTestName().equals(DESTROY_ACTION_TEST)) {
			runActionDestroyTest(actionRequest, actionResponse);
		}
		else if (getTestName().equals(NULLREQUEST_ACTION_TEST)) {
			runNullRequestActionTest(actionRequest, actionResponse);
		}
		else {
			super.processAction(actionRequest, actionResponse);
		}
	}

	public void processEvent(EventRequest request, EventResponse response) throws IOException, PortletException {

		if (getTestName().equals(DESTROY_EVENT_TEST)) {
			runEventDestroyTest(request, response);
		}
		else {
			super.processEvent(request, response);
		}
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {

		if (getTestName().equals(DESTROY_RESOURCE_TEST)) {
			runResourceDestroyTest(request, response);
			outputActionResult(request, response);

			// This test can't be rerun until the portlet is reloaded
			// So set a new ActionResult that indicates this
			if (!mActionResultOutput) {
				encodeDestroyTestAlreadyRun(getTestName());
				mActionResultOutput = true;
			}
		}
		else {
			super.serveResource(request, response);
		}
	}

	// Java 1.6+ @Override
	protected String getMActionResult() {
		return mActionResult;
	}

	private void encodeDestroyTestAlreadyRun(String testName) {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(testName);

		resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
		resultWriter.setDetail(
			"Test result has already been rendered. This can can only be rendered once prior to portlet or app reload.  To rerun this test reload.  The result that has previously been rendered is: " +
			mActionResult);

		mActionResult = resultWriter.toString();
	}

	private void outputActionResult(PortletRequest request, MimeResponse response) throws PortletException,
		IOException {
		response.setContentType("text/html");

		PrintWriter responsePrintWriter = response.getWriter();
		responsePrintWriter.println(mActionResult);
	}

	private void runActionDestroyTest(ActionRequest request, ActionResponse response) throws PortletException,
		IOException {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(DESTROY_ACTION_TEST);

		// Run test
		Bridge bridge = getFacesBridge(request, response);
		bridge.destroy();

		try {
			bridge.doFacesRequest(request, response);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(action) when passed a destroyed bridge. Instead the request completed without an exception.");
		}
		catch (BridgeUninitializedException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw BridgeUninitializedException from doFacesRequest(action) when passed a destroyed bridge.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(action) when passed a destroyed bridge. Instead it threw: " +
				e.toString());
		}

		mActionResult = resultWriter.toString();
	}

	private void runDoubleDestroyTest(RenderRequest request, RenderResponse response) throws PortletException,
		IOException {
		response.setContentType("text/html");

		PrintWriter responsePrintWriter = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(DESTROY_DOUBLE_TEST);

		// Run test
		Bridge bridge = getFacesBridge(request, response);
		bridge.destroy();

		try {
			bridge.destroy();
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail("Calling destroy on a destroyed bridge correctly completed without exception.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail("Calling destroy on a destroyed bridge incorrectly threw an exception: " +
				e.toString());
		}

		responsePrintWriter.println(resultWriter.toString());
	}

	private void runEventDestroyTest(EventRequest request, EventResponse response) throws PortletException,
		IOException {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(DESTROY_EVENT_TEST);

		// Run test
		Bridge bridge = getFacesBridge(request, response);
		bridge.destroy();

		try {
			bridge.doFacesRequest(request, response);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(event) when passed a destroyed bridge. Instead the request completed without an exception.");
		}
		catch (BridgeUninitializedException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw BridgeUninitializedException from doFacesRequest(event) when passed a destroyed bridge.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(event) when passed a destroyed bridge. Instead it threw: " +
				e.toString());
		}

		mActionResult = resultWriter.toString();
	}

	private void runNullRequestActionTest(ActionRequest request, ActionResponse response) throws PortletException,
		IOException {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(NULLREQUEST_ACTION_TEST);

		// Run test
		try {
			Bridge bridge = getFacesBridge(request, response);
			bridge.doFacesRequest((ActionRequest) null, (ActionResponse) null);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the NullPointerException from doFacesRequest(action) when passed a null request/response. Instead the request completed without an exception.");
		}
		catch (NullPointerException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw NullPointerException from doFacesRequest(action) when passed a null request/response.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the NullPointerException from doFacesRequest(action) when passed a null request/response. Instead it threw: " +
				e.toString());
		}

		mActionResult = resultWriter.toString();
	}

	private void runNullRequestRenderTest(RenderRequest request, RenderResponse response) throws PortletException,
		IOException {
		response.setContentType("text/html");

		PrintWriter responsePrintWriter = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(NULLREQUEST_RENDER_TEST);

		// Run test
		try {
			Bridge bridge = getFacesBridge(request, response);
			bridge.doFacesRequest((RenderRequest) null, (RenderResponse) null);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the NullPointerException from doFacesRequest(render) when passed a null request/response. Instead the request completed without an exception.");
		}
		catch (NullPointerException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw NullPointerException from doFacesRequest(render) when passed a null request/response.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the NullPointerException from doFacesRequest(render) when passed a null request/response. Instead it threw: " +
				e.toString());
		}

		responsePrintWriter.println(resultWriter.toString());
	}

	private void runRenderDestroyTest(RenderRequest request, RenderResponse response) throws PortletException,
		IOException {
		response.setContentType("text/html");

		PrintWriter responsePrintWriter = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(DESTROY_RENDER_TEST);

		// Run test
		Bridge bridge = getFacesBridge(request, response);
		bridge.destroy();

		try {
			bridge.doFacesRequest(request, response);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(render) when passed a destroyed bridge. Instead the request completed without an exception.");
		}
		catch (BridgeUninitializedException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw BridgeUninitializedException from doFacesRequest(render) when passed a destroyed bridge.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(render) when passed a destroyed bridge. Instead it threw: " +
				e.toString());
		}

		responsePrintWriter.println(resultWriter.toString());
	}

	private void runResourceDestroyTest(ResourceRequest request, ResourceResponse response) throws PortletException,
		IOException {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(DESTROY_RESOURCE_TEST);

		// Run test
		Bridge bridge = getFacesBridge(request, response);
		bridge.destroy();

		try {
			bridge.doFacesRequest(request, response);
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(resource) when passed a destroyed bridge. Instead the request completed without an exception.");
		}
		catch (BridgeUninitializedException bue) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(
				"Correctly threw BridgeUninitializedException from doFacesRequest(resource) when passed a destroyed bridge.");
		}
		catch (Exception e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(
				"Didn't throw the BridgeUninitializedException from doFacesRequest(resource) when passed a destroyed bridge. Instead it threw: " +
				e.toString());
		}

		mActionResult = resultWriter.toString();
	}
}
