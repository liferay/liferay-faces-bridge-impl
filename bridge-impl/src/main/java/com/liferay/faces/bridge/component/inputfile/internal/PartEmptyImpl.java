/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.component.inputfile.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.Part;


/**
 * This is a marker class used by {@link HtmlInputFileRenderer} to signify that an empty file was submitted.
 *
 * @author  Kyle Stiemann
 */
public class PartEmptyImpl implements Part {

	@Override
	public void delete() throws IOException {
		// no-op
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return Collections.emptyList();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new IOException("File does not exist.");
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public void write(String fileName) throws IOException {
		throw new IOException("File does not exist.");
	}
}
