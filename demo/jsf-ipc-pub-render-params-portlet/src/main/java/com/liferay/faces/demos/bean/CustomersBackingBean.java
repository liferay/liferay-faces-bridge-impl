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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "customersBackingBean")
@RequestScoped
public class CustomersBackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2920712441012786321L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(CustomersBackingBean.class);

	// Injections
	@ManagedProperty(name = "customersModelBean", value = "#{customersModelBean}")
	private CustomersModelBean customersModelBean;

	@PostConstruct
	public void postConstruct() {
		logger.trace("@PostConstruct annotation worked");
	}

	@PreDestroy
	public void preDestroy() {
		logger.trace("@PreDestroy annotation worked");
	}

	/**
	 * This method is a JSF action listener that is called when the user clicks on a customer. By calling {@link
	 * CustomersModelBean#setSelectedCustomerId(String)} it may seem that we are simply recording the selected
	 * customerId in the model. However, there is more than meets the eye. I the WEB-INF/faces-config.xml descriptor
	 * there is an EL expression of #{customersPortlet:customersModelBean.selectedCustomerId} which means that the
	 * bridge will be monitoring the status of that model bean property. If the value changes in the model bean, then
	 * the bridge will automatically call {@link BookingsModelBean#setSelectedCustomerId(String)} because of the other
	 * model-el Expression #{bookingsPortlet:bookingsModelBean.selectedCustomerId} found in the WEB-INF/faces-config.xml
	 * descriptor.
	 */
	public void selectionListener(ActionEvent actionEvent) {
		UICommand uiCommand = (UICommand) actionEvent.getComponent();
		Customer customer = (Customer) uiCommand.getValue();
		logger.debug("User click! selectedCustomerId=[{0}]", customer.getCustomerId());
		customersModelBean.setSelectedCustomerId(Long.toString(customer.getCustomerId()));
	}

	public void setCustomersModelBean(CustomersModelBean customersModelBean) {

		// Injected via ManagedProperty annotation.
		this.customersModelBean = customersModelBean;
	}
}
