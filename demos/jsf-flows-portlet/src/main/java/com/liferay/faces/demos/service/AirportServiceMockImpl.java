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
package com.liferay.faces.demos.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Airport;
import com.liferay.faces.util.helper.LongHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@Named("airportService")
@ApplicationScoped
public class AirportServiceMockImpl implements AirportService {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(AirportServiceMockImpl.class);

	// Private Constants
	private static final String AIRPORTS_FILENAME = "airports.csv";
	private static final String AIRPORTS_URL =
		"https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat";
	private static final String CANADA = "Canada";
	private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
	private static final String UNITED_STATES = "United States";

	// Private Data Members
	private List<Airport> airports;
	private Map<Long, Airport> airportMap;

	public AirportServiceMockImpl() {

		this.airports = new ArrayList<Airport>();
		this.airportMap = new HashMap<Long, Airport>();

		String tempFolderPath = System.getProperty(JAVA_IO_TMPDIR);
		File tempFolder = new File(tempFolderPath);
		File airportsFile = new File(tempFolder, AIRPORTS_FILENAME);

		if (!airportsFile.exists()) {

			URL url = null;

			try {
				url = new URL(AIRPORTS_URL);

				logger.info("Downloading url=[{0}]", url);

				InputStream inputStream = url.openStream();
				OutputStream outputStream = new FileOutputStream(airportsFile);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();
			}
			catch (Exception e) {
				logger.warn("Error '{0}' when trying to download url=[{1}]", e.getMessage(), url);
			}
		}

		try {
			FileReader fileReader = new FileReader(airportsFile);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String csvLine;

			while ((csvLine = bufferedReader.readLine()) != null) {

				if (csvLine != null) {
					csvLine = csvLine.replaceAll(", ", " ");
					csvLine = csvLine.replaceAll("\"", "");

					String[] csvParts = csvLine.split(",");

					String country = csvParts[3];

					if (UNITED_STATES.equals(country) || CANADA.equals(country)) {

						Airport airport = new Airport();
						long airportId = LongHelper.toLong(csvParts[0]);
						airport.setAirportId(airportId);
						airport.setName(csvParts[1]);
						airport.setCity(csvParts[2]);
						airport.setCountry(country);
						airport.setCode(csvParts[4]);
						airport.setLatitude(Double.parseDouble(csvParts[6]));
						airport.setLongitude(Double.parseDouble(csvParts[7]));
						this.airports.add(airport);
						this.airportMap.put(airportId, airport);
					}
				}
			}

			bufferedReader.close();
			fileReader.close();
		}
		catch (Exception e) {
			logger.error(e);
		}

		Collections.sort(this.airports, new AirportComparator());
	}

	@Override
	public Airport findById(long airportId) {
		return airportMap.get(airportId);
	}

	@Override
	public List<Airport> getAirports() {
		return airports;
	}

	private static class AirportComparator implements Comparator<Airport> {

		@Override
		public int compare(Airport airport1, Airport airport2) {
			return airport1.getCity().compareTo(airport2.getCity());
		}
	}
}
