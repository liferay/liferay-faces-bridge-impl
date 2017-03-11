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

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;


/**
 * This class is designed to be a {@link FacesContextWrapper} around the Mojarra/MyFaces {@link FacesContext}
 * implementation. Its purpose is to fulfill the requirements of TestPage082 (facesContextFactoryServiceProviderTest).
 * For JSF 2.x, it is possible to have a portlet bridge that does not wrap the FacesContext, so this test should not be
 * necessary for JSF 2.x versions of the TCK.
 *
 * @author  Neil Griffin
 */
public class FacesContextTCKImpl extends FacesContextWrapper {

	// Private Data Members
	private FacesContext wrappedFacesContext;

	public FacesContextTCKImpl(FacesContext facesContext) {
		this.wrappedFacesContext = facesContext;
	}

	@Override
	public FacesContext getWrapped() {
		return wrappedFacesContext;
	}
}
