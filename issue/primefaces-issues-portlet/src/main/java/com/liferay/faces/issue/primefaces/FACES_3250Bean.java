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
package com.liferay.faces.issue.primefaces;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.event.FileUploadEvent;

import org.primefaces.model.UploadedFile;


/**
 * @author  Yeray Rodriguez (yerayrodriguez@gmail.com)
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "FACES_3250Bean")
@RequestScoped
public class FACES_3250Bean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6700353339180510942L;

	// Private Data Members
	private UploadedFile simpleModeUploadedFile;
	private UploadedFile skinSimpleModeUploadedFile;
	private UploadedFile advancedModeUploadedFile;

	public UploadedFile getAdvancedModeUploadedFile() {
		return advancedModeUploadedFile;
	}

	public UploadedFile getSimpleModeUploadedFile() {
		return simpleModeUploadedFile;
	}

	public UploadedFile getSkinSimpleModeUploadedFile() {
		return skinSimpleModeUploadedFile;
	}

	public void handleAdvancedModeFileUpload(FileUploadEvent fileUploadEvent) {

		UploadedFile uploadedFile = fileUploadEvent.getFile();
		setAdvancedModeUploadedFile(uploadedFile);
	}

	public void setAdvancedModeUploadedFile(UploadedFile advancedModeUploadedFile) {
		this.advancedModeUploadedFile = advancedModeUploadedFile;
	}

	public void setSimpleModeUploadedFile(UploadedFile simpleModeUploadedFile) {
		this.simpleModeUploadedFile = simpleModeUploadedFile;
	}

	public void setSkinSimpleModeUploadedFile(UploadedFile skinSimpleModeUploadedFile) {
		this.skinSimpleModeUploadedFile = skinSimpleModeUploadedFile;
	}
}
