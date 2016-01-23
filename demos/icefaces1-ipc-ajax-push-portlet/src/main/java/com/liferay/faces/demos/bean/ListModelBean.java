/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.liferay.faces.demos.dto.BookingType;
import com.liferay.faces.demos.service.BookingTypeService;


/**
 * @author  Neil Griffin
 */
public class ListModelBean {

	// Private Data Memebers
	private List<SelectItem> selectItems;

	// Injections
	private BookingTypeService bookingTypeService;

	public List<SelectItem> getBookingTypeSelectItems() {

		if (selectItems == null) {
			selectItems = new ArrayList<SelectItem>();

			List<BookingType> bookingTypes = bookingTypeService.getAllBookingTypes();

			for (BookingType bookingType : bookingTypes) {
				selectItems.add(new SelectItem(bookingType.getBookingTypeId(), bookingType.getBookingTypeName()));
			}
		}

		return selectItems;
	}

	public BookingTypeService getBookingTypeService() {
		return bookingTypeService;
	}

	public void setBookingTypeService(BookingTypeService bookingTypeService) {

		// Injected via @ManagedProperty annotation
		this.bookingTypeService = bookingTypeService;
	}
}
