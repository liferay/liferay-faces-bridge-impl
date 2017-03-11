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

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import com.liferay.faces.util.application.ApplicationUtil;
import com.liferay.faces.util.context.FacesRequestContext;


/**
 * <p>Since liferay-faces-bridge-impl.jar!META-INF/faces-config.xml specifies ordering <code>
 * &lt;after&gt;&lt;name&gt;LiferayFacesUtil&lt;/name&gt;&lt;/after&gt;&lt;before&gt;&lt;others/&gt;&lt;/before&gt;</code>
 * , the factories in this module can only decorate those provided by liferay-faces-util.jar and the JSF implementation.
 * As a result, factories registered by Non-Liferay/3rd-Party component suites like ICEfaces, PrimeFaces, and RichFaces
 * will end up decorating the factories registered by this module.</p>
 *
 * <p>However, in order to ensure that scripts contained in {@link FacesRequestContext#getScripts()} are encoded before
 * the closing <code>&lt;/body&gt;</code> element, {@link BodyRendererBridgeImpl} needs to decorate body renderers
 * provided by any of the aforementioned component suites. This could be accomplished in one of two ways:
 *
 * <ol>
 *   <li>The {@link RenderKitBridgeImpl} class from this module could exist in a separate module that specifies
 *     &lt;after&gt;&lt;others/&gt;&lt;/after&gt;</code> and the separate module would register an HTML_BASIC <code>
 *     render-kit</code>.</li>
 *   <li>The {@link RenderKitBridgeImpl} class could remain in this module, but this module would have to register a
 *     <code>render-kit-factory</code> in order to programatically control the {@link RenderKit} delegation chain and
 *     wrapping of renderers. But the only reason why this works is because <strong>none</strong> of the aforementioned
 *     component suites register a <code>render-kit-factory</code>.</li>
 * </ol>
 *
 * For the sake of minimizing the number of modules, the second option has been implemented.</p>
 *
 * <p><strong>Note:</strong> liferay-faces-util.jar!META-INF/faces-config.xml also specifies a <code>
 * render-kit-factory</code> that will be decorated by this one.</p>
 *
 * @author  Kyle Stiemann
 */
public class RenderKitFactoryBridgeImpl extends RenderKitFactory {

	// Private Data Members
	private RenderKitFactory wrappedRenderKitFactory;

	public RenderKitFactoryBridgeImpl(RenderKitFactory renderKitFactory) {
		this.wrappedRenderKitFactory = renderKitFactory;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		wrappedRenderKitFactory.addRenderKit(renderKitId, renderKit);
	}

	@Override
	public RenderKit getRenderKit(FacesContext facesContext, String renderKitId) {

		RenderKit renderKit = wrappedRenderKitFactory.getRenderKit(facesContext, renderKitId);

		// FACES-2615 Only Add the RenderKit to the delegation chain when the application is not starting up or
		// shutting down.
		if (("HTML_BASIC".equals(renderKitId) || "PRIMEFACES_MOBILE".equals(renderKitId)) &&
				!ApplicationUtil.isStartupOrShutdown(facesContext)) {
			return new RenderKitBridgeImpl(renderKit);
		}
		else {
			return renderKit;
		}
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return wrappedRenderKitFactory.getRenderKitIds();
	}
}
