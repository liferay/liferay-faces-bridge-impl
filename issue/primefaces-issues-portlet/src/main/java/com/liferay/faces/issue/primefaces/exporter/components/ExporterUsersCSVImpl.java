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
package com.liferay.faces.issue.primefaces.exporter.components;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;

import org.primefaces.extensions.component.exporter.Exporter;


/**
 * @author  Kyle Stiemann
 */
public final class ExporterUsersCSVImpl extends Exporter {

	/* package-private */ static void appendDataTableAsCSV(FacesContext facesContext, ExternalContext externalContext, String dataTableId,
		Appendable appendable) throws IOException {

		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		DataTable dataTable = (DataTable) uiViewRoot.findComponent(dataTableId);
		List<UIComponent> children = dataTable.getChildren();
		boolean first = true;

		for (UIComponent child : children) {

			if (!first) {
				appendable.append(",");
			}

			String headerText = (String) child.getFacet("header").getAttributes().get("value");
			appendable.append("\"").append(headerText).append("\"");
			first = false;
		}

		appendable.append("\n");

		int originalRowIndex = dataTable.getRowIndex();
		int rowCount = dataTable.getRowCount();

		for (int i = 0; i < rowCount; i++) {

			dataTable.setRowIndex(i);

			User user = (User) dataTable.getRowData();

			//J-
			appendable.append("\"").append(user.getFirstName()).append("\",")
					.append("\"").append(user.getLastName()).append("\"\n");
			//J+
		}

		dataTable.setRowIndex(originalRowIndex);
	}

	@Override
	public void customFormat(String facetBackground, String facetFontSize, String facetFontColor, String facetFontStyle,
		String fontName, String cellFontSize, String cellFontColor, String cellFontStyle, String datasetPadding,
		String orientation) throws IOException {
		// no-op
	}

	@Override
	public void export(ActionEvent event, String dataTableId, FacesContext facesContext, String fileName,
		String dataTableTitle, boolean pageOnly, boolean selectionOnly, String encodingType,
		MethodExpression preProcessor, MethodExpression postProcessor, boolean subTable) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("text/csv");
		externalContext.setResponseHeader("Expires", "0");
		externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		externalContext.setResponseHeader("Pragma", "public");
		externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".csv");

		Writer responseOutputWriter = externalContext.getResponseOutputWriter();
		appendDataTableAsCSV(facesContext, externalContext, dataTableId, responseOutputWriter);
		externalContext.responseFlushBuffer();
	}
}
