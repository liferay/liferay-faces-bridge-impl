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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;

import javax.faces.context.ExternalContext;


/**
 * @author  Neil Griffin
 */
public abstract class IncongruityContextCompatImpl extends IncongruityContextBaseImpl {

	@Override
	public int getResponseContentLength() {

		// no-op for JSF 1.2
		return 0;
	}

	@Override
	public void setRequestContentLength(int length) {

		// no-op for JSF 1.2
	}

	@Override
	public void setResponseCommitted(boolean committed) {

		// no-op for JSF 1.2
	}

	protected void makeCongruousJSF2(ExternalContext externalContext, IncongruousAction incongruousAction)
		throws IOException {

		// no-op for JSF 1.2
	}

}
