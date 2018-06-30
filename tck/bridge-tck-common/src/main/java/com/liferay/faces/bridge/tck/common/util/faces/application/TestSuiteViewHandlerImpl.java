/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * View handler implementation for JSF portlet bridge. The only method we override here is getActionURL(). TODO JSF 1.2
 * note: JSF 1.2 RI implements ViewHandler.renderView() differently in order to handle emitting non-JSF markup that
 * follows the JSF tags after the JSF renders correctly. Unfortunately, the RI handles this by introducing several
 * servlet dependencies. Currently, the bridge handles this by overriding the renderView() and ignoring (not
 * interleafing) the non-JSF markup - see HACK below
 */
public class TestSuiteViewHandlerImpl extends ViewHandlerWrapper {

	// the ViewHandler to delegate to
	private ViewHandler mDelegate;

	public TestSuiteViewHandlerImpl(ViewHandler handler) {
		mDelegate = handler;
	}

	@Override
	public UIViewRoot createView(FacesContext facesContext, String viewId) throws IllegalArgumentException,
		NullPointerException {
		facesContext.getExternalContext().getRequestMap().put("com.liferay.faces.bridge.tck.viewCreated", Boolean.TRUE);

		return super.createView(facesContext, viewId);
	}

	public String getActionURL(FacesContext facesContext, String viewId) {

		// Call super to get the actionURL
		String resultURL = super.getActionURL(facesContext, viewId);

		// Then test to see if we are in a render and this is an encodeActionURL test that
		// tests the render encoding -- if so add the appropriate parameters to test.
		if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(context))) {
			String testName = (String) facesContext.getExternalContext().getRequestMap().get(Constants.TEST_NAME);

			if (testName == null)
				return resultURL;

			if (testName.equals("encodeActionURLWithParamRenderTest")) {
				return appendQueryString(resultURL, "param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithModeRenderTest") ||
					testName.equals("encodeResourceURLWithModeTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.PortletMode=edit&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidModeRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.PortletMode=blue&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithWindowStateRenderTest") ||
					testName.equals("encodeResourceURLWithWindowStateTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.WindowState=maximized&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidWindowStateRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.WindowState=blue&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithSecurityRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.Secure=true&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidSecurityRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.Secure=blue&param1=testValue");
			}
		}

		return resultURL;
	}

	public ViewHandler getWrapped() {
		return mDelegate;
	}

	@Override
	public void renderView(FacesContext facesContext, UIViewRoot viewToRender) throws IOException, FacesException {

		String testName = (String) facesContext.getExternalContext().getRequestMap().get(Constants.TEST_NAME);

		// Do nothing when not running in portlet request
		if (BridgeUtil.isPortletRequest(facesContext) && (testName != null)) {
			super.renderView(facesContext, viewToRender);
		}
	}

	@Override
	public UIViewRoot restoreView(FacesContext facesContext, String viewId) throws FacesException,
		NullPointerException {
		facesContext.getExternalContext().getRequestMap().put("com.liferay.faces.bridge.tck.viewCreated",
			Boolean.FALSE);

		return super.restoreView(facesContext, viewId);
	}

	private String appendQueryString(String url, String params) {

		if (url.indexOf('?') < 0) {
			return url + "?" + params;
		}
		else {
			return url + "&" + params;
		}
	}

}
