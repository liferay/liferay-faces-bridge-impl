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

import java.io.File;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.demos.dto.City;
import com.liferay.faces.demos.dto.UploadedFileWrapper;
import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This is a JSF backing managed-bean for the applicant.xhtml composition.
 *
 * @author  "Neil Griffin"
 */
public class ApplicantBackingBean {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantBackingBean.class);

	// Injections
	private ApplicantModelBean applicantModelBean;
	private ApplicantViewBean applicantViewBean;
	private BridgeFlash bridgeFlash;
	private ListModelBean listModelBean;

	public void deleteUploadedFile(ActionEvent actionEvent) {

		try {
			List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

			String uploadedFileId = applicantViewBean.getUploadedFileId();

			UploadedFile uploadedFileToDelete = null;

			for (UploadedFile uploadedFile : uploadedFiles) {

				if (uploadedFile.getId().equals(uploadedFileId)) {
					uploadedFileToDelete = uploadedFile;

					break;
				}
			}

			if (uploadedFileToDelete != null) {
				uploadedFileToDelete.delete();
				uploadedFiles.remove(uploadedFileToDelete);
				logger.debug("Deleted file=[{0}]", uploadedFileToDelete.getName());
			}

			applicantViewBean.setPopupRendered(false);
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public void fileUploadActionListener(ActionEvent actionEvent) {

		applicantViewBean.setPercentComplete(0);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {
			InputFile inputFile = (InputFile) actionEvent.getSource();
			FileInfo fileInfo = inputFile.getFileInfo();
			int status = fileInfo.getStatus();

			if (status == InputFile.INVALID) {
				FacesContextHelperUtil.addGlobalErrorMessage("you-have-entered-invalid-data");
			}
			else if ((status == InputFile.INVALID_CONTENT_TYPE) || (status == InputFile.INVALID_NAME_PATTERN)) {
				FacesContextHelperUtil.addGlobalErrorMessage("file-type-is-invalid");
			}
			else if (status == InputFile.SAVED) {
				List<UploadedFile> uploadedFiles = applicantModelBean.getUploadedFiles();

				UploadedFile uploadedFile = new UploadedFileWrapper(fileInfo);

				synchronized (uploadedFiles) {

					uploadedFiles.add(uploadedFile);
				}
			}
			else if (status == InputFile.SIZE_LIMIT_EXCEEDED) {
				FacesContextHelperUtil.addGlobalErrorMessage("please-enter-a-file-with-a-valid-file-size");
			}
		}
		catch (Exception e) {
			logger.error(e);
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
		}
	}

	public String getFileUploadAbsolutePath() {
		return System.getProperty("java.io.tmpdir");
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

		// Injected via WEB-INF/faces-config.xml managed-property
		this.applicantModelBean = applicantModelBean;
	}

	public void setApplicantViewBean(ApplicantViewBean applicantViewBean) {

		// Injected via WEB-INF/faces-config.xml managed-property
		this.applicantViewBean = applicantViewBean;
	}

	public void setBridgeFlash(BridgeFlash bridgeFlash) {

		// Injected via WEB-INF/faces-config.xml managed-property
		this.bridgeFlash = bridgeFlash;
	}

	public void setListModelBean(ListModelBean listModelBean) {

		// Injected via WEB-INF/faces-config.xml managed-property
		this.listModelBean = listModelBean;
	}

	public String submit() {

		String firstName = applicantModelBean.getFirstName();

		if (logger.isDebugEnabled()) {
			logger.debug("firstName=" + firstName);
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
				File file = new File(uploadedFile.getAbsolutePath());
				file.delete();
				logger.debug("Deleted file=[{0}]", file);
			}

			bridgeFlash.setFirstName(firstName);
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
