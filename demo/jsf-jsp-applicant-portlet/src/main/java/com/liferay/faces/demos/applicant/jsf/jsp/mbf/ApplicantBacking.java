/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.applicant.jsf.jsp.mbf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.liferay.faces.bridge.component.inputfile.InputFile;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.demos.applicant.jsf.jsp.dto.Applicant;
import com.liferay.faces.demos.applicant.jsf.jsp.dto.Attachment;
import com.liferay.faces.demos.applicant.jsf.jsp.dto.City;
import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This is a JSF backing managed-bean for the applicant.xhtml composition.
 *
 * @author  "Neil Griffin"
 */
@ManagedBean(name = "applicantBacking")
@RequestScoped
public class ApplicantBacking {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantBacking.class);

	// Injections
	@ManagedProperty(value = "#{applicantView}")
	private ApplicantView applicantView;
	@ManagedProperty(value = "#{attachmentManager}")
	private AttachmentManager attachmentManager;
	@ManagedProperty(value = "#{listManager}")
	private ListManager listManager;

	// Private Data Members
	private Applicant applicant;
	private transient InputFile attachment1;
	private transient InputFile attachment2;
	private transient InputFile attachment3;

	public void deleteUploadedFile(ActionEvent actionEvent) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
		int attachmentIndex = Integer.valueOf(requestParameterMap.get("fileId"));

		try {
			List<Attachment> attachments = applicant.getAttachments();

			Attachment attachmentToDelete = null;

			for (Attachment attachment : attachments) {

				if (attachment.getIndex() == attachmentIndex) {
					attachmentToDelete = attachment;

					break;
				}
			}

			if (attachmentToDelete != null) {
				attachmentToDelete.getFile().delete();
				attachments.remove(attachmentToDelete);
				logger.debug("Deleted file=[{0}]", attachmentToDelete.getName());
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

	public Applicant getModel() {
		return applicant;
	}

	@PostConstruct
	public void postConstruct() {
		applicant = new Applicant();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		File attachmentDir = attachmentManager.getAttachmentDir(facesContext);
		List<Attachment> attachments = attachmentManager.getAttachments(attachmentDir);
		applicant.setAttachments(attachments);
	}

	public void setApplicant(Applicant applicant) {

		// Injected via @ManagedProperty annotation
		this.applicant = applicant;
	}

	public void setApplicantView(ApplicantView applicantView) {

		// Injected via @ManagedProperty annotation
		this.applicantView = applicantView;
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

	public void setAttachmentManager(AttachmentManager attachmentManager) {

		// Injected via @ManagedProperty annotation
		this.attachmentManager = attachmentManager;
	}

	public void setListManager(ListManager listManager) {

		// Injected via @ManagedProperty annotation
		this.listManager = listManager;
	}

	public String submit() {

		if (logger.isDebugEnabled()) {
			logger.debug("firstName=" + applicant.getFirstName());
			logger.debug("lastName=" + applicant.getLastName());
			logger.debug("emailAddress=" + applicant.getEmailAddress());
			logger.debug("phoneNumber=" + applicant.getPhoneNumber());
			logger.debug("dateOfBirth=" + applicant.getDateOfBirth());
			logger.debug("city=" + applicant.getCity());
			logger.debug("provinceId=" + applicant.getProvinceId());
			logger.debug("postalCode=" + applicant.getPostalCode());

			List<Attachment> attachments = applicant.getAttachments();

			for (Attachment attachment : attachments) {
				logger.debug("attachment=[{0}]", attachment.getName());
			}
		}

		// Delete the uploaded files.
		try {
			List<Attachment> attachments = applicant.getAttachments();

			for (Attachment attachment : attachments) {
				attachment.getFile().delete();
				logger.debug("Deleted file=[{0}]", attachment.getName());
			}

			// Store the applicant's first name in JSF 2 Flash Scope so that it can be picked up
			// for use inside of confirmation.xhtml
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.getExternalContext().getFlash().put("firstName", applicant.getFirstName());

			applicant.clearProperties();

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

		FacesContext facesContext = FacesContext.getCurrentInstance();
		File attachmentDir = attachmentManager.getAttachmentDir(facesContext);

		if (!attachmentDir.exists()) {
			attachmentDir.mkdir();
		}

		try {
			List<UploadedFile> attachments1 = (List<UploadedFile>) attachment1.getValue();

			if (attachments1 != null) {

				for (UploadedFile uploadedFile : attachments1) {
					File copiedFile = new File(attachmentDir, uploadedFile.getName());
					uploadedFile.write(copiedFile.getAbsolutePath());
					uploadedFile.delete();
					logger.debug("Received fileName=[{0}] absolutePath=[{1}]", copiedFile.getName(),
						copiedFile.getAbsolutePath());
				}
			}

			List<UploadedFile> attachments2 = (List<UploadedFile>) attachment2.getValue();

			if (attachments2 != null) {

				for (UploadedFile uploadedFile : attachments2) {
					File copiedFile = new File(attachmentDir, uploadedFile.getName());
					uploadedFile.write(copiedFile.getAbsolutePath());
					uploadedFile.delete();
					logger.debug("Received fileName=[{0}] absolutePath=[{1}]", copiedFile.getName(),
						copiedFile.getAbsolutePath());
				}
			}

			List<UploadedFile> attachments3 = (List<UploadedFile>) attachment3.getValue();

			if (attachments3 != null) {

				for (UploadedFile uploadedFile : attachments3) {
					File copiedFile = new File(attachmentDir, uploadedFile.getName());
					uploadedFile.write(copiedFile.getAbsolutePath());
					uploadedFile.delete();
					logger.debug("Received fileName=[{0}] absolutePath=[{1}]", copiedFile.getName(),
						copiedFile.getAbsolutePath());
				}
			}

			List<Attachment> attachments = attachmentManager.getAttachments(attachmentDir);
			applicant.setAttachments(attachments);
		}
		catch (IOException e) {
			logger.error(e);
		}

		applicantView.setFileUploaderRendered(false);
	}

}
