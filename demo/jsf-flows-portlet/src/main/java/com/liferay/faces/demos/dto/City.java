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
 * This is a bean that represents a City, and implements the Transfer Object (formerly known as ValueObject/VO) design
 * pattern.
 *
 * @author  "Neil Griffin"
 */
public class City implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4456545299154327761L;

	// JavaBean Properties
	private long cityId;
	private String cityName;
	private String postalCode;
	private long provinceId;

	public City() {
		// Pluto requires a no-arg default constructor.
	}

	public City(long cityId, long provinceId, String cityName, String postalCode) {
		this.cityId = cityId;
		this.provinceId = provinceId;
		this.cityName = cityName;
		this.postalCode = postalCode;
	}

	public long getCityId() {
		return cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public long getProvinceId() {
		return provinceId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setProvinceId(long provinceId) {
		this.provinceId = provinceId;
	}
}
