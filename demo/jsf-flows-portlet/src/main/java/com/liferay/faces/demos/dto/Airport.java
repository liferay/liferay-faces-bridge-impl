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


/**
 * @author  Neil Griffin
 */
public class Airport implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7460564078311191391L;

	// Private Data Members
	private long airportId;
	private String city;
	private String code;
	private String country;
	private double latitude;
	private double longitude;
	private String name;

	public Airport() {
		// Pluto requires a no-arg default constructor.
	}

	public long getAirportId() {
		return airportId;
	}

	public String getCity() {
		return city;
	}

	public String getCode() {
		return code;
	}

	public String getCountry() {
		return country;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public void setAirportId(long airportId) {
		this.airportId = airportId;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setName(String name) {
		this.name = name;
	}
}
