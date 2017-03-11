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
package com.liferay.faces.bridge.context.internal;

import java.util.List;

import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class FacesViewImpl implements FacesView {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FacesViewImpl.class);

	// Private Data Members
	private String viewId;
	private String extension;
	private boolean extensionMapped;
	private String navigationQueryString;
	private String servletPath;
	private boolean pathMapped;

	public FacesViewImpl(String viewId, List<String> configuredExtensions,
		List<ConfiguredServletMapping> configuredFacesServletMappings) {
		this(viewId, null, configuredExtensions, configuredFacesServletMappings);
	}

	public FacesViewImpl(String viewId, String navigationQueryString, List<String> configuredSuffixes,
		List<ConfiguredServletMapping> configuredFacesServletMappings) {
		this.viewId = viewId;
		this.navigationQueryString = navigationQueryString;

		if (configuredFacesServletMappings != null) {

			// Determine whether or not the target viewId matches any of the path-mapped servlet-mapping entries.
			for (ConfiguredServletMapping facesServletMapping : configuredFacesServletMappings) {

				if (facesServletMapping.isPathMapped()) {

					logger.debug("Attempting to determine if viewId=[{0}] is path-mapped to urlPatttern=[{1}]", viewId,
						facesServletMapping.getUrlPattern());

					if (facesServletMapping.isMatch(viewId)) {
						this.servletPath = facesServletMapping.getServletPath();
						this.pathMapped = true;

						break;
					}
				}
			}

			// If not path-mapped, then
			if (!this.pathMapped) {

				// Find the first EXPLICIT extension-mapped servlet-mapping that might be found in the WEB-INF/web.xml
				// descriptor.
				ConfiguredServletMapping explicitFacesServletMapping = null;

				for (ConfiguredServletMapping facesServletMapping : configuredFacesServletMappings) {

					if (facesServletMapping.isExtensionMapped() && !facesServletMapping.isImplicit()) {
						explicitFacesServletMapping = facesServletMapping;

						break;
					}
				}

				// If an EXPLICIT extension-mapped servlet-mapping is found in the WEB-INF/web.xml descriptor, then
				if (explicitFacesServletMapping != null) {

					// Determine if the EXPLICIT extension-mapped servlet-mapping matches the extension of the viewId.
					if (explicitFacesServletMapping.isMatch(viewId)) {

						this.extension = explicitFacesServletMapping.getExtension();
						this.extensionMapped = true;
					}

					// Otherwise, determine if the viewId matches any of the IMPLICIT extension-mapped servlet-mappings.
					else {

						for (ConfiguredServletMapping facesServletMapping : configuredFacesServletMappings) {

							if (facesServletMapping.isExtensionMapped() && facesServletMapping.isImplicit() &&
									facesServletMapping.isMatch(viewId)) {

								this.extension = facesServletMapping.getExtension();
								this.extensionMapped = true;

								// As required by Section 6.1.3.1 of the Spec for
								// ExternalContext.getRequestServletMapping(), replace the extension of the viewId with
								// the extension found in the EXPLICIT servlet-mapping. For example, replace ".xhtml"
								// with ".jsf" or ".faces"
								int pos = viewId.lastIndexOf(".");

								if (pos > 0) {

									// TCK TestPage159: getRequestServletPathTest
									this.extension = configuredFacesServletMappings.get(0).getExtension();
									this.viewId = viewId.substring(0, pos) + this.extension;
								}

								break;
							}
						}
					}
				}

				// Otherwise, determine if the viewId matches any of the IMPLICIT extension-mapped servlet-mappings.
				else {

					for (ConfiguredServletMapping facesServletMapping : configuredFacesServletMappings) {

						if (facesServletMapping.isExtensionMapped() && facesServletMapping.isImplicit() &&
								facesServletMapping.isMatch(viewId)) {

							this.extension = facesServletMapping.getExtension();
							this.extensionMapped = true;

							break;
						}
					}
				}

				// If the target viewId did not match any of the extension-mapped servlet-mapping entries, then the
				// developer may have specified an extension like .jsp/.jspx in the WEB-INF/portlet.xml descriptor.
				if (!this.extensionMapped) {

					// For each of the valid extensions (.jsp, .jspx, etc.) that the developer may have specified:
					for (String configuredSuffix : configuredSuffixes) {

						if ((viewId != null) && viewId.contains(configuredSuffix)) {
							this.extension = configuredSuffix;
							this.extensionMapped = true;

							logger.debug("Associated viewId=[{0}] as extension-mapped to urlPattern=[*.{1}]", viewId,
								configuredSuffix);

							/* TODO: At one point in development, the line below was in place to replace ".jsp" with
							 * ".faces" but was potentially causing navigation-rules to fail because the to-view-id
							 * might not have been matching. Need to re-enable the line below and then retest with the
							 * TCK.
							 */
							// viewId = viewId.replace(extension, this.extension);

							break;
						}
					}
				}
			}
		}
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public String getQueryString() {
		return navigationQueryString;
	}

	@Override
	public String getServletPath() {
		return servletPath;
	}

	@Override
	public String getViewId() {
		return viewId;
	}

	@Override
	public boolean isExtensionMapped() {
		return extensionMapped;
	}

	@Override
	public boolean isPathMapped() {
		return pathMapped;
	}

	public boolean isServletMapped() {
		return (extensionMapped || pathMapped);
	}
}
