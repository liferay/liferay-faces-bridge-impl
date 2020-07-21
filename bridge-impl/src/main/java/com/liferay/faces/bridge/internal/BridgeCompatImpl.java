/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;


/**
 * This class provides a compatibility layer that isolates differences related to Portlet 2.0/3.0 and helps to minimize
 * diffs across branches.
 *
 * @author  Kyle Stiemann
 */
public abstract class BridgeCompatImpl implements Bridge {

	protected abstract void checkNull(PortletRequest portletRequest, PortletResponse portletResponse);

	protected abstract PortletConfig getPortletConfig();

	protected abstract boolean isInitialized();
}
