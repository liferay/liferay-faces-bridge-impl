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

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.liferay.faces.demos.dto.Booking;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "bookingService")
@ApplicationScoped
public class BookingServiceMockImpl implements BookingService {

	// Private Data Members
	private List<Booking> allBookings;

	public List<Booking> getAllBookings() {

		if (allBookings == null) {
			allBookings = new ArrayList<Booking>();

			// Bookings for Brian Green
			long customerId = CustomerServiceMockImpl.ID_BRIAN_GREEN;
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_RENTAL_CAR, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_HOTEL, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_AIRFARE, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_THEME_PARK, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_PLAY, customerId));

			// Bookings for Elizabeth Kessler
			customerId = CustomerServiceMockImpl.ID_LIZ_KESSLER;
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_HOTEL, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_AIRFARE, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_THEME_PARK, customerId));

			// Bookings for Rich Shearer
			customerId = CustomerServiceMockImpl.ID_RICH_SHEARER;
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_AIRFARE, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_RENTAL_CAR, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_PLAY, customerId));
			allBookings.add(new Booking(BookingTypeServiceMockImpl.TYPE_ID_HOTEL, customerId));
		}

		return allBookings;
	}

	public List<Booking> getBookingsByCustomerId(long customerId) {
		List<Booking> bookings = new ArrayList<Booking>();

		for (Booking booking : getAllBookings()) {

			if (booking.getCustomerId() == customerId) {
				bookings.add(booking);
			}
		}

		return bookings;
	}
}
