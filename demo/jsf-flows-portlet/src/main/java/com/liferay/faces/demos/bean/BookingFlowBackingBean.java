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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Booking;
import com.liferay.faces.demos.service.BookingTypeService;


/**
 * @author  Neil Griffin
 */
@Named
@RequestScoped
public class BookingFlowBackingBean {

	@Inject
	BookingFlowModelBean bookingFlowModelBean;

	@Inject
	BookingTypeService bookingTypeService;

	public String addFlightToCart(long bookingId) {

		List<Booking> flights = bookingFlowModelBean.getFlights();

		for (Booking flight : flights) {

			if (flight.getBookingId() == bookingId) {
				bookingFlowModelBean.getCartBookings().add(flight);

				break;
			}
		}

		return "cart";
	}

	public String bookAdditionalTravel() {
		bookingFlowModelBean.clearBooking();

		return "booking";
	}

	public void bookingTypeIdChanged(ValueChangeEvent valueChangeEvent) {
		Long bookingTypeId = (Long) valueChangeEvent.getNewValue();

		if (bookingTypeId != null) {
			String bookingTypeName = bookingTypeService.getName(bookingTypeId);
			bookingFlowModelBean.setBookingTypeName(bookingTypeName);
		}
	}

	public void removeBooking(long bookingId) {

		List<Booking> cartBookings = bookingFlowModelBean.getCartBookings();

		Booking bookingToRemove = null;

		for (Booking cartBooking : cartBookings) {

			if (cartBooking.getBookingId() == bookingId) {
				bookingToRemove = cartBooking;

				break;
			}
		}

		if (bookingToRemove != null) {
			cartBookings.remove(bookingToRemove);
		}
	}
}
