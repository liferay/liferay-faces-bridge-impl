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

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;


/**
 * @author  Neil Griffin
 */
@Named
@SessionScoped
public class ScopeTrackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 8440875513428473371L;

	// Private Data Members
	private boolean bookingFlowModelBeanInScope = false;
	private boolean cartModelBeanInScope = false;
	private boolean flightSearchModelBeanInScope = false;
	private boolean surveyFlowModelBeanInScope = false;

	public boolean isBookingFlowModelBeanInScope() {
		return bookingFlowModelBeanInScope;
	}

	public boolean isCartModelBeanInScope() {
		return cartModelBeanInScope;
	}

	public boolean isFlightSearchModelBeanInScope() {
		return flightSearchModelBeanInScope;
	}

	public boolean isSurveyFlowModelBeanInScope() {
		return surveyFlowModelBeanInScope;
	}

	public void setBookingFlowModelBeanInScope(boolean bookingFlowModelBeanInScope) {
		this.bookingFlowModelBeanInScope = bookingFlowModelBeanInScope;
	}

	public void setCartModelBeanInScope(boolean cartModelBeanInScope) {
		this.cartModelBeanInScope = cartModelBeanInScope;
	}

	public void setFlightSearchModelBeanInScope(boolean flightSearchModelBeanInScope) {
		this.flightSearchModelBeanInScope = flightSearchModelBeanInScope;
	}

	public void setSurveyFlowModelBeanInScope(boolean surveyFlowModelBeanInScope) {
		this.surveyFlowModelBeanInScope = surveyFlowModelBeanInScope;
	}
}
