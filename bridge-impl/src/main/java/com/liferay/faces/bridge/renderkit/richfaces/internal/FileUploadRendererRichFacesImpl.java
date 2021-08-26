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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;

import com.liferay.faces.bridge.context.internal.FacesContextUnsupportedImpl;


/**
 * @author  Kyle Stiemann
 */
public final class FileUploadRendererRichFacesImpl extends RendererWrapper {

	// Private Final Data Members
	private final Renderer wrappedRenderer;

	public FileUploadRendererRichFacesImpl(Renderer wrappedRenderer) {
		this.wrappedRenderer = wrappedRenderer;
	}

	@Override
	public void decode(FacesContext originalFacesContext, UIComponent uiComponent) {

		FacesContext facesContext = new FacesContextFACES_2638WorkaroundImpl(originalFacesContext);

		try {

			FacesContextUtil.setCurrentFacesContextInstance(facesContext);
			super.decode(facesContext, uiComponent);
		}
		finally {
			FacesContextUtil.setCurrentFacesContextInstance(originalFacesContext);
		}
	}

	@Override
	public void encodeEnd(FacesContext originalFacesContext, UIComponent uiComponent) throws IOException {

		FacesContext facesContext = new FacesContextFACES_2638WorkaroundImpl(originalFacesContext);

		try {

			FacesContextUtil.setCurrentFacesContextInstance(facesContext);
			super.encodeEnd(facesContext, uiComponent);
		}
		finally {
			FacesContextUtil.setCurrentFacesContextInstance(originalFacesContext);
		}
	}

	@Override
	public Renderer getWrapped() {
		return wrappedRenderer;
	}

	private static final class FacesContextUtil extends FacesContextUnsupportedImpl {

		private FacesContextUtil() {
			throw new AssertionError();
		}

		public static void setCurrentFacesContextInstance(FacesContext facesContext) {
			setCurrentInstance(facesContext);
		}
	}
}
