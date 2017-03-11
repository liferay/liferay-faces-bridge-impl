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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_1_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * @author  Michael Freedman
 */
public class BridgeClearRequestScopePortlet extends GenericFacesTestSuitePortlet implements PhaseListener {

	private static final String MESSAGE_VALUE1 = "Test Message1 Retention.";
	private static final String MESSAGE_VALUE2 = "Test Message2 Retention.";

	public void afterPhase(PhaseEvent e) {

		// Now that we are after the navigation and have the new view tree -- add the message
		e.getFacesContext().addMessage(e.getFacesContext().getViewRoot().getClientId(e.getFacesContext()),
			new FacesMessage(MESSAGE_VALUE1));
		e.getFacesContext().addMessage(e.getFacesContext().getViewRoot().getClientId(e.getFacesContext()),
			new FacesMessage(MESSAGE_VALUE2));
	}

	public void beforePhase(PhaseEvent e) {
	}

	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {
		super.doDispatch(renderRequest, renderResponse);
	}

	// PhaseListener methods
	public PhaseId getPhaseId() {
		return PhaseId.INVOKE_APPLICATION;
	}

	public void init(PortletConfig config) throws PortletException {
		super.init(config);

	}

	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
		IOException {
		Map<String, Object> m = copyAttributes(actionRequest);
		addLifecycleListener();
		super.processAction(actionRequest, actionResponse);
		removeLifecycleListener();
		clearAttributes(actionRequest, m);
	}

	private void addLifecycleListener() {
		LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);

		for (Iterator<String> lIds = factory.getLifecycleIds(); lIds.hasNext();) {
			factory.getLifecycle(lIds.next()).addPhaseListener(this);
		}
	}

	private void clearAttributes(ActionRequest r, Map<String, Object> m) {
		ArrayList<String> removeList = (ArrayList<String>) new ArrayList(10);
		Enumeration<String> e = r.getAttributeNames();

		while (e.hasMoreElements()) {
			String key = e.nextElement();

			if (!m.containsKey(key)) {

				// add to removeList so can remove after the loop to avoid potential ConcurrentModification Exceptions
				removeList.add(key);

			}
		}

		// Postpone the remove until after the iteration as it causes a ConcurrentModificationException on some
		// appServers (WebSphere)
		for (Iterator<String> iter = removeList.iterator(); iter.hasNext();) {
			r.removeAttribute(iter.next());
		}
	}

	private Map<String, Object> copyAttributes(ActionRequest r) {
		Map<String, Object> m = new HashMap(20);
		Enumeration<String> e = r.getAttributeNames();

		while (e.hasMoreElements()) {
			String key = e.nextElement();
			Object o = r.getAttribute(key);
			m.put(key, o);
		}

		return m;
	}

	private void removeLifecycleListener() {
		LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);

		for (Iterator<String> lIds = factory.getLifecycleIds(); lIds.hasNext();) {
			factory.getLifecycle(lIds.next()).removePhaseListener(this);
		}
	}
}
