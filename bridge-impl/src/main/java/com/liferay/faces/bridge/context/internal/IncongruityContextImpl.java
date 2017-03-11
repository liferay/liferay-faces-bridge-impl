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
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class IncongruityContextImpl extends IncongruityContextCompat_2_3_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(IncongruityContextImpl.class);

	@Override
	public void makeCongruous(FacesContext facesContext) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();

		Set<IncongruousAction> incongruousActions = getIncongruousActions();

		for (IncongruousAction incongruousAction : incongruousActions) {

			if (incongruousAction == IncongruousAction.SET_REQUEST_CHARACTER_ENCODING) {
				String requestCharacterEncoding = getRequestCharacterEncoding();
				logger.debug("setRequestCharacterEncoding(\"{0}\")", requestCharacterEncoding);
				externalContext.setRequestCharacterEncoding(requestCharacterEncoding);
			}
			else if (incongruousAction == IncongruousAction.SET_RESPONSE_CHARACTER_ENCODING) {
				String responseCharacterEncoding = getResponseCharacterEncoding();
				logger.debug("setResponseCharacterEncoding(\"{0}\")", responseCharacterEncoding);
				externalContext.setResponseCharacterEncoding(responseCharacterEncoding);
			}
			else {
				makeCongruousJSF2(externalContext, incongruousAction);
			}
		}
	}

}
