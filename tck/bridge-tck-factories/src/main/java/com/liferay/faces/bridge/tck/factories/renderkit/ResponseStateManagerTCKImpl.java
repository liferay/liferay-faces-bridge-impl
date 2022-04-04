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
package com.liferay.faces.bridge.tck.factories.renderkit;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;


/**
 * @author  Neil Griffin
 */
public class ResponseStateManagerTCKImpl extends ResponseStateManager {

	private ResponseStateManager wrappedResponseStateManager;

	public ResponseStateManagerTCKImpl(ResponseStateManager responseStateManager) {
		this.wrappedResponseStateManager = responseStateManager;
	}

	@Override
	public Object getComponentStateToRestore(FacesContext facesContext) {
		return wrappedResponseStateManager.getComponentStateToRestore(facesContext);
	}

	@Override
	public String getCryptographicallyStrongTokenFromSession(FacesContext facesContext) {
		return wrappedResponseStateManager.getCryptographicallyStrongTokenFromSession(facesContext);
	}

	@Override
	public Object getState(FacesContext facesContext, String viewId) {
		return wrappedResponseStateManager.getState(facesContext, viewId);
	}

	@Override
	public Object getTreeStructureToRestore(FacesContext facesContext, String viewId) {
		return wrappedResponseStateManager.getTreeStructureToRestore(facesContext, viewId);
	}

	@Override
	public String getViewState(FacesContext facesContext, Object state) {
		return wrappedResponseStateManager.getViewState(facesContext, state);
	}

	@Override
	public boolean isPostback(FacesContext facesContext) {
		return wrappedResponseStateManager.isPostback(facesContext);
	}

	@Override
	public boolean isStateless(FacesContext facesContext, String viewId) {
		return wrappedResponseStateManager.isStateless(facesContext, viewId);
	}

	@Override
	public void writeState(FacesContext facesContext, Object state) throws IOException {

		Map<Object, Object> attributes = facesContext.getAttributes();

		// The Mojarra ResponseStateManagerImpl attempts to access a session attribute, which will cause an
		// IllegalStateException after a session is invalidated. So in order for the invalidateSessionTest to
		// pass, it is necessary to avoid writing the state field.
		if (!attributes.containsKey("invalidateSessionTest")) {
			wrappedResponseStateManager.writeState(facesContext, state);
		}
	}

	@Override
	public void writeState(FacesContext facesContext, StateManager.SerializedView state) throws IOException {
		wrappedResponseStateManager.writeState(facesContext, state);
	}
}
