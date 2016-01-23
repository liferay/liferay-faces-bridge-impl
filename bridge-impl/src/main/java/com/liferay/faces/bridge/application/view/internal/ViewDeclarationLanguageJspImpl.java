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
package com.liferay.faces.bridge.application.view.internal;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageWrapper;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ViewDeclarationLanguageJspImpl extends ViewDeclarationLanguageWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewDeclarationLanguageJspImpl.class);

	// Private Data Members
	private ViewDeclarationLanguage wrappedViewDeclarationLanguage;

	public ViewDeclarationLanguageJspImpl(ViewDeclarationLanguage viewDeclarationLanguage) {
		this.wrappedViewDeclarationLanguage = viewDeclarationLanguage;
	}

	@Override
	public void buildView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException {

		// Set a flag on the BridgeContext indicating that JSP AFTER_VIEW_CONTENT processing has been activated. The
		// flag is referenced by {@link ExternalContextImpl#getRequest()} and {@link ExternalContextImpl#getResponse()}
		// and is unset by {@link ExternalContextImpl#dispatch(String)}.
		BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
		bridgeContext.setProcessingAfterViewContent(true);

		logger.debug("Activated JSP AFTER_VIEW_CONTENT feature");

		// Have the wrapped VDL build the view.
		super.buildView(facesContext, uiViewRoot);
	}

	@Override
	public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException {

		// This code is required by the spec in order to support a JSR 301 legacy feature to support usage of a
		// servlet filter to capture the AFTER_VIEW_CONTENT. In reality it will likely never be used.
		Map<String, Object> attributes = facesContext.getExternalContext().getRequestMap();
		attributes.put(Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);
		super.renderView(facesContext, uiViewRoot);
		attributes.remove(Bridge.RENDER_CONTENT_AFTER_VIEW);

		// TCK TestPage201: renderContentAfterViewTest
		Object afterViewContent = facesContext.getExternalContext().getRequestMap().get(Bridge.AFTER_VIEW_CONTENT);

		if (afterViewContent != null) {

			if (afterViewContent instanceof char[]) {
				facesContext.getResponseWriter().write((char[]) afterViewContent);
			}
			else if (afterViewContent instanceof byte[]) {
				facesContext.getResponseWriter().write(new String((byte[]) afterViewContent));
			}
			else {
				logger.error("Invalid type for {0}={1}", Bridge.AFTER_VIEW_CONTENT, afterViewContent.getClass());
			}
		}
	}

	@Override
	public ViewDeclarationLanguage getWrapped() {
		return wrappedViewDeclarationLanguage;
	}

}
