/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;


/**
 * @author  Michael Freedman
 */
public class TestSuiteApplicationFactoryImpl extends ApplicationFactory {
	private ApplicationFactory mHandler;

	public TestSuiteApplicationFactoryImpl(ApplicationFactory handler) {
		mHandler = handler;
	}

	public Application getApplication() {
		return new TestSuiteApplicationImpl(mHandler.getApplication());
	}

	public void setApplication(Application app) {
		mHandler.setApplication(app);
	}

}
