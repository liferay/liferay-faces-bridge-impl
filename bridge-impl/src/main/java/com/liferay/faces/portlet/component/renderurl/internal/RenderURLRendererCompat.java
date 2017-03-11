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
package com.liferay.faces.portlet.component.renderurl.internal;

import javax.faces.context.ExternalContext;
import javax.portlet.MimeResponse;
import javax.portlet.RenderURL;


/**
 * @author  Neil Griffin
 */
public abstract class RenderURLRendererCompat extends RenderURLRendererBase {

	protected enum ParamCopyOption {
		ALL_PUBLIC_PRIVATE, NONE
	}

	protected RenderURL createRenderURL(ExternalContext externalContext, ParamCopyOption paramCopyOption) {

		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();

		if (paramCopyOption == ParamCopyOption.ALL_PUBLIC_PRIVATE) {
			return mimeResponse.createRenderURL(MimeResponse.Copy.ALL);
		}
		else {
			return mimeResponse.createRenderURL(MimeResponse.Copy.NONE);
		}
	}
}
