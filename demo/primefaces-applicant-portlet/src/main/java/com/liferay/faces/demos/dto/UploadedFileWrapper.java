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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesWrapper;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public class UploadedFileWrapper implements Serializable, UploadedFile,
	FacesWrapper<org.primefaces.model.UploadedFile> {

	// serialVersionUID
	private static final long serialVersionUID = 5347502272011734546L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(UploadedFileWrapper.class);

	// Private Data Members
	private Map<String, Object> attributeMap;
	private String id;
	private File file;
	private Status status;
	private org.primefaces.model.UploadedFile wrappedUploadedFile;

	public UploadedFileWrapper(org.primefaces.model.UploadedFile uploadedFile, UploadedFile.Status status,
		String uniqueFolderName) {
		this.wrappedUploadedFile = uploadedFile;
		this.status = status;
		this.attributeMap = new HashMap<String, Object>();
		this.id = Long.toString(((long) hashCode()) + System.currentTimeMillis());
		this.file = getFile(uniqueFolderName);
	}

	public void delete() throws IOException {

		if (file != null) {
			file.delete();
		}
	}

	public String getAbsolutePath() {
		String absolutePath = null;

		if (file != null) {
			absolutePath = file.getAbsolutePath();
		}

		return absolutePath;
	}

	public Map<String, Object> getAttributes() {
		return attributeMap;
	}

	public byte[] getBytes() throws IOException {

		byte[] bytes = null;

		if ((file != null) && (file.exists())) {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			bytes = new byte[(int) randomAccessFile.length()];
			randomAccessFile.readFully(bytes);
			randomAccessFile.close();
		}

		return bytes;
	}

	public String getCharSet() {
		throw new UnsupportedOperationException();
	}

	public String getContentType() {
		return wrappedUploadedFile.getContentType();
	}

	public String getHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	public Collection<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	public String getId() {
		return id;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public String getMessage() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		return wrappedUploadedFile.getFileName();
	}

	public long getSize() {
		return wrappedUploadedFile.getSize();
	}

	public Status getStatus() {
		return status;
	}

	public org.primefaces.model.UploadedFile getWrapped() {
		return wrappedUploadedFile;
	}

	public void write(String fileName) throws IOException {
		OutputStream outputStream = new FileOutputStream(fileName);
		outputStream.write(getBytes());
		outputStream.close();
	}

	/**
	 * Since the PrimeFaces UploadedFile interface does not provide a method for deleting the file, Liferay Faces Bridge
	 * automatically deletes it when the wrappedUploadedFile.getContents() method is called. For more information, see
	 * {@link com.liferay.faces.bridge.renderkit.primefaces.FileUploadRendererPrimeFacesImpl.PrimeFacesFileItem#get()}
	 * and {@link
	 * com.liferay.faces.bridge.renderkit.primefaces.FileUploadRendererPrimeFacesImpl.UploadedFileInputStream#close()}.
	 */
	protected File getFile(String uniqueFolderName) {

		File file = null;

		try {
			File tempFolder = new File(System.getProperty("java.io.tmpdir"));
			File uniqueFolder = new File(tempFolder, uniqueFolderName);

			if (!uniqueFolder.exists()) {
				uniqueFolder.mkdirs();
			}

			String fileNamePrefix = "uploadedFile" + getId();
			String fileNameSuffix = ".dat";
			file = File.createTempFile(fileNamePrefix, fileNameSuffix, uniqueFolder);

			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(wrappedUploadedFile.getContents());
			outputStream.close();

			// Temporary file maintained by PrimeFaces is automatically deleted. See JavaDoc comments above.
		}
		catch (Exception e) {
			logger.error(e);
		}

		return file;
	}
}
