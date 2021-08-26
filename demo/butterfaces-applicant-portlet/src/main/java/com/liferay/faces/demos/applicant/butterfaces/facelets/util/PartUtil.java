/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.applicant.butterfaces.facelets.util;

import javax.servlet.http.Part;


/**
 * @author  Neil Griffin
 */
public class PartUtil {

	public static String getFileName(Part part) {

		String contentDisposition = part.getHeader("Content-Disposition");

		if (contentDisposition != null) {
			String[] nameValuePairs = contentDisposition.split(";");

			for (String nameValuePair : nameValuePairs) {
				nameValuePair = nameValuePair.trim();

				if (nameValuePair.startsWith("filename")) {
					int pos = nameValuePair.indexOf("=");

					if (pos > 0) {
						return nameValuePair.substring(pos + 1).replace("\"", "");
					}
				}
			}
		}

		return null;
	}
}
