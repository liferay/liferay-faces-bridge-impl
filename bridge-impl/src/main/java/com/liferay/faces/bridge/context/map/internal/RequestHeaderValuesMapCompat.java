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
package com.liferay.faces.bridge.context.map.internal;

import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;


/**
 * @author  Neil Griffin
 */
public abstract class RequestHeaderValuesMapCompat extends CaseInsensitiveHashMap<String[]> {

	// serialVersionUID
	private static final long serialVersionUID = 5256297252491398013L;

	// Private Constants
	private static final String HEADER_TRINIDAD_PPR = "Tr-XHR-Message";

	protected void addJSF1Headers(PortletRequest portletRequest) {

		String[] trinidadPPRHeader = get(HEADER_TRINIDAD_PPR);

		if (trinidadPPRHeader == null) {

			String pprHeader = portletRequest.getProperty(HEADER_TRINIDAD_PPR);

			if (pprHeader != null) {
				put(HEADER_TRINIDAD_PPR, new String[] { pprHeader });
			}
		}
	}

	protected boolean shouldAddContentHeaders(PortletRequest portletRequest) {

		Bridge.PortletPhase portletPhase = (Bridge.PortletPhase) portletRequest.getAttribute(
				Bridge.PORTLET_LIFECYCLE_PHASE);

		return (portletPhase != Bridge.PortletPhase.RENDER_PHASE) && (portletPhase != Bridge.PortletPhase.EVENT_PHASE);
	}
}
