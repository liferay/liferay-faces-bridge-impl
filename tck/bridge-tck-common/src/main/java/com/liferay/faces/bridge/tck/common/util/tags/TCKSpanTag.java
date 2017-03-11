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
package com.liferay.faces.bridge.tck.common.util.tags;

/**
 * This class provides a way to enclose text put in the HTTP response body by portlets or servlets in custom tags. On
 * the client side, a corresponding class CustomClientTag is used to read the text out.
 */
public class TCKSpanTag {
	protected String mId = ""; // tag Id
	protected String mSpanOpen = "";
	protected String mSpanClose = "";
	private StringBuffer mTagContent = new StringBuffer();

	/**
	 * Creates a new custom tag with no name and empty tag content.
	 */
	public TCKSpanTag() {
		this("");
	}

	/**
	 * Creates a new custom tag with the given name and empty tag content.
	 *
	 * @param      tagId  name of the custom tag.
	 *
	 * @exception  IllegalArgumentException  if tagName is <code>null</code>.
	 */
	public TCKSpanTag(String tagId) {

		/*
		 * Note: an empty tag name is actually allowed, in case you can't come up with a good name.  :-)  In this case,
		 * all you have is <>stuffs between the empty tags</>.  If you have only one tag in your HTTP response body,
		 * this may not be a bad idea...
		 */
		if (tagId == null) {
			throw new IllegalArgumentException("tagId can't be null");
		}

		mSpanOpen = "<span id=" + tagId + ">";
		mSpanClose = "</span>";
	}

	/*
	 * Appends the tag content.
	 *
	 * @param content the tag content to be set to.  It will be set                  literally to the string "null" if
	 * content                  is <code>null</code>.
	 */
	public void appendTagContent(String content) {
		mTagContent.append((content == null) ? "null" : content);
	}

	public String getEndTag() {
		return mSpanClose;
	}

	public String getStartTag() {
		return mSpanOpen;
	}

	/*
	 * Sets the tag content.
	 *
	 * @param content the tag content to be set to.  It will be set                  literally to the string "null" if
	 * content                  is <code>null</code>.
	 */
	public void setTagContent(String content) {
		mTagContent = new StringBuffer((content == null) ? "null" : content);
	}

	/**
	 * Returns a String that can be written out to the HTTP response.
	 */
	public String toString() {
		return mSpanOpen + mTagContent + mSpanClose;
	}

}
