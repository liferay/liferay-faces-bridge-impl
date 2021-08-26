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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;

import org.primefaces.extensions.component.exporter.Exporter;
import org.primefaces.extensions.component.exporter.ExporterFactory;


/**
 * @author  Kyle Stiemann
 */
public class ExporterFactoryUsersCSVImpl implements ExporterFactory {

	@Override
	public Exporter getExporterForType(String string) {

		if ((string != null) && !string.equalsIgnoreCase("csv")) {
			throw new IllegalArgumentException("Only type=\"csv\" is supported for pe:exporter in this demo");
		}

		return new ExporterUsersCSVImpl();
	}
}
