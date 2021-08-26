/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.applicant.jsf.facelets.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import org.springframework.context.annotation.Scope;

import com.liferay.faces.demos.applicant.jsf.facelets.dto.Attachment;


/**
 * @author  Neil Griffin
 */
@Named
@Scope("singleton")
public class AttachmentManager {

	public File getAttachmentDir(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();
		String sessionId = externalContext.getSessionId(true);
		PortletContext portletContext = (PortletContext) externalContext.getContext();
		File tempDir = (File) portletContext.getAttribute(ServletContext.TEMPDIR);

		return new File(tempDir, sessionId);
	}

	public List<Attachment> getAttachments(File attachmentDir) {

		List<Attachment> attachments = new ArrayList<>();

		if (attachmentDir.exists()) {

			File[] files = attachmentDir.listFiles();

			for (int i = 0; i < files.length; i++) {
				attachments.add(new Attachment(files[i], i));
			}
		}

		return attachments;
	}
}
