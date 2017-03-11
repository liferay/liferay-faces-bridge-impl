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
package com.liferay.faces.bridge.event.internal;

/**
 * @author  Neil Griffin
 */
public abstract class PublicRenderParameterBaseImpl implements PublicRenderParameter {

	// Protected Data Members
	protected boolean forThisPortlet;
	protected String modelEL;
	protected String originalRequestValue;

	public PublicRenderParameterBaseImpl(String prefix, String originalRequestValue, String originalModelEL,
		String portletName) {

		this.originalRequestValue = originalRequestValue;
		this.modelEL = originalModelEL;

		if (prefix == null) {
			this.forThisPortlet = true;
		}
		else {
			this.forThisPortlet = prefix.equals(portletName);
		}
	}
}
