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
package com.liferay.faces.support.thick.cdi.internal;

import java.io.Serializable;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Note: The source for class is mostly taken from the {@link
 * com.liferay.bean.portlet.cdi.extension.internal.scope.CDIScopedBean} class.
 *
 * @author  Neil Griffin
 */
public class CDIScopedBean<T> implements Serializable {

	private static final long serialVersionUID = 2388556996969921221L;

	Logger logger = LoggerFactory.getLogger(CDIScopedBean.class);

	private final Contextual<T> _bean;
	private final T _containerCreatedInstance;
	private final CreationalContext<T> _creationalContext;
	private final String _name;
	private final String _scopeName;

	public CDIScopedBean(Contextual<T> bean, CreationalContext<T> creationalContext, String name, String scopeName) {

		_bean = bean;
		_creationalContext = creationalContext;
		_name = name;
		_scopeName = scopeName;

		_containerCreatedInstance = bean.create(creationalContext);
	}

	public void destroy() {

		if (logger.isDebugEnabled()) {
			logger.debug("Destroying @" + _scopeName + " bean named " + _name);
		}

		_creationalContext.release();

		_bean.destroy(_containerCreatedInstance, _creationalContext);
	}

	public T getContainerCreatedInstance() {
		return _containerCreatedInstance;
	}

	public String getScopeName() {
		return _scopeName;
	}

}
