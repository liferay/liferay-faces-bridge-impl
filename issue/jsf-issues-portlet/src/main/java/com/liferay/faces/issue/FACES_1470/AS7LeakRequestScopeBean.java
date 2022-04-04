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
package com.liferay.faces.issue.FACES_1470;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "as7LeakRequestScopeBean")
@RequestScoped
public class AS7LeakRequestScopeBean {

	// Private Data Members
	private String foo = this.toString();

	public AS7LeakRequestScopeBean() {
		InstanceTrackerBean.trackAS7LeakInstance(this);
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
}
