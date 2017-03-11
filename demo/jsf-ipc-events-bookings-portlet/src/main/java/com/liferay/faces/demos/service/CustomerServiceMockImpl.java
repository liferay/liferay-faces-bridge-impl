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
import javax.faces.bean.ManagedProperty;

import com.liferay.faces.demos.dto.Customer;


/**
 * @author  Neil Griffin
 */
@ApplicationScoped
@ManagedBean(name = "customerService")
public class CustomerServiceMockImpl implements CustomerService, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7241863951014091848L;

	// Public Constants
	public static final long ID_BRIAN_GREEN = 1;
	public static final long ID_LIZ_KESSLER = 2;
	public static final long ID_RICH_SHEARER = 3;

	// Private Data Members
	private ArrayList<Customer> allCustomers;

	// Injections
	@ManagedProperty(name = "bookingService", value = "#{bookingService}")
	private BookingService bookingService;

	public List<Customer> getAllCustomers() {
		return allCustomers;
	}

	public BookingService getBookingService() {
		return bookingService;
	}

	@PostConstruct
	public void postConstruct() {
		allCustomers = new ArrayList<Customer>();

		BookingService bookingService = getBookingService();
		Customer customer = new Customer(ID_BRIAN_GREEN, "Brian", "Green");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_BRIAN_GREEN));
		allCustomers.add(customer);
		customer = new Customer(ID_LIZ_KESSLER, "Liz", "Kessler");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_LIZ_KESSLER));
		allCustomers.add(customer);
		customer = new Customer(ID_RICH_SHEARER, "Rich", "Shearer");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_RICH_SHEARER));
		allCustomers.add(customer);
	}

	public void save(Customer customer) {

		for (int i = 0; i < allCustomers.size(); i++) {

			if (allCustomers.get(i).getCustomerId() == customer.getCustomerId()) {
				allCustomers.set(i, customer);
			}
		}
	}

	public void setBookingService(BookingService bookingService) {

		// Injected via @ManagedProperty annotation
		this.bookingService = bookingService;
	}
}
