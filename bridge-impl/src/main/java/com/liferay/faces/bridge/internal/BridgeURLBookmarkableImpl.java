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
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.PortletURL;
import javax.portlet.faces.BridgeConfig;


/**
 * The JavaDoc for {@link javax.faces.application.ViewHandler#getBookmarkableURL(FacesContext, String, Map, boolean)}
 * requires that the return value be suitable to be used as the target of a link. This means that the renderer for
 * h:link must be able to use the return value for the href attribute of an anchor element, and that the renderer for
 * h:button be able to use the return value for the onclick attribute of a button element. Both
 * com.sun.faces.application.view.MultiViewHandler and org.apache.myfaces.application.ViewHandlerImpl call
 * ExternalContext.encodeActionURL(ExternalContext.encodeBookmarkableURL(ViewHandler.getActionURL(viewId))) in order to
 * satisfy this requirement. As a result, the Bridge's implementation of {@link
 * javax.faces.context.ExternalContext#encodeBookmarkableURL(String, Map)} must not attempt to return the result of
 * {@link PortletURL#toString()}. Instead, it must simply return a non-encoded String that appends the specified map of
 * bookmark parameters to the query-string of the specified URL. However, since the Bridge's version of {@link
 * javax.faces.context.ExternalContext#encodeActionURL(String url)} needs to detect whether or not the specified URL is
 * bookmarkable, {@link BridgeExt#BOOKMARKABLE_PARAMETER} must also be present in the query-string with a value of
 * "true".
 *
 * @author  Neil Griffin
 */
public class BridgeURLBookmarkableImpl extends BridgeURLBase {

	public BridgeURLBookmarkableImpl(String uri, String contextPath, String namespace, String currentViewId,
		Map<String, List<String>> bookmarkParameters, BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, currentViewId, bridgeConfig);

		// Since the Bridge's version of ExternalContext.encodeActionURL(String url) needs to detect whether or not the
		// specified URL is bookmarkable, ensure that "_jsfBridgeBookmarkable=true" appears in the query-string.
		bridgeURI.setParameter(BridgeExt.BOOKMARKABLE_PARAMETER, "true");

		if ((bridgeURI != null) && (bridgeURI.toString() != null) && (bookmarkParameters != null)) {

			Set<Map.Entry<String, List<String>>> entrySet = bookmarkParameters.entrySet();

			for (Map.Entry<String, List<String>> mapEntry : entrySet) {

				List<String> valueList = mapEntry.getValue();
				String[] values = valueList.toArray(new String[valueList.size()]);
				bridgeURI.setParameter(mapEntry.getKey(), values);
			}
		}
	}

	@Override
	public BaseURL toBaseURL(FacesContext facesContext) throws MalformedURLException {
		return new BaseURLNonEncodedImpl(bridgeURI);
	}
}
