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

import javax.faces.bean.ManagedBean;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class BeanManagerCompatImpl implements BeanManager {

	/**
	 * Determines whether or not the specified object is annotated as a JSF managed-bean.
	 *
	 * @param   obj  The object to check.
	 *
	 * @return  true if the specified object is annotated as a JSF managed-bean, otherwise false.
	 */
	protected boolean hasManagedBeanAnnotation(Object object) {

		if ((object != null) && (object.getClass().getAnnotation(ManagedBean.class) != null)) {
			return true;
		}
		else {
			return false;
		}
	}

}
