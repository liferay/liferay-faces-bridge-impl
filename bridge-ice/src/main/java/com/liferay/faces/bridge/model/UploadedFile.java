/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;


/**
 * @author  Neil Griffin
 */
public interface UploadedFile {

	/**
	 * @author  Neil Griffin
	 */
	public static enum Status {
		ERROR, FILE_SIZE_LIMIT_EXCEEDED, FILE_INVALID_NAME_PATTERN, FILE_SAVED, REQUEST_SIZE_LIMIT_EXCEEDED
	}

	public void delete() throws IOException;

	public void write(String fileName) throws IOException;

	public String getAbsolutePath();

	public Map<String, Object> getAttributes();

	public byte[] getBytes() throws IOException;

	public String getCharSet();

	public String getContentType();

	public String getHeader(String name);

	public Collection<String> getHeaderNames();

	public Collection<String> getHeaders(String name);

	public String getId();

	public InputStream getInputStream() throws IOException;

	public String getMessage();

	public String getName();

	public long getSize();

	public Status getStatus();
}
