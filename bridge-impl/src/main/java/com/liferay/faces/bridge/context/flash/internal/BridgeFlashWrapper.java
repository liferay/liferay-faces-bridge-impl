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
import java.util.Map;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeFlashWrapper extends BridgeFlash implements FacesWrapper<BridgeFlash> {

	public void clear() {
		getWrapped().clear();
	}

	public boolean containsKey(Object key) {
		return getWrapped().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return getWrapped().containsValue(value);
	}

	@Override
	public void doPostPhaseActions(FacesContext facesContext) {
		getWrapped().doPostPhaseActions(facesContext);
	}

	@Override
	public void doPrePhaseActions(FacesContext facesContext) {
		getWrapped().doPrePhaseActions(facesContext);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return getWrapped().entrySet();
	}

	public Object get(Object key) {
		return getWrapped().get(key);
	}

	@Override
	public void keep(String key) {
		getWrapped().keep(key);
	}

	public Set<String> keySet() {
		return getWrapped().keySet();
	}

	public Object put(String key, Object value) {
		return getWrapped().put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> values) {
		getWrapped().putAll(values);
	}

	@Override
	public void putNow(String key, Object value) {
		getWrapped().putNow(key, value);
	}

	public Object remove(Object key) {
		return getWrapped().remove(key);
	}

	public int size() {
		return getWrapped().size();
	}

	public Collection<Object> values() {
		return getWrapped().values();
	}

	@Override
	public abstract boolean isServletResponseRequired();

	@Override
	public void setKeepMessages(boolean newValue) {
		getWrapped().setKeepMessages(newValue);
	}

	@Override
	public void setRedirect(boolean newValue) {
		getWrapped().setRedirect(newValue);
	}

	@Override
	public boolean isKeepMessages() {
		return getWrapped().isKeepMessages();
	}

	@Override
	public boolean isRedirect() {
		return getWrapped().isRedirect();
	}

	public abstract BridgeFlash getWrapped();

	public boolean isEmpty() {
		return getWrapped().isEmpty();
	}
}
