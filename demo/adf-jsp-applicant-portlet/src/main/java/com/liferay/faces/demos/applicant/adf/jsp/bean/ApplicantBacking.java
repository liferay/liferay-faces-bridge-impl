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
package com.liferay.faces.demos.applicant.adf.jsp.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.model.UploadedFile;

import com.liferay.faces.demos.applicant.adf.jsp.dto.Applicant;
import com.liferay.faces.demos.applicant.adf.jsp.dto.Attachment;
import com.liferay.faces.demos.applicant.adf.jsp.dto.City;
import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.event.DialogEvent;


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
	@ManagedProperty(value = "#{attachmentManager}")
	private AttachmentManager attachmentManager;

	@ManagedProperty(value = "#{listManager}")
	private ListManager listManager;

	// Private Data Members
	private Applicant applicant;
	private String editModeURL;
	private String helpModeURL;
	private String viewModeURL;

	public void deleteUploadedFile(DialogEvent dialogEvent) {

		DialogEvent.Outcome dialogEventOutcome = dialogEvent.getOutcome();

		if (dialogEventOutcome == DialogEvent.Outcome.ok) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			UIViewRoot viewRoot = facesContext.getViewRoot();
			RichInputText richInputText = (RichInputText) ComponentUtil.matchComponentInHierarchy(facesContext,
					viewRoot, "attachmentIndex");
			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
			String attachmentIndex = requestParameterMap.get(richInputText.getClientId());

			if (attachmentIndex != null) {
				File attachmentDir = attachmentManager.getAttachmentDir(facesContext);
				List<Attachment> attachments = attachmentManager.getAttachments(attachmentDir);
				Attachment attachment = attachments.remove(Integer.valueOf(attachmentIndex).intValue());
				File file = attachment.getFile();
				file.delete();
				logger.debug("Deleted file=[{0}]", file.getName());
				attachments = attachmentManager.getAttachments(attachmentDir);
				applicant.setAttachments(attachments);
			}
		}
	}

	public String getEditModeURL() {

		if (editModeURL == null) {
			initPortletURLs();
		}

		return editModeURL;
	}

	public String getHelpModeURL() {

		if (helpModeURL == null) {
			initPortletURLs();
		}

		return helpModeURL;
	}

	public Applicant getModel() {
		return applicant;
	}

	public String getViewModeURL() {

		if (viewModeURL == null) {
			initPortletURLs();
		}

		return viewModeURL;
	}

	public void handleFileUpload(ValueChangeEvent valueChangeEvent) {

		Object newValue = valueChangeEvent.getNewValue();

		if ((newValue != null) && (newValue instanceof UploadedFile)) {

			UploadedFile uploadedFile = (UploadedFile) newValue;

			FacesContext facesContext = FacesContext.getCurrentInstance();
			File attachmentDir = attachmentManager.getAttachmentDir(facesContext);

			if (!attachmentDir.exists()) {
				attachmentDir.mkdir();
			}

			File copiedFile = new File(attachmentDir, uploadedFile.getFilename());

			byte[] buffer = new byte[1024];

			try(InputStream inputStream = uploadedFile.getInputStream();
					OutputStream outputStream = new FileOutputStream(copiedFile)) {

				for (int length; (length = inputStream.read(buffer)) != -1;) {
					outputStream.write(buffer, 0, length);
				}

			}
			catch (Exception e) {
				logger.error(e);
			}

			uploadedFile.dispose();

			logger.debug("Received fileName=[{0}] absolutePath=[{1}]", copiedFile.getName(),
				copiedFile.getAbsolutePath());

			List<Attachment> attachments = attachmentManager.getAttachments(attachmentDir);

			applicant.setAttachments(attachments);
		}
	}

	public void postalCodeListener(ValueChangeEvent valueChangeEvent) {

		try {
			String newPostalCode = (String) valueChangeEvent.getNewValue();
			City city = listManager.getCityByPostalCode(newPostalCode);

			if (city != null) {
				RichInputText postalCodeComponent = (RichInputText) valueChangeEvent.getComponent();
				RichInputText cityComponent = (RichInputText) postalCodeComponent.findComponent("city");
				FacesBean facesBean = cityComponent.getFacesBean();
				PropertyKey submittedValue = facesBean.getType().findKey("submittedValue");
				facesBean.setProperty(submittedValue, city.getCityName());

				RichSelectOneChoice provinceIdComponent = (RichSelectOneChoice) postalCodeComponent.findComponent(
						"provinceId");
				facesBean = provinceIdComponent.getFacesBean();
				submittedValue = facesBean.getType().findKey("submittedValue");
				facesBean.setProperty(submittedValue, city.getProvinceId());
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
		}
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

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setListManager(ListManager listManager) {

		// Injected via @ManagedProperty annotation
		this.listManager = listManager;
	}

	public String submit() {

		List<Attachment> attachments = applicant.getAttachments();

		if (logger.isDebugEnabled()) {
			logger.debug("firstName=" + applicant.getFirstName());
			logger.debug("lastName=" + applicant.getLastName());
			logger.debug("emailAddress=" + applicant.getEmailAddress());
			logger.debug("phoneNumber=" + applicant.getPhoneNumber());
			logger.debug("dateOfBirth=" + applicant.getDateOfBirth());
			logger.debug("city=" + applicant.getCity());
			logger.debug("provinceId=" + applicant.getProvinceId());
			logger.debug("postalCode=" + applicant.getPostalCode());
			logger.debug("comments=" + applicant.getComments());

			for (Attachment attachment : attachments) {
				logger.debug("uploadedFile=[{0}]", attachment.getName());
			}
		}

		// Delete the uploaded files.
		try {

			for (Attachment attachment : attachments) {
				File file = attachment.getFile();
				file.delete();
				logger.debug("Deleted file=[{0}]", file);
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

	protected void initPortletURLs() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Object response = externalContext.getResponse();

		if (response instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) response;
			PortletURL portletURL = mimeResponse.createRenderURL();

			try {
				portletURL.setPortletMode(PortletMode.VIEW);
				viewModeURL = portletURL.toString();
				portletURL.setPortletMode(PortletMode.EDIT);
				editModeURL = portletURL.toString();
				portletURL.setPortletMode(PortletMode.HELP);
				helpModeURL = portletURL.toString();
			}
			catch (PortletModeException e) {
				logger.error(e);
			}
		}
	}
}
