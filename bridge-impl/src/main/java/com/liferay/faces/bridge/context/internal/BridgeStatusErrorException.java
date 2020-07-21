/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.internal;

import javax.portlet.faces.BridgeException;


/**
 * @author  Kyle Stiemann
 */
public class BridgeStatusErrorException extends BridgeException {

	// serialVersionUID
	private static final long serialVersionUID = 6836710625809465305L;

	// Private Final Data Members
	private final int httpStatusCode;

	public BridgeStatusErrorException(int httpStatusCode) {
		super(getMessage(httpStatusCode, null));
		this.httpStatusCode = httpStatusCode;
	}

	public BridgeStatusErrorException(int httpStatusCode, String message) {
		super(getMessage(httpStatusCode, message));
		this.httpStatusCode = httpStatusCode;
	}

	public BridgeStatusErrorException(int httpStatusCode, Throwable cause) {
		super(getMessage(httpStatusCode, null), cause);
		this.httpStatusCode = httpStatusCode;
	}

	public BridgeStatusErrorException(int httpStatusCode, String message, Throwable cause) {
		super(getMessage(httpStatusCode, message), cause);
		this.httpStatusCode = httpStatusCode;
	}

	private static String getMessage(int httpStatusCode, String message) {

		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append("Status code ");
		messageBuilder.append(httpStatusCode);

		if (message != null) {

			messageBuilder.append(": ");
			messageBuilder.append(message);
		}

		return messageBuilder.toString();
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
