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
package com.liferay.faces.issue;

import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;


/**
 * @author  Neil Griffin
 */
public class ExternalContextCompat {

	private ExternalContext externalContext;

	public ExternalContextCompat(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public String encodeActionURL(String url) {
		return externalContext.encodeActionURL(url);
	}

	public String encodeBookmarkableURL(String url, Map<String, List<String>> params) {
		return "";
	}

	public String encodePartialActionURL(String url) {
		return "";
	}

	public String encodeRedirectURL(String url, Map<String, List<String>> params) {
		return "";
	}

	public String encodeResourceURL(String url) {
		return externalContext.encodeResourceURL(url);
	}
}
