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
package com.liferay.faces.bridge.tck.tests.chapter7.section_7_2;

import jakarta.portlet.annotations.EventDefinition;
import jakarta.portlet.annotations.PortletApplication;
import jakarta.portlet.annotations.PortletQName;


/**
 * @author  Neil Griffin
 */

@PortletApplication(
	events = {
			@EventDefinition(
				qname =
					@PortletQName(
						localPart = "faces.liferay.com.tck.testEvent",
						namespaceURI = "http://liferay.com/faces/event_ns"
					), payloadType = String.class
			),
		}
)
public class CDIPortletApp {
}
