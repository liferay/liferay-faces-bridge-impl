/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseWriter;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.internal.BridgeURLBaseCompat;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.render.FacesURLEncoder;


/**
 * @author  Neil Griffin
 */
public class URLUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(URLUtil.class);

	public static String encodeParameterNameOrValue(String nameOrValue, String encoding)
		throws UnsupportedEncodingException {

		String encodedNameOrValue = nameOrValue;

		if (nameOrValue != null) {
			encodedNameOrValue = URLEncoder.encode(nameOrValue, encoding);
		}

		return encodedNameOrValue;
	}

	public static String[] encodeParameterValues(String[] values, String encoding) throws UnsupportedEncodingException {
		return encodeOrDecodeParameterValues(true, values, encoding);
	}

	/**
	 * This method encodes a URL's illegal characters with percent encoding. The returned URL will not have XML
	 * characters (such as the ampersand character) escaped with their XML entities (such as &amp;) regardless of
	 * whether the passed URL includes escaped XML entities. In other words, this method does not preserve the XML
	 * escaping of the original URL.
	 *
	 * @param   url
	 * @param   facesURLEncoder
	 * @param   encoding
	 *
	 * @return
	 */
	public static String encodeURL(String url, FacesURLEncoder facesURLEncoder, String encoding) {

		String encodedURL = url;

		if (url != null) {

			// Ensure that the facesURLEncoder does not encode any spaces as pluses ("+") as recommended in the bridge
			// spec section 6.1.3.1 "Methods that deviate from Faces 1.2 Javadoc."
			encodedURL = url.replace(" ", "%20");
			encodedURL = facesURLEncoder.encode(encodedURL, encoding);

			// Undo any escaping performed by the facesURLEncoder.
			encodedURL = encodedURL.replace("&amp;", "&");
		}

		return encodedURL;
	}

	public static String getURLCharacterEncoding(ExternalContext externalContext, ResponseWriter responseWriter,
		String defaultEncoding) {

		Map<String, Object> requestMap = externalContext.getRequestMap();
		Bridge.PortletPhase portletPhase = (Bridge.PortletPhase) requestMap.get(Bridge.PORTLET_LIFECYCLE_PHASE);

		return getURLCharacterEncoding(portletPhase, externalContext, responseWriter, defaultEncoding);
	}

	public static String getURLCharacterEncoding(Bridge.PortletPhase portletPhase, ExternalContext externalContext,
		ResponseWriter responseWriter, String defaultEncoding) {

		String encoding = defaultEncoding;

		if (BridgeURLBaseCompat.isHeaderOrRenderOrResourcePhase(portletPhase)) {
			encoding = externalContext.getResponseCharacterEncoding();
		}
		else {

			if (responseWriter != null) {
				encoding = responseWriter.getCharacterEncoding();
			}
		}

		return encoding;
	}

	public static Map<String, String[]> parseParameterMapValuesArray(String url, String encoding) {

		Map<String, String[]> parameterMapValuesArray = new LinkedHashMap<String, String[]>();

		if (url != null) {
			int pos = url.indexOf("?");

			if (pos > 0) {
				String queryString = url.substring(pos + 1);
				queryString = queryString.replaceAll("&amp;", "&");

				if ((queryString != null) && (queryString.length() > 0)) {

					pos = queryString.indexOf("#");

					if (pos > 0) {
						queryString = queryString.substring(0, pos);
					}

					String[] queryParameters = queryString.split("[&]");

					for (String queryParameter : queryParameters) {

						String[] nameValueArray = queryParameter.split("[=]");

						String name = nameValueArray[0].trim();
						String[] existingValues = parameterMapValuesArray.get(name);

						if (nameValueArray.length == 1) {

							String[] newValues;

							if (existingValues == null) {
								newValues = new String[] { "" };
							}
							else {
								newValues = Arrays.copyOf(existingValues, existingValues.length + 1);
								newValues[existingValues.length] = "";
							}

							putParameter(parameterMapValuesArray, name, newValues, encoding);
						}
						else if (nameValueArray.length == 2) {

							if (name.length() == 0) {
								logger.error("Invalid name=value pair=[{0}] in URL=[{1}]: name cannot be empty",
									nameValueArray, url);
							}
							else {

								String[] newValues;

								if (existingValues == null) {
									newValues = new String[] { nameValueArray[1] };
								}
								else {
									newValues = Arrays.copyOf(existingValues, existingValues.length + 1);
									newValues[existingValues.length] = nameValueArray[1];
								}

								putParameter(parameterMapValuesArray, name, newValues, encoding);
							}
						}
						else {
							logger.error("Invalid name=value pair=[{0}] in URL=[{1}]", nameValueArray, url);
						}
					}
				}
			}
		}

		return parameterMapValuesArray;
	}

	public static Map<String, List<String>> parseParameterMapValuesList(String url) {

		Map<String, List<String>> parameterMapValuesList = new LinkedHashMap<String, List<String>>();
		Map<String, String[]> parameterMapValuesArray = parseParameterMapValuesArray(url, null);
		Set<Entry<String, String[]>> entrySet = parameterMapValuesArray.entrySet();

		for (Map.Entry<String, String[]> mapEntry : entrySet) {
			String[] mapEntryValues = mapEntry.getValue();
			List<String> values = new ArrayList<String>(mapEntryValues.length);

			for (int i = 0; i < mapEntryValues.length; i++) {
				values.add(mapEntryValues[i]);
			}

			parameterMapValuesList.put(mapEntry.getKey(), values);
		}

		return parameterMapValuesList;
	}

	private static String decodeParameterNameOrValue(String nameOrValue, String encoding)
		throws UnsupportedEncodingException {

		String decodedNameOrValue = nameOrValue;

		if (nameOrValue != null) {
			decodedNameOrValue = URLDecoder.decode(nameOrValue, encoding);
		}

		return decodedNameOrValue;
	}

	private static String[] decodeParameterValues(String[] values, String encoding)
		throws UnsupportedEncodingException {
		return encodeOrDecodeParameterValues(false, values, encoding);
	}

	private static String[] encodeOrDecodeParameterValues(boolean encode, String[] values, String encoding)
		throws UnsupportedEncodingException {

		String[] encodedValues = values;

		if (values != null) {

			encodedValues = new String[values.length];

			for (int i = 0; i < values.length; i++) {

				if (encode) {
					encodedValues[i] = URLUtil.encodeParameterNameOrValue(values[i], encoding);
				}
				else {
					encodedValues[i] = URLUtil.decodeParameterNameOrValue(values[i], encoding);
				}
			}
		}

		return encodedValues;
	}

	private static void putParameter(Map<String, String[]> parameterMapValuesArray, String name, String[] newValues,
		String encoding) {

		if (encoding != null) {

			try {

				name = decodeParameterNameOrValue(name, encoding);
				newValues = decodeParameterValues(newValues, encoding);
			}
			catch (UnsupportedEncodingException e) {

				logger.error("Unable to decode parameter name=\"{0}\" and values=\"{1}\" with encoding \"{2}\".", name,
					newValues, encoding);
				logger.error(e);
			}
		}

		parameterMapValuesArray.put(name, newValues);
	}
}
