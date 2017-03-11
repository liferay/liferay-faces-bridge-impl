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
package com.liferay.faces.bridge.demos.bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.liferay.faces.bridge.event.FileUploadEvent;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@RequestScoped
@ManagedBean
public class BridgeInputFileBackingBean {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeInputFileBackingBean.class);

	@ManagedProperty(value = "#{bridgeInputFileModelBean}")
	private com.liferay.faces.bridge.demos.bean.BridgeInputFileModelBean bridgeInputFileModelBean;

	public void deleteUploadedFile(ActionEvent actionEvent) {

		UICommand uiCommand = (UICommand) actionEvent.getComponent();
		String fileId = (String) uiCommand.getValue();

		try {
			List<UploadedFile> uploadedFiles = bridgeInputFileModelBean.getUploadedFiles();

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

	public void handleFileUpload(FileUploadEvent fileUploadEvent) {

		List<UploadedFile> uploadedFiles = bridgeInputFileModelBean.getUploadedFiles();
		UploadedFile uploadedFile = fileUploadEvent.getUploadedFile();

		if (uploadedFile.getStatus() == UploadedFile.Status.FILE_SAVED) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			FacesMessage facesMessage = new FacesMessage("Received fileUploadEvent for file named '" +
					uploadedFile.getName() + "' in the " + fileUploadEvent.getPhaseId().toString() + " phase.");
			facesContext.addMessage(null, facesMessage);
			uploadedFiles.add(uploadedFile);
			logger.debug("Received fileName=[{0}] absolutePath=[{1}]", uploadedFile.getName(),
				uploadedFile.getAbsolutePath());
		}
		else {
			logger.error("Failed to receive uploaded file due to error status=[{0}] message=[{1}]",
				uploadedFile.getStatus(), uploadedFile.getMessage());
		}
	}

	public void setBridgeInputFileModelBean(
		com.liferay.faces.bridge.demos.bean.BridgeInputFileModelBean bridgeInputFileModelBean) {

		// Injected via @ManagedProperty annotation
		this.bridgeInputFileModelBean = bridgeInputFileModelBean;
	}
}
