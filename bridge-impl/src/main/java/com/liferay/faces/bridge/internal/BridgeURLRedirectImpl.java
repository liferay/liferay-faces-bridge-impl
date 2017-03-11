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
package com.liferay.faces.bridge.internal;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.PortletURL;
import javax.portlet.faces.BridgeConfig;


/**
 * The JavaDoc for {@link javax.faces.application.ViewHandler#getRedirectURL(FacesContext, String, Map, boolean)}
 * requires that the {@link javax.faces.application.NavigationHandler} must be able to call
 * ExternalContext.redirect(ViewHandler.getRedirectURL(...)) in order to support the ability to redirect in a
 * navigation-rule. Both com.sun.faces.application.view.MultiViewHandler and
 * org.apache.myfaces.application.ViewHandlerImpl call
 * ExternalContext.encodeActionURL(ExternalContext.encodeRedirectURL(ViewHandler.getActionURL(viewId))) in order to
 * satisfy this requirement. As a result, the Bridge's implementation of {@link
 * javax.faces.context.ExternalContext#encodeRedirectURL(String url, Map parameters)} must not attempt to return the
 * result of {@link PortletURL#toString()}. Instead, it must simply return a non-encoded String that appends the
 * specified map of redirect parameters to the query-string of the specified URL. However, since the Bridge's version of
 * {@link javax.faces.context.ExternalContext#encodeActionURL(String url)} needs to detect whether or not the specified
 * URL is for a redirect, {@link BridgeExt#REDIRECT_PARAMETER} must also be present in the query-string with a value of
 * "true".
 *
 * @author  Neil Griffin
 */
public class BridgeURLRedirectImpl extends BridgeURLBase {

	public BridgeURLRedirectImpl(String uri, String contextPath, String namespace,
		Map<String, List<String>> redirectParameters, BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, null, bridgeConfig);

		// Since the Bridge's version of ExternalContext.encodeActionURL(String url) needs to detect whether or not the
		// specified URL is for a redirect, ensure that "_jsfBridgeRedirect=true" appears in the query-string.
		bridgeURI.setParameter(BridgeExt.REDIRECT_PARAMETER, "true");

		// Append the specified redirect parameters to the query-string.
		if (redirectParameters != null) {

			Set<Entry<String, List<String>>> entrySet = redirectParameters.entrySet();

			for (Entry<String, List<String>> mapEntry : entrySet) {

				List<String> valueList = mapEntry.getValue();
				String[] values = valueList.toArray(new String[valueList.size()]);
				bridgeURI.setParameter(mapEntry.getKey(), values);
			}
		}
	}

	// Java 1.6+ @Override
	@Override
	public BaseURL toBaseURL(FacesContext facesContext) throws MalformedURLException {

		// TCK TestPage039 (requestNoScopeOnRedirectTest)
		return new BaseURLNonEncodedImpl(bridgeURI);
	}
}
