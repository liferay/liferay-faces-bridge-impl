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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.liferay.faces.demos.list.CustomersDataModel;
import com.liferay.faces.demos.service.CustomerService;


/**
 * This is a model managed bean that manages customer data.
 *
 * @author  "Neil Griffin"
 */
@ManagedBean(name = "customersModelBean")
@ViewScoped
public class CustomersModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7459638254337818761L;

	// Injections
	@ManagedProperty(value = "#{customerService}")
	private CustomerService customerService;

	// Private Data Members
	private CustomersDataModel customers;

	public CustomersDataModel getCustomers() {

		if (customers == null) {
			customers = new CustomersDataModel(customerService);
		}

		return customers;
	}

	public void setCustomerService(CustomerService customerService) {

		// Injected via ManagedProperty annotation.
		this.customerService = customerService;
	}
}
