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
package com.liferay.faces.bridge.util.internal;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.faces.Bridge;


/**
 * @author  Neil Griffin
 */
public class ViewUtil {

	/**
	 * <p>Returns an immutable {@link Map} whose keys are determined by {@link PortletMode#toString()} and whose values
	 * are retrieved from the following sections of the WEB-INF/portlet.xml descriptor.</p>
	 * <code>
	 * <pre>
	 &lt;init-param&gt;
	 &lt;name&gt;javax.portlet.faces.defaultViewId.view&lt;/name&gt;
	 &lt;value&gt;/xhtml/portletViewMode.xhtml&lt;/value&gt;
	 &lt;/init-param&gt;
	 &lt;init-param&gt;
	 &lt;name&gt;javax.portlet.faces.defaultViewId.edit&lt;/name&gt;
	 &lt;value&gt;/xhtml/portletEditMode.xhtml&lt;/value&gt;
	 &lt;/init-param&gt;
	 &lt;init-param&gt;
	 &lt;name&gt;javax.portlet.faces.defaultViewId.help&lt;/name&gt;
	 &lt;value&gt;/xhtml/portletHelpMode.xhtml&lt;/value&gt;
	 &lt;/init-param&gt;
	 * </pre>
	 * </code>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getDefaultViewIdMap(PortletConfig portletConfig) {

		String portletName = portletConfig.getPortletName();
		String attrNameDefaultViewIdMap = Bridge.BRIDGE_PACKAGE_PREFIX + portletName + "." + Bridge.DEFAULT_VIEWID_MAP;
		PortletContext portletContext = portletConfig.getPortletContext();

		return (Map<String, String>) portletContext.getAttribute(attrNameDefaultViewIdMap);
	}
}
