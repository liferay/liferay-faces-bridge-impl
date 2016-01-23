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
package com.liferay.faces.test;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;


/**
 * @author  Neil Griffin
 */
public class HeadResource {

	// Private Data Members
	private List<HeadResourceAttribute> attributeList;
	private boolean duplicate;
	private String facesResource;
	private String facesLibrary;
	private String text;
	private String type;
	private String url;

	public HeadResource(String type, String url) {

		if (type != null) {
			type = type.toLowerCase();
		}

		this.type = type;
		this.url = url;
		initialize();
	}

	public HeadResource(String type, Attributes attributes) {

		if (type != null) {
			type = type.toLowerCase();
		}

		this.type = type;

		if (attributes != null) {
			this.attributeList = new ArrayList<HeadResourceAttribute>();

			int totalAttributes = attributes.getLength();

			for (int i = 0; i < totalAttributes; i++) {
				String name = attributes.getLocalName(i);
				String value = attributes.getValue(i);
				HeadResourceAttribute headResourceAttribute = new HeadResourceAttribute(name, value);
				this.attributeList.add(headResourceAttribute);
			}
		}

		if ("link".equals(type)) {
			url = attributes.getValue("href");
		}
		else if ("script".equals(type)) {
			url = attributes.getValue("src");
		}

		initialize();
	}

	@Override
	public boolean equals(Object headResource) {

		boolean equal = false;
		HeadResource otherHeadResource = (HeadResource) headResource;

		if ((url != null) && url.equals(otherHeadResource.getURL())) {
			equal = true;
		}
		else if (("link".equals(type) || "script".equals(type)) && type.equals(otherHeadResource.getType())) {

			String facesResource2 = otherHeadResource.getFacesResource();
			String facesLibrary2 = otherHeadResource.getFacesLibrary();

			if ((facesResource != null) && facesResource.equals(facesResource2)) {

				if ((facesLibrary != null) && facesLibrary.equals(facesLibrary2)) {
					equal = true;
				}
			}
		}

		return equal;
	}

	@Override
	public String toString() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<");
		stringBuilder.append(type);

		if (attributeList != null) {

			for (HeadResourceAttribute headResourceAttribute : attributeList) {
				stringBuilder.append(" ");
				stringBuilder.append(headResourceAttribute.getName());
				stringBuilder.append("=\"");
				stringBuilder.append(headResourceAttribute.getValue());
				stringBuilder.append("\"");
			}
		}

		stringBuilder.append(">");

		if (text != null) {
			stringBuilder.append(text);
		}

		stringBuilder.append("</");
		stringBuilder.append(type);
		stringBuilder.append(">");

		return stringBuilder.toString();
	}

	protected void initialize() {

		if (url != null) {

			int queryPos = url.indexOf("?");

			if (queryPos > 0) {
				String parameters = url.substring(queryPos + 1);
				String[] nameValuePairs = parameters.split("[&]");

				for (String nameValuePair : nameValuePairs) {
					int equalsPos = nameValuePair.indexOf("=");

					if (equalsPos > 0) {
						String name = nameValuePair.substring(0, equalsPos);

						if (name.endsWith("javax.faces.resource")) {
							facesResource = nameValuePair.substring(equalsPos + 1);
						}
						else if (name.endsWith("ln")) {
							facesLibrary = nameValuePair.substring(equalsPos + 1);
						}
					}

					if ((facesResource != null) && (facesLibrary != null)) {
						break;
					}
				}
			}
		}
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public String getFacesLibrary() {
		return facesLibrary;
	}

	public String getFacesResource() {
		return facesResource;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public String getURL() {
		return url;
	}
}
