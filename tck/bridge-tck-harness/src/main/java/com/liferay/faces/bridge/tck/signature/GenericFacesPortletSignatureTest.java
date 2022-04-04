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
package com.liferay.faces.bridge.tck.signature;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Michael Freedman
 */
public class GenericFacesPortletSignatureTest {

	@Test
	public void getBridgeClassName() {

		try {
			testSignature("getBridgeClassName", Class.forName("java.lang.String"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void getDefaultViewIdMap() {

		try {
			testSignature("getDefaultViewIdMap", Class.forName("java.util.Map"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void getExcludedRequestAttributes() {

		try {
			testSignature("getExcludedRequestAttributes", Class.forName("java.util.List"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void getFacesBridge() {

		try {
			testSignature("getFacesBridge", Class.forName("javax.portlet.faces.Bridge"),
				Class.forName("javax.portlet.PortletRequest"), Class.forName("javax.portlet.PortletResponse"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void getResponseCharacterSetEncoding() {

		try {
			testSignature("getResponseCharacterSetEncoding", Class.forName("java.lang.String"),
				Class.forName("javax.portlet.PortletRequest"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void getResponseContentType() {

		try {
			testSignature("getResponseContentType", Class.forName("java.lang.String"),
				Class.forName("javax.portlet.PortletRequest"));
		}
		catch (ClassNotFoundException cnfe) {
		}
	}

	@Test
	public void isPreserveActionParameters() {
		testSignature("isPreserveActionParameters", Boolean.TYPE);
	}

	private void testSignature(String methodName, Class expectedReturnType, Class<?>... parameters) {
		Class actualReturnType = null;

		try {
			Class facesClass = Class.forName("javax.portlet.faces.GenericFacesPortlet");
			Method method = facesClass.getMethod(methodName, parameters);

			actualReturnType = method.getReturnType();

			if (actualReturnType.equals(expectedReturnType)) {
				Assert.assertTrue(true);
			}
			else {
				Assert.fail("public void " + expectedReturnType + " " + methodName +
					" not found in GenericFacesPortlet.");
			}

		}
		catch (Exception e) {
			throw new AssertionError("public void " + expectedReturnType + " " + methodName +
				" not found in GenericFacesPortlet." + e.getMessage());
		}
	}
}
