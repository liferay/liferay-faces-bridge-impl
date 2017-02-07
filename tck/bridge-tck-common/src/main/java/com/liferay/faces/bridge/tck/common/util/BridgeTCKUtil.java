/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util;

import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;


/**
 * @author  Neil Griffin
 */
public class BridgeTCKUtil {

	public static boolean isHeaderOrRenderPhase(Bridge.PortletPhase portletPhase) {
		return Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase);
	}

	public static boolean isHeaderOrRenderPhase(FacesContext facesContext) {

		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase();

		return Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase);
	}
}
