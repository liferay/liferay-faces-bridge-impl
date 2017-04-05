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
package com.liferay.faces.issue.primefaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "FACES_2921Bean")
@ViewScoped
public class FACES_2921Bean {

	// Private Data Members
	private String text;
	private int slider1Value;
	private int slider2Value;
	private boolean switchValue;

	public int getSlider1Value() {
		return slider1Value;
	}

	public int getSlider2Value() {
		return slider2Value;
	}

	public String getText() {
		return text;
	}

	public boolean isSwitchValue() {
		return switchValue;
	}

	public void setSlider1Value(int slider1Value) {
		this.slider1Value = slider1Value;
	}

	public void setSlider2Value(int slider2Value) {
		this.slider2Value = slider2Value;
	}

	public void setSwitchValue(boolean switchValue) {
		this.switchValue = switchValue;
	}

	public void setText(String text) {
		this.text = text;
	}
}
