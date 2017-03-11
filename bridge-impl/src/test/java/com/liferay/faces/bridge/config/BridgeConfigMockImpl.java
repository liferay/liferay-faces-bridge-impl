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
package com.liferay.faces.bridge.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.faces.BridgeConfig;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.config.ConfiguredSystemEventListener;


/**
 * @author  Neil Griffin
 */
public class BridgeConfigMockImpl implements BridgeConfig {

	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getAttributes() {
		throw new UnsupportedOperationException();
	}

	public List<ConfiguredServletMapping> getConfiguredFacesServletMappings() {
		throw new UnsupportedOperationException();
	}

	public List<String> getConfiguredSuffixes() {
		throw new UnsupportedOperationException();
	}

	public List<ConfiguredSystemEventListener> getConfiguredSystemEventListeners() {
		throw new UnsupportedOperationException();
	}

	public String getContextParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getExcludedRequestAttributes() {
		throw new UnsupportedOperationException();
	}

	public Set<String> getIncludedRequestAttributes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getPublicParameterMappings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getViewIdRenderParameterName() {
		return "";
	}

	@Override
	public String getViewIdResourceParameterName() {
		return "";
	}

}
