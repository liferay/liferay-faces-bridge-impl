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
package com.liferay.faces.demos.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.service.CustomerService;
import com.liferay.faces.demos.util.PDFUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class CustomerExportResource extends Resource {

	// Public Constants
	public static final String CONTENT_TYPE = "application/pdf";
	public static final String RESOURCE_NAME = "export";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CustomerExportResource.class);

	// Private Constants
	private static final String PARAM_NAME_CUSTOMER_ID = "customerId";

	// Private Data Members
	private Customer customer;
	private String requestPath;

	public CustomerExportResource() {
		setLibraryName(CustomerResourceHandler.LIBRARY_NAME);
		setResourceName(RESOURCE_NAME);
		setContentType(CONTENT_TYPE);
	}

	public CustomerExportResource(Customer customer) {
		this();
		this.customer = customer;
	}

	@Override
	public InputStream getInputStream() throws IOException {

		byte[] byteArray = new byte[] {};

		try {

			// Convert the fragment into a full HTML document, and then to a PDF.
			StringBuilder buf = new StringBuilder();
			buf.append("<table>");
			buf.append("<thead>");
			buf.append("<tr>");
			buf.append("<th>First Name</th>");
			buf.append("<th>Last Name</th>");
			buf.append("</tr>");
			buf.append("</thead>");
			buf.append("<tbody>");
			buf.append("<tr>");
			buf.append("<td>");
			buf.append(getCustomer().getFirstName());
			buf.append("</td>");
			buf.append("<td>");
			buf.append(getCustomer().getLastName());
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</tbody>");
			buf.append("</table>");

			String htmlFragment = buf.toString();

			// Encode any ampersand characters to ensure that the PDF conversion will work.
			htmlFragment = htmlFragment.replaceAll("[&]", "&amp;");

			String pdfTile = "Customers";
			String description = pdfTile;
			String author = "Author Name";
			htmlFragment = htmlFragment.replaceAll("[\\n]", " ");
			htmlFragment = htmlFragment.replaceAll("[\\t]", " ");
			byteArray = PDFUtil.TXT2PDF(htmlFragment, null, pdfTile, description, author);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return new ByteArrayInputStream(byteArray);
	}

	@Override
	public String getRequestPath() {

		if (requestPath == null) {
			StringBuilder buf = new StringBuilder();
			buf.append(ResourceHandler.RESOURCE_IDENTIFIER);
			buf.append("/");
			buf.append(getResourceName());
			buf.append("?ln=");
			buf.append(getLibraryName());
			buf.append("&");
			buf.append(PARAM_NAME_CUSTOMER_ID);
			buf.append("=");
			buf.append(getCustomer().getCustomerId());
			requestPath = buf.toString();
		}

		return requestPath;
	}

	@Override
	public Map<String, String> getResponseHeaders() {
		Map<String, String> responseHeaders = new HashMap<String, String>();
		String fileName = customer.getLastName() + "-" + customer.getFirstName() + ".pdf";
		responseHeaders.put("Content-Disposition", "attachment; filename=" + fileName + ";");

		return responseHeaders;
	}

	@Override
	public URL getURL() {
		return null;
	}

	@Override
	public boolean userAgentNeedsUpdate(FacesContext context) {

		// Since this is a list that can potentially change dynamically, always return true.
		return true;
	}

	protected Customer getCustomer() {

		if (customer == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String customerId = facesContext.getExternalContext().getRequestParameterMap().get(PARAM_NAME_CUSTOMER_ID);
			String elExpression = "customerService";
			CustomerService customerService = (CustomerService) facesContext.getApplication().getELResolver().getValue(
					facesContext.getELContext(), null, elExpression);

			try {
				customer = customerService.getCustomer(Long.parseLong(customerId));
			}
			catch (NumberFormatException e) {
				logger.error(e);
			}
		}

		return customer;
	}
}
