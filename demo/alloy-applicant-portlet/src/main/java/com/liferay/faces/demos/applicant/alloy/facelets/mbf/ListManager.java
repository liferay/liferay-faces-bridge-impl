/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.applicant.alloy.facelets.mbf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.liferay.faces.demos.applicant.alloy.facelets.dto.Applicant;
import com.liferay.faces.demos.applicant.alloy.facelets.dto.Attachment;
import com.liferay.faces.demos.applicant.alloy.facelets.dto.City;
import com.liferay.faces.demos.applicant.alloy.facelets.dto.Province;


/**
 * @author  Neil Griffin
 */
@ApplicationScoped
@ManagedBean(name = "listManager")
public class ListManager {

	// Private Data Members
	private List<City> cities;
	private List<Province> provinces;

	public List<City> getCities() {
		return cities;
	}

	public City getCityByPostalCode(String postalCode) {

		for (City city : cities) {

			if (city.getPostalCode().equals(postalCode)) {
				return city;
			}
		}

		return null;
	}

	public long getProvinceId(String provinceName) {
		long provinceId = 0;

		for (Province province : provinces) {

			if (province.getProvinceName().equals(provinceName)) {
				provinceId = province.getProvinceId();

				break;
			}
		}

		return provinceId;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	@PostConstruct
	public void postConstruct() {

		long provinceId = 1;
		provinces = new ArrayList<Province>();

		Province province = new Province(provinceId++, "DE");
		provinces.add(province);
		province = new Province(provinceId++, "GA");
		provinces.add(province);
		province = new Province(provinceId++, "FL");
		provinces.add(province);
		province = new Province(provinceId++, "MD");
		provinces.add(province);
		province = new Province(provinceId++, "NC");
		provinces.add(province);
		province = new Province(provinceId++, "NJ");
		provinces.add(province);
		province = new Province(provinceId++, "NY");
		provinces.add(province);
		province = new Province(provinceId++, "SC");
		provinces.add(province);
		province = new Province(provinceId++, "VA");
		provinces.add(province);
		provinces = Collections.unmodifiableList(provinces);

		long cityId = 1;
		cities = new ArrayList<City>();

		City city = new City(cityId++, getProvinceId("DE"), "Wilmington", "19806");
		cities.add(city);
		city = new City(cityId++, getProvinceId("GA"), "Atlanta", "30329");
		cities.add(city);
		city = new City(cityId++, getProvinceId("FL"), "Orlando", "32801");
		cities.add(city);
		city = new City(cityId++, getProvinceId("MD"), "Baltimore", "21224");
		cities.add(city);
		city = new City(cityId++, getProvinceId("NC"), "Charlotte", "28202");
		cities.add(city);
		city = new City(cityId++, getProvinceId("NJ"), "Hoboken", "07030");
		cities.add(city);
		city = new City(cityId++, getProvinceId("NY"), "Albany", "12205");
		cities.add(city);
		city = new City(cityId++, getProvinceId("SC"), "Columbia", "29201");
		cities.add(city);
		city = new City(cityId++, getProvinceId("VA"), "Roanoke", "24013");
		cities.add(city);
		cities = Collections.unmodifiableList(cities);
	}
}
