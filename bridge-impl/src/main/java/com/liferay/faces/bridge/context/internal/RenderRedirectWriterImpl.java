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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * This class supports the render-redirect feature by queuing up a list of all write operations. Calling the {@link
 * #render()} method causes the queued operations to be invoked. Conversely, not calling the method will prevent the
 * queued operations from being invoked. This is necessary because in the case of a render-redirect, any markup written
 * to the response in the initially rendered view must be discarded.
 *
 * @author  Neil Griffin
 */
public class RenderRedirectWriterImpl extends RenderRedirectWriter {

	// Private Data Members
	private Writer wrappedWriter;
	private List<OutputOperation> outputOperationList;

	public RenderRedirectWriterImpl(Writer writer) {
		this.wrappedWriter = writer;
		this.outputOperationList = new ArrayList<OutputOperation>();
	}

	@Override
	public void close() throws IOException {
		outputOperationList.add(new CloseOperation(wrappedWriter));
	}

	@Override
	public void discard() {
		this.outputOperationList = new ArrayList<OutputOperation>();
	}

	@Override
	public void flush() throws IOException {
		outputOperationList.add(new FlushOperation(wrappedWriter));
	}

	public Writer getWrapped() {
		return wrappedWriter;
	}

	@Override
	public void render() throws IOException {

		for (OutputOperation outputOperation : outputOperationList) {
			outputOperation.invoke();
		}
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		if (cbuf != null) {
			outputOperationList.add(new CbufWriteOperation(wrappedWriter, cbuf));
		}
	}

	@Override
	public void write(int c) throws IOException {
		outputOperationList.add(new IntWriteOperation(wrappedWriter, c));
	}

	@Override
	public void write(String str) throws IOException {

		if (str != null) {
			outputOperationList.add(new StrWriteOperation(wrappedWriter, str));
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		if (cbuf != null) {
			outputOperationList.add(new CBufOffLenOutputOperation(wrappedWriter, cbuf, off, len));
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		if (str != null) {
			outputOperationList.add(new StrOffLenWriteOperation(wrappedWriter, str, off, len));
		}
	}

	protected interface OutputOperation {
		void invoke() throws IOException;
	}

	private static class CBufOffLenOutputOperation implements OutputOperation {

		private char[] cbuf;
		private int off;
		private int len;
		private Writer writer;

		public CBufOffLenOutputOperation(Writer writer, char[] cbuf, int off, int len) {
			this.cbuf = cbuf.clone();
			this.off = off;
			this.len = len;
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.write(cbuf, off, len);
		}
	}

	private static class CbufWriteOperation implements OutputOperation {

		private char[] cbuf;
		private Writer writer;

		public CbufWriteOperation(Writer writer, char[] cbuf) {
			this.cbuf = cbuf.clone();
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.write(cbuf);
		}
	}

	private static class CloseOperation implements OutputOperation {

		private Writer writer;

		public CloseOperation(Writer writer) {
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.close();
		}
	}

	private static class FlushOperation implements OutputOperation {

		private Writer writer;

		public FlushOperation(Writer writer) {
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.flush();
		}
	}

	private static class IntWriteOperation implements OutputOperation {

		private int c;
		private Writer writer;

		public IntWriteOperation(Writer writer, int c) {
			this.c = c;
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.write(c);
		}
	}

	private static class StrOffLenWriteOperation implements OutputOperation {

		private String str;
		private int off;
		private int len;
		private Writer writer;

		public StrOffLenWriteOperation(Writer writer, String str, int off, int len) {
			this.str = str;
			this.off = off;
			this.len = len;
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.write(str, off, len);
		}
	}

	private static class StrWriteOperation implements OutputOperation {

		private String str;
		private Writer writer;

		public StrWriteOperation(Writer writer, String str) {
			this.str = str;
			this.writer = writer;
		}

		public void invoke() throws IOException {
			writer.write(str);
		}
	}
}
