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
package com.liferay.faces.bridge.context.internal;

import javax.faces.FacesWrapper;
import javax.portlet.PortletContext;
import javax.portlet.faces.BridgeFactoryFinder;


/**
 * This abstract class provides a contract for defining a factory that knows how to create instances of type {@link
 * IncongruityContext}. It is inspired by the factory pattern found in the JSF API like {@link
 * javax.faces.context.FacesContextFactory} and {@link javax.faces.context.ExternalContextFactory}. By implementing the
 * {@link javax.faces.FacesWrapper} interface, the class provides implementations with the opportunity to wrap another
 * factory (participate in a chain-of-responsibility pattern). If an implementation wraps a factory, then it should
 * provide a one-arg constructor so that the wrappable factory can be passed at initialization time.
 *
 * @author  Neil Griffin
 */
public abstract class IncongruityContextFactory implements FacesWrapper<IncongruityContextFactory> {

	/**
	 * Returns an instance of {@link IncongruityContext} from the {@link IncongruityContextFactory} found by the {@link
	 * BridgeFactoryFinder}.
	 */
	public static IncongruityContext getIncongruityContextInstance(PortletContext portletContext) {

		IncongruityContextFactory incongruityContextFactory = (IncongruityContextFactory) BridgeFactoryFinder
			.getFactory(portletContext, IncongruityContextFactory.class);

		return incongruityContextFactory.getIncongruityContext();
	}

	public abstract IncongruityContext getIncongruityContext();
}
