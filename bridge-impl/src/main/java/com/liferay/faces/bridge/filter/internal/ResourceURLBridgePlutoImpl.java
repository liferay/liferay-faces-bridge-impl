/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.filter.internal;

import java.util.Map;

import javax.portlet.ResourceURL;


/**
 * @author  Neil Griffin
 */
public class ResourceURLBridgePlutoImpl extends ResourceURLWrapper {

	// Private Constants
	private static final String PLUTO_CACHE_LEVEL_PAGE_TOKEN = "/__clcacheLevelPage";

	// Protected Data Members
	protected String toStringValue;

	// Private Data Members
	private ResourceURL wrappedResourceURL;

	public ResourceURLBridgePlutoImpl(ResourceURL resourceURL) {
		this.wrappedResourceURL = resourceURL;
	}

	@Override
	public ResourceURL getWrapped() {
		return wrappedResourceURL;
	}

	@Override
	public void setParameter(String name, String[] values) {
		super.setParameter(name, values);
	}

	@Override
	public void setParameter(String name, String value) {

		if (value == null) {
			PlutoBaseURLUtil.removeParameter(wrappedResourceURL, name);
		}
		else {
			super.setParameter(name, value);
		}
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		super.setParameters(parameters);
	}

	/**
	 * Pluto has the habit of adding cache tokens to URLs during the RESOURCE_PHASE of the portlet lifecycle that are
	 * not present during the RENDER_PHASE. Although it would be nice to take advantage of cache-ability of resources,
	 * these differing URLs cause full-portlet DOM-diffs to take place when using ICEfaces. This method ensures that the
	 * cache tokens are removed, so that the return value of this method is the same during the RENDER_PHASE and
	 * RESOURCE_PHASE.
	 */
	@Override
	public String toString() {

		if (toStringValue == null) {

			// Invoke Pluto's toString() method.
			toStringValue = getWrapped().toString();

			// If cache tokens are found in the URL, then remove them.
			if (toStringValue != null) {
				int pos = toStringValue.indexOf(PLUTO_CACHE_LEVEL_PAGE_TOKEN);

				if (pos > 0) {
					toStringValue = toStringValue.substring(0, pos) +
						toStringValue.substring(pos + PLUTO_CACHE_LEVEL_PAGE_TOKEN.length());
				}
			}
		}

		// Return the normalized URL.
		return toStringValue;
	}

}
