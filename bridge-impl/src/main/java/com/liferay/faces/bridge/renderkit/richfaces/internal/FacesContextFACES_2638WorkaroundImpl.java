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
package com.liferay.faces.bridge.renderkit.richfaces.internal;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.portlet.PortletContext;
import javax.servlet.ServletRequest;

import com.liferay.faces.bridge.renderkit.bridge.internal.HttpServletRequestFileUploadAdapter;
import com.liferay.faces.bridge.renderkit.bridge.internal.ServletContextFileUploadAdapterImpl;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ final class FacesContextFACES_2638WorkaroundImpl extends FacesContextWrapper {

	// Private Data Members
	private ExternalContext externalContext;
	private FacesContext wrappedFacesContext;

	public FacesContextFACES_2638WorkaroundImpl(FacesContext wrappedFacesContext) {

		ExternalContext wrappedExternalContext = wrappedFacesContext.getExternalContext();
		this.externalContext = new ExternalContextFACES_2638WorkaroundImpl(wrappedExternalContext);
		this.wrappedFacesContext = wrappedFacesContext;
	}

	@Override
	public ExternalContext getExternalContext() {
		return externalContext;
	}

	@Override
	public FacesContext getWrapped() {
		return wrappedFacesContext;
	}

	@Override
	public void release() {

		super.release();
		wrappedFacesContext = null;
		externalContext = null;
	}

	private static final class ExternalContextFACES_2638WorkaroundImpl extends ExternalContextWrapper {

		// Private Data Members
		private final ExternalContext wrappedExternalContext;
		private final Object context;

		public ExternalContextFACES_2638WorkaroundImpl(ExternalContext wrappedExternalContext) {

			Object request = wrappedExternalContext.getRequest();

			if (request instanceof HttpServletRequestFileUploadAdapter) {

				ServletRequest servletRequest = (ServletRequest) request;
				this.context = servletRequest.getServletContext();
			}
			else {

				PortletContext portletContext = (PortletContext) wrappedExternalContext.getContext();
				this.context = new ServletContextFileUploadAdapterImpl(portletContext);
			}

			this.wrappedExternalContext = wrappedExternalContext;
		}

		@Override
		public Object getContext() {
			return context;
		}

		@Override
		public ExternalContext getWrapped() {
			return wrappedExternalContext;
		}
	}
}
