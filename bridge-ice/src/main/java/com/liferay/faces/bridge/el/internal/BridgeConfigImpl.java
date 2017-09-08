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
package com.liferay.faces.bridge.el.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeConfigImpl implements BridgeConfig {

	// Private Data Members
	private Map<String, Object> attributes;

	public BridgeConfigImpl() {

		// Initialize the map of attributes
		attributes = Collections.unmodifiableMap(new BridgeConfigAttributeMap());
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Set<String> getExcludedRequestAttributes() {
		throw new UnsupportedOperationException();
	}

	public Map<String, String[]> getPublicParameterMappings() {
		throw new UnsupportedOperationException();
	}

	public String getViewIdRenderParameterName() {
		throw new UnsupportedOperationException();
	}

	public String getViewIdResourceParameterName() {
		throw new UnsupportedOperationException();
	}

	public String getWriteBehindRenderResponseWrapper() {
		throw new UnsupportedOperationException();
	}

	public String getWriteBehindResourceResponseWrapper() {
		throw new UnsupportedOperationException();
	}

	protected class BridgeConfigAttributeMap extends HashMap<String, Object> {

		// serialVersionUID
		private static final long serialVersionUID = 8763346476317251569L;

		@Override
		public Object get(Object key) {
			Object value = super.get(key);

			if (value == null) {

				try {
					Product.Name productName = Product.Name.valueOf((String) key);
					value = ProductFactory.getProduct(productName);
				}
				catch (IllegalArgumentException e) {
					// do nothing.
				}
			}

			return value;
		}
	}
}
