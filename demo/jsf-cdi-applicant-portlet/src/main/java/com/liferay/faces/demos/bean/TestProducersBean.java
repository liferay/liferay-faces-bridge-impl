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
package com.liferay.faces.demos.bean;

import java.util.Map;

import javax.enterprise.context.RequestScoped;

import javax.faces.annotation.FacesConfig;
import javax.faces.annotation.HeaderMap;
import javax.faces.annotation.HeaderValuesMap;
import javax.faces.annotation.InitParameterMap;
import javax.faces.annotation.RequestCookieMap;
import javax.faces.annotation.RequestMap;
import javax.faces.annotation.RequestParameterMap;
import javax.faces.annotation.RequestParameterValuesMap;
import javax.faces.annotation.SessionMap;
import javax.faces.annotation.ViewMap;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author  Neil Griffin
 */
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@RequestScoped
public class TestProducersBean {

	@Inject
	private ExternalContext externalContext;

	@Inject
	private FacesContext facesContext;

	@Inject
	private Flash flash;

	@HeaderMap
	@Inject
	private Map<String, String> headerMap;

	@HeaderValuesMap
	@Inject
	private Map<String, String[]> headerValuesMap;

	@InitParameterMap
	@Inject
	private Map<String, String> initParameterMap;

	@Inject
	@RequestCookieMap
	private Map<String, Object> requestCookieMap;

	@Inject
	@RequestMap
	private Map<String, Object> requestMap;

	@Inject
	@RequestParameterMap
	private Map<String, String> requestParameterMap;

	@Inject
	@RequestParameterValuesMap
	private Map<String, String[]> requestParameterValuesMap;

	@Inject
	private ResourceHandler resourceHandler;

	@Inject
	@SessionMap
	private Map<String, Object> sessionMap;

	@Inject
	@ViewMap
	private Map<String, Object> viewMap;

	@Inject
	private UIViewRoot view;

	// Private Data Members
	private String results;

	public String getResults() {

		if (results == null) {

			results = "";

			if (externalContext == null) {
				results += "Failure: externalContext == null";
			}

			if (facesContext == null) {
				results += " | Failure: facesContext == null";
			}

			if (flash == null) {
				results += " | Failure: flash == null";
			}

			if (headerMap == null) {
				results += " | Failure: headerMap == null";
			}

			if (headerValuesMap == null) {
				results += " | Failure: headerValuesMap == null";
			}

			if (initParameterMap == null) {
				results += " | Failure: initParameterMap == null";
			}

			if (requestCookieMap == null) {
				results += " | Failure: requestCookieMap == null";
			}

			if (requestMap == null) {
				results += " | Failure: requestMap == null";
			}

			if (requestParameterMap == null) {
				results += " | Failure: requestParameterMap == null";
			}

			if (requestParameterValuesMap == null) {
				results += " | Failure: requestParameterValuesMap == null";
			}

			if (resourceHandler == null) {
				results += " | Failure: resourceHandler == null";
			}

			if (sessionMap == null) {
				results += " | Failure: sessionMap == null";
			}

			if (view == null) {
				results += " | Failure: view == null";
			}

			if (results.length() == 0) {
				results = "Success";
			}
		}

		return results;
	}
}
