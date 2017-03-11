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
 * @author  Neil Griffin
 */
public class PublicRenderParameterImpl extends PublicRenderParameterCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PublicRenderParameter.class);

	// Private Data Members
	protected String modelValue;
	protected Boolean modelValueChanged;
	protected boolean modelValueRetrieved;

	public PublicRenderParameterImpl(String prefix, String originalRequestValue, String originalModelEL,
		String portletName) {
		super(prefix, originalRequestValue, originalModelEL, portletName);
	}

	public String getModelValue(FacesContext facesContext) {

		if (!modelValueRetrieved) {

			try {

				ELContext elContext = facesContext.getELContext();
				ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory()
					.createValueExpression(elContext, modelEL, String.class);
				modelValue = (String) valueExpression.getValue(elContext);
			}
			catch (PropertyNotFoundException e) {
				String exceptionMessage = e.getMessage();

				if (exceptionMessage == null) {
					logger.error("javax.el.PropertyNotFoundException: model-el=[{0}]", modelEL);
				}
				else {
					logger.error("javax.el.PropertyNotFoundException: {0}: model-el=[{1}]", exceptionMessage, modelEL);
				}
			}

			modelValueRetrieved = true;
		}

		return modelValue;
	}

	public String getModifiedModelEL() {
		return modelEL;
	}

	public boolean isForThisPortlet() {
		return forThisPortlet;
	}

	public boolean isModelValueChanged(FacesContext facesContext) {

		if (modelValueChanged == null) {

			String retrievedModelValue = getModelValue(facesContext);

			if ((retrievedModelValue != null) && (originalRequestValue != null)) {
				modelValueChanged = !retrievedModelValue.equals(originalRequestValue);
			}
			else if (retrievedModelValue == null) {
				modelValueChanged = (originalRequestValue != null);
			}
			else if (originalRequestValue == null) {
				modelValueChanged = (retrievedModelValue != null);
			}
			else {
				modelValueChanged = Boolean.FALSE;
			}
		}

		return modelValueChanged;
	}
}
