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

import javax.faces.application.Application;

import com.liferay.faces.util.config.ConfiguredSystemEventListener;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ApplicationCompatImpl extends ApplicationWrapper {

	// Private Data Members
	private Application wrappedApplication;

	public ApplicationCompatImpl(Application application) {
		this.wrappedApplication = application;
	}

	/**
	 * @see  ApplicationWrapper#getWrapped()
	 */
	public Application getWrapped() {
		return wrappedApplication;
	}

	protected void subscribeToJSF2SystemEvent(ConfiguredSystemEventListener configuredSystemEventListener) {
		// This is a no-op for JSF 1.2
	}
}
