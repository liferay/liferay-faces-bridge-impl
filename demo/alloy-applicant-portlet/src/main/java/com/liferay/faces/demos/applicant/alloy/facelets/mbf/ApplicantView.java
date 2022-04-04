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
package com.liferay.faces.demos.applicant.alloy.facelets.mbf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import com.liferay.faces.util.model.UploadedFile;


/**
 * This is a JSF view managed-bean for the applicant.xhtml composition.
 *
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "applicantView")
@ViewScoped
public class ApplicantView implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6163667782815767889L;

	// JavaBean Properties for UI
	private int attachmentIndex;
	private boolean commentsRendered;
	private UploadedFile fileToDelete;

	public int getAttachmentIndex() {
		return attachmentIndex;
	}

	public boolean isCommentsRendered() {
		return commentsRendered;
	}

	public void setAttachmentIndex(int attachmentIndex) {
		this.attachmentIndex = attachmentIndex;
	}

	public void setCommentsRendered(boolean commentsRendered) {
		this.commentsRendered = commentsRendered;
	}

	public void toggleComments(ActionEvent actionEvent) {
		commentsRendered = !commentsRendered;
	}

}
