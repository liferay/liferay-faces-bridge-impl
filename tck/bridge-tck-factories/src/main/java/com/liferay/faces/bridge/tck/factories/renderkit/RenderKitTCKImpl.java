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
package com.liferay.faces.bridge.tck.factories.renderkit;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.ClientBehaviorRenderer;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.Renderer;
import jakarta.faces.render.ResponseStateManager;


/**
 * @author  Neil Griffin
 */
public class RenderKitTCKImpl extends RenderKit {

	private RenderKit wrappedRenderKit;

	public RenderKitTCKImpl(RenderKit renderKit) {
		this.wrappedRenderKit = renderKit;
	}

	@Override
	public void addClientBehaviorRenderer(String type, ClientBehaviorRenderer clientBehaviorRenderer) {
		wrappedRenderKit.addClientBehaviorRenderer(type, clientBehaviorRenderer);
	}

	@Override
	public void addRenderer(String family, String rendererType, Renderer renderer) {
		wrappedRenderKit.addRenderer(family, rendererType, renderer);
	}

	@Override
	public ResponseStream createResponseStream(OutputStream out) {
		return wrappedRenderKit.createResponseStream(out);
	}

	@Override
	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
		return wrappedRenderKit.createResponseWriter(writer, contentTypeList, characterEncoding);
	}

	@Override
	public ClientBehaviorRenderer getClientBehaviorRenderer(String type) {
		return wrappedRenderKit.getClientBehaviorRenderer(type);
	}

	@Override
	public Iterator<String> getClientBehaviorRendererTypes() {
		return wrappedRenderKit.getClientBehaviorRendererTypes();
	}

	@Override
	public Iterator<String> getComponentFamilies() {
		return wrappedRenderKit.getComponentFamilies();
	}

	@Override
	public Renderer getRenderer(String family, String rendererType) {
		return wrappedRenderKit.getRenderer(family, rendererType);
	}

	@Override
	public Iterator<String> getRendererTypes(String componentFamily) {
		return wrappedRenderKit.getRendererTypes(componentFamily);
	}

	@Override
	public ResponseStateManager getResponseStateManager() {
		return new ResponseStateManagerTCKImpl(wrappedRenderKit.getResponseStateManager());
	}
}
