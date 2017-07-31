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
package com.liferay.faces.bridge.component.inputfile.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;


/**
 * This is a marker class used by {@link HtmlInputFileRenderer} to signify that an empty file was submitted.
 *
 * @author  Kyle Stiemann
 */
public class PartEmptyImpl {

	public void delete() throws IOException {
		// no-op
	}

	public String getContentType() {
		return null;
	}

	public String getHeader(String name) {
		return null;
	}

	public Collection<String> getHeaderNames() {
		return Collections.emptyList();
	}

	public Collection<String> getHeaders(String name) {
		return Collections.emptyList();
	}

	public InputStream getInputStream() throws IOException {
		throw new IOException("File does not exist.");
	}

	public String getName() {
		return null;
	}

	public long getSize() {
		return 0;
	}

	public void write(String fileName) throws IOException {
		throw new IOException("File does not exist.");
	}
}
