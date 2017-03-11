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

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.bridge.component.inputfile.InputFile;


/**
 * This class is a {@link DelegationResponseWriter} that encodes HTML5 attributes for file upload components.
 *
 * @author  Neil Griffin
 */
public class InputFileDelegationResponseWriter extends InputFileDelegationResponseWriterCompat {

	public InputFileDelegationResponseWriter(ResponseWriter responseWriter) {
		super(responseWriter);
	}

	@Override
	public void startElement(String name, UIComponent uiComponent) throws IOException {

		super.startElement(name, uiComponent);

		if ("input".equals(name)) {

			InputFile inputFile = (InputFile) uiComponent;

			String multiple = inputFile.getMultiple();

			if ("multiple".equalsIgnoreCase(multiple)) {
				super.writeAttribute("multiple", multiple, "multiple");
			}
		}
	}
}
