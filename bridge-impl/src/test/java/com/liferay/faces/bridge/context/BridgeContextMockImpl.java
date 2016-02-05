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
package com.liferay.faces.bridge.context;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge.PortletPhase;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.context.url.BridgeResourceURL;
import com.liferay.faces.bridge.context.url.BridgeURL;
import com.liferay.faces.bridge.scope.BridgeRequestScope;


/**
 * @author  Neil Griffin
 */
public class BridgeContextMockImpl extends BridgeContext {

	// Private Data Members
	private BridgeConfig bridgeConfig;
	private String facesViewId;
	private PortletRequest portletRequest;

	public BridgeContextMockImpl(BridgeConfig bridgeConfig, PortletRequest portletRequest, String facesViewId) {
		this.bridgeConfig = bridgeConfig;
		this.portletRequest = portletRequest;
		this.facesViewId = facesViewId;
		setCurrentInstance(this);
	}

	@Override
	public BridgeURL encodeActionURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BridgeURL encodeBookmarkableURL(String baseURL, Map<String, List<String>> parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BridgeURL encodePartialActionURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BridgeURL encodeRedirectURL(String baseUrl, Map<String, List<String>> parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BridgeResourceURL encodeResourceURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void redirect(String url) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void release() {
		this.bridgeConfig = null;
		this.portletRequest = null;
		this.facesViewId = null;
		setCurrentInstance(null);
	}

	@Override
	public Map<String, Object> getAttributes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BridgeConfig getBridgeConfig() {
		return bridgeConfig;
	}

	@Override
	public BridgeRequestScope getBridgeRequestScope() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDefaultRenderKitId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getDefaultViewIdMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacesViewId() {
		return facesViewId;
	}

	@Override
	public String getFacesViewIdFromPath(String viewPath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacesViewIdFromPath(String viewPath, boolean mustExist) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacesViewQueryString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRenderRedirectAfterDispatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IncongruityContext getIncongruityContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletConfig getPortletConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletContext getPortletContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletRequest getPortletRequest() {
		return portletRequest;
	}

	@Override
	public void setPortletRequest(PortletRequest portletRequest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletPhase getPortletRequestPhase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletResponse getPortletResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPortletResponse(PortletResponse portletResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getPreFacesRequestAttrNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getPreservedActionParams() {
		return null;
	}

	@Override
	public void setProcessingAfterViewContent(boolean processingAfterViewContent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderRedirectAfterDispatch(boolean renderRedirectAfterDispatch) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRenderRedirectViewId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderRedirectViewId(String renderRedirectViewId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestPathInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestServletPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Writer getResponseOutputWriter() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPreserveActionParams() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSavedViewState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSavedViewState(String savedViewState) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isProcessingAfterViewContent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRenderRedirect() {
		throw new UnsupportedOperationException();
	}
}
