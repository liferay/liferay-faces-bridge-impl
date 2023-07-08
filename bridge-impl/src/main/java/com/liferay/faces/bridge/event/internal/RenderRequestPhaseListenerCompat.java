/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.event.internal;

import javax.portlet.PortletConfig;


/**
 * @author  Neil Griffin
 */
public class RenderRequestPhaseListenerCompat {

	protected boolean isViewParametersEnabled(PortletConfig portletConfig) {

		// The "View Parameters" feature was introduced in JSF 2.0, so return false for JSF 1.2.
		return false;
	}
}
