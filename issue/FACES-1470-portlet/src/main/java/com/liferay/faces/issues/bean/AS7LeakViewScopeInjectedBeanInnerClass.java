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
package com.liferay.faces.issues.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "as7LeakViewScopeInjectedBeanInnerClass")
@ViewScoped
public class AS7LeakViewScopeInjectedBeanInnerClass implements Serializable {

	private static final String AS7LEAK_ATTRIBUTE_MAP = "com.liferay.faces.support.as7LeakAttributeMap";

	private static final long serialVersionUID = 3854382137975349082L;

	// Injections
	@ManagedProperty(name = "applicationScopeBean", value = "#{applicationScopeBean}")
	private ApplicationScopeBean applicationScopeBean;

	// Private Data Members
	private String foo = this.toString();
	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	public AS7LeakViewScopeInjectedBeanInnerClass() {

		List<AS7LeakAttribute> savedAttributes = new ArrayList<AS7LeakAttribute>();
		AS7LeakAttribute as7LeakAttribute = new AS7LeakAttribute("as7LeakAttributeName", "as7LeakAttributeValue");
		savedAttributes.add(as7LeakAttribute);
		attributeMap.put(AS7LEAK_ATTRIBUTE_MAP, savedAttributes);
	}

	public String getFoo() {
		return foo;
	}

	public void setApplicationScopeBean(ApplicationScopeBean applicationScopeBean) {
		this.applicationScopeBean = applicationScopeBean;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	protected class AS7LeakAttribute extends NameValuePair<Object, Object> implements Serializable {

		// serialVersionUID
		private static final long serialVersionUID = 6818500149051763226L;

		public AS7LeakAttribute(Object name, Object value) {
			super(name, value);
		}
	}

}
