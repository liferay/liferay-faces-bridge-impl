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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.io.PrintWriter;
import java.util.Locale;


/**
 * This class decorates a {@link PrintWriter} in order to suppress write operations rather than actually performing the
 * write operations.
 *
 * @author  Kyle Stiemann
 */
public class SuppressedPrintWriter extends PrintWriter {

	// Private Data Members
	private boolean attemptedWrite;

	public SuppressedPrintWriter(PrintWriter printWriter) {
		super(printWriter);
	}

	@Override
	public PrintWriter append(CharSequence csq) {

		attemptedWrite = true;

		return this;
	}

	@Override
	public PrintWriter append(char c) {

		attemptedWrite = true;

		return this;
	}

	@Override
	public PrintWriter append(CharSequence csq, int start, int end) {

		attemptedWrite = true;

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

		attemptedWrite = true;

		return this;
	}

	@Override
	public PrintWriter format(Locale l, String format, Object... args) {

		attemptedWrite = true;

		return this;
	}

	public boolean isAttemptedWrite() {
		return attemptedWrite;
	}

	@Override
	public void print(boolean b) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(char c) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(int i) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(long l) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(float f) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(double d) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(char[] s) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(String s) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void print(Object obj) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public PrintWriter printf(String format, Object... args) {

		attemptedWrite = true;

		return this;
	}

	@Override
	public PrintWriter printf(Locale l, String format, Object... args) {

		attemptedWrite = true;

		return this;
	}

	@Override
	public void println() {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(boolean x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(char x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(int x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(long x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(float x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(double x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(char[] x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(String x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void println(Object x) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void write(int c) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void write(char[] buf) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void write(String s) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void write(char[] buf, int off, int len) {

		attemptedWrite = true;
		// no-op
	}

	@Override
	public void write(String s, int off, int len) {

		attemptedWrite = true;
		// no-op
	}
}
