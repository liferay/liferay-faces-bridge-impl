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
package com.liferay.faces.demos.applicant.html5.facelets.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * This is a model managed bean that represents an applicant that is applying for a job.
 *
 * @author  "Neil Griffin"
 */
public class Applicant implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7459628254337818761L;

	// Private Data Members
	private List<Attachment> attachments;
	private String city;
	private String comments;
	private Date dateOfBirth;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String postalCode;
	private Long provinceId;

	// Private Data Members (auto-fill)
	private String autoFillCity;
	private Long autoFillProvinceId;

	public Applicant() {
		clearProperties();

		Calendar calendar = new GregorianCalendar();
		this.dateOfBirth = calendar.getTime();
	}

	public void clearProperties() {
		attachments = new ArrayList<Attachment>();
		city = null;
		comments = null;
		dateOfBirth = null;
		emailAddress = null;
		firstName = null;
		lastName = null;
		phoneNumber = null;
		postalCode = null;
		provinceId = null;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public String getCity() {

		if (autoFillCity == null) {
			return city;
		}
		else {
			return autoFillCity;
		}
	}

	public String getComments() {
		return comments;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public Long getProvinceId() {

		if (autoFillProvinceId == null) {
			return provinceId;
		}
		else {
			return autoFillProvinceId;
		}
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setAutoFillCity(String autoFillCity) {
		this.autoFillCity = autoFillCity;
	}

	public void setAutoFillProvinceId(Long autoFillProvinceId) {
		this.autoFillProvinceId = autoFillProvinceId;
	}

	public void setCity(String city) {

		if (autoFillCity == null) {
			this.city = city;
		}
		else {
			this.city = autoFillCity;
			autoFillCity = null;
		}
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setProvinceId(Long provinceId) {

		if (autoFillProvinceId == null) {
			this.provinceId = provinceId;
		}
		else {
			this.provinceId = autoFillProvinceId;
			autoFillProvinceId = null;
		}
	}

}
