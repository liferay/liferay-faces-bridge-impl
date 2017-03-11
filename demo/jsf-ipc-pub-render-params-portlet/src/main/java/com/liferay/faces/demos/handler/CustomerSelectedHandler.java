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
package com.liferay.faces.demos.handler;

import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class CustomerSelectedHandler implements BridgePublicRenderParameterHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomerSelectedHandler.class);

	public void processUpdates(FacesContext facesContext) {
		logger.debug("Here is where you would perform any necessary processing of public render parameters");
	}

}
