/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import javax.faces.render.Renderer;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public final class ResourceRendererImplTest {

	@Test
	public void testSaveRestoreStateFACES_3280() {

		Renderer renderer = new RendererMockImpl();
		ResourceRendererImpl resourceRendererImpl = new ResourceRendererImpl(renderer);
		Object state = resourceRendererImpl.saveState(null);

		Assert.assertEquals(renderer.getClass(), state);
		resourceRendererImpl = new ResourceRendererImpl();
		Assert.assertNull(resourceRendererImpl.getWrapped());
		resourceRendererImpl.restoreState(null, state);
		Assert.assertNotNull(resourceRendererImpl.getWrapped());
	}

	/* package-private */ static final class RendererMockImpl extends Renderer {
	}
}
