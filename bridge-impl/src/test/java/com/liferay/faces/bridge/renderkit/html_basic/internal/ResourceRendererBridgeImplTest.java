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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import javax.faces.render.Renderer;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public final class ResourceRendererBridgeImplTest {

	@Test
	public void testSaveRestoreStateFACES_3280() {

		Renderer renderer = new RendererMockImpl();
		ResourceRendererBridgeImpl resourceRendererBridgeImpl = new ResourceRendererBridgeImpl(renderer);
		Object state = resourceRendererBridgeImpl.saveState(null);

		Assert.assertEquals(renderer.getClass(), state);
		resourceRendererBridgeImpl = new ResourceRendererBridgeImpl();
		Assert.assertNull(resourceRendererBridgeImpl.getWrapped());
		resourceRendererBridgeImpl.restoreState(null, state);
		Assert.assertNotNull(resourceRendererBridgeImpl.getWrapped());
	}

	/* package-private */ static final class RendererMockImpl extends Renderer {
	}
}
