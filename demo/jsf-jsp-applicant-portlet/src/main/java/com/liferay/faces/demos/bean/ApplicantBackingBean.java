/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.liferay.faces.bridge.component.inputfile.InputFile;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.demos.dto.City;
import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This is a JSF backing managed-bean for the applicant.xhtml composition.
 *
 * @author  "Neil Griffin"
 */
@ManagedBean(name = "applicantBackingBean")
@RequestScoped
public class ApplicantBackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2947548873495692163L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantBackingBean.class);

	// Injections
	@ManagedProperty(value = "#{applicantModelBean}")
	private transient ApplicantModelBean applicantModelBean;
	@ManagedProperty(value = "#{applicantViewBean}")
	private transient ApplicantViewBean applicantViewBean;
	@ManagedProperty(value = "#{listModelBean}")
	private transient ListModelBean listModelBean;

	// Private Data Members
	private transient InputFile attachment1;
	private transient InputFile attachment2;
	private transient InputFile attachment3;

	public void deleteUploadedFile(ActionEvent actionEvent) {

		String fileId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(
				"fileId");

		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			UploadedFile uploadedFileToDelete = null;

			for (UploadedFile uploadedFile : uploadedFiles) {

				if (uploadedFile.getId().equals(fileId)) {
					uploadedFileToDelete = uploadedFile;

					break;
				}
			}

			if (uploadedFileToDelete != null) {
				File file = new File(uploadedFileToDelete.getAbsolutePath());
				file.delete();
				uploadedFiles.remove(uploadedFileToDelete);
				logger.debug("Deleted file=[{0}]", file);
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public InputFile getAttachment1() {
		return attachment1;
	}

	public InputFile getAttachment2() {
		return attachment2;
	}

	public InputFile getAttachment3() {
		return attachment3;
	}

	public void postalCodeListener(ValueChangeEvent valueChangeEvent) {

		try {
			String newPostalCode = (String) valueChangeEvent.getNewValue();
			City city = listModelBean.getCityByPostalCode(newPostalCode);

			if (city != null) {
				applicantModelBean.setAutoFillCity(city.getCityName());
				applicantModelBean.setAutoFillProvinceId(city.getProvinceId());
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
		}
	}

	public void setApplicantModelBean(ApplicantModelBean applicantModelBean) {

		// Injected via @ManagedProperty annotation
		this.applicantModelBean = applicantModelBean;
	}

	public void setApplicantViewBean(ApplicantViewBean applicantViewBean) {

		// Injected via @ManagedProperty annotation
		this.applicantViewBean = applicantViewBean;
	}

	public void setAttachment1(InputFile attachment1) {
		this.attachment1 = attachment1;
	}

	public void setAttachment2(InputFile attachment2) {
		this.attachment2 = attachment2;
	}

	public void setAttachment3(InputFile attachment3) {
		this.attachment3 = attachment3;
	}

	public void setListModelBean(ListModelBean listModelBean) {

		// Injected via @ManagedProperty annotation
		this.listModelBean = listModelBean;
	}

	public String submit() {

		if (logger.isDebugEnabled()) {
			logger.debug("firstName=" + applicantModelBean.getFirstName());
			logger.debug("lastName=" + applicantModelBean.getLastName());
			logger.debug("emailAddress=" + applicantModelBean.getEmailAddress());
			logger.debug("phoneNumber=" + applicantModelBean.getPhoneNumber());
			logger.debug("dateOfBirth=" + applicantModelBean.getDateOfBirth());
			logger.debug("city=" + applicantModelBean.getCity());
			logger.debug("provinceId=" + applicantModelBean.getProvinceId());
			logger.debug("postalCode=" + applicantModelBean.getPostalCode());

			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				logger.debug("uploadedFile=[{0}]", uploadedFile.getName());
			}
		}

		// Delete the uploaded files.
		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				File file = new File(uploadedFile.getAbsolutePath());
				file.delete();
				logger.debug("Deleted file=[{0}]", file);
			}

			// Store the applicant's first name in JSF 2 Flash Scope so that it can be picked up
			// for use inside of confirmation.xhtml
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.getExternalContext().getFlash().put("firstName", applicantModelBean.getFirstName());

			applicantModelBean.clearProperties();

			return "success";

		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();

			return "failure";
		}
	}

	@SuppressWarnings("unchecked")
	public void uploadAttachments(ActionEvent actionEvent) {

		List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

		List<UploadedFile> uploadedFiles1 = (List<UploadedFile>) attachment1.getValue();

		if (uploadedFiles1 != null) {
			uploadedFiles.addAll(uploadedFiles1);
		}

		List<UploadedFile> uploadedFiles2 = (List<UploadedFile>) attachment2.getValue();

		if (uploadedFiles2 != null) {
			uploadedFiles.addAll(uploadedFiles2);
		}

		List<UploadedFile> uploadedFiles3 = (List<UploadedFile>) attachment3.getValue();

		if (uploadedFiles3 != null) {
			uploadedFiles.addAll(uploadedFiles3);
		}

		applicantViewBean.setFileUploaderRendered(false);
	}
}
