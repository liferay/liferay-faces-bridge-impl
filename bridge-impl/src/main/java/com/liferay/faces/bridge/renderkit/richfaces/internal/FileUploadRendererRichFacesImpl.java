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
package com.liferay.faces.bridge.renderkit.richfaces.internal;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class is a runtime wrapper around the RichFaces FileUploadRenderer class that makes the rich:fileUpload
 * component compatible with a portlet environment.
 *
 * @author  Neil Griffin
 */
public class FileUploadRendererRichFacesImpl extends RendererWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FileUploadRendererRichFacesImpl.class);

	// Private Constants
	private static final String RICHFACES_UPLOADED_FILE_FQCN = "org.richfaces.model.UploadedFile";
	private static final String RICHFACES_FILE_UPLOAD_EVENT_FQCN = "org.richfaces.event.FileUploadEvent";
	private static final Product RICHFACES = ProductFactory.getProduct(Product.Name.RICHFACES);
	private static final boolean FACES_2638 = RICHFACES.isDetected() && (RICHFACES.getMajorVersion() == 4) &&
		(RICHFACES.getMinorVersion() == 5) && (RICHFACES.getPatchVersion() >= 16);

	// Private Data Members
	private Renderer wrappedRenderer;

	public FileUploadRendererRichFacesImpl(Renderer renderer) {
		this.wrappedRenderer = renderer;
	}

	/**
	 * This method overrides the {@link RendererWrapper#decode(FacesContext, UIComponent)} method so that it can avoid a
	 * Servlet-API dependency in the RichFaces FileUploadRenderer. Note that rich:fileUpload will do an Ajax postback
	 * and invoke the JSF lifecycle for each individual file.
	 */
	@Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {

		try {

			// Get the UploadedFile from the request attribute map.
			ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(
					ContextMapFactory.class);

			ExternalContext externalContext = facesContext.getExternalContext();
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			Map<String, List<UploadedFile>> uploadedFileMap = contextMapFactory.getUploadedFileMap(portletRequest);

			if (uploadedFileMap != null) {

				// Use reflection to create a dynamic proxy class that implements the RichFaces UploadedFile interface.
				Class<?> uploadedFileInterface = Class.forName(RICHFACES_UPLOADED_FILE_FQCN);
				Class<?> fileUploadEventClass = Class.forName(RICHFACES_FILE_UPLOAD_EVENT_FQCN);
				ClassLoader classLoader = uploadedFileInterface.getClassLoader();

				String clientId = uiComponent.getClientId(facesContext);
				List<UploadedFile> uploadedFiles = uploadedFileMap.get(clientId);

				if (uploadedFiles != null) {

					for (UploadedFile uploadedFile : uploadedFiles) {
						RichFacesUploadedFileHandler richFacesUploadedFileHandler = new RichFacesUploadedFileHandler(
								uploadedFile);
						Object richFacesUploadedFile = Proxy.newProxyInstance(classLoader,
								new Class[] { uploadedFileInterface }, richFacesUploadedFileHandler);
						FacesEvent fileUploadEvent = (FacesEvent) fileUploadEventClass.getConstructor(UIComponent.class,
								uploadedFileInterface).newInstance(uiComponent, richFacesUploadedFile);

						// Queue the RichFaces FileUploadEvent instance so that it can be handled with an
						// ActionListener.
						uiComponent.queueEvent(fileUploadEvent);
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		if (FACES_2638) {

			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("FACES-2638", Boolean.TRUE);
			super.encodeEnd(facesContext, uiComponent);
			requestMap.remove("FACES-2638");
		}
		else {
			super.encodeEnd(facesContext, uiComponent);

		}
	}

	@Override
	public Renderer getWrapped() {
		return wrappedRenderer;
	}
}
