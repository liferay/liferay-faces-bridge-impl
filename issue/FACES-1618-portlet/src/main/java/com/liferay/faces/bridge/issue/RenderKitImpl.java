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
package com.liferay.faces.bridge.issue;

import java.io.Writer;

import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.Renderer;


/**
 * @author  Kyle Stiemann
 */
public class RenderKitImpl extends RenderKitWrapper {

	// Private Constants
	private static final String SCRIPT_RENDERER_TYPE = "javax.faces.resource.Script";
	private static final String STYLESHEET_RENDERER_TYPE = "javax.faces.resource.Stylesheet";

	// Private Data Members
	private RenderKit wrappedRenderKit;

	public RenderKitImpl(RenderKit wrappedRenderKit) {
		this.wrappedRenderKit = wrappedRenderKit;
	}

	@Override
	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
		return wrappedRenderKit.createResponseWriter(writer, contentTypeList, characterEncoding);
	}

	@Override
	public Renderer getRenderer(String family, String rendererType) {

		Renderer renderer = super.getRenderer(family, rendererType);

		if (SCRIPT_RENDERER_TYPE.equals(rendererType) || STYLESHEET_RENDERER_TYPE.equals(rendererType)) {
			renderer = new ResourceRendererImpl(renderer);
		}

		return renderer;
	}

	@Override
	public RenderKit getWrapped() {
		return wrappedRenderKit;
	}
}
