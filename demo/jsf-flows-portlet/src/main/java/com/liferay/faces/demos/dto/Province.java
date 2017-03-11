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
 * This is a bean that represents a Province, and implements the Transfer Object (formerly known as ValueObject/VO)
 * design pattern.
 *
 * @author  "Neil Griffin"
 */
public class Province implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4055995992262228819L;

	// JavaBean Properties
	private long provinceId;
	private long countryId;
	private String provinceName;
	private String provinceAbbreviation;

	public Province() {
		// Pluto requires a no-arg default constructor.
	}

	public Province(long provinceId, long countryId, String provinceName, String provinceAbbreviation) {
		this.provinceId = provinceId;
		this.setCountryId(countryId);
		this.provinceName = provinceName;
		this.provinceAbbreviation = provinceAbbreviation;
	}

	public long getCountryId() {
		return countryId;
	}

	public String getProvinceAbbreviation() {
		return provinceAbbreviation;
	}

	public long getProvinceId() {
		return provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public void setProvinceAbbreviation(String provinceAbbreviation) {
		this.provinceAbbreviation = provinceAbbreviation;
	}

	public void setProvinceId(long provinceId) {
		this.provinceId = provinceId;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
}
