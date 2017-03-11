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
package com.liferay.faces.bridge.context.url;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.faces.BridgeConfig;

import com.liferay.faces.bridge.internal.BaseURLNonEncodedImpl;
import com.liferay.faces.bridge.internal.BridgeURLBase;


/**
 * @author  Neil Griffin
 */
public class BridgeURLMockImpl extends BridgeURLBase {

	public BridgeURLMockImpl(String uri, String contextPath, String namespace, String currentViewId,
		BridgeConfig bridgeConfig) throws URISyntaxException {
		super(uri, contextPath, namespace, currentViewId, bridgeConfig);
	}

	@Override
	public BaseURL toBaseURL(FacesContext facesContext) throws MalformedURLException {
		return new BaseURLNonEncodedImpl(bridgeURI);
	}
}
