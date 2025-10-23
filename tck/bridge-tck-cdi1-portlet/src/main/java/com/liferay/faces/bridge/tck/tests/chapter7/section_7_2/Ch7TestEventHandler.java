/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter7.section_7_2;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;


/**
 * @author  Neil Griffin
 */
public class Ch7TestEventHandler implements BridgeEventHandler {

	@Override
	public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {

		String testName = (String) event.getValue();

		if ("eventRequestAlternativeTest".equals(testName) || "eventResponseAlternativeTest".equals(testName)) {

			ELContext elContext = facesContext.getELContext();
			ELResolver elResolver = elContext.getELResolver();

			TestsCDI1 tests = (TestsCDI1) elResolver.getValue(elContext, null, "chapter7_2CDITests");

			if ("eventRequestAlternativeTest".equals(testName)) {
				tests.eventRequestAlternativeTest(null);
			}
			else {
				tests.eventResponseAlternativeTest(null);
			}
		}

		return null;
	}
}
