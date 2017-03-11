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
package com.liferay.faces.demos.dto;

import java.io.Serializable;


/**
 * @author  Neil Griffin
 */
public class Title implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4331013979804532233L;

	// Private Data Members
	private long titleId;
	private String abbreviation;

	public Title(long titleId, String abbreviation) {
		this.titleId = titleId;
		this.abbreviation = abbreviation;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public long getTitleId() {
		return titleId;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public void setTitleId(long titleId) {
		this.titleId = titleId;
	}
}
