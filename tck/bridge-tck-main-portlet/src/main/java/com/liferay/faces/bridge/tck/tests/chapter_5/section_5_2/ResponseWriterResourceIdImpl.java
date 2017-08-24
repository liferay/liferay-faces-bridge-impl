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
 * This {@link ResponseWriter} writes the id attribute of script and stylesheet resources as the resource name or the
 * client id if the resource name is unavailable.
 * See src/main/webapp/resources/test/resourcesRenderedInHeadTest.js for details on how this id is
 * used. This {@link ResponseWriter} is set in {@link
 * ResourcesRenderedInHeadTestBean#preRenderHeadListener(javax.faces.event.ComponentSystemEvent)}.
 *
 * @author  Kyle Stiemann
 */
public class ResponseWriterResourceIdImpl extends ResponseWriterWrapper {

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;

	public ResponseWriterResourceIdImpl(ResponseWriter wrappedResponseWriter) {
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

			// By default, scripts and stylesheets are rendered without an id attribute. For the purposes of the
			// resourcesRenderedInHeadTest, render the id as the resource name (without illegal characters) or the
			// client id if the resource name is unavailable. See DependencyTrackingHeaderResponse.addDependency()
			// for more details.
			Map<String, Object> attributes = uiComponent.getAttributes();
			String resourceName = (String) attributes.get("name");

			if (resourceName != null) {

				// Replace illegal (JSF/HTML) id characters with underscores.
				resourceName = ResourcesRenderedInHeadTestUtil.escapeId(resourceName);
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
