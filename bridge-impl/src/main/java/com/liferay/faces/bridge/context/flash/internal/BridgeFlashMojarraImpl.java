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
package com.liferay.faces.bridge.context.flash.internal;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;


/**
 * The purpose of this class is to workaround http://java.net/jira/browse/JAVASERVERFACES-1987
 *
 * @author  Neil Griffin
 */
public class BridgeFlashMojarraImpl extends BridgeFlashBase {

	private boolean mojarraServletDependencyActive;
	private Flash wrappedFlash;

	public BridgeFlashMojarraImpl(Flash flash) {
		this.wrappedFlash = flash;
	}

	@Override
	public void doPostPhaseActions(FacesContext facesContext) {

		mojarraServletDependencyActive = true;
		getWrapped().doPostPhaseActions(facesContext);
		mojarraServletDependencyActive = false;
	}

	@Override
	public Object put(String key, Object value) {

		mojarraServletDependencyActive = true;

		Object putValue = getWrapped().put(key, value);
		mojarraServletDependencyActive = false;

		return putValue;
	}

	@Override
	public boolean isServletResponseRequired() {
		return mojarraServletDependencyActive;
	}

	@Override
	public Flash getWrapped() {
		return wrappedFlash;
	}
}
