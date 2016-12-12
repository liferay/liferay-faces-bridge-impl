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
package com.liferay.faces.demos.bean;

import java.io.Serializable;


/**
 * This is a session-scoped managed bean used to accommodate for the missing flash scope in JSF 1.2.
 *
 * @author  "Neil Griffin"
 */
public class BridgeFlash implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7459628254337818761L;

	// Private Data Member
	private String firstName;

	public BridgeFlash() {
		clearProperties();
	}

	public void clearProperties() {
		firstName = null;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
