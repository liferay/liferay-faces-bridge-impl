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
package com.liferay.faces.bridge.renderkit.bridge.internal;

import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.Renderer;
import jakarta.faces.render.RendererWrapper;
import javax.portlet.ClientDataRequest;

import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.factory.FactoryExtensionFinder;


/**
 * @author  Kyle Stiemann
 */
public final class FileUploadRendererPortletImpl extends RendererWrapper {

	// Private Final Data Members
	private final Renderer wrappedRenderer;

	public FileUploadRendererPortletImpl(Renderer renderer) {
		this.wrappedRenderer = renderer;
	}

	@Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {

		ExternalContext externalContext = facesContext.getExternalContext();
		ClientDataRequest clientDataRequest = (ClientDataRequest) externalContext.getRequest();
		ContextMapFactory contextMapFactory = (ContextMapFactory) FactoryExtensionFinder.getFactory(externalContext,
				ContextMapFactory.class);
		Map<String, List<UploadedFile>> uploadedFileMap = (Map<String, List<UploadedFile>>)
			contextMapFactory.getUploadedFileMap(clientDataRequest);

		if (!uploadedFileMap.isEmpty()) {

			try {

				externalContext.setRequest(new HttpServletRequestFileUploadAdapter(clientDataRequest, uploadedFileMap,
						externalContext));
				super.decode(facesContext, uiComponent);
			}
			finally {
				externalContext.setRequest(clientDataRequest);
			}
		}
	}

	@Override
	public Renderer getWrapped() {
		return wrappedRenderer;
	}
}
