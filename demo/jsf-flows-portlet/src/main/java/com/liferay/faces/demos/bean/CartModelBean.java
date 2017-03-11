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
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Booking;


/**
 * @author  Neil Griffin
 */
@Named
@ViewScoped
public class CartModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6106226342160723375L;

	@Inject
	BookingFlowModelBean bookingFlowModelBean;

	@Inject
	ScopeTrackingBean scopeTrackingBean;

	// Private Data Members
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {

		if (totalPrice == null) {

			totalPrice = new BigDecimal(0L);
			totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);

			List<Booking> cartBookings = bookingFlowModelBean.getCartBookings();

			for (Booking booking : cartBookings) {
				totalPrice = totalPrice.add(booking.getPrice());
			}
		}

		return totalPrice;
	}

	@PostConstruct
	public void postConstruct() {
		scopeTrackingBean.setCartModelBeanInScope(true);
	}

	@PreDestroy
	public void preDestroy() {
		scopeTrackingBean.setCartModelBeanInScope(false);
	}
}
