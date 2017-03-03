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

import javax.faces.application.Resource;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceRichFacesPackedJSImpl extends FilteredResourceBase {

	// Private Constants
	private static final Product JSF = ProductFactory.getProduct(Product.Name.JSF);
	private static final int JSF_MAJOR_VERSION = JSF.getMajorVersion();
	private static final int JSF_MINOR_VERSION = JSF.getMinorVersion();

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRichFacesPackedJSImpl.class);

	// Private Members
	private Resource wrappedResource;

	public ResourceRichFacesPackedJSImpl(Resource wrappedResource) {

		// Since we cannot extend two classes, we wrap the default ResourceRichFacesImpl to ensure that all RichFaces
		// resource implementations include the base functionality.
		this.wrappedResource = new ResourceRichFacesImpl(wrappedResource);
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
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
				"this.form.attr(\"action\", this.form.children(\"input[name&='javax.faces.encodedURL']\").val() + delimiter + UID + \"=\" + this.loadableItem.uid);");
			buf.append(javaScriptText.substring(pos + token.length() + 1));
			javaScriptText = buf.toString();
		}

		javaScriptText = replaceToken(javaScriptText, "this.fileUpload.form.attr(\"action\")",
				"this.fileUpload.form.children(\"input[name$='javax.faces.encodedURL']\").val()");

		// Fix JavaScript error "TypeError: jQuery.atmosphere is undefined" by inserting checks for undefined variable.
		// http://issues.liferay.com/browse/FACES-1532
		javaScriptText = prependToken(javaScriptText, "if (jQuery.atmosphere.requests.length > 0) {",
				"if (!jQuery.atmosphere) { return; }; ");

		javaScriptText = prependToken(javaScriptText, "jQuery.atmosphere.unsubscribe();",
				"if (!jQuery.atmosphere) { return; }; ");

		javaScriptText = prependToken(javaScriptText, "$.atmosphere.unsubscribe();",
				"if (!$.atmosphere) { return; }; ");

		// JSF 2.3 incompatibility due to non-namespaced request parameters.
		// http://issues.liferay.com/browse/FACES-3014
		token = "this.fileUpload.form.find(\"input[name='javax.faces.ViewState']\").val();";
		pos = javaScriptText.indexOf(token);

		if (((JSF_MAJOR_VERSION > 2) || ((JSF_MAJOR_VERSION == 2) && (JSF_MINOR_VERSION >= 3))) && (pos > 0)) {

			logger.debug("Found javax.faces.ViewState selector in packed.js");

			javaScriptText = replaceToken(javaScriptText, token,
					"this.fileUpload.form.find(\"input[name$='javax.faces.ViewState']\"), parameterPrefix=viewState.attr('name').substring(0,viewState.attr('name').indexOf('javax.faces.ViewState'));");

			javaScriptText = replaceToken(javaScriptText, "formData.append(\"javax.faces.ViewState\"",
					"formData.append(viewState.attr('name')");

			javaScriptText = replaceToken(javaScriptText, "javax.faces.partial.ajax=",
					"\" + parameterPrefix + \"javax.faces.partial.ajax=");

			javaScriptText = replaceToken(javaScriptText, "javax.faces.partial.execute=",
					"\" + parameterPrefix + \"javax.faces.partial.execute=");

			javaScriptText = replaceToken(javaScriptText, "javax.faces.ViewState=",
					"\" + parameterPrefix + \"javax.faces.ViewState=");

			javaScriptText = replaceToken(javaScriptText, "javax.faces.source=",
					"\" + parameterPrefix + \"javax.faces.source=");

			javaScriptText = replaceToken(javaScriptText, "org.richfaces.ajax.component=",
					"\" + parameterPrefix + \"org.richfaces.ajax.component=");

			javaScriptText = replaceToken(javaScriptText, "delimiter + UID", "delimiter + parameterPrefix + UID");

			javaScriptText = replaceToken(javaScriptText, "delimiter+UID", "delimiter+parameterPrefix+UID");

			javaScriptText = javaScriptText.replace("viewState)", "viewState.val())");
		}

		return javaScriptText;
	}

	private String prependToken(String javaScriptText, String token, String prependText) {

		int pos = javaScriptText.indexOf(token);

		if (pos > 0) {

			logger.debug("Found token=[{0}] in packed.js prependText=[{1}]", token, prependText);

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append(prependText);
			buf.append(javaScriptText.substring(pos));

			javaScriptText = buf.toString();
		}

		return javaScriptText;
	}

	private String replaceToken(String javaScriptText, String token, String replacementText) {

		int pos = javaScriptText.indexOf(token);

		if (pos > 0) {

			logger.debug("Found token=[{0}] in packed.js replacementText=[{1}]", token, replacementText);

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append(replacementText);
			buf.append(javaScriptText.substring(pos + token.length()));

			javaScriptText = buf.toString();
		}

		return javaScriptText;
	}
}
