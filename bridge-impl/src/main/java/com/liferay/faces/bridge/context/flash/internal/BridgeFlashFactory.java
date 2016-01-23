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
package com.liferay.faces.bridge.context.flash.internal;

import javax.faces.FacesWrapper;


/**
 * NOTE: PROPOSED-FOR-JSR344-API (FACTORY FOR THE JSF 2.2 API) See:
 * http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1071
 *
 * @author  Neil Griffin
 */
public abstract class BridgeFlashFactory implements FacesWrapper<BridgeFlashFactory> {

	public abstract BridgeFlash getBridgeFlash();
}
