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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public class HeadResponseWriterPortlet2_0Tests {

	// Private Constants
	private static final Attribute[] ATTRIBUTES = new Attribute[] {
			new Attribute("rel", "stylesheet"), new Attribute("type", "text/css"),
			new Attribute("href", "http://liferayfaces.org/test?param1=1&param2=2&param3=3", true)
		};

	private enum AtLocation {
		START, MIDDLE, END;
	}

	/**
	 * Verify that the {@link HeadResponseWriterCompatImpl} does not escape any characters thus allowing the portlet
	 * container to perform escaping as necessary. For more details see <a
	 * href="https://issues.liferay.com/browse/FACES-3220">https://issues.liferay.com/browse/FACES-3220</a>.
	 *
	 * @throws  IOException
	 */
	@Test
	public void testFACES_3220() throws IOException {

		RenderResponseMockImpl renderResponseMockImpl = new RenderResponseMockImpl();
		ResponseWriter responseWriter = new HeadResponseWriterCompatImpl(new ResponseWriterMockImpl(),
				renderResponseMockImpl);
		responseWriter.startElement("link", null);

		for (Attribute attribute : ATTRIBUTES) {

			if (attribute.uri) {
				responseWriter.writeURIAttribute(attribute.name, attribute.value, null);
			}
			else {
				responseWriter.writeAttribute(attribute.name, attribute.value, null);
			}
		}

		responseWriter.endElement("link");

		String elementString = renderResponseMockImpl.getLastElementPropertyAsString();
		String doesNotContainMessageTemplate =
			"FACES-3220 regression test failed! String \"{0}\" does not {1} string \"{2}\".";
		Assert.assertTrue(getMessage(doesNotContainMessageTemplate, elementString, "start with", "<link"),
			elementString.startsWith("<link"));

		for (Attribute attribute : ATTRIBUTES) {
			Assert.assertTrue(getMessage(doesNotContainMessageTemplate, elementString, "contain", attribute.toString()),
				elementString.contains(attribute.toString()));
		}

		Assert.assertTrue(getMessage(doesNotContainMessageTemplate, elementString, "end with", ">"),
			elementString.endsWith(">"));
	}

	/**
	 * Verify that the {@link HeadResponseWriterCompatImpl} correctly handles nested elements (for example in a script
	 * template). For more details see <a href="https://issues.liferay.com/browse/FACES-3231">
	 * https://issues.liferay.com/browse/FACES-3231</a>.
	 *
	 * @throws  IOException
	 */
	@Test
	public void testFACES_3231() throws IOException {

		RenderResponseMockImpl renderResponseMockImpl = new RenderResponseMockImpl();
		ResponseWriter responseWriter = new HeadResponseWriterCompatImpl(new ResponseWriterMockImpl(),
				renderResponseMockImpl);
		responseWriter.startElement("script", null);
		responseWriter.writeAttribute("id", "script:template", null);
		responseWriter.writeAttribute("type", "data/template", null);
		responseWriter.write("Template Text 1");
		responseWriter.startElement("div", null);
		responseWriter.writeAttribute("class", "template:div", null);
		responseWriter.write("Div Text 1");
		responseWriter.startElement("a", null);
		responseWriter.writeAttribute("href", "http://liferay.com", null);
		responseWriter.write("liferay");
		responseWriter.endElement("a");
		responseWriter.write("Div Text 2");
		responseWriter.endElement("div");
		responseWriter.write("Template Text 2");
		responseWriter.startElement("span", null);
		responseWriter.write("Span Text");
		responseWriter.endElement("span");
		responseWriter.endElement("script");

		String elementString = renderResponseMockImpl.getLastElementPropertyAsString();

		//J-
		String expectedElementStringRegex =
			"<script(\\s+id=\"script:template\"|\\s+type=\"data/template\"){2}\\s*>" +
				"(\\s*Template Text 1\\s*)?" +
				"<div\\s+class=\"template:div\">" +
					"(\\s*Div Text 1)?" +
					"<a\\s+href=\"http://liferay[.]com\">liferay</a>" +
					"(\\s*Div Text 2\\s*)?" +
				"</div>" +
				"(\\s*Template Text 2\\s*)?" +
				"<span>\\s*Span Text\\s*</span>" +
			"</script>";
		//J+
		Assert.assertTrue("Nested head elements were not written correctly.",
			elementString.matches(expectedElementStringRegex));
	}

	private String getMessage(String message, String... arguements) {

		for (int i = 0; i < arguements.length; i++) {
			message = message.replace("{" + i + "}", arguements[i]);
		}

		return message;
	}

	private static final class Attribute {

		// Private Data Members
		public final String name;
		public final String value;
		public final boolean uri;

		public Attribute(String name, String value) {
			this(name, value, false);
		}

		public Attribute(String name, String value, boolean uriAttribute) {

			this.name = name;
			this.value = value;
			this.uri = uriAttribute;
		}

		@Override
		public String toString() {
			return name + "=\"" + value + "\"";
		}
	}
}
