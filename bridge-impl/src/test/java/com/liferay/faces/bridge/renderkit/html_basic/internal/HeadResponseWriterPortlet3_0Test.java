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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public class HeadResponseWriterPortlet3_0Test {

	/**
	 * Verify that the {@link HeadResponseWriterLiferayImpl} correctly handles nested elements (for example in a script
	 * template). For more details see <a href="https://issues.liferay.com/browse/FACES-3231">
	 * https://issues.liferay.com/browse/FACES-3231</a>.
	 *
	 * @throws  IOException
	 */
	@Test
	public void testFACES_3231() throws IOException {

		HeadResponseWriterLiferayMockImpl headResponseWriterLiferayMockImpl = new HeadResponseWriterLiferayMockImpl(
				new ResponseWriterMockImpl(new StringWriter()));
		headResponseWriterLiferayMockImpl.startElement("script", null);
		headResponseWriterLiferayMockImpl.writeAttribute("id", "script:template", null);
		headResponseWriterLiferayMockImpl.writeAttribute("type", "data/template", null);
		headResponseWriterLiferayMockImpl.writeAttribute("data-senna-track", "temporary", null);
		headResponseWriterLiferayMockImpl.write("Template Text 1");
		headResponseWriterLiferayMockImpl.startElement("div", null);
		headResponseWriterLiferayMockImpl.writeAttribute("class", "template:div", null);
		headResponseWriterLiferayMockImpl.write("Div Text 1");
		headResponseWriterLiferayMockImpl.startElement("a", null);
		headResponseWriterLiferayMockImpl.writeAttribute("href", "http://liferay.com", null);
		headResponseWriterLiferayMockImpl.write("liferay");
		headResponseWriterLiferayMockImpl.endElement("a");
		headResponseWriterLiferayMockImpl.write("Div Text 2");
		headResponseWriterLiferayMockImpl.endElement("div");
		headResponseWriterLiferayMockImpl.write("Template Text 2");
		headResponseWriterLiferayMockImpl.startElement("span", null);
		headResponseWriterLiferayMockImpl.write("Span Text");
		headResponseWriterLiferayMockImpl.endElement("span");
		headResponseWriterLiferayMockImpl.endElement("script");

		String elementString = headResponseWriterLiferayMockImpl.getLastNodeAsString();

		//J-
		String expectedElementStringRegex =
			"<script\\s+id=\"script:template\"\\s+type=\"data/template\"\\s+data-senna-track=\"temporary\"\\s*>" +
				"\\s*Template Text 1\\s*" +
				"<div\\s+class=\"template:div\">" +
					"\\s*Div Text 1?" +
					"<a\\s+href=\"http://liferay[.]com\">liferay</a>" +
					"\\s*Div Text 2\\s*" +
				"</div>" +
				"\\s*Template Text 2\\s*" +
				"<span>\\s*Span Text\\s*</span>" +
			"</script>";
		//J+
		Assert.assertTrue("Nested head elements were not written correctly.",
			elementString.matches(expectedElementStringRegex));
	}
}
