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
 * This class saves {@link Writer} operations so that they can be executed or discarded at a later time.
 *
 * @author  Neil Griffin
 */
public abstract class BufferedRenderWriterImpl extends Writer {

	// Protected Data Members
	protected List<OutputOperation> outputOperationList;

	public BufferedRenderWriterImpl() {
		this.outputOperationList = new ArrayList<OutputOperation>();
	}

	@Override
	public void close() throws IOException {
		outputOperationList.add(new CloseOperation());
	}

	/**
	 * Discards the buffered response output so that it will not be written to the wrapped {@link Writer}.
	 */
	public void discard() {
		this.outputOperationList = new ArrayList<OutputOperation>();
	}

	@Override
	public void flush() throws IOException {
		outputOperationList.add(new FlushOperation());
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		if (cbuf != null) {
			outputOperationList.add(new CbufWriteOperation(cbuf));
		}
	}

	@Override
	public void write(int c) throws IOException {
		outputOperationList.add(new IntWriteOperation(c));
	}

	@Override
	public void write(String str) throws IOException {

		if (str != null) {
			outputOperationList.add(new StrWriteOperation(str));
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		if (cbuf != null) {
			outputOperationList.add(new CBufOffLenOutputOperation(cbuf, off, len));
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		if (str != null) {
			outputOperationList.add(new StrOffLenWriteOperation(str, off, len));
		}
	}

	protected interface OutputOperation {
		void invoke(Writer writer) throws IOException;
	}

	private static class CBufOffLenOutputOperation implements OutputOperation {

		private char[] cbuf;
		private int off;
		private int len;

		public CBufOffLenOutputOperation(char[] cbuf, int off, int len) {
			this.cbuf = cbuf.clone();
			this.off = off;
			this.len = len;
		}

		public void invoke(Writer writer) throws IOException {
			writer.write(cbuf, off, len);
		}
	}

	private static class CbufWriteOperation implements OutputOperation {

		private char[] cbuf;

		public CbufWriteOperation(char[] cbuf) {
			this.cbuf = cbuf.clone();
		}

		public void invoke(Writer writer) throws IOException {
			writer.write(cbuf);
		}
	}

	private static class CloseOperation implements OutputOperation {

		public CloseOperation() {
		}

		public void invoke(Writer writer) throws IOException {
			writer.close();
		}
	}

	private static class FlushOperation implements OutputOperation {

		public FlushOperation() {
		}

		public void invoke(Writer writer) throws IOException {
			writer.flush();
		}
	}

	private static class IntWriteOperation implements OutputOperation {

		private int c;

		public IntWriteOperation(int c) {
			this.c = c;
		}

		public void invoke(Writer writer) throws IOException {
			writer.write(c);
		}
	}

	private static class StrOffLenWriteOperation implements OutputOperation {

		private String str;
		private int off;
		private int len;

		public StrOffLenWriteOperation(String str, int off, int len) {
			this.str = str;
			this.off = off;
			this.len = len;
		}

		public void invoke(Writer writer) throws IOException {
			writer.write(str, off, len);
		}
	}

	private static class StrWriteOperation implements OutputOperation {

		private String str;

		public StrWriteOperation(String str) {
			this.str = str;
		}

		public void invoke(Writer writer) throws IOException {
			writer.write(str);
		}
	}
}
