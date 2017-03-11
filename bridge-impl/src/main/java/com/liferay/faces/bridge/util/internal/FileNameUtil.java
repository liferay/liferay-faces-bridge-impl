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
package com.liferay.faces.bridge.util.internal;

import java.util.HashMap;
import java.util.Map;


/**
 * This class contains static helper/convenience methods for {@link java.io.File} objects and {@link String} filenames.
 *
 * @author  Neil Griffin
 */
public class FileNameUtil {

	private static Map<String, String> mimeTypeMap;

	static {
		mimeTypeMap = new HashMap<String, String>();
		mimeTypeMap.put("css", "text/css");
		mimeTypeMap.put("csv", "text/csv");
		mimeTypeMap.put("doc", "application/msword");
		mimeTypeMap.put("gif", "image/gif");
		mimeTypeMap.put("groovy", "application/x-groovy");
		mimeTypeMap.put("gz", "application/x-gzip");
		mimeTypeMap.put("jpg", "image/jpeg");
		mimeTypeMap.put("jpeg", "image/jpeg");
		mimeTypeMap.put("js", "text/javascript");
		mimeTypeMap.put("kml", "application/vnd.google-earth.kml+xml");
		mimeTypeMap.put("mov", "video/quicktime");
		mimeTypeMap.put("mp4", "audio/mp4");
		mimeTypeMap.put("mp3", "audio/mpeg");
		mimeTypeMap.put("mpg", "video/mpeg");
		mimeTypeMap.put("mpeg", "video/mpeg");
		mimeTypeMap.put("ogg", "application/ogg");
		mimeTypeMap.put("pdf", "application/pdf");
		mimeTypeMap.put("png", "image/png");
		mimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
		mimeTypeMap.put("properties", "text/plain");
		mimeTypeMap.put("rar", "application/x-rar-compressed");
		mimeTypeMap.put("rss", "application/rss+xml");
		mimeTypeMap.put("swf", "application/x-shockwave-flash");
		mimeTypeMap.put("svg", "image/svg+xml");
		mimeTypeMap.put("tar", "application/x-tar");
		mimeTypeMap.put("tif", "image/tiff");
		mimeTypeMap.put("tiff", "image/tiff");
		mimeTypeMap.put("txt", "text/plain");
		mimeTypeMap.put("wav", "audio/vnd.wave");
		mimeTypeMap.put("wma", "audio/x-ms-wma");
		mimeTypeMap.put("xls", "application/vnd.ms-excel");
		mimeTypeMap.put("xml", "text/xml");
		mimeTypeMap.put("zip", "application/zip");
	}

	public static String getFileNameExtension(String fileName) {
		String fileNameExtension = null;
		int pos = fileName.lastIndexOf('.');

		if ((pos > 0) && (pos < fileName.length())) {
			fileNameExtension = fileName.substring(pos + 1);
		}

		return fileNameExtension;
	}

	public static String getFileNameMimeType(String fileName) {
		return mimeTypeMap.get(getFileNameExtension(fileName));
	}
}
