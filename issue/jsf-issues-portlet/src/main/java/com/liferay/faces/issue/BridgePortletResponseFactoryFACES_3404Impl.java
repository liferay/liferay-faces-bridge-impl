/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.issue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.BaseURL;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.filter.BridgePortletResponseFactory;
import javax.portlet.filter.HeaderResponseWrapper;
import javax.portlet.filter.RenderResponseWrapper;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * @author  Kyle Stiemann
 */
public final class BridgePortletResponseFactoryFACES_3404Impl extends BridgePortletResponseFactory {

	// Private Final Data Members
	private final BridgePortletResponseFactory wrappedBridgePortletResponseFactory;

	public BridgePortletResponseFactoryFACES_3404Impl(
		BridgePortletResponseFactory wrappedBridgePortletResponseFactory) {
		this.wrappedBridgePortletResponseFactory = wrappedBridgePortletResponseFactory;
	}

	private static <T extends BaseURL> T newURLProxy(T t, Class<T> proxyInterface,
		String parameterValueWithCharactersThatMustBeEncoded) {

		try {

			Method setDoAsUserIdMethod = t.getClass().getMethod("setDoAsUserId", long.class);
			setDoAsUserIdMethod.invoke(t, Long.MAX_VALUE);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		ClassLoader classLoader = InvocationHandlerURL_FACES_3404Impl.class.getClassLoader();
		Class[] implementedInterfaces = new Class[] { proxyInterface };
		InvocationHandlerURL_FACES_3404Impl invocationHandler = new InvocationHandlerURL_FACES_3404Impl(t,
				parameterValueWithCharactersThatMustBeEncoded);

		return (T) Proxy.newProxyInstance(classLoader, implementedInterfaces, invocationHandler);
	}

	@Override
	public ActionResponse getActionResponse(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getActionResponse(actionRequest, actionResponse, portletConfig, bridgeConfig);
	}

	@Override
	public EventResponse getEventResponse(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getEventResponse(eventRequest, eventResponse, portletConfig, bridgeConfig);
	}

	@Override
	public HeaderResponse getHeaderResponse(HeaderRequest renderRequest, HeaderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		String parameterValueWithCharactersThatMustBeEncoded = portletConfig.getInitParameter(
				"parameterValueWithCharactersThatMustBeEncoded");

		if (parameterValueWithCharactersThatMustBeEncoded != null) {
			renderResponse = new HeaderResponseFACES_3404Impl(renderResponse,
					parameterValueWithCharactersThatMustBeEncoded);
		}

		return getWrapped().getHeaderResponse(renderRequest, renderResponse, portletConfig, bridgeConfig);
	}

	@Override
	public RenderResponse getRenderResponse(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		String parameterValueWithCharactersThatMustBeEncoded = portletConfig.getInitParameter(
				"parameterValueWithCharactersThatMustBeEncoded");

		if (parameterValueWithCharactersThatMustBeEncoded != null) {
			renderResponse = new RenderResponseFACES_3404Impl(renderResponse,
					parameterValueWithCharactersThatMustBeEncoded);
		}

		return getWrapped().getRenderResponse(renderRequest, renderResponse, portletConfig, bridgeConfig);
	}

	@Override
	public ResourceResponse getResourceResponse(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		String parameterValueWithCharactersThatMustBeEncoded = portletConfig.getInitParameter(
				"parameterValueWithCharactersThatMustBeEncoded");

		if (parameterValueWithCharactersThatMustBeEncoded != null) {
			resourceResponse = new ResourceResponseFACES_3404Impl(resourceResponse,
					parameterValueWithCharactersThatMustBeEncoded);
		}

		return getWrapped().getResourceResponse(resourceRequest, resourceResponse, portletConfig, bridgeConfig);
	}

	@Override
	public BridgePortletResponseFactory getWrapped() {
		return wrappedBridgePortletResponseFactory;
	}

	private static final class InvocationHandlerURL_FACES_3404Impl<T extends BaseURL> implements InvocationHandler {

		// Private Final Data Members
		private final T wrappedURL;
		private final String parameterValueWithCharactersThatMustBeEncoded;

		public InvocationHandlerURL_FACES_3404Impl(T wrappedURL, String parameterValueWithCharactersThatMustBeEncoded) {
			this.wrappedURL = wrappedURL;
			this.parameterValueWithCharactersThatMustBeEncoded = parameterValueWithCharactersThatMustBeEncoded;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if ("toString".equals(method.getName()) && (args == null)) {

				String urlString = (String) method.invoke(wrappedURL, args);
				String encodedParameterValueWithCharactersThatMustBeEncoded = URLEncoder.encode(
						parameterValueWithCharactersThatMustBeEncoded, "UTF-8");

				return urlString.replace(Long.toString(Long.MAX_VALUE),
						encodedParameterValueWithCharactersThatMustBeEncoded);
			}
			else {
				return method.invoke(wrappedURL, args);
			}
		}
	}

	private static final class HeaderResponseFACES_3404Impl extends HeaderResponseWrapper {

		// Private Constants
		private final String parameterValueWithCharactersThatMustBeEncoded;

		public HeaderResponseFACES_3404Impl(HeaderResponse wrappedHeaderResponse,
			String parameterValueWithCharactersThatMustBeEncoded) {

			super(wrappedHeaderResponse);
			this.parameterValueWithCharactersThatMustBeEncoded = parameterValueWithCharactersThatMustBeEncoded;
		}

		@Override
		public PortletURL createActionURL() throws IllegalStateException {

			PortletURL actionURL = super.createActionURL();

			return newURLProxy(actionURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public PortletURL createRenderURL() throws IllegalStateException {

			PortletURL renderURL = super.createRenderURL();

			return newURLProxy(renderURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public ResourceURL createResourceURL() throws IllegalStateException {

			ResourceURL resourceURL = super.createResourceURL();

			return newURLProxy(resourceURL, ResourceURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}
	}

	private static final class RenderResponseFACES_3404Impl extends RenderResponseWrapper {

		// Private Constants
		private final String parameterValueWithCharactersThatMustBeEncoded;

		public RenderResponseFACES_3404Impl(RenderResponse wrappedRenderResponse,
			String parameterValueWithCharactersThatMustBeEncoded) {

			super(wrappedRenderResponse);
			this.parameterValueWithCharactersThatMustBeEncoded = parameterValueWithCharactersThatMustBeEncoded;
		}

		@Override
		public PortletURL createActionURL() throws IllegalStateException {

			PortletURL actionURL = super.createActionURL();

			return newURLProxy(actionURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public PortletURL createRenderURL() throws IllegalStateException {

			PortletURL renderURL = super.createRenderURL();

			return newURLProxy(renderURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public ResourceURL createResourceURL() throws IllegalStateException {

			ResourceURL resourceURL = super.createResourceURL();

			return newURLProxy(resourceURL, ResourceURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}
	}

	private static final class ResourceResponseFACES_3404Impl extends ResourceResponseWrapper {

		// Private Constants
		private final String parameterValueWithCharactersThatMustBeEncoded;

		public ResourceResponseFACES_3404Impl(ResourceResponse wrappedResourceResponse,
			String parameterValueWithCharactersThatMustBeEncoded) {

			super(wrappedResourceResponse);
			this.parameterValueWithCharactersThatMustBeEncoded = parameterValueWithCharactersThatMustBeEncoded;
		}

		@Override
		public PortletURL createActionURL() throws IllegalStateException {

			PortletURL actionURL = super.createActionURL();

			return newURLProxy(actionURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public PortletURL createRenderURL() throws IllegalStateException {

			PortletURL renderURL = super.createRenderURL();

			return newURLProxy(renderURL, PortletURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}

		@Override
		public ResourceURL createResourceURL() throws IllegalStateException {

			ResourceURL resourceURL = super.createResourceURL();

			return newURLProxy(resourceURL, ResourceURL.class, parameterValueWithCharactersThatMustBeEncoded);
		}
	}
}
