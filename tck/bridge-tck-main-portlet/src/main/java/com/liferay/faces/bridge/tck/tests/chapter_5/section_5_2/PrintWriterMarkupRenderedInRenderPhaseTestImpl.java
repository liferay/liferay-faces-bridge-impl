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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.io.PrintWriter;
import java.util.Locale;


/**
 * @author  Kyle Stiemann
 */
public class PrintWriterMarkupRenderedInRenderPhaseTestImpl extends PrintWriter {

	// Private Data Members
	private int writeMethodCalls = 0;

	public PrintWriterMarkupRenderedInRenderPhaseTestImpl(PrintWriter printWriter) {
		super(printWriter);
	}

	@Override
	public PrintWriter append(CharSequence csq) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public PrintWriter append(char c) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public PrintWriter append(CharSequence csq, int start, int end) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public boolean checkError() {
		return false;
	}

	@Override
	public void close() {
		// no-op
	}

	@Override
	public void flush() {
		// no-op
	}

	@Override
	public PrintWriter format(String format, Object... args) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public PrintWriter format(Locale l, String format, Object... args) {

		writeMethodCalls++;

		return this;
	}

	public int getWriteMethodCalls() {
		return writeMethodCalls;
	}

	@Override
	public void print(boolean b) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(char c) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(int i) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(long l) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(float f) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(double d) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(char[] s) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(String s) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void print(Object obj) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public PrintWriter printf(String format, Object... args) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public PrintWriter printf(Locale l, String format, Object... args) {

		writeMethodCalls++;

		return this;
	}

	@Override
	public void println() {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(boolean x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(char x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(int x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(long x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(float x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(double x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(char[] x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(String x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void println(Object x) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void write(int c) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void write(char[] buf) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void write(String s) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void write(char[] buf, int off, int len) {

		writeMethodCalls++;
		// no-op
	}

	@Override
	public void write(String s, int off, int len) {

		writeMethodCalls++;
		// no-op
	}
}
