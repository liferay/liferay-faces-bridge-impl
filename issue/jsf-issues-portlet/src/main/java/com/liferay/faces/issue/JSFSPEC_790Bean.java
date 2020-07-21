/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.issues;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "JSFSPEC_790Bean")
@RequestScoped
public class JSFSPEC_790Bean {

	private String foo = "1234";
	private String currentServerTime;

	public void action() {
		System.err.println("TestBean.action called");
	}

	public String getCurrentServerTime() {

		if (currentServerTime == null) {
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
			currentServerTime = formatter.format(date);
		}

		return currentServerTime;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
}
