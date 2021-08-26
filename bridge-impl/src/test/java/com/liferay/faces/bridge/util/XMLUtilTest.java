/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.util;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.util.internal.XMLUtil;


/**
 * @author  Kyle Stiemann
 */
public class XMLUtilTest {

	@Test
	public void testEscapeXML() {

		Assert.assertEquals("&amp;", XMLUtil.escapeXML("&"));
		Assert.assertEquals("&lt;", XMLUtil.escapeXML("<"));
		Assert.assertEquals("&gt;", XMLUtil.escapeXML(">"));
		Assert.assertEquals("&#34;", XMLUtil.escapeXML("\""));
		Assert.assertEquals("&#39;", XMLUtil.escapeXML("'"));
		Assert.assertEquals("&#187;", XMLUtil.escapeXML("\u00bb"));
		Assert.assertEquals("&#x2013;", XMLUtil.escapeXML("\u2013"));
		Assert.assertEquals("&#x2014;", XMLUtil.escapeXML("\u2014"));
		Assert.assertEquals("&#x2028;", XMLUtil.escapeXML("\u2028"));

		Assert.assertEquals("http://www.liferay.com/hello.world?foo=bar&amp;bar=baz&amp;baz=foo",
			XMLUtil.escapeXML("http://www.liferay.com/hello.world?foo=bar&bar=baz&baz=foo"));
		Assert.assertEquals(
			"http://www.liferay.com/hello.world?foo=&lt;script&gt;&amp;bar=&#34;baz&#34;&amp;baz=&#39;foo&#39;",
			XMLUtil.escapeXML("http://www.liferay.com/hello.world?foo=<script>&bar=\"baz\"&baz='foo'"));
		Assert.assertEquals(
			"http://www.liferay.com/hello.world?foo=&#187;bar&amp;bar=&#x2013;baz&amp;baz=&#x2014;foo&#x2028;",
			XMLUtil.escapeXML("http://www.liferay.com/hello.world?foo=\u00bbbar&bar=\u2013baz&baz=\u2014foo\u2028"));
		Assert.assertEquals("http://www.liferay.com/hello.world?foo=bar&amp;amp;bar=baz",
			XMLUtil.escapeXML("http://www.liferay.com/hello.world?foo=bar&amp;bar=baz"));
	}
}
