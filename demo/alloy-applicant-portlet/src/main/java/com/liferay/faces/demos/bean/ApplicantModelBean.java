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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.liferay.faces.util.model.UploadedFile;


/**
 * This is a model managed bean that represents an applicant that is applying for a job.
 *
 * @author  "Neil Griffin"
 */
@ManagedBean(name = "applicantModelBean")
@ViewScoped
public class ApplicantModelBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7559628254337818761L;

	// Private Data Members
	private List<UploadedFile> uploadedFiles;
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

	public ApplicantModelBean() {
		clearProperties();

		Calendar calendar = new GregorianCalendar();
		this.dateOfBirth = calendar.getTime();
	}

	public void clearProperties() {
		uploadedFiles = new ArrayList<UploadedFile>();
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

	public List<UploadedFile> getUploadedFiles() {
		return uploadedFiles;
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
