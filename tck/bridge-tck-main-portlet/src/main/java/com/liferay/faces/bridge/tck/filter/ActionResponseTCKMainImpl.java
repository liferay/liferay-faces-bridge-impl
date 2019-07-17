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
package com.liferay.faces.bridge.tck.filter;

import javax.portlet.ActionResponse;
import javax.portlet.filter.ActionResponseWrapper;


/**
 * This class exists simply to make sure that the chain-of-delegation pattern is working for factories. See JSF_ELTest
 * for more details.
 *
 * @author  Neil Griffin
 */
public class ActionResponseTCKMainImpl extends ActionResponseWrapper {
	public ActionResponseTCKMainImpl(ActionResponse response) {
		super(response);
	}
}
