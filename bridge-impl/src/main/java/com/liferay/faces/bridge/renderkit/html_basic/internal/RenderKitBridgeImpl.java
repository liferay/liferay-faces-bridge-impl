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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import javax.faces.component.UIForm;
import javax.faces.component.UIOutput;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.Renderer;

import com.liferay.faces.bridge.component.primefaces.internal.PrimeFacesFileUpload;
import com.liferay.faces.bridge.renderkit.icefaces.internal.HeadRendererICEfacesImpl;
import com.liferay.faces.bridge.renderkit.primefaces.internal.FileUploadRendererPrimeFacesImpl;
import com.liferay.faces.bridge.renderkit.primefaces.internal.FormRendererPrimeFacesImpl;
import com.liferay.faces.bridge.renderkit.primefaces.internal.HeadRendererPrimeFacesImpl;
import com.liferay.faces.bridge.renderkit.richfaces.internal.FileUploadRendererRichFacesImpl;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class extends {@link RenderKitWrapper} in order to programmatically control the {@link RenderKit} delegation
 * chain and wrapping of renderers.
 *
 * @author  Neil Griffin
 */
public class RenderKitBridgeImpl extends RenderKitBridgeImplCompat {

	// Package-Private Constants
	/* package-private */ static final String SCRIPT_RENDERER_TYPE = "javax.faces.resource.Script";
	/* package-private */ static final String STYLESHEET_RENDERER_TYPE = "javax.faces.resource.Stylesheet";

	// Private Constants
	private static final boolean ICEFACES_DETECTED = ProductFactory.getProduct(Product.Name.ICEFACES).isDetected();
	private static final String JAVAX_FACES_BODY = "javax.faces.Body";
	private static final String JAVAX_FACES_FORM = "javax.faces.Form";
	private static final String JAVAX_FACES_HEAD = "javax.faces.Head";
	private static final Product PRIMEFACES = ProductFactory.getProduct(Product.Name.PRIMEFACES);
	private static final boolean PRIMEFACES_DETECTED = PRIMEFACES.isDetected();
	private static final String PRIMEFACES_FAMILY = "org.primefaces.component";
	private static final String RICHFACES_FILE_UPLOAD_FAMILY = "org.richfaces.FileUpload";
	private static final String RICHFACES_FILE_UPLOAD_RENDERER_TYPE = "org.richfaces.FileUploadRenderer";

	public RenderKitBridgeImpl(RenderKit wrappedRenderKit) {
		super(wrappedRenderKit);
	}

	@Override
	public Renderer getRenderer(String family, String rendererType) {

		Renderer renderer = super.getRenderer(family, rendererType);

		if (UIOutput.COMPONENT_FAMILY.equals(family)) {

			if (JAVAX_FACES_HEAD.equals(rendererType)) {

				if (ICEFACES_DETECTED) {
					renderer = new HeadRendererICEfacesImpl();
				}
				else if (PRIMEFACES_DETECTED) {
					renderer = new HeadRendererPrimeFacesImpl();
				}
				else {
					renderer = new HeadRendererBridgeImpl();
				}
			}
			else if (JAVAX_FACES_BODY.equals(rendererType)) {
				renderer = new BodyRendererBridgeImpl(renderer);
			}
			else if (SCRIPT_RENDERER_TYPE.equals(rendererType) || STYLESHEET_RENDERER_TYPE.equals(rendererType)) {
				renderer = new ResourceRendererBridgeImpl(renderer);
			}
		}
		else if (UIForm.COMPONENT_FAMILY.equals(family) && JAVAX_FACES_FORM.equals(rendererType) &&
				PRIMEFACES_DETECTED) {

			renderer = new FormRendererPrimeFacesImpl(PRIMEFACES.getMajorVersion(), PRIMEFACES.getMinorVersion(),
					renderer);
		}
		else if (PRIMEFACES_FAMILY.equals(family) && PrimeFacesFileUpload.RENDERER_TYPE.equals(rendererType)) {
			renderer = new FileUploadRendererPrimeFacesImpl(renderer);
		}
		else if (RICHFACES_FILE_UPLOAD_FAMILY.equals(family) &&
				RICHFACES_FILE_UPLOAD_RENDERER_TYPE.equals(rendererType)) {
			renderer = new FileUploadRendererRichFacesImpl(renderer);
		}

		return renderer;
	}
}
