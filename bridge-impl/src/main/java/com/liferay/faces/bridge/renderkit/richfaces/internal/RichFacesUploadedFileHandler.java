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
package com.liferay.faces.bridge.renderkit.richfaces.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class RichFacesUploadedFileHandler implements InvocationHandler, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 8136440019333815546L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(RichFacesUploadedFileHandler.class);

	// Private Constants
	private static final String METHOD_DELETE = "delete";
	private static final String METHOD_GET_CONTENT_TYPE = "getContentType";
	private static final String METHOD_GET_DATA = "getData";
	private static final String METHOD_GET_INPUT_STREAM = "getInputStream";
	private static final String METHOD_GET_NAME = "getName";
	private static final String METHOD_GET_SIZE = "getSize";
	private static final String METHOD_WRITE = "write";

	// Private Data Members
	private UploadedFile uploadedFile;

	public RichFacesUploadedFileHandler(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		String methodName = method.getName();

		if (METHOD_DELETE.equals(methodName)) {
			File file = new File(uploadedFile.getAbsolutePath());
			file.delete();

			return null;
		}
		else if (METHOD_GET_CONTENT_TYPE.equals(methodName)) {
			return uploadedFile.getContentType();
		}
		else if (METHOD_GET_DATA.equals(methodName)) {
			return getBytes();
		}
		else if (METHOD_GET_INPUT_STREAM.equals(methodName)) {
			return new FileInputStream(uploadedFile.getAbsolutePath());
		}
		else if (METHOD_GET_NAME.equals(methodName)) {
			return uploadedFile.getName();
		}
		else if (METHOD_GET_SIZE.equals(methodName)) {
			return uploadedFile.getSize();
		}
		else if (METHOD_WRITE.equals(methodName)) {
			String fileName = (String) args[0];
			OutputStream outputStream = new FileOutputStream(fileName);
			outputStream.write(getBytes());
			outputStream.close();

			return null;
		}
		else {

			// Unsupported method.
			return null;
		}
	}

	protected byte[] getBytes() {
		byte[] bytes = null;

		try {
			File file = new File(uploadedFile.getAbsolutePath());

			if (file.exists()) {
				RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
				bytes = new byte[(int) randomAccessFile.length()];
				randomAccessFile.readFully(bytes);
				randomAccessFile.close();
			}
		}
		catch (Exception e) {
			logger.error(e);
		}

		return bytes;
	}
}
