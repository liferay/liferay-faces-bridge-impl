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
package com.liferay.faces.bridge.context.url;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.util.internal.URLUtil;


/**
 * @author  Kyle Stiemann
 */
public class URLUtilTest {

	@Test
	public void testEscapeXML() {

		Assert.assertEquals("&amp;", URLUtil.escapeXML("&"));
		Assert.assertEquals("&lt;", URLUtil.escapeXML("<"));
		Assert.assertEquals("&gt;", URLUtil.escapeXML(">"));
		Assert.assertEquals("&#034;", URLUtil.escapeXML("\""));
		Assert.assertEquals("&#039;", URLUtil.escapeXML("'"));
		Assert.assertEquals("&#187;", URLUtil.escapeXML("\u00bb"));
		Assert.assertEquals("&#x2013;", URLUtil.escapeXML("\u2013"));
		Assert.assertEquals("&#x2014;", URLUtil.escapeXML("\u2014"));
		Assert.assertEquals("&#x2028;", URLUtil.escapeXML("\u2028"));

		Assert.assertEquals("http://www.liferay.com/hello.world?foo=bar&amp;bar=baz&amp;baz=foo",
			URLUtil.escapeXML("http://www.liferay.com/hello.world?foo=bar&bar=baz&baz=foo"));
		Assert.assertEquals(
			"http://www.liferay.com/hello.world?foo=&lt;script&gt;&amp;bar=&#034;baz&#034;&amp;baz=&#039;foo&#039;",
			URLUtil.escapeXML("http://www.liferay.com/hello.world?foo=<script>&bar=\"baz\"&baz='foo'"));
		Assert.assertEquals(
			"http://www.liferay.com/hello.world?foo=&#187;bar&amp;bar=&#x2013;baz&amp;baz=&#x2014;foo&#x2028;",
			URLUtil.escapeXML("http://www.liferay.com/hello.world?foo=\u00bbbar&bar=\u2013baz&baz=\u2014foo\u2028"));
		Assert.assertEquals("http://www.liferay.com/hello.world?foo=bar&amp;amp;bar=baz",
			URLUtil.escapeXML("http://www.liferay.com/hello.world?foo=bar&amp;bar=baz"));
	}
}
