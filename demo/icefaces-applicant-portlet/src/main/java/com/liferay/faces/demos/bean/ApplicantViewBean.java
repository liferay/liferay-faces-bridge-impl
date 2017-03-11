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

import java.util.EventObject;

import javax.faces.event.ActionEvent;

import com.icesoft.faces.component.inputfile.InputFile;


/**
 * @author  Neil Griffin
 */
public class ApplicantViewBean {

	// JavaBeans Properties for UI
	private boolean commentsRendered;
	private int percentComplete;
	private boolean popupRendered;
	private String uploadedFileId;

	public void fileUploadProgressListener(EventObject eventObject) {
		InputFile inputFile = (InputFile) eventObject.getSource();
		percentComplete = inputFile.getFileInfo().getPercent();
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public String getUploadedFileId() {
		return uploadedFileId;
	}

	public boolean isCommentsRendered() {
		return commentsRendered;
	}

	public boolean isPopupRendered() {
		return popupRendered;
	}

	public void setCommentsRendered(boolean commentsRendered) {
		this.commentsRendered = commentsRendered;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

	public void setPopupRendered(boolean popupRendered) {
		this.popupRendered = popupRendered;
	}

	public void setUploadedFileId(String uploadedFileId) {
		this.uploadedFileId = uploadedFileId;
	}

	public void toggleComments(ActionEvent actionEvent) {
		commentsRendered = !commentsRendered;
	}

	public void togglePopup(ActionEvent actionEvent) {
		popupRendered = !popupRendered;
	}

}
