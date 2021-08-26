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
package com.liferay.faces.bridge.context.url;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;

import javax.portlet.BaseURL;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.internal.BaseURLBridgeURIAdapterImpl;
import com.liferay.faces.bridge.internal.BridgeURI;
import com.liferay.faces.util.render.FacesURLEncoder;


/**
 * @author  Neil Griffin
 */
public class BridgeURITest {

	// Private Constants
	private static final String CONTEXT_PATH = "/my-portlet";

	@Test
	public void testEscaped() throws UnsupportedEncodingException {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo").isEscaped());
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo?a=1&b=2").isEscaped());
			Assert.assertFalse(newBridgeURI("http://www.liferay.com/foo?a=1&b=2&amp;c=3").isEscaped());
			Assert.assertTrue(newBridgeURI("http://www.liferay.com/foo?a=1&amp;b=2&amp;c=3").isEscaped());
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testExternal() throws UnsupportedEncodingException {

		try {
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").isExternal(CONTEXT_PATH));
			Assert.assertFalse(newBridgeURI(CONTEXT_PATH).isExternal(CONTEXT_PATH));
			Assert.assertTrue(newBridgeURI("/relativeToContextPath?someurl=" + CONTEXT_PATH).isExternal(CONTEXT_PATH));
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testHierarchical() throws UnsupportedEncodingException {

		try {
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").isHierarchical());
			Assert.assertTrue(newBridgeURI("/foo/bar.gif").isHierarchical());
			Assert.assertTrue(newBridgeURI("foo/bar.gif").isHierarchical());
			Assert.assertFalse(newBridgeURI("mailto:foo@liferay.com").isHierarchical());
			Assert.assertFalse(newBridgeURI("portlet:render").isHierarchical());
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testOpaque() throws UnsupportedEncodingException {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com").isOpaque());
			Assert.assertTrue(newBridgeURI("mailto:foo@liferay.com").isOpaque());
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testPathRelative() throws UnsupportedEncodingException {

		try {
			Assert.assertFalse(newBridgeURI("http://www.liferay.com").isPathRelative());
			Assert.assertFalse(newBridgeURI("/foo/bar.gif").isPathRelative());
			Assert.assertTrue(newBridgeURI("foo/bar.gif").isPathRelative());
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testViewPath() throws UnsupportedEncodingException {

		try {
			Assert.assertTrue(newBridgeURI("http://www.liferay.com").getContextRelativePath("") == null);
			Assert.assertTrue(newBridgeURI("/views/foo.xhtml").getContextRelativePath("").equals("/views/foo.xhtml"));
			Assert.assertTrue(newBridgeURI(CONTEXT_PATH + "/views/foo.xhtml").getContextRelativePath(CONTEXT_PATH)
				.equals("/views/foo.xhtml"));
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testXmlEscaping() throws UnsupportedEncodingException {

		try {
			BridgeURI bridgeURI = newBridgeURI("http://www.liferay.com/hello.world?a=1&b=2");
			BaseURL nonEncodedURL = new BaseURLBridgeURIAdapterImpl(bridgeURI);
			Writer stringWriter = new StringWriter();
			nonEncodedURL.write(stringWriter, false);
			Assert.assertTrue("http://www.liferay.com/hello.world?a=1&b=2".equals(stringWriter.toString()));
			stringWriter = new StringWriter();
			nonEncodedURL.write(stringWriter, true);
			Assert.assertTrue("http://www.liferay.com/hello.world?a=1&amp;b=2".equals(stringWriter.toString()));
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	protected BridgeURI newBridgeURI(String uri) throws URISyntaxException, UnsupportedEncodingException {
		return new BridgeURI(uri, "", new FacesURLEncoder() {
					@Override
					public String encode(String url, String encoding) {
						return url;
					}
				}, "UTF-8");
	}
}
