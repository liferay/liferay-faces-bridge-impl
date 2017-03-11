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

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import com.liferay.faces.demos.dto.BookingType;
import com.liferay.faces.demos.service.BookingTypeService;


/**
 * @author  Neil Griffin
 */
@ApplicationScoped
@ManagedBean(name = "listModelBean")
public class ListModelBean {

	// Private Data Memebers
	private List<BookingType> bookingTypes;

	// Injections
	@ManagedProperty(name = "bookingTypeService", value = "#{bookingTypeService}")
	private BookingTypeService bookingTypeService;

	public List<BookingType> getBookingTypes() {

		if (bookingTypes == null) {
			bookingTypes = bookingTypeService.getAllBookingTypes();
		}

		return bookingTypes;
	}

	public BookingTypeService getBookingTypeService() {
		return bookingTypeService;
	}

	public void setBookingTypeService(BookingTypeService bookingTypeService) {

		// Injected via @ManagedProperty annotation
		this.bookingTypeService = bookingTypeService;
	}
}
