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

import java.io.Writer;

import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;

import com.liferay.faces.bridge.renderkit.bridge.internal.ResponseWriterBridgeImpl;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class RenderKitBridgeImplCompat extends RenderKitWrapper {

	// Protected Constants
	protected static final Product ICEFACES = ProductFactory.getProduct(Product.Name.ICEFACES);
	protected static final boolean ICEFACES_DETECTED = ICEFACES.isDetected();

	// Private Data Members
	private RenderKit wrappedRenderKit;

	public RenderKitBridgeImplCompat(RenderKit wrappedRenderKit) {
		this.wrappedRenderKit = wrappedRenderKit;
	}

	/**
	 * Provides the bridge with the ability to wrap the HTML_BASIC ResponseWriter provided by the JSF implementation.
	 */
	@Override
	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
		ResponseWriter wrappedResponseWriter = wrappedRenderKit.createResponseWriter(writer, contentTypeList,
				characterEncoding);

		return new ResponseWriterBridgeImpl(wrappedResponseWriter);
	}

	@Override
	public RenderKit getWrapped() {
		return wrappedRenderKit;
	}
}
