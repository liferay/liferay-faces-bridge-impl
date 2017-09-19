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
package com.liferay.faces.bridge.context;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * @author  Kyle Stiemann
 */
public class PortalContextMockImpl implements PortalContext {

	// Private Data Members
	private boolean markupHeadElementSupport;
	private Enumeration<String> propertyNames;

	public PortalContextMockImpl(boolean markupHeadElementSupport) {

		this.markupHeadElementSupport = markupHeadElementSupport;

		if (markupHeadElementSupport) {

			Set<String> propertyNames = new HashSet<String>();
			propertyNames.add(MARKUP_HEAD_ELEMENT_SUPPORT);
			this.propertyNames = Collections.enumeration(propertyNames);
		}
		else {
			this.propertyNames = Collections.emptyEnumeration();
		}
	}

	@Override
	public String getPortalInfo() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getProperty(String name) {

		String propertyValue = null;

		if (markupHeadElementSupport && MARKUP_HEAD_ELEMENT_SUPPORT.equals(name)) {
			propertyValue = "true";
		}

		return propertyValue;
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return propertyNames;
	}

	@Override
	public Enumeration<PortletMode> getSupportedPortletModes() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<WindowState> getSupportedWindowStates() {
		throw new UnsupportedOperationException("");
	}
}
