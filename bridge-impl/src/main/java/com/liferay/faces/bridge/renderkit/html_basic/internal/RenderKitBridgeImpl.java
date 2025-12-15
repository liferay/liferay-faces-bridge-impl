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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIOutput;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitWrapper;
import jakarta.faces.render.Renderer;

import com.liferay.faces.bridge.renderkit.bridge.internal.FileUploadRendererPortletImpl;
import com.liferay.faces.bridge.renderkit.primefaces.internal.FormRendererPrimeFacesImpl;
import com.liferay.faces.bridge.renderkit.primefaces.internal.HeadRendererPrimeFacesImpl;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class extends {@link RenderKitWrapper} in order to programmatically control the {@link RenderKit} delegation
 * chain and wrapping of renderers.
 *
 * @author  Neil Griffin
 */
public class RenderKitBridgeImpl extends RenderKitBridgeImplCompat {

	// Public Constants
	public static final String PRIMEFACES_FILE_UPLOAD_RENDERER_TYPE = "org.primefaces.component.FileUploadRenderer";

	// Private Constants
	private static final String JAVAX_FACES_BODY = "jakarta.faces.Body";
	private static final String JAVAX_FACES_FORM = "jakarta.faces.Form";
	private static final String JAVAX_FACES_HEAD = "jakarta.faces.Head";
	private static final String PRIMEFACES_FAMILY = "org.primefaces.component";

	public RenderKitBridgeImpl(RenderKit wrappedRenderKit) {
		super(wrappedRenderKit);
	}

	@Override
	public Renderer getRenderer(String family, String rendererType) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ProductFactory productFactory = (ProductFactory) FactoryExtensionFinder.getFactory(externalContext,
				ProductFactory.class);
		final Product PRIMEFACES = productFactory.getProductInfo(Product.Name.PRIMEFACES);
		final boolean PRIMEFACES_DETECTED = PRIMEFACES.isDetected();

		Renderer renderer = super.getRenderer(family, rendererType);

		if (UIOutput.COMPONENT_FAMILY.equals(family)) {

			if (JAVAX_FACES_HEAD.equals(rendererType)) {
				if (PRIMEFACES_DETECTED) {
					renderer = new HeadRendererPrimeFacesImpl();
				}
				else {
					renderer = new HeadRendererBridgeImpl();
				}
			}
			else if (JAVAX_FACES_BODY.equals(rendererType)) {
				renderer = new BodyRendererBridgeImpl(renderer);
			}
			else if (RenderKitUtil.SCRIPT_RENDERER_TYPE.equals(rendererType) ||
					RenderKitUtil.STYLESHEET_RENDERER_TYPE.equals(rendererType)) {
				renderer = new ResourceRendererBridgeImpl(renderer);
			}
		}
		else if (UIForm.COMPONENT_FAMILY.equals(family) && JAVAX_FACES_FORM.equals(rendererType) &&
				PRIMEFACES_DETECTED) {

			renderer = new FormRendererPrimeFacesImpl(PRIMEFACES.getMajorVersion(), PRIMEFACES.getMinorVersion(),
					renderer);
		}
		else if (PRIMEFACES_FAMILY.equals(family) && PRIMEFACES_FILE_UPLOAD_RENDERER_TYPE.equals(rendererType)) {
			renderer = new FileUploadRendererPortletImpl(renderer);
		}

		return renderer;
	}
}
