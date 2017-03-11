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
import javax.faces.flow.FlowScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@Named
@FlowScoped("survey")
public class SurveyFlowModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1400852859531077077L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SurveyFlowModelBean.class);

	@Inject
	ScopeTrackingBean scopeTrackingBean;

	// Private Data Members
	private String answer1;
	private String answer2;
	private Customer customer;

	public String getAnswer1() {
		return answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public Customer getCustomer() {
		return customer;
	}

	@PostConstruct
	public void postConstruct() {
		logger.debug("SurveyFlowModelBean initialized!");
		scopeTrackingBean.setSurveyFlowModelBeanInScope(true);
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("SurveyFlowModelBean going out of scope!");
		scopeTrackingBean.setSurveyFlowModelBeanInScope(false);
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
