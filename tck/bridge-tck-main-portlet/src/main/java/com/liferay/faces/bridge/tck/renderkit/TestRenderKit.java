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

import javax.faces.component.UIOutput;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.Renderer;

import com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.ResourceRendererIdImpl;


/**
 * @author  Kyle Stiemann
 */
public class TestRenderKit extends RenderKitWrapper {

	// Public Constants
	public static final String TEST_RENDER_KIT_ID = "TestRenderKit";

	// Private Data Members
	private String testName;
	private RenderKit wrappedRenderKit;

	public TestRenderKit(RenderKit wrappedRenderKit, String testName) {

		this.testName = testName;
		this.wrappedRenderKit = wrappedRenderKit;
	}

	@Override
	public Renderer getRenderer(String family, String rendererType) {

		Renderer renderer = super.getRenderer(family, rendererType);

		if ("resourcesRenderedInHeadTest".equals(testName) && UIOutput.COMPONENT_FAMILY.equals(family) &&
				("javax.faces.resource.Script".equals(rendererType) ||
					"javax.faces.resource.Stylesheet".equals(rendererType))) {
			renderer = new ResourceRendererIdImpl(renderer);
		}

		return renderer;
	}

	@Override
	public RenderKit getWrapped() {
		return wrappedRenderKit;
	}
}
