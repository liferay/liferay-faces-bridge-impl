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
package com.liferay.faces.demos.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Country;


/**
 * @author  Neil Griffin
 */
@Named("countryService")
@ApplicationScoped
public class CountryServiceMockImpl implements CountryService {

	// Private Data Members
	private List<Country> countries;

	@Override
	public Country findByAbbreviation(String countryAbbreviation) {

		Country country = null;

		getCountries();

		for (Country curCountry : countries) {

			if (curCountry.getCountryAbbreviation().equals(countryAbbreviation)) {
				country = curCountry;

				break;
			}
		}

		return country;
	}

	@Override
	public List<Country> getCountries() {

		if (countries == null) {

			countries = new ArrayList<Country>();
			countries.add(new Country(1, "United States", "US"));
			countries.add(new Country(2, "Canada", "CA"));
		}

		return countries;
	}
}
