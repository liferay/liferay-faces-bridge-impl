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
package com.liferay.faces.bridge.tck.driver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.driver.PortalDriverFilter;


/**
 * This is a small class to work around an issue with the PlutoPortletDriverFilter in the pluto embedded plugin
 * environment for maven.
 */
public class PlutoPortalDriverFilter implements Filter {

	private final Filter _portalDriver = new PortalDriverFilter();
	private FilterConfig _config;

	public PlutoPortalDriverFilter() {
	}

	public void destroy() {
		_portalDriver.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
		ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String[] portletId = request.getParameterValues("portlet");

		if (portletId != null) {
			req.getSession().setAttribute("org_apache_pluto_embedded_portletIds", portletId);
		}

		request.setAttribute("org_apache_pluto_embedded_portletIds",
			_encodePortletIds((String[]) req.getSession().getAttribute("org_apache_pluto_embedded_portletIds")));
		_portalDriver.doFilter(request, response, chain);

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		_portalDriver.init(filterConfig);
		_config = filterConfig;
	}

	private String[] _encodePortletIds(String... rawIds) {
		String[] ids = new String[rawIds.length];
		String contextPath = _config.getServletContext().getContextPath();
		StringBuffer tempId = new StringBuffer();

		if (!contextPath.startsWith("/")) {
			tempId.append("/");
		}

		for (int i = 0; i < rawIds.length; i++) {
			ids[i] = contextPath + "." + rawIds[i] + "!";
		}

		return ids;
	}
}
