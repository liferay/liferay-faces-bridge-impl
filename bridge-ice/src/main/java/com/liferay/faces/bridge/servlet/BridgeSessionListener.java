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
package com.liferay.faces.bridge.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * This class exists simply to prevent startup errors when developers happen to uniformly specify {@link
 * BridgeSessionListener} as a listener in the WEB-INF/web.xml for all portlets projects. Since ICEfaces 1.8 does not
 * utilize the "Bridge Request Scope" feature of the JSR 329 Portlet Bridge Spec, the methods in this class perform no
 * operation (no-op).
 *
 * @author  Neil Griffin
 */
public class BridgeSessionListener implements HttpSessionListener, ServletContextListener {

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// no-op
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// no-op
	}

	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		// no-op
	}

	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		// no-op
	}

}
