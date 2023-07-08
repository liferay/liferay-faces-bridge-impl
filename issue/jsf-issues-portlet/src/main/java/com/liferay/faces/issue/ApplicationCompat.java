/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.issue;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public class ApplicationCompat {

	private Application application;

	public ApplicationCompat(Application application) {
		this.application = application;
	}

	public <T> T evaluateExpressionGet(FacesContext facesContext, String elExpression, Class<? extends T> clazz) {

		ELResolver elResolver = application.getELResolver();
		ELContext elContext = facesContext.getELContext();

		return (T) elResolver.getValue(elContext, null, elExpression);
	}
}
