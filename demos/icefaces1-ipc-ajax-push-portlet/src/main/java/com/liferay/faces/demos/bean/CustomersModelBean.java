/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.util.List;

import javax.annotation.PreDestroy;
import javax.faces.event.ValueChangeEvent;

import com.icesoft.faces.async.render.SessionRenderer;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.service.CustomerService;
import com.liferay.faces.demos.util.PortletSessionUtil;


/**
 * @author  Neil Griffin
 */
public class CustomersModelBean {

	// Private Constants
	private static final String CUSTOMER_RENDER_GROUP = "CUSTOMER_RENDER_GROUP";

	// Injections
	private CustomerService customerService;

	public CustomersModelBean() {
		SessionRenderer.addCurrentSession(CUSTOMER_RENDER_GROUP);
	}

	@PreDestroy
	public void preDestroy() {
		SessionRenderer.removeCurrentSession(CUSTOMER_RENDER_GROUP);
	}

	public void valueChangeListener(ValueChangeEvent valueChangeEvent) {
		SessionRenderer.render(CUSTOMER_RENDER_GROUP);
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getAllCustomers() {
		List<Customer> allCustomers = (List<Customer>) PortletSessionUtil.getSharedSessionAttribute(
				PortletSessionUtil.CUSTOMER_LIST);

		if (allCustomers == null) {
			allCustomers = customerService.getAllCustomers();
			PortletSessionUtil.setSharedSessionAttribute(PortletSessionUtil.CUSTOMER_LIST, allCustomers);
		}

		return allCustomers;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {

		// Injected via ManagedProperty annotation
		this.customerService = customerService;
	}

	public Customer getSelected() {
		return (Customer) PortletSessionUtil.getSharedSessionAttribute(PortletSessionUtil.SELECTED_CUSTOMER);
	}

	public void setSelected(Customer customer) {
		PortletSessionUtil.setSharedSessionAttribute(PortletSessionUtil.SELECTED_CUSTOMER, customer);
		SessionRenderer.render(CUSTOMER_RENDER_GROUP);
	}
}
