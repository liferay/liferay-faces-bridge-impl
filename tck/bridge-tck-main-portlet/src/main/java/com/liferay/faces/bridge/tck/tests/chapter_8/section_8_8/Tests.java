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
package com.liferay.faces.bridge.tck.tests.chapter_8.section_8_8;

import javax.faces.FacesWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.faces.BridgeConfigFactory;
import javax.portlet.faces.BridgeEventHandlerFactory;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgePublicRenderParameterHandlerFactory;
import javax.portlet.faces.BridgeURLFactory;
import javax.portlet.faces.RequestAttributeInspectorFactory;
import javax.portlet.faces.filter.BridgePortletConfigFactory;
import javax.portlet.faces.filter.BridgePortletRequestFactory;
import javax.portlet.faces.filter.BridgePortletResponseFactory;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.factories.filter.BridgeConfigFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgeEventHandlerFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgePortletConfigFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgePortletRequestFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgePortletResponseFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgePublicRenderParameterHandlerFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.BridgeURLFactoryTCKImpl;
import com.liferay.faces.bridge.tck.factories.filter.RequestAttributeInspectorFactoryTCKImpl;


/**
 * @author  Neil Griffin
 */
public class Tests {

	@BridgeTest(test = "bridgeFactoryFinderTest")
	public String bridgeFactoryFinderTest(TestBean testBean) {
		String value = "true";

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();

		Class<? extends FacesWrapper<?>>[] factoryClasses = (Class<? extends FacesWrapper<?>>[]) new Class<?>[] {
				BridgeConfigFactory.class, BridgeEventHandlerFactory.class, BridgePortletConfigFactory.class,
				BridgePortletRequestFactory.class, BridgePortletResponseFactory.class,
				BridgePublicRenderParameterHandlerFactory.class, BridgeURLFactory.class,
				RequestAttributeInspectorFactory.class
			};

		Class<?>[] tckFactoryClasses = new Class<?>[] {
				BridgeConfigFactoryTCKImpl.class, BridgeEventHandlerFactoryTCKImpl.class,
				BridgePortletConfigFactoryTCKImpl.class, BridgePortletRequestFactoryTCKImpl.class,
				BridgePortletResponseFactoryTCKImpl.class, BridgePublicRenderParameterHandlerFactoryTCKImpl.class,
				BridgeURLFactoryTCKImpl.class, RequestAttributeInspectorFactoryTCKImpl.class
			};

		for (int i = 0; i < factoryClasses.length; i++) {
			FacesWrapper<?> facesWrapper = (FacesWrapper<?>) BridgeFactoryFinder.getFactory(portletContext,
					factoryClasses[i]);

			if (!isFoundInChainOfDelegation(facesWrapper, tckFactoryClasses[i])) {
				testBean.setTestResult(false,
					"The BridgeFactoryFinder was NOT able to discover " + tckFactoryClasses[i].getName());

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(true, "The BridgeFactoryFinder was able to discover the factories");

		return Constants.TEST_SUCCESS;
	}

	private boolean isFoundInChainOfDelegation(FacesWrapper<?> factoryWrapper, Class<?> clazz) {

		if (factoryWrapper.getClass().equals(clazz)) {
			return true;
		}

		FacesWrapper<?> wrapped = (FacesWrapper<?>) factoryWrapper.getWrapped();

		if (wrapped == null) {
			return false;
		}

		return isFoundInChainOfDelegation(wrapped, clazz);
	}
}
