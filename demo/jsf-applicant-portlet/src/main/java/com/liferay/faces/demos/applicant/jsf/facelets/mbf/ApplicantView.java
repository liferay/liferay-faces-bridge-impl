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
package com.liferay.faces.demos.applicant.jsf.facelets.mbf;

import java.io.File;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;


/**
 * This is a JSF view managed-bean for the applicant.xhtml composition.
 *
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "applicantView")
@ViewScoped
public class ApplicantView implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6063667782815767889L;

	// JavaBean Properties for UI
	private boolean commentsRendered;

	public boolean isCommentsRendered() {
		return commentsRendered;
	}

	public void setCommentsRendered(boolean commentsRendered) {
		this.commentsRendered = commentsRendered;
	}

	public void toggleComments(ActionEvent actionEvent) {
		commentsRendered = !commentsRendered;
	}

}
