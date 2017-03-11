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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class TestEventHandler implements BridgeEventHandler {
	public static final String EVENT_RECEIVED = "com.liferay.faces.bridge.tck.eventReceived";
	public static final String EVENT_TEST_FAILED = "com.liferay.faces.bridge.tck.eventTestFailed";
	public static final String EVENTATTR = "portlet.bridge.tck.testAttr";
	public static final String EVENT_QNAME = "http://liferay.com/faces/event_ns";
	public static final String EVENT_NAME = "faces.liferay.com.tck.testEvent";

	public TestEventHandler() {

	}

	public EventNavigationResult handleEvent(FacesContext context, Event event) {

		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();

		String testName = (String) event.getValue();
		String portletTestName = (String) requestMap.get(Constants.TEST_NAME);

		if (testName == null) {
			requestMap.put(EVENT_TEST_FAILED,
				"Event test failed because the payload is null instead of being the name of the test this event pertains to.");
		}

		if ((testName == null) || (portletTestName == null) || !testName.equals(portletTestName)) {

			// all the tests use the same event -- so only handle the event if raised from the portlet containing the
			// test and ignore for all the other tests/portlets
			return null;
		}

		sessionMap.put(EVENT_RECEIVED, event);

		if (testName.equals("eventScopeRestoredTest")) {

			// test -- that the request attr set in action is restored
			String payload = (String) requestMap.get("portlet.bridge.tck.testAttr");

			if ((payload == null) || !payload.equals(testName)) {
				sessionMap.put(EVENT_TEST_FAILED, "Event received but request scope wasn't restored.");

				return null;
			}

			return null;
		}
		else if (testName.equals("eventScopeNotRestoredRedirectTest")) {

			// test -- that the request attr set in action is restored
			String payload = (String) requestMap.get("portlet.bridge.tck.testAttr");

			if ((payload == null) || !payload.equals(testName)) {
				sessionMap.put(EVENT_TEST_FAILED, "Event received but request scope wasn't restored.");
			}

			return new EventNavigationResult(null, testName + "EventNavigation");
		}
		else if (testName.equals("eventScopeNotRestoredModeChangedTest")) {

			// test -- that the request attr set in action is restored
			String payload = (String) requestMap.get("portlet.bridge.tck.testAttr");

			if ((payload == null) || !payload.equals(testName)) {
				sessionMap.put(EVENT_TEST_FAILED, "Event received but request scope wasn't restored.");
			}

			return new EventNavigationResult(null, testName + "EventNavigation");
		}
		else if (testName.equals("eventControllerTest")) {

			// Verify the event phase attribute is set
			Bridge.PortletPhase phase = (Bridge.PortletPhase) requestMap.get(Bridge.PORTLET_LIFECYCLE_PHASE);
			requestMap.put("tck.eventPhaseCheck",
				new Boolean((phase != null) && (phase == Bridge.PortletPhase.EVENT_PHASE)));

			// Now verify that a change to a public render parameter is carried forward
			String currentValue = (String) requestMap.get("modelPRP");

			if (currentValue == null) {
				currentValue = "1";
			}
			else {
				currentValue = currentValue.concat("1");
			}

			// Config is setup to exclude this value from bridge request scope -- so only get carried forward
			// if received in render request
			requestMap.put("modelPRP", currentValue);

			// Stash copy of value in an attr that is carried forward to compare.
			requestMap.put("tck.compareModelPRPValue", currentValue);

			// Verify that event navigation works
			return new EventNavigationResult(null, testName + "EventNavigation");
		}

		return null;
	}
}
