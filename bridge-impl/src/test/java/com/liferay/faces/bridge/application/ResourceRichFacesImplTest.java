/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application;

import javax.faces.application.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.application.internal.ResourceRichFacesImpl;


/**
 * @author  Kyle Stiemann
 */
public final class ResourceRichFacesImplTest {

	private static void assertResourceRichFacesImplRequestPathEquals(String expectedRequestPath,
		String providedRequestPath) {

		Resource resource = new ResourceMockImpl(providedRequestPath);
		resource = new ResourceRichFacesImpl(resource);
		Assert.assertEquals(expectedRequestPath, resource.getRequestPath());
	}

	@Test
	public final void testFACES_3373AlbfernandezRichfacesGetRequestPath() {

		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?javax.faces.resource=jquery.js");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true#test",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?javax.faces.resource=jquery.js&test=true#test");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true#test",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true&javax.faces.resource=jquery.js#test");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?javax.faces.resource=jquery.js&test=true");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?test=true&javax.faces.resource=jquery.js");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js#test",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js?javax.faces.resource=jquery.js#test");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js",
			"/com.liferay.faces.demo.richfaces.applicant.portlet/org.richfaces.resources/javax.faces.resource/org.richfaces/jquery.js");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet?javax.faces.resource=/org.richfaces/jquery.js",
			"/com.liferay.faces.demo.richfaces.applicant.portlet?javax.faces.resource=/org.richfaces/jquery.js");
		assertResourceRichFacesImplRequestPathEquals(
			"/com.liferay.faces.demo.richfaces.applicant.portlet?javax.faces.resource=/org.richfaces/jquery.js&ln=test",
			"/com.liferay.faces.demo.richfaces.applicant.portlet?javax.faces.resource=/org.richfaces/jquery.js&ln=test");
	}
}
