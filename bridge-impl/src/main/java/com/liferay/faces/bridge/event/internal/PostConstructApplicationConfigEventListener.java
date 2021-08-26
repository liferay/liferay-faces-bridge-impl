/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.liferay.faces.bridge.internal.BridgeDependencyVerifier;
import com.liferay.faces.util.config.ApplicationConfig;


/**
 * @author  Kyle Stiemann
 */
public class PostConstructApplicationConfigEventListener implements SystemEventListener {

	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof ApplicationConfig;
	}

	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		BridgeDependencyVerifier.verify(externalContext);
	}
}
