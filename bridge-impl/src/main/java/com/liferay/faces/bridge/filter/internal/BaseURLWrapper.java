/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;


/**
 * @author  Neil Griffin
 */
public abstract class BaseURLWrapper implements BaseURL {

	public void addProperty(String key, String value) {
		getWrapped().addProperty(key, value);
	}

	@Override
	public String toString() {
		return getWrapped().toString();
	}

	public void write(Writer out) throws IOException {
		getWrapped().write(out);
	}

	public void write(Writer out, boolean escapeXML) throws IOException {
		getWrapped().write(out, escapeXML);
	}

	public void setParameter(String name, String value) {
		getWrapped().setParameter(name, value);
	}

	public void setParameter(String name, String[] values) {
		getWrapped().setParameter(name, values);
	}

	public Map<String, String[]> getParameterMap() {
		return getWrapped().getParameterMap();
	}

	public void setParameters(Map<String, String[]> parameters) {
		getWrapped().setParameters(parameters);
	}

	public void setProperty(String key, String value) {
		getWrapped().setProperty(key, value);
	}

	public void setSecure(boolean secure) throws PortletSecurityException {
		getWrapped().setSecure(secure);
	}

	public abstract BaseURL getWrapped();
}
