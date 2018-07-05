/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import java.io.IOException;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletException;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.HeaderFilter;
import javax.portlet.filter.HeaderFilterChain;
import javax.portlet.filter.HeaderResponseWrapper;


/**
 * @author  Neil Griffin
 */
public class NullContentTypePortletFilter implements HeaderFilter {

	public void destroy() {
	}

	public void doFilter(HeaderRequest headerRequest, HeaderResponse headerResponse, HeaderFilterChain headerFilterChain)
		throws IOException, PortletException {
		headerResponse = new NullContentTypeHeaderResponse(headerResponse);
		headerFilterChain.doFilter(headerRequest, headerResponse);
	}

	public void init(FilterConfig filterConfig) throws PortletException {
	}

	private static class NullContentTypeHeaderResponse extends
		HeaderResponseWrapper {

		NullContentTypeHeaderResponse(HeaderResponse headerResponse) {
			super(headerResponse);
		}

		@Override
		public String getContentType() {

			// Return null in order to cause the bridge's implementation of ExternalContext.getResponseContentType()
			// to consult HeaderRequest.getContentType()
			return null;
		}
	}
}
