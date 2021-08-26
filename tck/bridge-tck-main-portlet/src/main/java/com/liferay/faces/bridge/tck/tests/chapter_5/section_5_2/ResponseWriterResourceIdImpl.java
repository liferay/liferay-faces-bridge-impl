/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;


/**
 * This {@link ResponseWriter} writes the id attribute of script and stylesheet resources as the resource name or the
 * client id if the resource name is unavailable. See src/main/webapp/resources/test/resourcesRenderedInHeadTest.js for
 * details on how this id is used. This {@link ResponseWriter} is set in {@link
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

				// In case this isn't the outermost ResponseWriter, get the outermost ResponseWriter and call
				// writeAttribute(). This gives other ResponseWriter implmentations a chance to react to the fact that
				// an id attribute has been rendered. For example, FACES-3175 requires that an id be rendered on each
				// resource, but the ResponseWriterHeadResourceLiferayImpl in Bridge Ext detects if the id has already
				// been written and skips writing it a second time if it has already been written. Since
				// ResponseWriterHeadResourceLiferayImpl is the outermost ResponseWriter, it must be alerted to the fact
				// that the id is written here. Other implementations may be similar.
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ResponseWriter responseWriter = facesContext.getResponseWriter();
				responseWriter.writeAttribute("id", resourceName, null);
			}
		}
	}

}
