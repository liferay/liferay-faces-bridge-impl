/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
public class CustomerServiceMockImpl implements CustomerService {

	// Public Constants
	public static final long ID_BRIAN_GREEN = 1;
	public static final long ID_LIZ_KESSLER = 2;
	public static final long ID_RICH_SHEARER = 3;

	// Private Data Members
	private ConcurrentMap<Long, Customer> allCustomers;

	// Injections
	@ManagedProperty(name = "bookingService", value = "#{bookingService}")
	private BookingService bookingService;

	@Override
	public List<Customer> getAllCustomers() {
		return Collections.unmodifiableList(new ArrayList<Customer>(allCustomers.values()));
	}

	public BookingService getBookingService() {
		return bookingService;
	}

	@PostConstruct
	public void postConstruct() {
		allCustomers = new ConcurrentHashMap<Long, Customer>();

		BookingService bookingService = getBookingService();
		Customer customer = new Customer(ID_BRIAN_GREEN, "Brian", "Green");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_BRIAN_GREEN));
		allCustomers.put(ID_BRIAN_GREEN, customer);
		customer = new Customer(ID_LIZ_KESSLER, "Liz", "Kessler");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_LIZ_KESSLER));
		allCustomers.put(ID_LIZ_KESSLER, customer);
		customer = new Customer(ID_RICH_SHEARER, "Rich", "Shearer");
		customer.setBookings(bookingService.getBookingsByCustomerId(ID_RICH_SHEARER));
		allCustomers.put(ID_RICH_SHEARER, customer);
	}

	@Override
	public void save(Customer customer) {

		long customerId = customer.getCustomerId();
		allCustomers.put(customerId, customer);
	}

	public void setBookingService(BookingService bookingService) {

		// Injected via @ManagedProperty annotation
		this.bookingService = bookingService;
	}
}
