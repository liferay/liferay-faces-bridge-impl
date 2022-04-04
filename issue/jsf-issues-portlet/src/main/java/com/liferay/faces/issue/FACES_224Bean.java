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
package com.liferay.faces.issue;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "FACES_224Bean")
@ViewScoped
public class FACES_224Bean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 591128176912990631L;

	// Private Data Members
	private String value1;
	private String value2;

	public String getValue1() {
		return value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

}
