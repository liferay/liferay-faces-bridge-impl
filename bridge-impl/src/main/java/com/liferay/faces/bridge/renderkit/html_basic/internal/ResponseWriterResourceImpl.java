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

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.util.render.FacesURLEncoder;
import com.liferay.faces.util.render.FacesURLEncoderFactory;


/**
 * The purpose of this class is to bypass the Mojarra/MyFaces encoding of the URL found in the "src" attribute of a
 * &lt;script&gt; element or the "href" attribute of a &lt;link&gt; element. For more info, see: <a
 * href="http://issues.liferay.com/browse/FACES-1236">FACES-1236</a>.
 *
 * @author  Neil Griffin
 */
public class ResponseWriterResourceImpl extends ResponseWriterWrapper {

	// Private Constants
	private static final String REGEX_AMPERSAND = "[&]amp;";

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;

	public ResponseWriterResourceImpl(ResponseWriter responseWriter) {
		this.wrappedResponseWriter = responseWriter;
	}

	@Override
	public void endElement(String name) throws IOException {
		write("></");
		write(name);
		write(">");
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {
		write("<");
		write(name);
	}

	@Override
	public void write(String str) throws IOException {
		super.write(str);
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {
		write(" ");
		write(name);
		write("=\"");
		write((String) value);
		write("\"");
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {

		if ((value != null) && (value instanceof String)) {
			String encoding = wrappedResponseWriter.getCharacterEncoding();
			FacesURLEncoder facesURLEncoder = FacesURLEncoderFactory.getFacesURLEncoderInstance();
			String encodedURI = facesURLEncoder.encode((String) value, encoding);

			if (encodedURI != null) {

				// Remove all the encoded ampersands. See: http://issues.liferay.com/browse/FACES-1236
				encodedURI = encodedURI.replaceAll(REGEX_AMPERSAND, "&");
			}

			writeAttribute(name, encodedURI, property);
		}
		else {
			writeAttribute(name, value, property);
		}
	}
}
