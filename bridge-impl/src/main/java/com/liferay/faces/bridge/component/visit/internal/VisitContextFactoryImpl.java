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
package com.liferay.faces.bridge.component.visit.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitContextFactory;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;


/**
 * @author  Vernon Singleton
 */
public class VisitContextFactoryImpl extends VisitContextFactory {

	VisitContextFactory wrappedVisitContextFactory;

	public VisitContextFactoryImpl(VisitContextFactory visitContextFactory) {
		this.wrappedVisitContextFactory = visitContextFactory;
	}

	@Override
	public VisitContext getVisitContext(FacesContext facesContext, Collection<String> ids, Set<VisitHint> hints) {

		// Prepend the ids with the portlet namespace unless they already start with the namespace, or
		// if the id starts with the SeparatorChar
		if (ids != null) {

			UIViewRoot viewRoot = facesContext.getViewRoot();
			String separator = String.valueOf(UINamingContainer.getSeparatorChar(facesContext));
			String containerClientIdAndSeparator = viewRoot.getContainerClientId(facesContext) + separator;

			List<String> newIds = new ArrayList<String>();

			for (String id : ids) {

				if (!id.startsWith(separator) && !id.startsWith(containerClientIdAndSeparator)) {
					id = containerClientIdAndSeparator + id;
				}

				newIds.add(id);
			}

			ids = newIds;

		}

		return wrappedVisitContextFactory.getVisitContext(facesContext, ids, hints);
	}
}
