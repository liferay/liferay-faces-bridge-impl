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
package com.liferay.faces.bridge.context.internal;

import javax.portlet.faces.Bridge;


/**
 * This interface represents a JSF View from the perspective of how it is mapped to the {@link
 * javax.faces.webapp.FacesServlet}.
 *
 * @author  Neil Griffin
 */
public interface FacesView {

	/**
	 * Determines the extension (such as *.faces) used by the extension-mapped servlet-mapping.
	 *
	 * @return  If the view is extension-mapped, returns the extension. Otherwise, returns <code>null</code>.
	 */
	String getExtension();

	/**
	 * Returns the query-string part of the to-view-id of the last navigation-rule that fired, or the query-string part
	 * of the {@link Bridge#VIEW_ID} request attribute. Specifically, it returns the query-string which may contain
	 * navigation parameters such as {@link Bridge#PORTLET_MODE_PARAMETER}, {@link
	 * Bridge#NONFACES_TARGET_PATH_PARAMETER}, {@link Bridge#PORTLET_SECURE_PARAMETER}, {@link
	 * Bridge#PORTLET_WINDOWSTATE_PARAMETER}, {@link Bridge#FACES_VIEW_ID_PARAMETER}, or {@link
	 * Bridge#FACES_VIEW_PATH_PARAMETER}. Note that "navigation" does not refer to JSF navigation-rules, but rather
	 * changes in {@link javax.portlet.PortletMode}, {@link javax.portlet.WindowState}, etc. It could also contain
	 * user-define name=value parameters specified in a {@link Bridge#VIEW_ID} request attribute.
	 */
	String getQueryString();

	/**
	 * Determines the servlet-path (such as /faces/views) used by the path-mapped servlet-mapping.
	 *
	 * @return  If the view is path-mapped, returns the path. Otherwise, returns <code>null</code>.
	 */
	String getServletPath();

	/**
	 * Returns the viewId associated with this view.
	 */
	String getViewId();

	/**
	 * Flag indicating whether or not the view is mapped to the {@link javax.faces.webapp.FacesServlet} via
	 * extension-mapping (such as *.faces) or some other extension.
	 *
	 * @return  <code>true</code> if extension-mapped, otherwise <code>false</code>.
	 */
	boolean isExtensionMapped();

	/**
	 * Flag indicating whether or not the view is mapped to the {@link javax.faces.webapp.FacesServlet} via path-mapping
	 * (such as /faces/views/*) or some other extension.
	 *
	 * @return  <code>true</code> if extension-mapped, otherwise <code>false</code>.
	 */
	boolean isPathMapped();

}
