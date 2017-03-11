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

import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import javax.portlet.BaseURL;

import org.junit.Test;

import com.liferay.faces.bridge.internal.BaseURLNonEncodedImpl;
import com.liferay.faces.bridge.internal.BridgeURI;
import com.liferay.faces.bridge.internal.BridgeURIImpl;

import junit.framework.Assert;


/**
 * @author  Neil Griffin
 */
public class BridgeURLTest {

	// Private Constants
	private static final String CONTEXT_PATH = "/my-portlet";

	@Test
	public void testEscaped() {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo").isEscaped());
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo?a=1&b=2").isEscaped());
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo?a=1&b=2&amp;c=3").isEscaped());
			Assert.assertTrue(newBridgeURI("http://www.liferay.com/foo?a=1&amp;b=2&amp;c=3").isEscaped());
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testExternal() {

		try {
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").isExternal(CONTEXT_PATH));
			Assert.assertFalse(newBridgeURI(CONTEXT_PATH).isExternal(CONTEXT_PATH));
			Assert.assertTrue(newBridgeURI("/relativeToContextPath?someurl=" + CONTEXT_PATH).isExternal(CONTEXT_PATH));
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHierarchical() {

		try {
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").isHierarchical());
			Assert.assertTrue(newBridgeURI("/foo/bar.gif").isHierarchical());
			Assert.assertTrue(newBridgeURI("foo/bar.gif").isHierarchical());
			Assert.assertFalse(newBridgeURI("mailto:foo@liferay.com").isHierarchical());
			Assert.assertFalse(newBridgeURI("portlet:render").isHierarchical());
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testOpaque() {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com").isOpaque());
			Assert.assertTrue(newBridgeURI("mailto:foo@liferay.com").isOpaque());
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testPathRelative() {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com").isPathRelative());
			Assert.assertFalse(newBridgeURI("/foo/bar.gif").isPathRelative());
			Assert.assertTrue(newBridgeURI("foo/bar.gif").isPathRelative());
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testViewPath() {

		try {
			newBridgeURI("http://www.liferay.com");
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").getContextRelativePath("") == null);
			Assert.assertTrue(newBridgeURI("/views/foo.xhtml").getContextRelativePath("").equals("/views/foo.xhtml"));
			Assert.assertTrue(newBridgeURI(CONTEXT_PATH + "/views/foo.xhtml").getContextRelativePath(CONTEXT_PATH)
				.equals("/views/foo.xhtml"));
		}
		catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testXmlEscaping() {

		try {
			BridgeURI bridgeURI = newBridgeURI("http://www.liferay.com/hello.world?a=1&b=2");
			BaseURL nonEncodedURL = new BaseURLNonEncodedImpl(bridgeURI);
			Writer stringWriter = new StringWriter();
			nonEncodedURL.write(stringWriter, false);
			Assert.assertTrue("http://www.liferay.com/hello.world?a=1&b=2".equals(stringWriter.toString()));
			stringWriter = new StringWriter();
			nonEncodedURL.write(stringWriter, true);
			Assert.assertTrue("http://www.liferay.com/hello.world?a=1&amp;b=2".equals(stringWriter.toString()));
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected BridgeURI newBridgeURI(String uri) throws URISyntaxException {
		return new BridgeURIImpl("", uri);
	}
}
