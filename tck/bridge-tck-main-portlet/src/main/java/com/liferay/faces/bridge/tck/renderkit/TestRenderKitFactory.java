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
package com.liferay.faces.bridge.tck.renderkit;

import java.util.Iterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.util.application.ApplicationUtil;


/**
 * @author  Neil Griffin
 */
public class TestRenderKitFactory extends RenderKitFactory {

	// Private Data Members
	private RenderKitFactory wrappedRenderKitFactory;

	public TestRenderKitFactory(RenderKitFactory renderKitFactory) {
		this.wrappedRenderKitFactory = renderKitFactory;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		wrappedRenderKitFactory.addRenderKit(renderKitId, renderKit);
	}

	@Override
	public RenderKit getRenderKit(FacesContext facesContext, String renderKitId) {

		RenderKit renderKit;

		if (!ApplicationUtil.isStartupOrShutdown(facesContext) &&
				TestRenderKit.TEST_RENDER_KIT_ID.equals(renderKitId)) {

			renderKit = wrappedRenderKitFactory.getRenderKit(facesContext, "HTML_BASIC");

			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, Object> requestMap = externalContext.getRequestMap();
			String testName = (String) requestMap.get(Constants.TEST_NAME);
			renderKit = new TestRenderKit(renderKit, testName);
		}
		else {
			renderKit = wrappedRenderKitFactory.getRenderKit(facesContext, renderKitId);
		}

		return renderKit;
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return wrappedRenderKitFactory.getRenderKitIds();
	}
}
