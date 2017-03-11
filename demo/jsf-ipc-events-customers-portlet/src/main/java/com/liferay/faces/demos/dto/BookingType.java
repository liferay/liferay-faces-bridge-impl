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
package com.liferay.faces.demos.dto;

/**
 * @author  Neil Griffin
 */
public class BookingType {

	private String bookingTypeName;
	private long bookingTypeId;

	public BookingType(long bookingTypeId, String bookingTypeName) {
		this.bookingTypeId = bookingTypeId;
		this.bookingTypeName = bookingTypeName;
	}

	public long getBookingTypeId() {
		return bookingTypeId;
	}

	public String getBookingTypeName() {
		return bookingTypeName;
	}

	public void setBookingTypeId(long bookingTypeId) {
		this.bookingTypeId = bookingTypeId;
	}

	public void setBookingTypeName(String bookingTypeName) {
		this.bookingTypeName = bookingTypeName;
	}
}
