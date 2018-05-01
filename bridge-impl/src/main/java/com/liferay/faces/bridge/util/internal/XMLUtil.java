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
package com.liferay.faces.bridge.util.internal;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author  Kyle Stiemann
 */
public final class XMLUtil {

	//J-
	/**
	 * Escapes XML characters so that text can be safely written to the response. This method's functionality was
	 * copied from {@link com.liferay.portal.util.HtmlImpl#escape(java.lang.String)}
	 * (https://github.com/liferay/liferay-portal/blob/7.0.2-ga3/portal-impl/src/com/liferay/portal/util/HtmlImpl.java#L99-L178).
	 * The code was copied from 7.0.2-ga3 in order to avoid https://issues.liferay.com/browse/LPS-75183 (which was
	 * introduced in 7.0.3-ga4).
	 *
	 * This method follows recommendations from
	 * http://www.owasp.org/index.php/Cross_Site_Scripting#How_to_Protect_Yourself.
	 */
	public static String escapeXML(String text) {
		if (text == null) {
			return null;
		}

		if (text.length() == 0) {
			return "";
		}

		// Escape using XSS recommendations from
		// http://www.owasp.org/index.php/Cross_Site_Scripting
		// #How_to_Protect_Yourself

		StringBuilder sb = null;

		int lastReplacementIndex = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			String replacement = null;

			if (c == '<') {
				replacement = "&lt;";
			}
			else if (c == '>') {
				replacement = "&gt;";
			}
			else if (c == '&') {
				replacement = "&amp;";
			}
			else if (c == '"') {
				replacement = "&#34;";
			}
			else if (c == '\'') {
				replacement = "&#39;";
			}
			else if (c == '\u00bb') {
				replacement = "&#187;";
			}
			else if (c == '\u2013') {
				replacement = "&#x2013;";
			}
			else if (c == '\u2014') {
				replacement = "&#x2014;";
			}
			else if (c == '\u2028') {
				replacement = "&#x2028;";
			}
			else if (!_isValidXmlCharacter(c) ||
					 _isUnicodeCompatibilityCharacter(c)) {

				replacement = " ";
			}

			if (replacement != null) {
				if (sb == null) {
					sb = new StringBuilder();
				}

				if (i > lastReplacementIndex) {
					sb.append(text.substring(lastReplacementIndex, i));
				}

				sb.append(replacement);

				lastReplacementIndex = i + 1;
			}
		}

		if (sb == null) {
			return text;
		}

		if (lastReplacementIndex < text.length()) {
			sb.append(text.substring(lastReplacementIndex));
		}

		return sb.toString();
	}

	/**
	 * Copied from https://github.com/liferay/liferay-portal/blob/7.0.2-ga3/portal-impl/src/com/liferay/portal/util/HtmlImpl.java#L828-L837.
	 */
	private static boolean _isUnicodeCompatibilityCharacter(char c) {
		if (((c >= '\u007f') && (c <= '\u0084')) ||
			((c >= '\u0086') && (c <= '\u009f')) ||
			((c >= '\ufdd0') && (c <= '\ufdef'))) {

			return true;
		}

		return false;
	}

	/**
	 * Copied from https://github.com/liferay/liferay-portal/blob/7.0.2-ga3/portal-impl/src/com/liferay/portal/util/HtmlImpl.java#L839-L849.
	 */
	private static boolean _isValidXmlCharacter(char c) {
		if ((c == '\u0009') || (c == '\n') ||
			(c == '\r') || ((c >= '\u0020') && (c <= '\ud7ff')) ||
			((c >= '\ue000') && (c <= '\ufffd')) ||
			Character.isLowSurrogate(c) || Character.isHighSurrogate(c)) {

			return true;
		}

		return false;
	}
	//J+

	private XMLUtil() {
		throw new AssertionError();
	}

	public static String elementToString(Node element) {
		return elementToString(element, true);
	}

	public static String elementToString(Node element, boolean escapeAttributeValues) {

		StringBuilder buf = new StringBuilder();
		appendElementRecurse(buf, element, escapeAttributeValues);

		return buf.toString();
	}

	public static String nodeToString(Node node) {

		String textContent = node.getTextContent();
		StringBuilder buf = new StringBuilder();

		if (textContent != null) {

			short nodeType = node.getNodeType();

			switch (nodeType) {

			case Node.TEXT_NODE: {
				buf.append(textContent);

				break;
			}

			case Node.COMMENT_NODE: {
				buf.append("<!--");
				buf.append(textContent);
				buf.append("-->");

				break;
			}

			case Node.CDATA_SECTION_NODE: {
				buf.append("<![CDATA[");
				buf.append(textContent);
				buf.append("]]>");

				break;
			}

			case Node.ELEMENT_NODE: {
				throw new IllegalArgumentException("Call XMLUtil.elementToString() to obtain an element as a string.");
			}

			default: {
				throw new IllegalArgumentException("Node type [" + nodeType + "] not supported.");
			}
			}
		}

		return buf.toString();
	}

	private static void appendElementRecurse(StringBuilder buf, Node element, boolean escapeAttributeValues) {

		buf.append("<");

		String nodeName = element.getNodeName();
		buf.append(nodeName);

		NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {

			Node attribute = attributes.item(i);
			buf.append(" ");
			buf.append(attribute.getNodeName());
			buf.append("=\"");

			String nodeValue = attribute.getNodeValue();

			if (nodeValue != null) {

				if (escapeAttributeValues) {
					buf.append(escapeXML(nodeValue));
				}
				else {
					buf.append(nodeValue);
				}
			}

			buf.append("\"");
		}

		buf.append(">");

		if (!nodeName.equals("link")) {

			if (element.hasChildNodes()) {

				NodeList childNodes = element.getChildNodes();

				for (int i = 0; i < childNodes.getLength(); i++) {

					Node node = childNodes.item(i);
					short nodeType = node.getNodeType();

					if (nodeType == Node.ELEMENT_NODE) {
						appendElementRecurse(buf, node, escapeAttributeValues);
					}
					else {
						buf.append(nodeToString(node));
					}
				}
			}
			else {

				String textContent = element.getTextContent();

				if (textContent != null) {
					buf.append(textContent);
				}
			}

			buf.append("</");
			buf.append(nodeName);
			buf.append(">");
		}
	}
}
