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
package com.liferay.faces.bridge.servlet;

import java.lang.reflect.Method;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.annotation.BridgeRequestScopeAttributeAdded;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * <p>This class is designed to support the {@link BridgeRequestScopeAttributeAdded} annotation. It has to be specified
 * as a listener in the WEB-INF/web.xml descriptor like this:</p>
 * <code>&lt;listener&gt;
 * &lt;listener-class&gt;com.liferay.faces.bridge.context.map.BridgeRequestAttributeListener&lt;/listener-class&gt;
 * &lt;/listener&gt;</code>
 *
 * @see     <a href="http://issues.liferay.com/browse/FACES-146">FACES-146</a>
 * @author  Neil Griffin
 */
public class BridgeRequestAttributeListener implements ServletRequestAttributeListener {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestAttributeListener.class);

	/**
	 * This method is called after an attribute is added to the ServletRequest. Note that this should only get called
	 * for remote WSRP portlets. For more info, see: http://issues.liferay.com/browse/FACES-146
	 */
	@Override
	public void attributeAdded(ServletRequestAttributeEvent servletRequestAttributeEvent) {

		// NOTE: We only care about phases prior to the RENDER_PHASE because we're concerned here about managed beans
		// that get added to the request scope when the BridgeRequestScope begins. We're trying to provide those managed
		// beans with an opportunity to prepare for an unexpected invocation of their methods annotated with
		// @PreDestroy.
		ServletRequest servletRequest = servletRequestAttributeEvent.getServletRequest();
		PortletPhase phase = (PortletPhase) servletRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);

		// If this is taking place within a PortletRequest handled by the bridge in any phase prior to the
		// RENDER_PHASE, then
		if ((phase != null) && (phase != PortletPhase.RENDER_PHASE)) {

			// If the attribute being added is not excluded, then invoke all methods on the attribute value (class
			// instance) that are annotated with the BridgeRequestScopeAttributeAdded annotation.
			String attributeName = servletRequestAttributeEvent.getName();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			BridgeConfig bridgeConfig = RequestMapUtil.getBridgeConfig(facesContext);
			Set<String> excludedRequestScopeAttributes = bridgeConfig.getExcludedRequestAttributes();

			if (!excludedRequestScopeAttributes.contains(attributeName)) {

				Object attributeValue = servletRequestAttributeEvent.getValue();
				logger.trace("Attribute added name=[{0}] value=[{1}]", attributeName, attributeValue);

				if (attributeValue != null) {
					Method[] methods = attributeValue.getClass().getMethods();

					if (methods != null) {

						for (Method method : methods) {

							if (method != null) {

								if (method.isAnnotationPresent(BridgeRequestScopeAttributeAdded.class)) {

									try {
										method.invoke(attributeValue);
									}
									catch (Exception e) {
										logger.error(e);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method is called after an attribute is removed from the ServletRequest. One might expect that this is a good
	 * time to call any managed bean methods annotated with @BridgePreDestroy, but that actually takes place in the
	 * Bridge's {@link com.liferay.faces.bridge.context.map.internal.RequestScopeMap#remove(Object)} method. Note that
	 * this should only get called for remote WSRP portlets. For more info, see:
	 * http://issues.liferay.com/browse/FACES-146
	 */
	@Override
	public void attributeRemoved(ServletRequestAttributeEvent servletRequestAttributeEvent) {
		String attributeName = servletRequestAttributeEvent.getName();
		Object attributeValue = servletRequestAttributeEvent.getValue();
		logger.trace("Attribute removed name=[{0}] value=[{1}]", attributeName, attributeValue);
	}

	/**
	 * This method is called after an attribute is replaced in the ServletRequest. One might expect that this is a good
	 * time to call any managed bean methods annotated with @BridgePreDestroy, but that actually takes place in the
	 * Bridge's {@link com.liferay.faces.bridge.context.map.internal.RequestScopeMap#remove(Object)} method. Note that
	 * this should only get called for remote WSRP portlets. For more info, see:
	 * http://issues.liferay.com/browse/FACES-146
	 */
	@Override
	public void attributeReplaced(ServletRequestAttributeEvent servletRequestAttributeEvent) {
		String attributeName = servletRequestAttributeEvent.getName();
		Object attributeValue = servletRequestAttributeEvent.getValue();
		logger.trace("Attribute replaced name=[{0}] value=[{1}]", attributeName, attributeValue);
	}

}
