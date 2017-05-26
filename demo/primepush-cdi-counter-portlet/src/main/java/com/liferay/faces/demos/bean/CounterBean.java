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
package com.liferay.faces.demos.bean;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;


/**
 * @author  Kyle Stiemann
 */
@Named
@ApplicationScoped
public class CounterBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 6944317221506521710L;

	// Private Data Members
	private volatile int count;

	public int getCount() {
		return count;
	}

	public void increment() {

		count++;

		EventBus eventBus = EventBusFactory.getDefault().eventBus();
		eventBus.publish("/counter", String.valueOf(count));
	}

	public void setCount(int count) {
		this.count = count;
	}
}
