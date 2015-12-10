/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;

import com.liferay.faces.demos.dto.Customer;


/**
 * @author  Neil Griffin
 */
public class CustomersBackingBean {

	// Injections
	private CustomersModelBean customersModelBean;

	public void selectionListener(ActionEvent actionEvent) {
		UICommand uiCommand = (UICommand) actionEvent.getComponent();
		Customer customer = (Customer) uiCommand.getValue();
		customersModelBean.setSelected(customer);
	}

//  public void selectionListener(RowSelectorEvent rowSelectorEvent) {
//      Customer customer = customersModelBean.getAllCustomers().get(rowSelectorEvent.getRow());
//      customersModelBean.setSelected(customer);
//      System.err.println("!@#$ rowSelected!");
//  }

	public void setCustomersModelBean(CustomersModelBean customersModelBean) {

		// Injected via WEB-INF/faces-config.xml managed-property
		this.customersModelBean = customersModelBean;
	}
}
