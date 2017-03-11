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
package com.liferay.faces.demos.event;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.service.CustomerService;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class CustomerEditedEventHandler implements BridgeEventHandler {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CustomerEditedEventHandler.class);

	public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {
		EventNavigationResult eventNavigationResult = null;
		String eventQName = event.getQName().toString();

		if (eventQName.equals("{http://liferay.com/events}ipc.customerEdited")) {
			Customer customer = (Customer) event.getValue();
			getCustomerService(facesContext).save(customer);
			logger.debug("Received event ipc.customerEdited for customerId=[{0}] firstName=[{1}] lastName=[{2}]",
				new Object[] { customer.getCustomerId(), customer.getFirstName(), customer.getLastName() });
		}

		return eventNavigationResult;
	}

	protected CustomerService getCustomerService(FacesContext facesContext) {
		String elExpression = "#{customerService}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, CustomerService.class);

		return (CustomerService) valueExpression.getValue(elContext);
	}
}
