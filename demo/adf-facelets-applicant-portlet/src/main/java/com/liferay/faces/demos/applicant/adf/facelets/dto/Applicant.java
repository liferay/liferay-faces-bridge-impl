/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.applicant.adf.facelets.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.liferay.faces.util.model.UploadedFile;


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

	@NotBlank
	private String city;
	private String comments;

	@NotNull
	private Date dateOfBirth;

	@Email
	@NotBlank
	private String emailAddress;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	@Pattern(regexp = "([0-9]+)")
	private String phoneNumber;

	@NotBlank
	private String postalCode;

	@NotNull
	@Positive
	private Long provinceId;

	public Applicant() {
		clearProperties();

		Calendar calendar = new GregorianCalendar();
		this.dateOfBirth = calendar.getTime();
	}

	public void clearProperties() {
		attachments = new ArrayList<>();
		city = null;
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
		return city;
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
		return provinceId;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setCity(String city) {
		this.city = city;
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
		this.provinceId = provinceId;
	}
}
