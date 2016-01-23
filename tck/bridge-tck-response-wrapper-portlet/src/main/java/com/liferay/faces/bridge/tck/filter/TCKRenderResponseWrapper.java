/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.filter;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;

import javax.faces.context.FacesContext;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgeWriteBehindResponse;
import javax.portlet.filter.RenderResponseWrapper;
import javax.servlet.http.HttpServletResponse;


/**
 * @author  Neil Griffin
 */
public class TCKRenderResponseWrapper extends RenderResponseWrapper implements BridgeWriteBehindResponse {

	private DirectByteArrayServletOutputStream mByteStream;
	private CharArrayWriter mCharWriter;
	private PrintWriter mPrintWriter;
	private int mStatus = HttpServletResponse.SC_OK;
	private boolean mHasWriteBehindMarkup = false;

	public TCKRenderResponseWrapper() {
		super((RenderResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
	}

	public TCKRenderResponseWrapper(RenderResponse wrapped) {
		super(wrapped);
	}

	public void clearWrappedResponse() throws IOException {
		resetBuffers();
	}

	public void flushBuffer() {

		if (isChars()) {
			mPrintWriter.flush();
		}
	}

	/**
	 * This is the Mojarra specific API used by its tags to flush the pre-view content to the wrapped response. Works
	 * automatically in newer Mojarra -- one's that use inspection to determine if the API is supported.
	 *
	 * @throws  IOException  if content cannot be written
	 */
	public void flushContentToWrappedResponse() throws IOException {
		mHasWriteBehindMarkup = true;

		flushMarkupToWrappedResponse();

	}

	/**
	 * Flush the current buffered content to the wrapped response (this could be a Servlet or Portlet response)
	 *
	 * @throws  IOException  if content cannot be written
	 */
	public void flushMarkupToWrappedResponse() throws IOException {
		RenderResponse response = getResponse();

		flushBuffer();

		if (isBytes()) {
			response.getPortletOutputStream().write(getBytes());
			mByteStream.reset();
		}
		else if (isChars()) {
			response.getWriter().write(getChars());
			mCharWriter.reset();
		}

	}

	/**
	 * This is the MyFaces specific API used by its tags to flush the pre-view content to the wrapped response. MyFaces
	 * doesn't yet use reflection so this will only work if you subclass with a class that claims it implements the
	 * MyFaces interface.
	 *
	 * @throws  IOException  if content cannot be written
	 */
	public void flushToWrappedResponse() throws IOException {
		mHasWriteBehindMarkup = true;

		flushMarkupToWrappedResponse();

	}

	/**
	 * Flush the current buffered content to the provided <code>Writer</code>
	 *
	 * @param   writer    target <code>Writer</code>
	 * @param   encoding  the encoding that should be used
	 *
	 * @throws  IOException  if content cannot be written
	 */
	public void flushToWriter(Writer writer, String encoding) throws IOException {
		flushBuffer();

		if (isBytes()) {
			throw new IOException("Invalid flushToWriter as the code is writing bytes to an OutputStream.");
		}
		else if (isChars()) {
			writer.write(getChars());
			mCharWriter.reset();
		}
	}

	/**
	 * Called by the bridge to detect whether this response actively participated in the Faces writeBehind support and
	 * hence has data that should be written after the View is rendered. Typically, this method will return <code>
	 * true</code> if the Faces write behind implementation specific flush api has been called on this response,
	 * otherwise <code>false</code>
	 *
	 * @return  an indication of whether the response actually particpated in the writeBehind mechanism.
	 */
	public boolean hasFacesWriteBehindMarkup() {
		return mHasWriteBehindMarkup;
	}

	public void reset() {
		super.reset();

		if (isBytes()) {
			mByteStream.reset();
		}
		else if (isChars()) {
			mPrintWriter.flush();
			mCharWriter.reset();
		}
	}

	public void resetBuffer() {
		super.resetBuffer();

		if (isBytes()) {
			mByteStream.reset();
		}
		else if (isChars()) {
			mPrintWriter.flush();
			mCharWriter.reset();
		}
	}

	/**
	 * Clear the internal buffers.
	 *
	 * @throws  IOException  if some odd error occurs
	 */
	public void resetBuffers() throws IOException {

		if (isBytes()) {
			mByteStream.reset();
		}
		else if (isChars()) {
			mPrintWriter.flush();
			mCharWriter.reset();
		}
	}

	public String toString() {

		if (isChars()) {
			mCharWriter.flush();

			return mCharWriter.toString();
		}
		else if (isBytes()) {
			return mByteStream.toString();
		}
		else {
			return null;
		}
	}

	public int getBufferSize() {

		if (isBytes()) {
			return mByteStream.size();
		}
		else if (isChars()) {
			return mCharWriter.size();
		}
		else {
			return 0;
		}
	}

	public byte[] getBytes() {

		if (isBytes()) {
			return mByteStream.toByteArray();
		}
		else {
			return null;
		}
	}

	public char[] getChars() {

		if (isChars()) {
			mCharWriter.flush();

			return mCharWriter.toCharArray();
		}
		else {
			return null;
		}
	}

	public OutputStream getPortletOutputStream() throws IOException {

		if (mPrintWriter != null) {
			throw new IllegalStateException();
		}

		if (mByteStream == null) {
			mByteStream = new DirectByteArrayServletOutputStream();
		}

		return mByteStream;
	}

	public boolean isBytes() {
		return (mByteStream != null);
	}

	public boolean isChars() {
		return (mCharWriter != null);
	}

	public int getStatus() {
		return mStatus;
	}

	public PrintWriter getWriter() throws IOException {

		if (mByteStream != null) {
			throw new IllegalStateException();
		}

		if (mPrintWriter == null) {
			mCharWriter = new CharArrayWriter(4096);
			mPrintWriter = new PrintWriter(mCharWriter);
		}

		return mPrintWriter;
	}

	private class DirectByteArrayOutputStream extends ByteArrayOutputStream {

		// -------------------------------------------------------- Constructors

		public DirectByteArrayOutputStream(int initialCapacity) {
			super(initialCapacity);
		}

		// ------------------------------------------------------- PublicMethods

		/**
		 * Return the buffer backing this ByteArrayOutputStream as a ByteBuffer.
		 *
		 * @return  buf wrapped in a ByteBuffer
		 */
		public ByteBuffer getByteBuffer() {
			return (ByteBuffer.wrap(buf, 0, count));
		}

	}

	// ----------------------------------------------------------- Inner Classes

	private class DirectByteArrayServletOutputStream extends OutputStream {
		private DirectByteArrayOutputStream mByteArrayOutputStream;

		public DirectByteArrayServletOutputStream() {
			mByteArrayOutputStream = new DirectByteArrayOutputStream(4096);
		}

		public void reset() {
			mByteArrayOutputStream.reset();
		}

		public int size() {
			return mByteArrayOutputStream.size();
		}

		public byte[] toByteArray() {
			return mByteArrayOutputStream.toByteArray();
		}

		public void write(int n) {
			mByteArrayOutputStream.write(n);
		}

	}

	// end of class BridgeRenderFilterResponseWrapper
}
