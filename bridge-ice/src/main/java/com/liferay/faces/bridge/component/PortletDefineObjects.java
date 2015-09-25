/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.component;

import javax.faces.component.UIComponentBase;


/**
 * This has been implemented for the sake of completeness of portlet:defineObjects tag from JSR 286 specification.
 * Please refer to {@link ELResolverImpl} for more information and section 6.5.1 of the JSR 329 specification.
 *
 * @author  Neil Griffin
 */
public class PortletDefineObjects extends UIComponentBase {

	public static final String COMPONENT_TYPE = "com.liferay.faces.bridge.component.PortletDefineObjects";

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

}
