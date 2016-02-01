/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.bridge.application.internal;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import javax.faces.application.Resource;

/**
 * @author  Kyle Stiemann
 */
public class ResourceRichFacesPackedJSImpl extends FilteredResourceBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRichFacesPackedJSImpl.class);

	// Private Members
	private Resource wrappedResource;

	public ResourceRichFacesPackedJSImpl(Resource wrappedResource) {

		// Since we cannot extend two classes, we wrap the default ResourceRichFacesImpl to ensure that all RichFaces resource implementations include the base functionality.
		this.wrappedResource = new ResourceRichFacesImpl(wrappedResource);
	}

	@Override
	protected String filter(String javaScriptText) {

		// Replace the URL used by rich:fileUpload for forum submission.
		// http://issues.liferay.com/browse/FACES-1234
		// https://issues.jboss.org/browse/RF-12273
		String token = "this.form.attr(\"action\", originalAction + delimiter + UID + \"=\" + this.loadableItem.uid);";
		int pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found first token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append(
				"this.form.attr(\"action\", this.form.children(\"input[name='javax.faces.encodedURL']\").val() + delimiter + UID + \"=\" + this.loadableItem.uid);");
			buf.append(javaScriptText.substring(pos + token.length() + 1));
			javaScriptText = buf.toString();
		}

		// Fix JavaScript error "TypeError: jQuery.atmosphere is undefined" by inserting checks for undefined variable.
		// http://issues.liferay.com/browse/FACES-1532
		token = "if (jQuery.atmosphere.requests.length > 0) {";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found second token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!jQuery.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		// jQuery.atmosphere.unsubscribe();
		token = "jQuery.atmosphere.unsubscribe();";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found third token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!jQuery.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		token = "$.atmosphere.unsubscribe();";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found fourth token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!$.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		return javaScriptText;
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
	}
}
