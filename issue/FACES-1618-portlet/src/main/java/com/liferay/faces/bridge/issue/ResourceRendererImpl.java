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
package com.liferay.faces.bridge.issue;

import java.io.IOException;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;

import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.application.ResourceVerifierFactory;
import com.liferay.faces.util.context.FacesRequestContext;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class ResourceRendererImpl extends RendererWrapper implements ComponentSystemEventListener, StateHolder {

	// Private Constants

	//J-
	private static final String SCRIPT =
		"var listItem = document.createElement('li');" +
		"listItem.innerHTML = '{0}';" +
		"document.getElementById('FACES_1618_resources').appendChild(listItem);";
	//J+

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRendererImpl.class);

	// Private Data Members
	private boolean transientFlag;
	private Renderer wrappedRenderer;

	public ResourceRendererImpl() {
		// Defer initialization of wrappedRenderer until restoreState(FacesContext, Object) is called.
	}

	public ResourceRendererImpl(Renderer renderer) {
		this.wrappedRenderer = renderer;
	}

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponentResource) throws IOException {

		String resourceInfo = "<span>" + ResourceUtil.getResourceId(uiComponentResource) + "</span>";

		if (ResourceVerifierFactory.getResourceVerifierInstance().isDependencySatisfied(facesContext,
					uiComponentResource)) {
			resourceInfo += " was suppressed.";
		}
		else {
			resourceInfo += " was rendered.";
		}

		FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
		String resourceInfoScript = SCRIPT.replace("{0}", resourceInfo);
		facesRequestContext.addScript(resourceInfoScript);

		super.encodeBegin(facesContext, uiComponentResource);
	}

	@Override
	public Renderer getWrapped() {
		return wrappedRenderer;
	}

	@Override
	public boolean isTransient() {
		return transientFlag;
	}

	/**
	 * Since the Mojarra {@link com.sun.faces.renderkit.html_basic.ScriptStyleBaseRenderer} class implements {@link
	 * ComponentSystemEventListener}, this class must implement that interface too, since this is a wrapper type of
	 * class. Mojarra uses this method to intercept {@link PostAddToViewEvent} in order to add script and link resources
	 * to the head (if the target attribute has a value of "head").
	 */
	@Override
	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {

		// If the wrapped renderer has the ability to listen to component system events, then invoke the event
		// processing on the wrapped renderer. This is necessary when wrapping the Mojarra ScriptRenderer or
		// StylesheetRenderer because they extend ScriptStyleBaseRenderer which attempts to add the component
		// associated with the specified event as a resource on the view root.
		if (wrappedRenderer instanceof ComponentSystemEventListener) {

			ComponentSystemEventListener wrappedListener = (ComponentSystemEventListener) wrappedRenderer;
			wrappedListener.processEvent(event);
		}
		else {
			logger.debug("Wrapped renderer=[{0}] does not implement ComponentSystemEventListener", wrappedRenderer);
		}
	}

	@Override
	public void restoreState(FacesContext facesContext, Object state) {

		if (wrappedRenderer == null) {

			try {
				String wrappedRendererClassName = (String) state;
				Class<?> wrappedRendererClass = Class.forName(wrappedRendererClassName);
				wrappedRenderer = (Renderer) wrappedRendererClass.newInstance();
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public Object saveState(FacesContext facesContext) {
		return wrappedRenderer.getClass().getName();
	}

	@Override
	public void setTransient(boolean newTransientValue) {
		this.transientFlag = newTransientValue;
	}
}
