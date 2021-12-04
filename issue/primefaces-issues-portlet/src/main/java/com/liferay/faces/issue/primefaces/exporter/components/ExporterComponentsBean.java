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
package com.liferay.faces.issue.primefaces.exporter.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "exporterComponentsBean")
@RequestScoped
public class ExporterComponentsBean {

	//J-
	private static final List<User> USERS = Collections.unmodifiableList(Arrays.asList(
		new User("Neil", "Griffin"),
		new User("Vernon", "Singleton"),
		new User("Kyle", "Stiemann")
	));
	//J+

	public StreamedContent getFile() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		StringBuilder stringBuilder = new StringBuilder();

		try {

			ExporterUsersCSVImpl.appendDataTableAsCSV(facesContext, externalContext, ":usersTable", stringBuilder);

			String dataTableAsCSV = stringBuilder.toString();
			InputStream inputStream = new ByteArrayInputStream(dataTableAsCSV.getBytes("UTF-8"));

			return DefaultStreamedContent.builder().stream(() -> inputStream).contentType("text/csv").name("p_fileDownloadUsers.csv").build();
		}
		catch (Exception e) {
			throw new FacesException(e);
		}
	}

	public List<User> getUsers() {
		return USERS;
	}
}
