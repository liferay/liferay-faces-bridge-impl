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
package com.liferay.faces.issue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "FACES_2958Bean")
@RequestScoped
public class FACES_2958Bean {

	// Private Constants
	private static final String[] TEST_PARAMETER_NAME_SUFFIXES = new String[] { "20", "09", "0A", "0D" };
	private static final Map<String, List<String>> TEST_PARAMETERS;
	private static final String TEST_PARAMETERS_AS_STRING;

	static {

		Map<String, List<String>> testParameters = new LinkedHashMap<String, List<String>>();
		testParameters.put("param", Arrays.asList("paramValueWithoutSpaces"));

		for (String paramNameSuffix : TEST_PARAMETER_NAME_SUFFIXES) {

			Character unencodedParamChar = getUnencodedParamChar(paramNameSuffix);
			testParameters.put("param" + paramNameSuffix,
				Arrays.asList("encoded" + unencodedParamChar + "param" + unencodedParamChar + "value"));
		}

		TEST_PARAMETERS = Collections.unmodifiableMap(testParameters);

		String parametersString = "";
		Set<Map.Entry<String, List<String>>> entrySet = TEST_PARAMETERS.entrySet();
		boolean first = true;

		for (Map.Entry<String, List<String>> entry : entrySet) {

			if (first) {
				parametersString += "?";
			}
			else {
				parametersString += "&";
			}

			parametersString += entry.getKey() + "=" + entry.getValue().get(0);
		}

		TEST_PARAMETERS_AS_STRING = parametersString;
	}

	// Private Data Members
	private String testResultDetail;
	private String testResultStatus;

	public static boolean exceptionThrownWhenEncodingURLsWithWhitespace(ExternalContextCompat externalContextCompat) {

		boolean exceptionThrownWhenEncodingURLsWithWhitespace = true;

		try {

			externalContextCompat.encodeActionURL("path/to/view.xhtml" + TEST_PARAMETERS_AS_STRING);
			externalContextCompat.encodeBookmarkableURL("path/to/view.xhtml", TEST_PARAMETERS);
			externalContextCompat.encodePartialActionURL("path/to/view.xhtml" + TEST_PARAMETERS_AS_STRING);
			externalContextCompat.encodeRedirectURL("path/to/view.xhtml", TEST_PARAMETERS);
			externalContextCompat.encodeResourceURL("path/to/resource.js" + TEST_PARAMETERS_AS_STRING);
			exceptionThrownWhenEncodingURLsWithWhitespace = false;
		}
		catch (Exception e) {
			// Do nothing.
		}

		return exceptionThrownWhenEncodingURLsWithWhitespace;
	}

	public static boolean testWhitespaceEncoded(String url) {

		boolean whitespaceEncoded = true;

		if (url.contains("paramValueWithoutSpaces")) {

			whitespaceEncoded = false;

			for (String paramNameSuffix : TEST_PARAMETER_NAME_SUFFIXES) {

				Pattern paramPattern = Pattern.compile("param" + paramNameSuffix + "[=_]encoded([+]|[%]" +
						paramNameSuffix + ")param([+]|[%]" + paramNameSuffix + ")value");

				whitespaceEncoded = paramPattern.matcher(url).find();

				if (!whitespaceEncoded) {
					break;
				}
			}
		}

		return whitespaceEncoded;
	}

	public static boolean whitespaceEncodedInURLsWithParameters(ExternalContextCompat externalContextCompat) {
		return testWhitespaceEncoded(externalContextCompat.encodeBookmarkableURL("path/to/view.xhtml",
					TEST_PARAMETERS)) &&
			testWhitespaceEncoded(externalContextCompat.encodeRedirectURL("path/to/view.xhtml", TEST_PARAMETERS));
	}

	private static Character getUnencodedParamChar(String paramNameSuffix) {
		return Character.toChars(Integer.parseInt(paramNameSuffix, 16))[0];
	}

	public String getTestResultDetail() {

		if (testResultDetail == null) {
			getTestResultStatus();
		}

		return testResultDetail;
	}

	public String getTestResultStatus() {

		if (testResultStatus == null) {

			String testResultStatus = "SUCCESS";
			String testResultDetail = "";
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Application application = facesContext.getApplication();
			ApplicationCompat applicationCompat = new ApplicationCompat(application);

			for (String paramNameSuffix : TEST_PARAMETER_NAME_SUFFIXES) {

				String param = applicationCompat.evaluateExpressionGet(facesContext,
						"#{param['param" + paramNameSuffix + "']}", String.class);
				Character unencodedParamChar = getUnencodedParamChar(paramNameSuffix);

				if (!param.equals("encoded" + unencodedParamChar + "param" + unencodedParamChar + "value") &&
						!(unencodedParamChar.equals(' ') && param.equals("encoded+param+value"))) {

					testResultStatus = "FAILED";

					if (testResultDetail.length() > 0) {
						testResultDetail += ", ";
					}

					testResultDetail += "ASCII character with hex code <code>0x" + paramNameSuffix +
						"</code> (should be encoded as &quot;<code>%" + paramNameSuffix + "</code>&quot;)";
				}
			}

			if (testResultStatus.equals("FAILED")) {
				testResultDetail = "The following characters were not encoded: " + testResultDetail + ".";
			}
			else {

				ExternalContext externalContext = facesContext.getExternalContext();
				ExternalContextCompat externalContextCompat = new ExternalContextCompat(externalContext);
				String urlParam = applicationCompat.evaluateExpressionGet(facesContext, "#{param['urlParam']}",
						String.class);

				if (!urlParam.equals("http://liferay.com?name1=value1&name2=value2")) {

					testResultStatus = "FAILED";
					testResultDetail =
						"The url &quot;http://liferay.com?name1=value1&amp;name2=value2&quot; was not found encoded on the URL.";
				}
				else if (exceptionThrownWhenEncodingURLsWithWhitespace(externalContextCompat)) {

					testResultStatus = "FAILED";
					testResultDetail =
						"ExternalContext.encode*URL() method threw an exception when encoding URLs with whitespace characters.";
				}
				else if (!whitespaceEncodedInURLsWithParameters(externalContextCompat)) {

					testResultStatus = "FAILED";
					testResultDetail = "ExternalContext.encode*URL() methods failed to encode whitespace characters.";
				}
				else {
					testResultDetail = "All special URL characters were correctly encoded.";
				}
			}

			this.testResultStatus = testResultStatus;
			this.testResultDetail = testResultDetail;
		}

		return testResultStatus;
	}
}
