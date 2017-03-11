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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class serves as a wrapper around the renderer provided by the JSF runtime (Mojarra, MyFaces) for either the
 * h:outputScript or h:outputStylesheet components. The main purpose of wrapping the renderer is to provide a way for
 * the bridge to keep track of which resources are being added to the &lt;head&gt;...&lt;/head&gt; section of the portal
 * page.
 *
 * @author  Neil Griffin
 */
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class ResourceRendererBridgeImpl extends RendererWrapper implements ComponentSystemEventListener, StateHolder {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRendererBridgeImpl.class);

	// Private Data Members
	private boolean transientFlag;
	private Renderer wrappedRenderer;

	/**
	 * This zero-arg constructor is required by the {@link javax.faces.component.StateHolderSaver} class during the
	 * RESTORE_VIEW phase of the JSF lifecycle. The reason why this class is involved in restoring state is because the
	 * {@link javax.faces.component.UIComponent.ComponentSystemEventListenerAdapter} implements {@link
	 * javax.faces.component.StateHolder} and will attempt to restore the state of any class in the restored view that
	 * implements {@link ComponentSystemEventListener}. It does this first by instantiating the class with a zero-arg
	 * constructor, and then calls the {@link #restoreState(FacesContext, Object)} method.
	 */
	public ResourceRendererBridgeImpl() {
		// Defer initialization of wrappedRenderer until restoreState(FacesContext, Object) is called.
	}

	public ResourceRendererBridgeImpl(Renderer renderer) {
		this.wrappedRenderer = renderer;
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponentResource) throws IOException {

		boolean ajaxRequest = facesContext.getPartialViewContext().isAjaxRequest();
		ResponseWriter responseWriter = null;

		// If this is taking place during an Ajax request, then:
		if (ajaxRequest) {

			// Set a custom response writer that knows how to remove double-encoded ampersands from URLs.
			responseWriter = facesContext.getResponseWriter();
			facesContext.setResponseWriter(new ResponseWriterResourceImpl(responseWriter));
		}

		// Ask the wrapped renderer to encode the script.
		super.encodeEnd(facesContext, uiComponentResource);

		if (ajaxRequest) {

			// Restore the original response writer.
			facesContext.setResponseWriter(responseWriter);
		}
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
