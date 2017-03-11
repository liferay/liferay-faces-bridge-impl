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
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author  Neil Griffin
 */
public class Booking implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2384129997014505129L;

	// Private Data Members
	private Date arrivalDate;
	private long arrivalId;
	private long bookingId;
	private long bookingTypeId;
	private long customerId;
	private long departureId;
	private String description;
	private double distance;
	private double duration;
	private Date departureDate;
	private String label;
	private BigDecimal price;

	public Booking() {
		// Pluto requires a no-arg default constructor.
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public long getArrivalId() {
		return arrivalId;
	}

	public long getBookingId() {
		return bookingId;
	}

	public long getBookingTypeId() {
		return bookingTypeId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public long getDepartureId() {
		return departureId;
	}

	public String getDescription() {
		return description;
	}

	public double getDistance() {
		return distance;
	}

	public double getDuration() {
		return duration;
	}

	public String getLabel() {
		return label;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public void setArrivalId(long arrivalId) {
		this.arrivalId = arrivalId;
	}

	public void setBookingId(long bookingId) {
		this.bookingId = bookingId;
	}

	public void setBookingTypeId(long bookingTypeId) {
		this.bookingTypeId = bookingTypeId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public void setDepartureId(long departureId) {
		this.departureId = departureId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
