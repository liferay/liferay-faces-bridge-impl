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

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class UploadedFileWrapper implements Serializable, UploadedFile, FacesWrapper<org.richfaces.model.UploadedFile> {

	// serialVersionUID
	private static final long serialVersionUID = 1396113323129362295L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(UploadedFileWrapper.class);

	// Private Data Members
	private Map<String, Object> attributeMap;
	private File file;
	private String id;
	private Status status;
	private org.richfaces.model.UploadedFile wrappedUploadedFile;

	public UploadedFileWrapper(org.richfaces.model.UploadedFile uploadedFile, String uniqueFolderName) {
		this(uploadedFile, UploadedFile.Status.FILE_SAVED, uniqueFolderName);
	}

	public UploadedFileWrapper(org.richfaces.model.UploadedFile uploadedFile, UploadedFile.Status status,
		String uniqueFolderName) {
		this.wrappedUploadedFile = uploadedFile;
		this.attributeMap = new HashMap<String, Object>();
		this.id = Long.toString(((long) hashCode()) + System.currentTimeMillis());
		this.status = status;
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
		return wrappedUploadedFile.getHeader(name);
	}

	public Collection<String> getHeaderNames() {
		return wrappedUploadedFile.getHeaderNames();
	}

	public Collection<String> getHeaders(String name) {
		return wrappedUploadedFile.getHeaders(name);
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
		return wrappedUploadedFile.getName();
	}

	public long getSize() {
		return wrappedUploadedFile.getSize();
	}

	public Status getStatus() {
		return status;
	}

	public org.richfaces.model.UploadedFile getWrapped() {
		return wrappedUploadedFile;
	}

	public void write(String fileName) throws IOException {
		OutputStream outputStream = new FileOutputStream(fileName);
		outputStream.write(getBytes());
		outputStream.close();
	}

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
			outputStream.write(wrappedUploadedFile.getData());
			outputStream.close();

			// Delete the file maintained by RichFaces now that a copy of it has been made.
			wrappedUploadedFile.delete();
		}
		catch (Exception e) {
			logger.error(e);
		}

		return file;
	}

}
