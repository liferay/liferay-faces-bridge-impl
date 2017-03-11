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

import javax.faces.FacesWrapper;

import com.liferay.faces.demos.dto.Customer;


/**
 * @author  Neil Griffin
 */
public class CustomerWrapper extends Customer implements FacesWrapper<Customer> {

	// serialVersionUID
	private static final long serialVersionUID = 2212060479044678860L;

	// Private Data Members
	private Customer wrappedCustomer;
	private String exportResourceURL;

	public CustomerWrapper(Customer wrappedCustomer, String exportURL) {
		this.wrappedCustomer = wrappedCustomer;
		this.exportResourceURL = exportURL;
	}

	@Override
	public long getCustomerId() {
		return getWrapped().getCustomerId();
	}

	public String getExportResourceURL() {
		return exportResourceURL;
	}

	@Override
	public String getFirstName() {
		return getWrapped().getFirstName();
	}

	@Override
	public String getLastName() {
		return getWrapped().getLastName();
	}

	public Customer getWrapped() {
		return wrappedCustomer;
	}

	@Override
	public void setCustomerId(long customerId) {
		getWrapped().setCustomerId(customerId);
	}

	public void setExportResourceURL(String exportResourceURL) {
		this.exportResourceURL = exportResourceURL;
	}

	@Override
	public void setFirstName(String firstName) {
		getWrapped().setFirstName(firstName);
	}

	@Override
	public void setLastName(String lastName) {
		getWrapped().setLastName(lastName);
	}

}
