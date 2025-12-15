/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseListener;
import jakarta.faces.lifecycle.Lifecycle;


/**
 * @author  Michael Freedman
 */
public class TestSuiteLifecycleImpl extends Lifecycle {
	private Lifecycle mWrapped;

	public TestSuiteLifecycleImpl(Lifecycle parent) {
		mWrapped = parent;
	}

	@Override
	public void addPhaseListener(PhaseListener listener) {
		getWrapped().addPhaseListener(listener);
	}

	@Override
	public void execute(FacesContext facesContext) throws FacesException {
		getWrapped().execute(facesContext);
	}

	@Override
	public PhaseListener[] getPhaseListeners() {
		return getWrapped().getPhaseListeners();
	}

	@Override
	public void removePhaseListener(PhaseListener listener) {
		getWrapped().removePhaseListener(listener);
	}

	@Override
	public void render(FacesContext facesContext) throws FacesException {
		getWrapped().render(facesContext);
	}

	Lifecycle getWrapped() {
		return mWrapped;
	}

}
