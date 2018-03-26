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
package com.liferay.faces.demos.dto;

import com.liferay.faces.util.model.UploadedFile;

import javax.faces.FacesWrapper;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;


/**
 * @author  Neil Griffin
 */
public class UploadedFilePart implements UploadedFile, FacesWrapper<Part> {

	// Private Data Members
	private Map<String, Object> attributeMap;
	private String id;
	private String message;
	private String name;
	private Status status;
	private Part wrappedPart;

	public UploadedFilePart(Part part, String id, Status status) {
		this(part, id, status, null);
	}

	public UploadedFilePart(Part part, String id, Status status, String message) {
		this.wrappedPart = part;
		this.id = id;
		this.status = status;
		this.message = message;

		Part wrappedPart = getWrapped();

		if (wrappedPart != null) {
			String contentDisposition = wrappedPart.getHeader("Content-Disposition");

			if (contentDisposition != null) {
				String[] nameValuePairs = contentDisposition.split(";");

				for (String nameValuePair : nameValuePairs) {
					nameValuePair = nameValuePair.trim();

					if (nameValuePair.startsWith("filename")) {
						int pos = nameValuePair.indexOf("=");

						if (pos > 0) {
							name = nameValuePair.substring(pos + 1).replace("\"", "");
						}
					}
				}
			}
		}
	}

	@Override
	public void delete() throws IOException {
		getWrapped().delete();
	}

	@Override
	public String getAbsolutePath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributeMap;
	}

	@Override
	public byte[] getBytes() throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		InputStream inputStream = getWrapped().getInputStream();
		byte[] byteBuffer = new byte[1024];
		int bytesRead = inputStream.read(byteBuffer);

		while (bytesRead != -1) {
			byteArrayOutputStream.write(byteBuffer, 0, bytesRead);
			bytesRead = inputStream.read(byteBuffer);
		}

		byteArrayOutputStream.flush();

		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public String getCharSet() {
		throw new UnsupportedOperationException();
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
	public String getId() {
		return id;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getWrapped().getInputStream();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getSize() {
		return getWrapped().getSize();
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Part getWrapped() {
		return wrappedPart;
	}

	@Override
	public void write(String fileName) throws IOException {
		getWrapped().write(fileName);
	}
}
