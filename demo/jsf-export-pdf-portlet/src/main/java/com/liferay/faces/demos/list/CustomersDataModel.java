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
package com.liferay.faces.demos.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.ListDataModel;

import com.liferay.faces.demos.dto.Customer;
import com.liferay.faces.demos.resource.CustomerExportResource;
import com.liferay.faces.demos.service.CustomerService;


/**
 * @author  Neil Griffin
 */
public class CustomersDataModel extends ListDataModel<CustomerWrapper> implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1639576409231493573L;

	public CustomersDataModel(CustomerService customerService) {

		List<Customer> customers = customerService.getAllCustomers();
		List<CustomerWrapper> wrappedData = new ArrayList<CustomerWrapper>(customers.size());

		if (customers != null) {

			for (Customer customer : customers) {
				CustomerExportResource customerExportResource = new CustomerExportResource(customer);
				String exportResourceURL = customerExportResource.getRequestPath();
				wrappedData.add(new CustomerWrapper(customer, exportResourceURL));
			}

			setWrappedData(wrappedData);
		}
	}

}
