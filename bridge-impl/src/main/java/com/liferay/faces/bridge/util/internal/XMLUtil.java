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
package com.liferay.faces.bridge.util.internal;

/**
 * @author  Kyle Stiemann
 */
public final class XMLUtil {

	private XMLUtil() {
		throw new AssertionError();
	}

	/**
	 * Escapes XML characters so that a URL can be safely written to the response. This method's functionality was
	 * copied from {@link com.liferay.portal.util.HtmlImpl#escape(java.lang.String)}
	 * (https://github.com/liferay/liferay-portal/blob/7.0.1-ga2/portal-impl/src/com/liferay/portal/util/HtmlImpl.java#L112-L120).
	 * This method follows recommendations from
	 * http://www.owasp.org/index.php/Cross_Site_Scripting#How_to_Protect_Yourself.
	 */
	public static String escapeXML(String url) {

		// "&" must be replaced before the others to avoid double escaping them.
		char[] tokens = new char[] { '&', '<', '>', '"', '\'', '\u00bb', '\u2013', '\u2014', '\u2028' };
		String[] replacements = new String[] {
				"&amp;", "&lt;", "&gt;", "&#034;", "&#039;", "&#187;", "&#x2013;", "&#x2014;", "&#x2028;"
			};

		for (int i = 0; i < tokens.length; i++) {

			String token = Character.toString(tokens[i]);
			url = url.replace(token, replacements[i]);
		}

		return url;
	}
}
