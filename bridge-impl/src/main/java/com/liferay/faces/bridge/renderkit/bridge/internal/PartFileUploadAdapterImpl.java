/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.renderkit.bridge.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;

import javax.faces.FacesWrapper;
import javax.servlet.http.Part;

import com.liferay.faces.bridge.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public final class PartFileUploadAdapterImpl implements Part, FacesWrapper<UploadedFile>, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 9094557806016190724L;

	// Private Final Data Members
	private final String clientId;
	private final UploadedFile wrappedUploadedFile;

	public PartFileUploadAdapterImpl(UploadedFile uploadedFile, String clientId) {
		this.wrappedUploadedFile = uploadedFile;
		this.clientId = clientId;
	}

	@Override
	public void delete() throws IOException {
		getWrapped().delete();
	}

	@Override
	public String getContentType() {
		return getWrapped().getContentType();
	}

	@Override
	public String getHeader(String name) {
		return getWrapped().getHeader(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return getWrapped().getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return getWrapped().getHeaders(name);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getWrapped().getInputStream();
	}

	@Override
	public String getName() {
		return clientId;
	}

	@Override
	public long getSize() {
		return getWrapped().getSize();
	}

	public String getSubmittedFileName() {
		return getWrapped().getName();
	}

	@Override
	public UploadedFile getWrapped() {
		return wrappedUploadedFile;
	}

	@Override
	public void write(String fileName) throws IOException {
		getWrapped().write(fileName);
	}
}
