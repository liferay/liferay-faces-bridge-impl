/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
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
	private boolean primeFacesDetected;
	private int primeFacesMajorVersion;
	private byte[] primeFacesFileContent;
	private com.liferay.faces.util.model.UploadedFile wrappedUploadedFile;

	public UploadedFileBridgeImpl(com.liferay.faces.util.model.UploadedFile uploadedFile, boolean primeFacesDetected,
		int primeFacesMajorVersion) {
		this.wrappedUploadedFile = uploadedFile;
		this.primeFacesDetected = primeFacesDetected;
		this.primeFacesMajorVersion = primeFacesMajorVersion;
	}

	@Override
	public void delete() throws IOException {

		try {
			String tempDirPath = null;
			String absolutePath = wrappedUploadedFile.getAbsolutePath();

			if (absolutePath != null) {
				File wrappedFile = new File(absolutePath);
				tempDirPath = wrappedFile.getParent();

				if (tempDirPath != null) {
					File tempDir = new File(tempDirPath);
					File[] files = tempDir.listFiles(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									return ((name != null) && name.endsWith(".tmp"));
								}
							});

					if (files != null) {

						for (File file : files) {
							file.delete();
						}
					}
				}
			}

			wrappedUploadedFile.delete();
		}
		catch (Exception e) {

			if (e instanceof IOException) {
				throw e;
			}

			throw new IOException(e);
		}
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

		if (primeFacesDetected) {

			if (primeFacesMajorVersion >= 8) {

				// PrimeFaces 8.0 fixed the following issue:
				// https://github.com/primefaces/primefaces/issues/5408
				// With the following commit:
				// https://github.com/primefaces/primefaces/commit/bdfc969e60bdcfe077caafe0821388535cbb1551
				// This change causes PrimeFaces to internally call this getInputStream() method before the
				// PrimeFaces webapp code has an opportunity to do so. Since the PrimeFaces UploadedFile
				// interface does not contain a delete() method, the legacy behavior here was to delete
				// the file after the inputStream was closed. This was accomplished via the DeletingInputStream
				// wrapper. But in the case of PrimeFaces 8.0, it is necessary to cache the file content
				// before deleting the file, so that multiple calls to this getInputStream() method can be
				// supported.
				if (primeFacesFileContent == null) {
					primeFacesFileContent = getBytes();
				}

				delete();

				return new ByteArrayInputStream(primeFacesFileContent);
			}
			else {
				return new DeletingInputStream(wrappedUploadedFile.getInputStream());
			}
		}

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

	private class DeletingInputStream extends InputStream {

		private InputStream wrappedInputStream;

		public DeletingInputStream(InputStream inputStream) {
			this.wrappedInputStream = inputStream;
		}

		@Override
		public void close() throws IOException {
			wrappedInputStream.close();
			UploadedFileBridgeImpl.this.delete();
		}

		@Override
		public int read() throws IOException {
			return wrappedInputStream.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return wrappedInputStream.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return wrappedInputStream.read(b, off, len);
		}
	}
}
