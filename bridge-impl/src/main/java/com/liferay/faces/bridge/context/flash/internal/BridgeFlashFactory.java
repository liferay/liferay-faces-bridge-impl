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
package com.liferay.faces.bridge.context.flash.internal;

import javax.faces.FacesWrapper;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import com.liferay.faces.bridge.BridgeFactoryFinder;


/**
 * This class is was the inspiration for the JSF 2.2 javax.faces.context.FlashFactory. For more information, see:
 * http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1071
 *
 * @author  Neil Griffin
 */
public abstract class BridgeFlashFactory implements FacesWrapper<BridgeFlashFactory> {

	/**
	 * Returns a thread-safe instance of {@link BridgeFlash} from the {@link BridgeFlashFactory} found by the {@link
	 * BridgeFactoryFinder}. The returned instance is not guaranteed to be {@link java.io.Serializable}.
	 *
	 * @param  portletContext  The context associated with the current portlet.
	 */
	public static BridgeFlash getBridgeFlashInstance(PortletContext portletContext) {

		BridgeFlashFactory bridgeFlashFactory = (BridgeFlashFactory) BridgeFactoryFinder.getFactory(portletContext,
				BridgeFlashFactory.class);

		return bridgeFlashFactory.getBridgeFlash();
	}

	public abstract BridgeFlash getBridgeFlash();
}
