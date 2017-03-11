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

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.bridge.event.EventPayloadWrapper;
import com.liferay.faces.demos.bean.BookingsModelBean;
import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class CustomerSelectedEventHandler implements BridgeEventHandler {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CustomerSelectedEventHandler.class);

	public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {
		EventNavigationResult eventNavigationResult = null;
		String eventQName = event.getQName().toString();

		if (eventQName.equals("{http://liferay.com/events}ipc.customerSelected")) {
			Serializable value = event.getValue();

			// FACES-1465: If the payload is wrapped, then a redirect may have taken place. In any case, get the
			// payload from the wrapper.
			if (value instanceof EventPayloadWrapper) {
				value = ((EventPayloadWrapper) value).getWrapped();
			}

			Customer customer = (Customer) value;
			BookingsModelBean bookingsModelBean = getBookingsModelBean(facesContext);
			bookingsModelBean.setCustomer(customer);

			String fromAction = null;
			String outcome = "ipc.customerSelected";
			eventNavigationResult = new EventNavigationResult(fromAction, outcome);
			logger.debug("Received event ipc.customerSelected for customerId=[{0}] firstName=[{1}] lastName=[{2}]",
				new Object[] { customer.getCustomerId(), customer.getFirstName(), customer.getLastName() });
		}

		return eventNavigationResult;
	}

	protected BookingsModelBean getBookingsModelBean(FacesContext facesContext) {
		String elExpression = "#{bookingsModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, BookingsModelBean.class);

		return (BookingsModelBean) valueExpression.getValue(elContext);
	}
}
