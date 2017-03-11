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
package com.liferay.faces.bridge.tck.filter;

import java.io.IOException;


/**
 * This exception is thrown by PortletRequestDispatcherTCKImpl.forward(PortletRequest,PortletResponse) in order to
 * work-around a problem in the design of the TCK. Specifically, the TCK incorrectly assumes that the bridge must render
 * the view itself because the Faces implementation is not able to render views in a portlet environment. This exception
 * is caught by ViewHandlerTCKImpl.renderView(FacesContext,UIViewRoot) so that it can proceed with invoking the Faces
 * implementation to render the view.
 *
 * @author  Neil Griffin
 */
public class RenderSelfException extends IOException {

	public RenderSelfException(IllegalStateException e) {
		super(e);
	}
}
