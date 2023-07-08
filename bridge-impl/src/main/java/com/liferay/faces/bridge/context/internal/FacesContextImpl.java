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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.context.FacesContextWrapper;


/**
 * @author  Neil Griffin
 */
public class FacesContextImpl extends FacesContextWrapper {

	// Private Data Members
	private ExternalContext externalContext;
	private FacesContext wrappedFacesContext;

	public FacesContextImpl(FacesContext facesContext, ExternalContext externalContext) {

		this.wrappedFacesContext = facesContext;
		this.externalContext = externalContext;
		setCurrentInstance(this);
	}

	@Override
	public ExternalContext getExternalContext() {
		return externalContext;
	}

	@Override
	public FacesContext getWrapped() {
		return wrappedFacesContext;
	}

}
