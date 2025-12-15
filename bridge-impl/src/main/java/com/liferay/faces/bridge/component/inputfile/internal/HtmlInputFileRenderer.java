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
package com.liferay.faces.bridge.component.inputfile.internal;

import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.html.HtmlInputFile;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.Renderer;
import jakarta.servlet.http.Part;

import com.liferay.faces.bridge.component.inputfile.InputFile;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.bridge.renderkit.bridge.internal.PartFileUploadAdapterImpl;
import com.liferay.faces.bridge.util.internal.TCCLUtil;
import com.liferay.faces.util.lang.ThreadSafeAccessor;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;
import com.liferay.faces.util.render.DelegatingRendererBase;
import com.liferay.faces.util.render.RendererUtil;


/**
 * @author  Neil Griffin
 */
public class HtmlInputFileRenderer extends DelegatingRendererBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HtmlInputFileRenderer.class);

	// Private Final Data Members
	private final DelegateRendererAccessor delegateRendererAccessor = new DelegateRendererAccessor();

	@Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {

		HtmlInputFile htmlInputFile = (HtmlInputFile) uiComponent;

		Map<String, List<UploadedFile>> uploadedFileMap = InputFileRenderer.getUploadedFileMap(facesContext);

		if (!uploadedFileMap.isEmpty()) {

			String clientId = uiComponent.getClientId(facesContext);

			if (uploadedFileMap.containsKey(clientId)) {

				List<UploadedFile> uploadedFiles = uploadedFileMap.get(clientId);

				if ((uploadedFiles != null) && (uploadedFiles.size() > 0)) {

					Part part = new PartFileUploadAdapterImpl(uploadedFiles.get(0), clientId);
					htmlInputFile.setTransient(true);
					htmlInputFile.setSubmittedValue(part);
				}
				else {
					htmlInputFile.setSubmittedValue(new PartEmptyImpl());
				}
			}
		}

		RendererUtil.decodeClientBehaviors(facesContext, uiComponent);
	}

	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent uiComponent, Object submittedValue)
		throws ConverterException {

		if (submittedValue instanceof PartEmptyImpl) {
			return null;
		}
		else {
			return submittedValue;
		}
	}

	@Override
	public String getDelegateComponentFamily() {
		return InputFile.COMPONENT_FAMILY;
	}

	@Override
	public Renderer getDelegateRenderer(FacesContext facesContext) {
		return delegateRendererAccessor.get(facesContext);
	}

	@Override
	public String getDelegateRendererType() {
		return "jakarta.faces.File";
	}

	private static final class DelegateRendererAccessor extends ThreadSafeAccessor<Renderer, FacesContext> {

		@Override
		protected Renderer computeValue(FacesContext facesContext) {

			Renderer delegateRenderer;
			String delegateRendererFQCN = "com.sun.faces.renderkit.html_basic.FileRenderer";
			ExternalContext externalContext = facesContext.getExternalContext();

			try {

				Class<?> delegateRendererClass = TCCLUtil.loadClassFromContext(getClass(), delegateRendererFQCN);
				delegateRenderer = (Renderer) delegateRendererClass.newInstance();
			}
			catch (Exception e) {

				logger.error(e);
				delegateRenderer = new NoOpRenderer();
			}

			return delegateRenderer;
		}
	}

	private static final class NoOpRenderer extends Renderer {

	}
}
