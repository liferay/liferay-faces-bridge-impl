/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.context;

import java.util.Iterator;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.RenderKit;


/**
 * @author  Michael Freedman
 */
public class TCK_FacesContextImpl extends FacesContext {
	private FacesContext mWrapped;

	public TCK_FacesContextImpl(FacesContext facesContext) {
		mWrapped = facesContext;

		FacesContext.setCurrentInstance(this);
	}

	public void addMessage(String clientId, FacesMessage message) {
		mWrapped.addMessage(clientId, message);
	}

	public Application getApplication() {
		return mWrapped.getApplication();
	}

	public Iterator<String> getClientIdsWithMessages() {
		return mWrapped.getClientIdsWithMessages();
	}

	// Start of JSF 1.2 API

	public ELContext getELContext() {
		return mWrapped.getELContext();
	}

	public ExternalContext getExternalContext() {
		return mWrapped.getExternalContext();
	}

	public FacesMessage.Severity getMaximumSeverity() {
		return mWrapped.getMaximumSeverity();
	}

	public Iterator<FacesMessage> getMessages() {
		return mWrapped.getMessages();
	}

	public Iterator<FacesMessage> getMessages(String clientId) {
		return mWrapped.getMessages(clientId);
	}

	public RenderKit getRenderKit() {
		return mWrapped.getRenderKit();

	}

	public boolean getRenderResponse() {
		return mWrapped.getRenderResponse();
	}

	public boolean getResponseComplete() {
		return mWrapped.getResponseComplete();
	}

	public ResponseStream getResponseStream() {
		return mWrapped.getResponseStream();
	}

	public ResponseWriter getResponseWriter() {
		return mWrapped.getResponseWriter();
	}

	public UIViewRoot getViewRoot() {
		return mWrapped.getViewRoot();
	}

	public void release() {
		Map<Object, Object> attributes = getAttributes();

		if (!attributes.containsKey("invalidateSessionTest")) {
			getExternalContext().getSessionMap().put("org.apache.portlet.faces.tck.facesContextReleased", Boolean.TRUE);
		}

		mWrapped.release();
	}

	public void renderResponse() {
		mWrapped.renderResponse();
	}

	public void responseComplete() {
		mWrapped.responseComplete();
	}

	public void setResponseStream(ResponseStream responseStream) {
		mWrapped.setResponseStream(responseStream);
	}

	public void setResponseWriter(ResponseWriter responseWriter) {
		mWrapped.setResponseWriter(responseWriter);
	}

	public void setViewRoot(UIViewRoot uiViewRoot) {
		mWrapped.setViewRoot(uiViewRoot);
	}
}
