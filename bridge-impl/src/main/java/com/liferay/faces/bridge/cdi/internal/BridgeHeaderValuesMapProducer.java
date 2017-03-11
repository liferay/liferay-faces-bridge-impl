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
package com.liferay.faces.bridge.cdi.internal;

import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.annotation.HeaderValuesMap;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;


/**
 * Alternative to com.sun.faces.cdi.HeaderValuesMapProducer
 */
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION + 10)
public class BridgeHeaderValuesMapProducer {

	@HeaderValuesMap
	@Named(value = "headerValues")
	@PortletRequestScoped
	@Produces
	public Map<String, String[]> getHeaderValuesMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderValuesMap();
	}
}
