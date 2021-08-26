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
package com.liferay.faces.bridge.application.internal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.util.internal.TCCLUtil;
import com.liferay.faces.util.application.ResourceValidator;
import com.liferay.faces.util.application.ResourceValidatorWrapper;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.config.ConfiguredServlet;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.config.WebConfig;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class ResourceValidatorBridgeImpl extends ResourceValidatorWrapper implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 5058022096981863159L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceValidatorBridgeImpl.class);

	// Private Data Members
	private ResourceValidator wrappedResourceValidator;

	public ResourceValidatorBridgeImpl(ResourceValidator resourceValidator) {
		this.wrappedResourceValidator = resourceValidator;
	}

	@Override
	public ResourceValidator getWrapped() {
		return wrappedResourceValidator;
	}

	@Override
	public boolean isSelfReferencing(FacesContext facesContext, String resourceId) {

		ExternalContext externalContext = facesContext.getExternalContext();
		final Product PLUTO = ProductFactory.getProductInstance(externalContext, Product.Name.PLUTO);

		// If the delegation chain indicates that the specified resource is not self-referencing, then
		boolean selfReferencing = super.isSelfReferencing(facesContext, resourceId);

		if (PLUTO.isDetected() && (!selfReferencing) && (resourceId != null)) {

			// Process the configured servlet entries in order to determine which ones are portlet invokers.
			Set<String> invokerServletNames = new HashSet<String>();
			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			ApplicationConfig applicationConfig = (ApplicationConfig) applicationMap.get(ApplicationConfig.class
					.getName());
			WebConfig webConfig = applicationConfig.getWebConfig();
			List<ConfiguredServlet> configuredServlets = webConfig.getConfiguredServlets();

			for (ConfiguredServlet configuredServlet : configuredServlets) {

				String configuredServletClass = configuredServlet.getServletClass();

				if (isInvokerServletClass(configuredServletClass)) {
					invokerServletNames.add(configuredServlet.getServletName());
				}
			}

			// For each of the servlet-mapping entries:
			List<ConfiguredServletMapping> configuredServletMappings = webConfig.getConfiguredServletMappings();

			for (ConfiguredServletMapping configuredServletMapping : configuredServletMappings) {

				// Determine whether or not the current servlet-mapping is mapped to a portlet invoker servlet-class.
				if (invokerServletNames.contains(configuredServletMapping.getServletName())) {

					if (configuredServletMapping.isMatch(resourceId)) {
						selfReferencing = true;

						break;
					}
				}
			}
		}

		return selfReferencing;
	}

	private boolean isInvokerServletClass(String servletClassFQCN) {

		boolean invokerServletClass = false;
		String invokerServletFQCN = "org.apache.pluto.container.driver.PortletServlet";

		if (invokerServletFQCN.equals(servletClassFQCN)) {

			invokerServletClass = true;
		}
		else {

			try {
				ClassLoader classLoader = TCCLUtil.getThreadContextClassLoaderOrDefault(getClass());
				Class<?> invokerServletClazz = classLoader.loadClass(invokerServletFQCN);

				try {
					Class<?> servletClazz = classLoader.loadClass(servletClassFQCN);
					invokerServletClass = invokerServletClazz.isAssignableFrom(servletClazz);
				}
				catch (Throwable t) {
					logger.error("Unable to load servletClassFQCN=[{0}] error=[{1}]", servletClassFQCN, t.getMessage());
				}

			}
			catch (Throwable t) {
				logger.error("Unable to load invokerServletFQCN=[{0}] error=[{1}]", invokerServletFQCN, t.getMessage());
			}
		}

		return invokerServletClass;
	}
}
