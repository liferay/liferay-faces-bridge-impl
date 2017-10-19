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
package com.liferay.faces.issue;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.context.FacesContextHelperUtil;


/**
 * @author  Kyle Stiemann
 */
public class JSFSPEC_1433Bean {

	public String getInputHiddenClientId() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIComponent inputHidden = FacesContextHelperUtil.matchComponentInViewRoot(facesContext, "h_inputHidden");

		return inputHidden.getClientId(facesContext);
	}
}
