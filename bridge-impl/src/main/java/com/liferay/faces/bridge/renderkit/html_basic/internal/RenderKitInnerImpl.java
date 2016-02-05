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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.Writer;

import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;

import com.liferay.faces.bridge.renderkit.bridge.internal.ResponseWriterBridgeImpl;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * @author  Kyle Stiemann
 */
public class RenderKitInnerImpl extends RenderKitWrapper {

	// Private Members
	private static final Product ICEFACES = ProductMap.getInstance().get(ProductConstants.ICEFACES);
	private static final boolean ICEFACES_DETECTED = ICEFACES.isDetected();
	private static final boolean ICEFACES3_OR_LOWER = (ICEFACES_DETECTED && (ICEFACES.getMajorVersion() <= 3));

	// Private Members
	private RenderKit wrappedRenderKit;

	public RenderKitInnerImpl(RenderKit wrappedRenderKit) {
		this.wrappedRenderKit = wrappedRenderKit;
	}

	@Override
	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {

		ResponseWriter responseWriter = wrappedRenderKit.createResponseWriter(writer, contentTypeList,
				characterEncoding);

		// FACES-2567 ICEfaces ice: (or compat) components require that the outermost ResponseWriter be an ICEfaces
		// DOMResponseWriter. So when ICEfaces or less is detected, RenderKitInnerImpl must add
		// ResponseWriterBridgeImpl to the inside of the delegation chain, since RenderKitBridgeImpl avoids adding
		// ResponseWriterBridgeImpl to the outside of the delegation chain.
		if (ICEFACES3_OR_LOWER) {
			responseWriter = new ResponseWriterBridgeImpl(responseWriter);
		}

		return responseWriter;
	}

	@Override
	public RenderKit getWrapped() {
		return wrappedRenderKit;
	}
}
