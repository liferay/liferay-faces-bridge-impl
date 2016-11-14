/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application.internal;

import javax.faces.application.ResourceHandlerWrapper;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 */
public abstract class ResourceHandlerBridgeCompat_2_0_Impl extends ResourceHandlerWrapper {

	@Override
	public boolean isResourceURL(String url) {
		return (url != null) &&
			(url.contains("javax.faces.resource=") || url.equals(MissingResourceImpl.RES_NOT_FOUND));
	}
}
