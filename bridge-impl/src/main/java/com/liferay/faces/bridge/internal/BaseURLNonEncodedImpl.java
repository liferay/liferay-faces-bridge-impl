/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public class BaseURLNonEncodedImpl extends BaseURLBridgeURIAdapterImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BaseURLNonEncodedImpl.class);

	// Private Final Data Members
	private final String encoding;

	public BaseURLNonEncodedImpl(BridgeURI bridgeURI, String encoding) {

		super(bridgeURI);
		this.encoding = encoding;
	}

	@Override
	public String toString() {

		String uri = bridgeURI.toString();
		String decodedURL = uri;

		if (uri != null) {

			try {
				decodedURL = URLDecoder.decode(uri, encoding);
			}
			catch (UnsupportedEncodingException e) {

				logger.error("Failed to decode uri=\"{0}\" with encoding=\"{1}\"", uri, encoding);
				logger.error(e);
			}
		}

		return decodedURL;
	}
}
