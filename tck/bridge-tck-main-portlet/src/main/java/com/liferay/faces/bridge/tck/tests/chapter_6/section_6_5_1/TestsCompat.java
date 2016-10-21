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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_5_1;

import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.beans.TestRunnerBean;


/**
 * @author  Kyle Stiemann
 */
public abstract class TestsCompat {

	public void testImplicitRequestResponseObjects(TestRunnerBean testRunner, FacesContext facesContext) {
		ExternalContext externalContext = facesContext.getExternalContext();
		ELResolver elResolver = facesContext.getELContext().getELResolver();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.HEADER_PHASE) { // HEADER REQUEST

			// headerRequest -> object of type javax.portlet.HeaderRequest
			testImplicitObject(testRunner, elResolver, facesContext, "headerRequest", externalContext.getRequest());

			// headerResponse -> object of type javax.portlet.HeaderResponse
			testImplicitObject(testRunner, elResolver, facesContext, "headerResponse", externalContext.getResponse());
		}
		else // RENDER REQUEST
		{

			// renderRequest -> object of type javax.portlet.RenderRequest
			testImplicitObject(testRunner, elResolver, facesContext, "renderRequest", externalContext.getRequest());

			// renderResponse -> object of type javax.portlet.RenderResponse
			testImplicitObject(testRunner, elResolver, facesContext, "renderResponse", externalContext.getResponse());
		}
	}

	protected abstract void testImplicitObject(TestRunnerBean testRunner, ELResolver elResolver,
		FacesContext facesContext, String implicitObject, Object compareTo);
}
