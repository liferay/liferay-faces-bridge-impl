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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import com.liferay.faces.demos.dto.Airport;
import com.liferay.faces.demos.dto.Booking;


/**
 * @author  Neil Griffin
 */
@ApplicationScoped
public class FlightServiceMockImpl implements FlightService {

	// Private Constants
	private static final int AVG_SPEED_KM_PER_HOUR = 900;
	private static final double PRICE_USD_PER_KM = 0.1D;

	@Inject
	AirportService airportService;

	public FlightServiceMockImpl() {

		Calendar departureCalendar = new GregorianCalendar();
		Calendar arrivalCalendar = (Calendar) departureCalendar.clone();
		arrivalCalendar.add(Calendar.HOUR, 5);
	}

	// TODO
	public int getTimeZoneOffset(double latitude, double longitude) {

		String url = "http://www.earthtools.org/timezone/" + latitude + "/" + longitude;

		return 10;
	}

	@Override
	public List<Booking> searchDirect(long departureAirportId, Date departureDate, long arrivalAirportId) {

		DateFormat dateFormat = new SimpleDateFormat();
		List<Booking> searchResults = new ArrayList<Booking>();

		Airport departureAirport = airportService.findById(departureAirportId);
		double departureLatitude = departureAirport.getLatitude();
		double departureLongitude = departureAirport.getLongitude();
		LatLng departureLatLong = new LatLng(departureLatitude, departureLongitude);
		Airport arrivalAirport = airportService.findById(arrivalAirportId);
		double arrivalLatitude = arrivalAirport.getLatitude();
		double arrivalLongitude = arrivalAirport.getLongitude();
		LatLng arrivalLatLong = new LatLng(arrivalLatitude, arrivalLongitude);
		double distanceInKilometers = LatLngTool.distance(departureLatLong, arrivalLatLong, LengthUnit.KILOMETER);
		double durationInHours = distanceInKilometers / AVG_SPEED_KM_PER_HOUR;
		double priceUSD = distanceInKilometers * PRICE_USD_PER_KM;
		Calendar departureCalendar = new GregorianCalendar();
		departureCalendar.setTime(departureDate);
		departureCalendar.set(Calendar.HOUR_OF_DAY, 6);

		Random random = new Random();

		while (departureCalendar.get(Calendar.HOUR_OF_DAY) < 18) {

			Calendar arrivalCalendar = (Calendar) departureCalendar.clone();
			arrivalCalendar.add(Calendar.HOUR_OF_DAY, (int) Math.round(durationInHours));

			Booking flight = new Booking();
			Date flightDepartureDate = departureCalendar.getTime();
			flight.setDepartureDate(flightDepartureDate);
			flight.setArrivalId(arrivalAirportId);

			Date flightArrivalDate = arrivalCalendar.getTime();
			flight.setArrivalDate(flightArrivalDate);
			flight.setDistance(distanceInKilometers);
			flight.setDuration(durationInHours);
			flight.setBookingId(Math.abs(random.nextLong()));

			String flightNumber = Integer.toString(Math.abs(random.nextInt()));
			flight.setLabel(flightNumber);

			StringBuilder description = new StringBuilder();
			description.append("Flight#");
			description.append(flightNumber);
			description.append(" departing from ");
			description.append(departureAirport.getCity());
			description.append(" (");
			description.append(departureAirport.getCode());
			description.append(") on ");
			description.append(dateFormat.format(flightDepartureDate));
			description.append(" arriving at ");
			description.append(arrivalAirport.getCity());
			description.append("(");
			description.append(arrivalAirport.getCode());
			description.append(") on ");
			description.append(dateFormat.format(flightArrivalDate));
			flight.setDescription(description.toString());

			BigDecimal price = new BigDecimal(priceUSD);
			price.setScale(2, BigDecimal.ROUND_HALF_UP);
			flight.setPrice(price);
			departureCalendar.add(Calendar.MINUTE, 90);

			searchResults.add(flight);
		}

		return searchResults;
	}
}
