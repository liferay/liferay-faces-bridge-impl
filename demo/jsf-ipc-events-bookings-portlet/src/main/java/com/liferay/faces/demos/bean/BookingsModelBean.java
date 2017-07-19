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
package com.liferay.faces.demos.bean;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.liferay.faces.demos.dto.Booking;
import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.event.CustomerSelectedEventHandler;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is a JSF model managed-bean that contains the selected customer. First, the Portlet 2.0 EVENT_PHASE
 * broadcasts the IPC events. At that point, the bridge executes the RESTORE_VIEW phase of the JSF lifecycle so that the
 * CustomerSelectedEventHandler.handleEvent(FacesContext, Event) method can handle the "ipc.customerSelected" event as
 * defined in the WEB-INF/portlet.xml descriptor. Then, the Portlet 2.0 RENDER_PHASE will cause the RENDER_RESPONSE
 * phase of the JSF lifecycle to be executed. Note that this class has to be {@link SessionScoped} instead of {@link
 * ViewScoped} because the {@link Customer} received in the {@link CustomerSelectedEventHandler} (which is injected into
 * this bean) has to survive the navigation from the noCustomerSelected.xhtml view to the customers.xhtml view due to
 * the navigation-rule in the WEB-INF/faces-config.xml descriptor. Also, it needs to be {@link SessionScoped} instead of
 * {@link RequestScoped} because otherwise the bookings form data submitted in customers.xhtml won't have a
 * correctly-sized List&lt;Booking&gt; for the Faces APPLY_REQUEST_VALUES phase to postback into.
 *
 * @author  Neil Griffin
 */
public class BookingsModelBean {


	private static final Logger logger = LoggerFactory.getLogger(BookingsModelBean.class);

	// Private Bean Properties
	Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	@PostConstruct
	public void postConstruct() {
		logger.trace("@PostConstruct annotation worked");
		customer = new Customer();
		customer.setBookings(new ArrayList<Booking>(5));
	}

	@PreDestroy
	public void preDestroy() {
		logger.trace("@PreDestroy annotation worked");
	}

	public void setCustomer(Customer customer) {

		// Called by the CustomerEditedEventHandler
		this.customer = customer;
	}
}
