/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.application;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ViewHandlerTCKImpl extends ViewHandlerWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewHandlerTCKImpl.class);

	// Private Data Members
	private ViewHandler wrappedViewHandler;

	public ViewHandlerTCKImpl(ViewHandler viewHandler) {
		this.wrappedViewHandler = viewHandler;
	}

	/**
	 * The purpose of overriding this method is to work-around a JSF 1.2 dependency in the TCK. Once the
	 * TestSuiteViewHandlerImpl is changed to be a subclass of ViewDeclarationLanguage this can be removed.
	 */
	@Override
	public void renderView(FacesContext facesContext, UIViewRoot viewToRender) throws IOException, FacesException {

		try {
			super.renderView(facesContext, viewToRender);
		}
		catch (FacesException e) {
			String tckRequestAttribute = (String) facesContext.getExternalContext().getRequestMap().get(
					"javax.portlet.faces.tck.testRenderPolicyPass");

			if (tckRequestAttribute != null) {
				logger.info("Working around JSF 1.2 dependency in the TCK");

				ViewDeclarationLanguage viewDeclarationLanguage = getViewDeclarationLanguage(facesContext,
						viewToRender.getViewId());
				viewDeclarationLanguage.renderView(facesContext, viewToRender);
			}
			else {
				throw e;
			}
		}
	}

	@Override
	public ViewHandler getWrapped() {
		return wrappedViewHandler;
	}
}
