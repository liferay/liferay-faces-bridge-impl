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
package com.liferay.faces.demos.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Airport;
import com.liferay.faces.demos.service.AirportService;


/**
 * @author  Neil Griffin
 */
@Named
@ViewScoped
public class FlightSearchModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4976012166797843537L;

	@Inject
	AirportService airportService;

	@Inject
	BookingFlowModelBean bookingFlowModelBean;

	@Inject
	ScopeTrackingBean scopeTrackingBean;

	// Private Data Members
	private String arrivalAirportName;
	private String arrivalCity;
	private String departureAirportName;
	private String departureCity;

	public String getArrivalAirportName() {
		return arrivalAirportName;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public String getDepartureAirportName() {
		return departureAirportName;
	}

	public String getDepartureCity() {
		return departureCity;
	}

	@PostConstruct
	public void postConstruct() {

		long departureAirportId = bookingFlowModelBean.getBookingDepartureId();
		Airport departureAirport = airportService.findById(departureAirportId);
		this.departureAirportName = departureAirport.getName();
		this.departureCity = departureAirport.getCity();

		long arrivalAirportId = bookingFlowModelBean.getBookingArrivalId();
		Airport arrivalAirport = airportService.findById(arrivalAirportId);
		this.setArrivalAirportName(arrivalAirport.getName());
		this.setArrivalCity(arrivalAirport.getCity());

		scopeTrackingBean.setFlightSearchModelBeanInScope(true);
	}

	@PreDestroy
	public void preDestroy() {
		scopeTrackingBean.setFlightSearchModelBeanInScope(false);
	}

	public void setArrivalAirportName(String arrivalAirportName) {
		this.arrivalAirportName = arrivalAirportName;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}
}
