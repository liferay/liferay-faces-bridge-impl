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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;


/**
 * @author  Neil Griffin
 */
public class ResponseWriterPrimeFacesBodyImpl extends ResponseWriterWrapper {

	// Private Data Members
	private String nonAjaxPartialActionURL;
	private ResponseWriter wrappedResponseWriter;
	private boolean writingForm;

	public ResponseWriterPrimeFacesBodyImpl(ResponseWriter responseWriter, String nonAjaxPartialActionURL) {
		this.wrappedResponseWriter = responseWriter;
		this.nonAjaxPartialActionURL = nonAjaxPartialActionURL;
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {
		super.startElement(name, component);

		if ("form".equals(name)) {
			writingForm = true;
		}
		else {
			writingForm = false;
		}
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		if (writingForm && "action".equals(name)) {
			value = nonAjaxPartialActionURL;
		}

		super.writeAttribute(name, value, property);
	}
}
