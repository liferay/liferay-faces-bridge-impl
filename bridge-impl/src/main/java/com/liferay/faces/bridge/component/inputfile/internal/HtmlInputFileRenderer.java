/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputFile;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import javax.servlet.http.Part;

import com.liferay.faces.bridge.component.inputfile.InputFile;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.bridge.renderkit.bridge.internal.PartFileUploadAdapterImpl;
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

	// Private Data Members
	private Renderer delegateRenderer;

	public HtmlInputFileRenderer() {

		String delegateRendererFQCN = "com.sun.faces.renderkit.html_basic.FileRenderer";

		final boolean MYFACES_DETECTED = ProductFactory.getProduct(Product.Name.MYFACES).isDetected();

		if (MYFACES_DETECTED) {
			delegateRendererFQCN = "org.apache.myfaces.renderkit.html.HtmlInputFileRenderer";
		}

		try {
			Class<?> delegateRendererClass = Class.forName(delegateRendererFQCN);
			delegateRenderer = (Renderer) delegateRendererClass.newInstance();
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

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
		return delegateRenderer;
	}

	@Override
	public String getDelegateRendererType() {
		return "javax.faces.File";
	}
}
