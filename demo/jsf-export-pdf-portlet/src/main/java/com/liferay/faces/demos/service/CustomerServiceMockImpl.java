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
package com.liferay.faces.demos.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.liferay.faces.demos.dto.Customer;


/**
 * @author  Neil Griffin
 */
@ApplicationScoped
@ManagedBean(name = "customerService")
public class CustomerServiceMockImpl implements CustomerService, Serializable {

	// Public Constants
	public static final long ID_BRIAN_GREEN = 1;
	public static final long ID_LIZ_KESSLER = 2;
	public static final long ID_RICH_SHEARER = 3;

	// serialVersionUID
	private static final long serialVersionUID = 6515562063250005643L;

	// Private Data Members
	private ArrayList<Customer> allCustomers;

	public List<Customer> getAllCustomers() {
		return allCustomers;
	}

	public Customer getCustomer(long customerId) {

		Customer customer = null;
		List<Customer> customers = getAllCustomers();

		for (Customer curCustomer : customers) {

			if (curCustomer.getCustomerId() == customerId) {
				customer = curCustomer;

				break;
			}
		}

		return customer;
	}

	@PostConstruct
	public void postConstruct() {
		allCustomers = new ArrayList<Customer>();

		Customer customer = new Customer(ID_BRIAN_GREEN, "Brian", "Green");
		allCustomers.add(customer);
		customer = new Customer(ID_LIZ_KESSLER, "Liz", "Kessler");
		allCustomers.add(customer);
		customer = new Customer(ID_RICH_SHEARER, "Rich", "Shearer");
		allCustomers.add(customer);
	}

	public void save(Customer customer) {

		for (int i = 0; i < allCustomers.size(); i++) {

			if (allCustomers.get(i).getCustomerId() == customer.getCustomerId()) {
				allCustomers.set(i, customer);
			}
		}
	}
}
