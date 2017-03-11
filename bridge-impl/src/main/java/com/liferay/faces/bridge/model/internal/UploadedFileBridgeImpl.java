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
package com.liferay.faces.bridge.model.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.liferay.faces.bridge.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public class UploadedFileBridgeImpl implements Serializable, UploadedFile {

	// serialVersionUID
	private static final long serialVersionUID = 2492812271137403881L;

	// Private Data Members
	private com.liferay.faces.util.model.UploadedFile wrappedUploadedFile;

	public UploadedFileBridgeImpl(com.liferay.faces.util.model.UploadedFile uploadedFile) {
		this.wrappedUploadedFile = uploadedFile;
	}

	@Override
	public void delete() throws IOException {
		wrappedUploadedFile.delete();
	}

	@Override
	public String getAbsolutePath() {
		return wrappedUploadedFile.getAbsolutePath();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return wrappedUploadedFile.getAttributes();
	}

	@Override
	public byte[] getBytes() throws IOException {
		return wrappedUploadedFile.getBytes();
	}

	@Override
	public String getCharSet() {
		return wrappedUploadedFile.getCharSet();
	}

	@Override
	public String getContentType() {
		return wrappedUploadedFile.getContentType();
	}

	@Override
	public String getHeader(String name) {
		return wrappedUploadedFile.getHeader(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return wrappedUploadedFile.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return wrappedUploadedFile.getHeaders(name);
	}

	@Override
	public String getId() {
		return wrappedUploadedFile.getId();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrappedUploadedFile.getInputStream();
	}

	@Override
	public String getMessage() {
		return wrappedUploadedFile.getMessage();
	}

	@Override
	public String getName() {
		return wrappedUploadedFile.getName();
	}

	@Override
	public long getSize() {
		return wrappedUploadedFile.getSize();
	}

	@Override
	public Status getStatus() {

		com.liferay.faces.util.model.UploadedFile.Status wrappedStatus = wrappedUploadedFile.getStatus();

		return UploadedFile.Status.valueOf(wrappedStatus.name());
	}

	@Override
	public void write(String fileName) throws IOException {
		wrappedUploadedFile.write(fileName);
	}
}
