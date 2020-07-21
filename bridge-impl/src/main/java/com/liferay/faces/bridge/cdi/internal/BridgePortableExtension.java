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
package com.liferay.faces.bridge.cdi.internal;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.portlet.faces.annotation.BridgeRequestScoped;


/**
 * @author  Neil Griffin
 */
public class BridgePortableExtension implements Extension {

	public void step1BeforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {

		beforeBeanDiscovery.addScope(BridgeRequestScoped.class, true, false);
	}

	public void step2AfterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery) {

		afterBeanDiscovery.addContext(new BridgeRequestBeanContext());
	}
}
