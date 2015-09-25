/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
public class BookingListModelBean {

	// Private Data Memebers
	private List<SelectItem> bookingTypes;

	// Injections
	private BookingTypeService bookingTypeService;

	public List<SelectItem> getBookingTypes() {

		if (bookingTypes == null) {
			List<BookingType> bookingTypeList = bookingTypeService.getAllBookingTypes();
			bookingTypes = new ArrayList<SelectItem>(bookingTypeList.size());

			for (BookingType bookingType : bookingTypeList) {
				SelectItem selectItem = new SelectItem(bookingType.getBookingTypeId(),
						bookingType.getBookingTypeName());
				bookingTypes.add(selectItem);
			}
		}

		return bookingTypes;
	}

	public BookingTypeService getBookingTypeService() {
		return bookingTypeService;
	}

	public void setBookingTypeService(BookingTypeService bookingTypeService) {

		// Injected via WEB-INF/faces-config.xml managed-property
		this.bookingTypeService = bookingTypeService;
	}
}
