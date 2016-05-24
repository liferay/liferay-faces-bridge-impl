/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.faces.FacesWrapper;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;


/**
 * @author  Neil Griffin
 */
public abstract class NavigationCaseWrapper extends NavigationCase implements FacesWrapper<NavigationCase> {

	public NavigationCaseWrapper() {
		super((String) null, (String) null, (String) null, (String) null, (String) null,
			(Map<String, List<String>>) null, false, false);
	}

	public abstract NavigationCase getWrapped();

	@Override
	public boolean equals(Object o) {
		return getWrapped().equals(o);
	}

	@Override
	public URL getActionURL(FacesContext context) throws MalformedURLException {
		return getWrapped().getActionURL(context);
	}

	@Override
	public URL getBookmarkableURL(FacesContext context) throws MalformedURLException {
		return getWrapped().getBookmarkableURL(context);
	}

	@Override
	public Boolean getCondition(FacesContext context) {
		return getWrapped().getCondition(context);
	}

	@Override
	public String getFromAction() {
		return getWrapped().getFromAction();
	}

	@Override
	public String getFromOutcome() {
		return getWrapped().getFromOutcome();
	}

	@Override
	public String getFromViewId() {
		return getWrapped().getFromViewId();
	}

	@Override
	public Map<String, List<String>> getParameters() {
		return getWrapped().getParameters();
	}

	@Override
	public URL getRedirectURL(FacesContext context) throws MalformedURLException {
		return getWrapped().getRedirectURL(context);
	}

	@Override
	public URL getResourceURL(FacesContext context) throws MalformedURLException {
		return getWrapped().getResourceURL(context);
	}

	@Override
	public String getToViewId(FacesContext context) {
		return getWrapped().getToViewId(context);
	}

	@Override
	public boolean hasCondition() {
		return getWrapped().hasCondition();
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	@Override
	public boolean isIncludeViewParams() {
		return getWrapped().isIncludeViewParams();
	}

	@Override
	public boolean isRedirect() {
		return getWrapped().isRedirect();
	}

	@Override
	public String toString() {
		return getWrapped().toString();
	}

}
