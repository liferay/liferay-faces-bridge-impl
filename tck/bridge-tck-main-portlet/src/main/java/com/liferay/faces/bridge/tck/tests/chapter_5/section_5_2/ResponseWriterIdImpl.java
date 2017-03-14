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

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;


/**
 * @author  Kyle Stiemann
 */
public class ResponseWriterIdImpl extends ResponseWriterWrapper {

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;

	public ResponseWriterIdImpl(ResponseWriter wrappedResponseWriter) {
		this.wrappedResponseWriter = wrappedResponseWriter;
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startElement(String name, UIComponent uiComponent) throws IOException {

		super.startElement(name, uiComponent);

		if ("script".equals(name) || "style".equals(name) || "link".equals(name)) {

			Map<String, Object> attributes = uiComponent.getAttributes();
			String resourceName = (String) attributes.get("name");

			if (resourceName != null) {
				resourceName = resourceName.replaceAll("[^A-Za-z0-9-_]", "_");
			}
			else {
				resourceName = uiComponent.getClientId();
			}

			if (resourceName != null) {
				writeAttribute("id", resourceName, null);
			}
		}
	}

}
