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
package com.liferay.faces.bridge.internal;

import javax.faces.FacesWrapper;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;


/**
 * @author  Neil Griffin
 */
public abstract class BridgePhaseWrapper implements BridgePhase, FacesWrapper<BridgePhase> {

	public abstract BridgePhase getWrapped();

	public void execute() throws BridgeDefaultViewNotSpecifiedException, BridgeException {
		getWrapped().execute();
	}
}
