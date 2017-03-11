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
package com.liferay.faces.demos.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * @author  Neil Griffin
 */
public class Customer implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 3155011527137171447L;

	// Private Bean Properties
	private String accountNumber;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private Long countryId;
	private Long customerId;
	private String cvv;
	private String emailAddress;
	private Date expirationMonth;
	private String firstName;
	private String lastName;
	private Long paymentTypeId;
	private String phoneNumber;
	private Long provinceId;
	private String postalCode;
	private Long titleId;

	public Customer() {
		// Pluto requires a no-arg default constructor.
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public String getCity() {
		return city;
	}

	public Long getCountryId() {
		return countryId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getCvv() {
		return cvv;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public Date getExpirationMonth() {
		return expirationMonth;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getPaymentTypeId() {
		return paymentTypeId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public Long getTitleId() {
		return titleId;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setExpirationMonth(Date expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPaymentTypeId(Long paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}
}
