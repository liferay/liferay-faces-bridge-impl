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
public class HeadResponseWriterFACES_3220Test {

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
	public void testHeadResponseWriterFACES_3220() throws IOException {

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
		assertStringContains(AtLocation.START, elementString, "<link");

		for (Attribute attribute : ATTRIBUTES) {
			assertStringContains(elementString, attribute.toString());
		}

		assertStringContains(AtLocation.END, elementString, ">");
	}

	private void assertStringContains(String actualContainerString, String expectedContainedString) {
		assertStringContains(AtLocation.MIDDLE, actualContainerString, expectedContainedString);
	}

	private void assertStringContains(AtLocation atLocation, String actualContainerString,
		String expectedContainedString) {

		String doesNotContainMessage;

		switch (atLocation) {

		case START: {
			doesNotContainMessage = "start with";

			break;
		}

		case END: {
			doesNotContainMessage = "end with";

			break;
		}

		default: {
			doesNotContainMessage = "contain";

			break;
		}
		}

		doesNotContainMessage = "String \"" + actualContainerString + "\" does not " + doesNotContainMessage +
			" string \"" + expectedContainedString + "\"";

		boolean stringContains;

		switch (atLocation) {

		case START: {
			stringContains = actualContainerString.startsWith(expectedContainedString);

			break;
		}

		case END: {
			stringContains = actualContainerString.endsWith(expectedContainedString);

			break;
		}

		default: {
			stringContains = actualContainerString.contains(expectedContainedString);

			break;
		}
		}

		Assert.assertTrue(doesNotContainMessage, stringContains);
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
