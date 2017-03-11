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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author  Neil Griffin
 */
public class Customer implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6155011527137171447L;

	// Private Bean Properties
	private List<Booking> bookings;
	private long customerId;
	private String firstName;
	private String lastName;
	private boolean selected;

	public Customer() {
	}

	public Customer(long customerId, String firstName, String lastName) {
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public List<Booking> getBookings() {

		if (bookings == null) {
			bookings = new ArrayList<Booking>();
		}

		return bookings;
	}

	public long getCustomerId() {
		return customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
