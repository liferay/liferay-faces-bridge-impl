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
package com.liferay.faces.bridge.context.internal;

import java.util.Iterator;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.render.RenderKit;


/**
 * @author  Kyle Stiemann
 */
public abstract class FacesContextUnsupportedImpl extends FacesContext {

	@Override
	public void addMessage(String clientId, FacesMessage message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Application getApplication() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> getClientIdsWithMessages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExternalContext getExternalContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Lifecycle getLifecycle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public FacesMessage.Severity getMaximumSeverity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<FacesMessage> getMessages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<FacesMessage> getMessages(String clientId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderKit getRenderKit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getRenderResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getResponseComplete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseStream getResponseStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseWriter getResponseWriter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UIViewRoot getViewRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void release() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void responseComplete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResponseStream(ResponseStream responseStream) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResponseWriter(ResponseWriter responseWriter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setViewRoot(UIViewRoot root) {
		throw new UnsupportedOperationException();
	}
}
