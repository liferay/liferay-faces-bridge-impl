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
package com.liferay.faces.bridge.application.internal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceRichFacesCSSImpl extends FilteredResourceBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRichFacesCSSImpl.class);

	// FACES-1214
	private enum RichFacesImageResource {
		TYPE1(ResourceRichFacesImpl.ORG_RICHFACES, "../../org.richfaces.images/", "richfaces-type1"),
		TYPE2(ResourceRichFacesImpl.ORG_RICHFACES, "../../", "richfaces-type2"),
		TYPE3("org.richfaces.images", "../org.richfaces.images/", "richfaces-type3"),
		TYPE4("org.richfaces.images", "org.richfaces.images/", "richfaces-type4");

		private String libraryName;
		private String pathPrefix;
		private String substitutionToken;

		private RichFacesImageResource(String libraryName, String pathPrefix, String substitutionToken) {
			this.libraryName = libraryName;
			this.pathPrefix = pathPrefix;
			this.substitutionToken = substitutionToken;
		}

		public String getLibraryName() {
			return libraryName;
		}

		public String getPathPrefix() {
			return pathPrefix;
		}

		public String getSubstitutionToken() {
			return substitutionToken;
		}
	}

	// Private Members
	private Resource wrappedResource;

	public ResourceRichFacesCSSImpl(Resource wrappedResource) {

		// Since we cannot extend two classes, we wrap the default ResourceRichFacesImpl to ensure that all RichFaces
		// resource implementations include the base functionality.
		this.wrappedResource = new ResourceRichFacesImpl(wrappedResource);
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
	}

	@Override
	protected String filter(String cssText) {

		// Since the same image URL often appears more then once, maintain a cache of URLs for fast lookup.
		Map<String, String> resourceURLCache = new HashMap<String, String>();
		ResourceHandler resourceHandler = FacesContext.getCurrentInstance().getApplication().getResourceHandler();

		// For each of the RichFaces image resource types:
		for (RichFacesImageResource richFacesImageResource : RichFacesImageResource.values()) {

			// Parse the specified CSS text, and replace each relative URL with a ResourceURL.
			boolean doneProcessingURLs = false;

			while (!doneProcessingURLs) {

				String pathPrefix = richFacesImageResource.getPathPrefix();
				int urlStartPos = cssText.indexOf(pathPrefix);

				if (urlStartPos > 0) {

					int fileNameStartPos = urlStartPos + pathPrefix.length();

					int dotPos = cssText.indexOf(".", fileNameStartPos);

					if (dotPos > 0) {
						boolean doneFindingExtension = false;
						int extensionStartPos = dotPos + 1;
						int extensionFinishPos = extensionStartPos;

						while (!doneFindingExtension) {

							if ((extensionFinishPos < cssText.length()) &&
									Character.isLetterOrDigit(cssText.charAt(extensionFinishPos))) {
								extensionFinishPos++;
							}
							else {
								doneFindingExtension = true;
							}
						}

						String relativePathKey = cssText.substring(urlStartPos, extensionFinishPos);
						String imageResourceURL = resourceURLCache.get(relativePathKey);

						if (imageResourceURL == null) {
							String resourceName = cssText.substring(fileNameStartPos, extensionFinishPos);
							String libraryName = richFacesImageResource.getLibraryName();
							String substitutionToken = richFacesImageResource.getSubstitutionToken();
							Resource imageResource = resourceHandler.createResource(resourceName, libraryName);
							imageResourceURL = imageResource.getRequestPath();
							imageResourceURL = imageResourceURL.replaceAll(libraryName, substitutionToken);
							resourceURLCache.put(relativePathKey, imageResourceURL);
						}

						StringBuilder buf = new StringBuilder();
						buf.append(cssText.substring(0, urlStartPos));
						buf.append(imageResourceURL);
						buf.append(cssText.substring(extensionFinishPos));
						cssText = buf.toString();
					}
					else {
						logger.error("Unable to find image filename in URL");
					}
				}
				else {
					doneProcessingURLs = true;
				}
			}
		}

		for (RichFacesImageResource richFacesImageResource : RichFacesImageResource.values()) {
			cssText = cssText.replace(richFacesImageResource.getSubstitutionToken(),
					richFacesImageResource.getLibraryName());
		}

		return cssText;
	}
}
