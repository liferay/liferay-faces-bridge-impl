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

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.service.CustomerService;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "customersModelBean")
@RequestScoped
public class CustomersModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2241487919972557504L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CustomersModelBean.class);

	// Injections
	@ManagedProperty(name = "customerService", value = "#{customerService}")
	private CustomerService customerService;

	// Private Bean Properties
	private List<Customer> allCustomers;
	private String selectedCustomerId;

	public List<Customer> getAllCustomers() {
		return allCustomers;
	}

	public String getSelectedCustomerId() {
		return selectedCustomerId;
	}

	@PostConstruct
	public void postConstruct() {
		logger.trace("@PostConstruct annotation worked");
		allCustomers = customerService.getAllCustomers();
	}

	@PreDestroy
	public void preDestroy() {
		logger.trace("@PreDestroy annotation worked");
	}

	public void setCustomerService(CustomerService customerService) {

		// Injected via ManagedProperty annotation
		this.customerService = customerService;
	}

	public void setSelectedCustomerId(String selectedCustomerId) {
		this.selectedCustomerId = selectedCustomerId;
	}
}
