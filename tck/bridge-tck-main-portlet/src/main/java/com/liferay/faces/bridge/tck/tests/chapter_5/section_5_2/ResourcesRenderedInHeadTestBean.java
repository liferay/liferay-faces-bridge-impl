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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIOutput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.ComponentSystemEventListener;
import jakarta.faces.event.PreRenderComponentEvent;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.filter.PortletResponseWrapper;


/**
 * This class is a JSF managed-bean that is used by resourcesRenderedInHeadTest.xhtml in order to display test results.
 *
 * @author  Kyle Stiemann
 */
@ManagedBean
@RequestScoped
public class ResourcesRenderedInHeadTestBean {

	// Private Data Members
	private Boolean addPropertyMarkupHeadElementCalled;
	private String testHeadElementsNotAddedViaAddDependency;

	public String getTestHeadElementIdsJSArray() {
		return ResourcesRenderedInHeadTestUtil.TEST_HEAD_ELEMENT_IDS_JS_ARRAY;
	}

	/**
	 * This method returns the elements that were EXPECTED to be added via {@link
	 * jakarta.portlet.HeaderResponse#addDependency(String, String, String)} or {@link
	 * jakarta.portlet.HeaderResponse#addDependency(String, String, String, String)} but were not. If this method returns
	 * a non-empty value then that would indicate a test condition failure.
	 */
	public String getTestHeadElementsNotAddedViaAddDependency() {

		if (testHeadElementsNotAddedViaAddDependency == null) {

			StringBuilder buf = new StringBuilder();
			DependencyTrackingHeaderResponse dependencyTrackingHeaderResponse = getDependencyTrackingHeaderResponse();
			Set<String> testHeadElementsAddedViaAddDependency =
				dependencyTrackingHeaderResponse.getTestHeadElementsAddedViaAddDependency();

			for (String[] testHeadElementId : ResourcesRenderedInHeadTestUtil.TEST_HEAD_ELEMENT_IDS) {

				if ((testHeadElementsAddedViaAddDependency == null) ||
						!testHeadElementsAddedViaAddDependency.contains(testHeadElementId[0])) {

					if (buf.length() > 0) {
						buf.append(", ");
					}

					buf.append(testHeadElementId[0]);
				}
			}

			testHeadElementsNotAddedViaAddDependency = buf.toString();
		}

		return testHeadElementsNotAddedViaAddDependency;
	}

	/**
	 * Determines whether or not the {@link jakarta.portlet.HeaderResponse#addProperty(String, org.w3c.dom.Element)}
	 * method was called in order to add a resource to the &lt;head&gt;...&lt;/head&gt; section of the page. Since the
	 * FacesBridge is not supposed to do this, returning a value of <code>true</code> would indicate a test condition
	 * failure.
	 */
	public boolean isAddPropertyMarkupHeadElementCalled() {

		if (addPropertyMarkupHeadElementCalled == null) {

			DependencyTrackingHeaderResponse dependencyTrackingHeaderResponse = getDependencyTrackingHeaderResponse();
			addPropertyMarkupHeadElementCalled =
				dependencyTrackingHeaderResponse.isAddPropertyMarkupHeadElementCalled();
		}

		return addPropertyMarkupHeadElementCalled;
	}

	/**
	 * This method causes each head resource to render an id attribute containing the resource name or client id if the
	 * resource name is unavailable. See {@link ResponseWriterResourceIdImpl} and the comments below for more details.
	 */
	public void preRenderHeadListener(ComponentSystemEvent componentSystemEvent) throws AbortProcessingException {

		// Obtain all head resources.
		UIComponent uiComponent = componentSystemEvent.getComponent();
		List<UIComponent> children = uiComponent.getChildren();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		List<UIComponent> componentResources = uiViewRoot.getComponentResources(facesContext, "head");
		List<UIComponent> headResources = new ArrayList<UIComponent>(children);
		headResources.addAll(componentResources);

		// For each script and stylesheet resource add a PreRenderComponentEvent listener that sets the outermost
		// ResponseWriter to an instance of ResponseWriterResourceIdImpl.
		for (UIComponent headResource : headResources) {

			String rendererType = headResource.getRendererType();

			if ((headResource instanceof UIOutput) &&
					(rendererType.equals("jakarta.faces.resource.Script") ||
						rendererType.equals("jakarta.faces.resource.Stylesheet"))) {
				headResource.subscribeToEvent(PreRenderComponentEvent.class, new ComponentSystemEventListener() {

						@Override
						public void processEvent(ComponentSystemEvent componentSystemEvent)
							throws AbortProcessingException {

							FacesContext facesContext = FacesContext.getCurrentInstance();
							ResponseWriter responseWriter = facesContext.getResponseWriter();

							if (!(responseWriter instanceof ResponseWriterResourceIdImpl)) {
								facesContext.setResponseWriter(new ResponseWriterResourceIdImpl(responseWriter));
							}
						}
					});
			}
		}
	}

	private DependencyTrackingHeaderResponse getDependencyTrackingHeaderResponse() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		return getDependencyTrackingHeaderResponse(portletResponse);
	}

	private DependencyTrackingHeaderResponse getDependencyTrackingHeaderResponse(PortletResponse portletResponse) {

		if (portletResponse instanceof DependencyTrackingHeaderResponse) {
			return (DependencyTrackingHeaderResponse) portletResponse;
		}
		else if (portletResponse instanceof PortletResponseWrapper) {
			PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;

			return getDependencyTrackingHeaderResponse(portletResponseWrapper.getResponse());
		}
		else {

			// Unexpected error
			return null;
		}
	}
}
