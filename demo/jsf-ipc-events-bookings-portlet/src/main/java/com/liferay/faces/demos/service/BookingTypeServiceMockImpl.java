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

import com.liferay.faces.demos.dto.BookingType;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "bookingTypeService")
@ApplicationScoped
public class BookingTypeServiceMockImpl implements BookingTypeService {

	// Public Constants
	public static final long TYPE_ID_AIRFARE = 1;
	public static final long TYPE_ID_CRUISE = 2;
	public static final long TYPE_ID_HOTEL = 3;
	public static final long TYPE_ID_PLAY = 4;
	public static final long TYPE_ID_RENTAL_CAR = 5;
	public static final long TYPE_ID_THEME_PARK = 6;
	public static final long TYPE_ID_TRAIN = 7;

	// Private Data Members
	private List<BookingType> allBookingTypes;

	public List<BookingType> getAllBookingTypes() {

		if (allBookingTypes == null) {
			allBookingTypes = new ArrayList<BookingType>();
			allBookingTypes.add(new BookingType(TYPE_ID_AIRFARE, "Airfare"));
			allBookingTypes.add(new BookingType(TYPE_ID_CRUISE, "Cruise"));
			allBookingTypes.add(new BookingType(TYPE_ID_HOTEL, "Hotel"));
			allBookingTypes.add(new BookingType(TYPE_ID_PLAY, "Play/Theatre"));
			allBookingTypes.add(new BookingType(TYPE_ID_RENTAL_CAR, "Rental Car"));
			allBookingTypes.add(new BookingType(TYPE_ID_THEME_PARK, "Theme Park"));
			allBookingTypes.add(new BookingType(TYPE_ID_TRAIN, "Train"));
		}

		return allBookingTypes;
	}
}
