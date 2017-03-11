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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * @author  Neil Griffin
 */
public class CapturingWriterImpl extends CapturingWriter {

	// Protected Data Members
	private List<WriterOperation> writerOperations;

	public CapturingWriterImpl() {
		this.writerOperations = new ArrayList<WriterOperation>();
	}

	@Override
	public void close() throws IOException {
		writerOperations.add(new CloseOperation());
	}

	@Override
	public void flush() throws IOException {
		writerOperations.add(new FlushOperation());
	}

	@Override
	public List<WriterOperation> getWriterOperations() {
		return writerOperations;
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		if (cbuf != null) {
			writerOperations.add(new CbufWriteOperation(cbuf));
		}
	}

	@Override
	public void write(int c) throws IOException {
		writerOperations.add(new IntWriteOperation(c));
	}

	@Override
	public void write(String str) throws IOException {

		if (str != null) {
			writerOperations.add(new StrWriteOperation(str));
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		if (cbuf != null) {
			writerOperations.add(new CBufOffLenWriterOperation(cbuf, off, len));
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		if (str != null) {
			writerOperations.add(new StrOffLenWriteOperation(str, off, len));
		}
	}

	private static class CBufOffLenWriterOperation implements WriterOperation {

		private char[] cbuf;
		private int off;
		private int len;

		public CBufOffLenWriterOperation(char[] cbuf, int off, int len) {
			this.cbuf = cbuf.clone();
			this.off = off;
			this.len = len;
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.write(cbuf, off, len);
		}
	}

	private static class CbufWriteOperation implements WriterOperation {

		private char[] cbuf;

		public CbufWriteOperation(char[] cbuf) {
			this.cbuf = cbuf.clone();
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.write(cbuf);
		}
	}

	private static class CloseOperation implements WriterOperation {

		public CloseOperation() {
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.close();
		}
	}

	private static class FlushOperation implements WriterOperation {

		public FlushOperation() {
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.flush();
		}
	}

	private static class IntWriteOperation implements WriterOperation {

		private int c;

		public IntWriteOperation(int c) {
			this.c = c;
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.write(c);
		}
	}

	private static class StrOffLenWriteOperation implements WriterOperation {

		private String str;
		private int off;
		private int len;

		public StrOffLenWriteOperation(String str, int off, int len) {
			this.str = str;
			this.off = off;
			this.len = len;
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.write(str, off, len);
		}
	}

	private static class StrWriteOperation implements WriterOperation {

		private String str;

		public StrWriteOperation(String str) {
			this.str = str;
		}

		@Override
		public void invoke(Writer writer) throws IOException {
			writer.write(str);
		}
	}
}
