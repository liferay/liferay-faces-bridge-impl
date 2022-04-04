/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.liferay.faces.demos.dto.PaymentType;


/**
 * @author  Neil Griffin
 */
@Named("paymentTypeService")
@ApplicationScoped
public class PaymentTypeServiceMockImpl implements PaymentTypeService {

	// Private Data Members
	private List<PaymentType> paymentTypes;

	@Override
	public List<PaymentType> getPaymentTypes() {
		return paymentTypes;
	}

	@PostConstruct
	public void postConstruct() {

		paymentTypes = new ArrayList<PaymentType>();
		paymentTypes.add(new PaymentType(1, "visa"));
		paymentTypes.add(new PaymentType(1, "mastercard"));
		paymentTypes.add(new PaymentType(1, "amex"));
		paymentTypes.add(new PaymentType(1, "discover"));
		paymentTypes.add(new PaymentType(1, "paypal"));
		paymentTypes = Collections.unmodifiableList(paymentTypes);
	}
}
