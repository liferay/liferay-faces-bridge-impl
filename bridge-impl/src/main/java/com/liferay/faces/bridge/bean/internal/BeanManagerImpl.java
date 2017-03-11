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
package com.liferay.faces.bridge.bean.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.faces.util.config.ConfiguredManagedBean;


/**
 * @author  Neil Griffin
 */
public class BeanManagerImpl extends BeanManagerCompatImpl {

	// Private Constants
	private static final String JAVAX_PORTLET_P = "javax.portlet.p.";

	// Private Data Members
	private Map<String, ConfiguredManagedBean> configuredManagedBeanSet;

	public BeanManagerImpl(List<ConfiguredManagedBean> configuredManagedBeans) {

		this.configuredManagedBeanSet = new HashMap<String, ConfiguredManagedBean>();

		if (configuredManagedBeans != null) {

			for (ConfiguredManagedBean configuredManagedBean : configuredManagedBeans) {
				this.configuredManagedBeanSet.put(configuredManagedBean.getManagedBeanName(), configuredManagedBean);
			}
		}
	}

	public boolean isManagedBean(String name, Object value) {

		boolean managedBean = false;

		if (value != null) {

			if (hasManagedBeanAnnotation(value)) {
				managedBean = true;
			}
			else {

				if (name != null) {

					// Section PLT.18.3 of the Portlet 2.0 Specification titled "Binding Attributes into a Session"
					// requires that PortletSession attribute names be namespaced/prefixed with the
					// "javax.portlet.p.<ID>?" pattern. In order to determine if the specified name is a SessionScoped
					// managed-bean, it is necessary to first strip the pattern from it.
					if (name.startsWith(JAVAX_PORTLET_P)) {
						int pos = name.indexOf("?");

						if (pos > 0) {
							name = name.substring(pos + 1);
						}
					}

					ConfiguredManagedBean configuredManagedBean = configuredManagedBeanSet.get(name);

					if (configuredManagedBean != null) {
						String managedBeanClass = value.getClass().getName();
						managedBean = managedBeanClass.equals(configuredManagedBean.getManagedBeanClass());
					}
				}
			}
		}

		return managedBean;
	}
}
