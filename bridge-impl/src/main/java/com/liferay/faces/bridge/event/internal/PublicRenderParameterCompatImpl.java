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
package com.liferay.faces.bridge.event.internal;

import javax.el.ELContext;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class PublicRenderParameterCompatImpl extends PublicRenderParameterBaseImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PublicRenderParameterCompatImpl.class);

	public PublicRenderParameterCompatImpl(String prefix, String originalRequestValue, String originalModelEL,
		String portletName) {
		super(prefix, originalRequestValue, originalModelEL, portletName);
	}

	public boolean injectIntoModel(FacesContext facesContext) {

		try {

			ELContext elContext = facesContext.getELContext();
			ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory()
				.createValueExpression(elContext, modelEL, String.class);
			valueExpression.setValue(elContext, originalRequestValue);

			return true;
		}
		catch (PropertyNotFoundException e) {
			String exceptionMessage = e.getMessage();

			if (exceptionMessage == null) {
				logger.error("javax.el.PropertyNotFoundException: model-el=[{0}]", modelEL);
			}
			else {
				logger.error("javax.el.PropertyNotFoundException: {0}: model-el=[{1}]", exceptionMessage, modelEL);
			}

			return false;
		}
	}

}
