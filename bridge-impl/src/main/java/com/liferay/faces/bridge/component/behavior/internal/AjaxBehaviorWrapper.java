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
package com.liferay.faces.bridge.component.behavior.internal;

import java.util.Collection;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.FacesWrapper;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.BehaviorEvent;


/**
 * @author  Neil Griffin
 */
public abstract class AjaxBehaviorWrapper extends AjaxBehavior implements FacesWrapper<AjaxBehavior> {

	@Override
	public abstract AjaxBehavior getWrapped();

	@Override
	public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
		getWrapped().addAjaxBehaviorListener(listener);
	}

	@Override
	public void broadcast(BehaviorEvent event) throws AbortProcessingException {
		getWrapped().broadcast(event);
	}

	@Override
	public void clearInitialState() {
		getWrapped().clearInitialState();
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		getWrapped().decode(context, component);
	}

	@Override
	public Collection<String> getExecute() {
		return getWrapped().getExecute();
	}

	@Override
	public Set<ClientBehaviorHint> getHints() {
		return getWrapped().getHints();
	}

	@Override
	public String getOnerror() {
		return getWrapped().getOnerror();
	}

	@Override
	public String getOnevent() {
		return getWrapped().getOnevent();
	}

	@Override
	public Collection<String> getRender() {
		return getWrapped().getRender();
	}

	@Override
	public String getRendererType() {
		return getWrapped().getRendererType();
	}

	@Override
	public String getScript(ClientBehaviorContext behaviorContext) {
		return getWrapped().getScript(behaviorContext);
	}

	@Override
	public ValueExpression getValueExpression(String name) {
		return getWrapped().getValueExpression(name);
	}

	@Override
	public boolean initialStateMarked() {
		return getWrapped().initialStateMarked();
	}

	@Override
	public boolean isDisabled() {
		return getWrapped().isDisabled();
	}

	@Override
	public boolean isImmediate() {
		return getWrapped().isImmediate();
	}

	@Override
	public boolean isImmediateSet() {
		return getWrapped().isImmediateSet();
	}

	@Override
	public boolean isTransient() {
		return getWrapped().isTransient();
	}

	@Override
	public void markInitialState() {
		getWrapped().markInitialState();
	}

	@Override
	public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
		getWrapped().removeAjaxBehaviorListener(listener);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		getWrapped().restoreState(context, state);
	}

	@Override
	public Object saveState(FacesContext context) {
		return getWrapped().saveState(context);
	}

	@Override
	public void setDisabled(boolean disabled) {
		getWrapped().setDisabled(disabled);
	}

	@Override
	public void setExecute(Collection<String> execute) {
		getWrapped().setExecute(execute);
	}

	@Override
	public void setImmediate(boolean immediate) {
		getWrapped().setImmediate(immediate);
	}

	@Override
	public void setOnerror(String onerror) {
		getWrapped().setOnerror(onerror);
	}

	@Override
	public void setOnevent(String onevent) {
		getWrapped().setOnevent(onevent);
	}

	@Override
	public void setRender(Collection<String> render) {
		getWrapped().setRender(render);
	}

	@Override
	public void setTransient(boolean transientFlag) {
		getWrapped().setTransient(transientFlag);
	}

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		getWrapped().setValueExpression(name, binding);
	}
}
