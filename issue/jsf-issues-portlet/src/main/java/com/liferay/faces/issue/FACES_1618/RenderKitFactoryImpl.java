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
package com.liferay.faces.issue.FACES_1618;

import java.util.Iterator;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitFactory;
import jakarta.portlet.PortletConfig;

import com.liferay.faces.util.application.ApplicationUtil;


/**
 * @author  Kyle Stiemann
 */
public class RenderKitFactoryImpl extends RenderKitFactory {

	// Private Data Members
	private RenderKitFactory wrappedRenderKitFactory;

	public RenderKitFactoryImpl(RenderKitFactory renderKitFactory) {
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
		if (!ApplicationUtil.isStartupOrShutdown(facesContext)) {

			Application application = facesContext.getApplication();
			PortletConfig portletConfig = application.evaluateExpressionGet(facesContext, "#{portletConfig}",
					PortletConfig.class);
			String portletName = portletConfig.getPortletName();

			if ((portletName.startsWith("FACES") && portletName.endsWith("1618")) &&
					("HTML_BASIC".equals(renderKitId) || "PRIMEFACES_MOBILE".equals(renderKitId))) {
				renderKit = new RenderKitImpl(renderKit);
			}
		}

		return renderKit;
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return wrappedRenderKitFactory.getRenderKitIds();
	}
}
