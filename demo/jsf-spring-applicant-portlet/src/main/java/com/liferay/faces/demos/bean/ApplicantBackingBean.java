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
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.liferay.faces.bridge.event.FileUploadEvent;
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
@Named
@Scope("request")
public class ApplicantBackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7950793873895036307L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantBackingBean.class);

	// Injections
	@Inject
	private transient ApplicantModelBean applicantModelBean;
	@Inject
	private transient ListModelBean listModelBean;

	public void deleteUploadedFile(ActionEvent actionEvent) {

		UICommand uiCommand = (UICommand) actionEvent.getComponent();
		String fileId = (String) uiCommand.getValue();

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
				uploadedFileToDelete.delete();
				uploadedFiles.remove(uploadedFileToDelete);
				logger.debug("Deleted file=[{0}]", uploadedFileToDelete.getName());
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public void handleFileUpload(FileUploadEvent fileUploadEvent) throws Exception {
		List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();
		UploadedFile uploadedFile = fileUploadEvent.getUploadedFile();

		if (uploadedFile != null) {

			if (uploadedFile.getStatus() == UploadedFile.Status.FILE_SAVED) {
				uploadedFiles.add(uploadedFile);
				logger.debug("Received fileName=[{0}] absolutePath=[{1}]", uploadedFile.getName(),
					uploadedFile.getAbsolutePath());
			}
			else {
				logger.error("Uploaded file status=[" + uploadedFile.getStatus().toString() + "] " +
					uploadedFile.getMessage());
				FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
			}
		}
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

		// Injected via @Inject annotation
		this.applicantModelBean = applicantModelBean;
	}

	public void setListModelBean(ListModelBean listModelBean) {

		// Injected via @Inject annotation
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
			logger.debug("comments=" + applicantModelBean.getComments());

			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				logger.debug("uploadedFile=[{0}]", uploadedFile.getName());
			}
		}

		// Delete the uploaded files.
		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			for (UploadedFile uploadedFile : uploadedFiles) {
				uploadedFile.delete();
				logger.debug("Deleted file=[{0}]", uploadedFile.getName());
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
}
