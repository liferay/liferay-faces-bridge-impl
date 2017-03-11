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
package com.liferay.faces.bridge.tck.context;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;


/**
 * This class is necessary in order to pass the facesContextFactoryServiceProviderTest in the TCK. In addition, if the
 * TCK is detected, then it provides a special FacesContext implementation that handles special cases within the TCK.
 *
 * @author  Neil Griffin
 */
public class FacesContextFactoryTCKImpl extends FacesContextFactory {

	// Private Data Members
	private FacesContextFactory wrappedFacesContextFactory;

	public FacesContextFactoryTCKImpl(FacesContextFactory facesContextFactory) {
		this.wrappedFacesContextFactory = facesContextFactory;
	}

	@Override
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
		throws FacesException {

		FacesContext facesContext = wrappedFacesContextFactory.getFacesContext(context, request, response, lifecycle);

		facesContext = new FacesContextTCKImpl(facesContext);

		return facesContext;
	}

	@Override
	public FacesContextFactory getWrapped() {
		return wrappedFacesContextFactory;
	}

}
