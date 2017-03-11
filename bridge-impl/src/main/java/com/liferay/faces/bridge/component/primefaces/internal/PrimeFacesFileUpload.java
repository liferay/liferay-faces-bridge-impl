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
package com.liferay.faces.bridge.component.primefaces.internal;

import java.lang.reflect.Method;

import javax.faces.component.UIInput;

import com.liferay.faces.bridge.component.internal.UIInputWrapper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This is a wrapper around the org.primefaces.component.fileupload.FileUpload component that has decorator methods that
 * call the wrapped component via reflection in order to avoid a compile-time dependency.
 *
 * @author  Neil Griffin
 */
public class PrimeFacesFileUpload extends UIInputWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PrimeFacesFileUpload.class);

	// Public Constants
	public static final String AJAX_FILE_UPLOAD = "ajax.file.upload";
	public static final String MODE_SIMPLE = "simple";
	public static final String RENDERER_TYPE = "org.primefaces.component.FileUploadRenderer";

	// Private Constants
	private static final String METHOD_GET_MODE = "getMode";

	// Private Data Members
	private UIInput wrappedUIInput;

	public PrimeFacesFileUpload(UIInput uiInput) {
		this.wrappedUIInput = uiInput;
	}

	public String getMode() {

		String mode = MODE_SIMPLE;
		Class<?> clazz = wrappedUIInput.getClass();

		try {
			Method method = clazz.getMethod(METHOD_GET_MODE, (Class[]) null);
			mode = (String) method.invoke(wrappedUIInput, (Object[]) null);
		}
		catch (Exception e) {
			logger.error(e);
		}

		return mode;
	}

	@Override
	public UIInput getWrapped() {
		return wrappedUIInput;
	}
}
