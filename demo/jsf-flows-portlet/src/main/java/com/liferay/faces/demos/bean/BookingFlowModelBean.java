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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.flow.FlowScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Booking;
import com.liferay.faces.demos.dto.Country;
import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.service.CountryService;
import com.liferay.faces.demos.service.FlightService;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@Named
@FlowScoped("booking")
public class BookingFlowModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 8604734954883987583L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BookingFlowModelBean.class);

	@Inject
	private CountryService countryService;

	@Inject
	private FlightService flightService;

	@Inject
	private ScopeTrackingBean scopeTrackingBean;

	// Private Data Members
	private long bookingArrivalId;
	private Date bookingDepartureDate;
	private long bookingDepartureId;
	private long bookingTypeId;
	private String bookingTypeName;
	private List<Booking> cartBookings;
	private Customer customer;
	private List<Booking> flights;

	public BookingFlowModelBean() {
		this.cartBookings = new ArrayList<Booking>();
		this.customer = new Customer();
		clearBooking();
	}

	public void clearBooking() {
		bookingArrivalId = 0L;
		bookingDepartureDate = null;
		bookingDepartureId = 0L;
		bookingTypeId = 0L;
		bookingTypeName = "none";
		flights = null;
	}

	public long getBookingArrivalId() {
		return bookingArrivalId;
	}

	public Date getBookingDepartureDate() {
		return bookingDepartureDate;
	}

	public long getBookingDepartureId() {
		return bookingDepartureId;
	}

	public long getBookingTypeId() {
		return bookingTypeId;
	}

	public String getBookingTypeName() {
		return bookingTypeName;
	}

	public List<Booking> getCartBookings() {
		return cartBookings;
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<Booking> getFlights() {

		if (flights == null) {
			flights = flightService.searchDirect(bookingDepartureId, bookingDepartureDate, bookingArrivalId);
		}

		return flights;
	}

	@PostConstruct
	public void postContruct() {
		Country unitedStates = countryService.findByAbbreviation("US");
		customer.setCountryId(unitedStates.getCountryId());
		logger.debug("BookingFlowModelBean initialized!");
		scopeTrackingBean.setBookingFlowModelBeanInScope(true);
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("BookingFlowModelBean going out of scope!");
		scopeTrackingBean.setBookingFlowModelBeanInScope(false);
	}

	public void setBookingArrivalId(long bookingArrivalId) {
		this.bookingArrivalId = bookingArrivalId;
	}

	public void setBookingDepartureDate(Date bookingDepartureDate) {
		this.bookingDepartureDate = bookingDepartureDate;
	}

	public void setBookingDepartureId(long bookingDepartureId) {
		this.bookingDepartureId = bookingDepartureId;
	}

	public void setBookingTypeId(long bookingTypeId) {
		this.bookingTypeId = bookingTypeId;
	}

	public void setBookingTypeName(String bookingTypeName) {
		this.bookingTypeName = bookingTypeName;
	}

	public void setCartBookings(List<Booking> cartBookings) {
		this.cartBookings = cartBookings;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
