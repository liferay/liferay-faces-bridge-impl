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
package com.liferay.faces.bridge.internal;

import com.liferay.faces.bridge.context.BridgeContext;


/**
 * @author  Neil Griffin
 */
public interface BridgeExt {

	/**
	 * The name of the {@link PortletRequest} attribute that contains the {@link BridgeContext} instance. NOTE that
	 * changing the value of BRIDGE_CONTEXT_ATTRIBUTE will have a negative impact on the ICEfaces {@link
	 * FileEntryPhaseListener#setPortletRequestWrapper(Object)} method.
	 *
	 * @deprecated  The new technique for acquiring the BridgeContext instance is to call {@link
	 *              BridgeContext#getCurrentInstance()}.
	 */
	@Deprecated
	public static final String BRIDGE_CONTEXT_ATTRIBUTE = "javax.portlet.faces.bridgeContext";

	public static final String FACES_AJAX_PARAMETER = "_jsfBridgeAjax";
}
