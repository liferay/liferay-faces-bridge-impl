/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.flash.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;


/**
 * This class is a "fallback" implementation of the JSF {@link Flash} scope that only implements enough of the scope
 * functionality to prevent errors from being thrown at runtime. In theory it should never be necessary, because the JSF
 * runtime (Mojarra or MyFaces) provide their own implementations. See the {@link BridgeFlashFactory} class for more
 * information.
 *
 * @author  Neil Griffin
 */
public class BridgeFlashFallbackImpl extends BridgeFlash {

	// Private Data Members
	private boolean keepMessages = false;
	private HashMap<String, Object> hashMap = new HashMap<String, Object>();
	private boolean redirect = false;

	public void clear() {
		hashMap.clear();
	}

	public boolean containsKey(Object key) {
		return hashMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return hashMap.containsValue(value);
	}

	@Override
	public void doPostPhaseActions(FacesContext facesContext) {
		// no-op
	}

	@Override
	public void doPrePhaseActions(FacesContext facesContext) {
		// no-op
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return hashMap.entrySet();
	}

	public Object get(Object key) {
		return hashMap.get(key);
	}

	public boolean isEmpty() {
		return hashMap.isEmpty();
	}

	@Override
	public boolean isKeepMessages() {
		return keepMessages;
	}

	@Override
	public boolean isRedirect() {
		return redirect;
	}

	@Override
	public boolean isServletResponseRequired() {
		return false;
	}

	@Override
	public void keep(String key) {
		// TODO Auto-generated method stub
	}

	public Set<String> keySet() {
		return hashMap.keySet();
	}

	public Object put(String key, Object value) {
		return hashMap.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> t) {
		hashMap.putAll(t);
	}

	@Override
	public void putNow(String key, Object value) {
		// no-op
	}

	public Object remove(Object key) {
		return hashMap.remove(key);
	}

	@Override
	public void setKeepMessages(boolean newValue) {
		this.keepMessages = newValue;
	}

	@Override
	public void setRedirect(boolean newValue) {
		this.redirect = newValue;
	}

	public int size() {
		return hashMap.size();
	}

	public Collection<Object> values() {
		return hashMap.values();
	}
}
