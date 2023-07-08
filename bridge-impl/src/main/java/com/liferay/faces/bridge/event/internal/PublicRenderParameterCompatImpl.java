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
package com.liferay.faces.bridge.event.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ELContext;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class PublicRenderParameterCompatImpl extends PublicRenderParameterBaseImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PublicRenderParameterCompatImpl.class);

	// Private Constants
	private static final boolean MOJARRA_DETECTED = ProductFactory.getProduct(Product.Name.MOJARRA).isDetected();

	public PublicRenderParameterCompatImpl(String prefix, String originalRequestValue, String originalModelEL,
		String portletName) {
		super(prefix, originalRequestValue, originalModelEL, portletName);
	}

	/**
	 * <p>This method provides a workaround for an issue in Mojarra 1.2 that prevents String-based managed-bean
	 * instances from having their values set properly via EL-expressions. For more information, see:
	 * http://java.net/jira/browse/JAVASERVERFACES-1803</p>
	 */
	public boolean injectIntoModel(FacesContext facesContext) {

		try {

			// Inject the value via the EL-expression.
			ELContext elContext = facesContext.getELContext();
			ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory()
				.createValueExpression(elContext, modelEL, String.class);
			valueExpression.setValue(elContext, originalRequestValue);

			// If Mojarra is detected, then
			if (MOJARRA_DETECTED) {

				// Get the value that Mojarra claims to have injected.
				Object mojarraInjectedValue = valueExpression.getValue(elContext);

				// If the injected value doesn't match up with the value that Mojarra claims to have injected, and the
				// value is an empty String, then Mojarra simply resolved the EL-expression to a managed-bean and failed
				// to actually set the value (replace the managed-bean instance with a new String instance).
				if ((originalRequestValue != mojarraInjectedValue) && (mojarraInjectedValue != null) &&
						(mojarraInjectedValue instanceof String) && (((String) mojarraInjectedValue).length() == 0)) {

					// If the value that is to be injected is a String-based managed-bean in request scope, then
					// update the value directly in the request map.
					ExternalContext externalContext = facesContext.getExternalContext();
					Map<String, Object> requestMap = externalContext.getRequestMap();
					Entry<String, Object> mapEntry = getMapEntryByValue(requestMap, mojarraInjectedValue);

					if (mapEntry != null) {
						requestMap.put(mapEntry.getKey(), originalRequestValue);
					}

					// Otherwise, if the value that is to be injected is a managed-bean in session scope, then update
					// the value directly in the session map.
					else {
						Map<String, Object> sessionMap = externalContext.getSessionMap();
						mapEntry = getMapEntryByValue(sessionMap, mojarraInjectedValue);

						if (mapEntry != null) {
							sessionMap.put(mapEntry.getKey(), originalRequestValue);
						}

						// Otherwise, if the value that is to be injected is a managed-bean in application scope, then
						// update the value directly in the application map.
						else {
							Map<String, Object> applicationMap = externalContext.getApplicationMap();
							mapEntry = getMapEntryByValue(applicationMap, mojarraInjectedValue);

							if (mapEntry != null) {
								applicationMap.put(mapEntry.getKey(), originalRequestValue);
							}
						}
					}
				}
			}

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

	protected Map.Entry<String, Object> getMapEntryByValue(Map<String, Object> map, Object value) {

		Map.Entry<String, Object> mapEntry = null;
		Set<Entry<String, Object>> entrySet = map.entrySet();

		for (Map.Entry<String, Object> curMapEntry : entrySet) {
			Object curValue = curMapEntry.getValue();

			if ((curValue == value)) {
				mapEntry = curMapEntry;

				break;
			}
		}

		return mapEntry;
	}

}
