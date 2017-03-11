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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;


/**
 * @author  Michael Freedman
 */
public class TestSuiteLifecycleFactoryImpl extends LifecycleFactory {

	/** The id of the intercepting lifecycle. */
	public static final String TCK_LIFECYCLE_ID = "TCKLifecycle";
	private LifecycleFactory mHandler;

	/**
	 * Delegate injecting constructor.
	 *
	 * @param  defaultFactory  the injected delegate LifecycleFactory.
	 */
	public TestSuiteLifecycleFactoryImpl(LifecycleFactory defaultFactory) {
		mHandler = defaultFactory;

		// 1. get default lifecycle to be wrapped by our lifecycle
		Lifecycle defaultLifecycle = mHandler.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

		// 2. register intercepting lifecycle implementation.
		addLifecycle(TCK_LIFECYCLE_ID, new TestSuiteLifecycleImpl(defaultLifecycle));
	}

	@Override
	public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
		mHandler.addLifecycle(lifecycleId, lifecycle);
	}

	@Override
	public Lifecycle getLifecycle(String lifecycleId) {
		return mHandler.getLifecycle(lifecycleId);
	}

	@Override
	public Iterator<String> getLifecycleIds() {
		return mHandler.getLifecycleIds();
	}

}
