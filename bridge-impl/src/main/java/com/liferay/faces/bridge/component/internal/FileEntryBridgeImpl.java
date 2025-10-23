/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.component.internal;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.icefaces.ace.component.fileentry.FileEntry;

import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.internal.PortletConfigParamUtil;


/**
 * This class exists to ensure that <code>ace:fileEntry</code> respects the bridge's file upload configuration
 * parameters: {@link com.liferay.faces.bridge.internal.PortletConfigParam#UploadedFileMaxSize} and {@link
 * com.liferay.faces.bridge.internal.PortletConfigParam#UploadedFilesDir}. See <a
 * href="https://issues.liferay.com/browse/LSV-485">LSV-485</a> and <a
 * href="https://issues.liferay.com/browse/FACES-3390">FACES-3390</a> for more details.
 *
 * @author  Kyle Stiemann
 */
public class FileEntryBridgeImpl extends FileEntry {

	// Private Constants
	private static final AttributeGetter<Long> MAX_FILE_SIZE_GETTER = new AttributeGetterMaxFileSizeImpl();
	private static final AttributeGetter<String> ABSOLUTE_PATH_GETTER = new AttributeGetterAbsolutePathImpl();

	@Override
	public String getAbsolutePath() {
		return ABSOLUTE_PATH_GETTER.getAttribute(this);
	}

	@Override
	public long getMaxFileSize() {
		return MAX_FILE_SIZE_GETTER.getAttribute(this);
	}

	private String getOriginalAbsolutePath() {
		return super.getAbsolutePath();
	}

	private long getOriginalMaxFileSize() {
		return super.getMaxFileSize();
	}

	/**
	 * Subclasses of this class must be stateless/thread-safe.
	 *
	 * @param  <T>
	 */
	private abstract static class AttributeGetter<T> {

		public abstract T getAttribute(FileEntryBridgeImpl fileEntryBridgeImpl);

		/* package-private */ abstract T convertInitParamStringValueToAttribute(String initParamValue);

		/* package-private */ abstract T getOriginalAttribute(FileEntryBridgeImpl fileEntryBridgeImpl);

		/**
		 * @param   attr                 guaranteed to be non-null by the time this method is called.
		 * @param   fileEntryBridgeImpl
		 *
		 * @return
		 */
		/* package-private */ abstract boolean useOriginalAttribute(T attr, FileEntryBridgeImpl fileEntryBridgeImpl);

		/* package-private */ final T getAttribute(FileEntryBridgeImpl fileEntryBridgeImpl, String bridgeAttrName,
			PortletConfigParam portletConfigParam) {

			Map<String, Object> attributes = fileEntryBridgeImpl.getAttributes();
			T attr = (T) attributes.get(bridgeAttrName);

			if (attr == null) {

				FacesContext facesContext = fileEntryBridgeImpl.getFacesContext();
				ExternalContext externalContext = facesContext.getExternalContext();
				String initParamStringValue = PortletConfigParamUtil.getStringValue(externalContext,
						portletConfigParam);

				if (initParamStringValue == null) {
					attr = getOriginalAttribute(fileEntryBridgeImpl);
				}
				else {

					attr = convertInitParamStringValueToAttribute(initParamStringValue);

					if (useOriginalAttribute(attr, fileEntryBridgeImpl)) {
						attr = getOriginalAttribute(fileEntryBridgeImpl);
					}
				}
			}

			return attr;
		}
	}

	private static final class AttributeGetterAbsolutePathImpl extends AttributeGetter<String> {

		// Private Constants
		private static final String BRIDGE_ABSOLUTE_PATH_ATTR_NAME = AttributeGetterMaxFileSizeImpl.class.getName() +
			".absolutePath";

		@Override
		public String getAttribute(FileEntryBridgeImpl fileEntryBridgeImpl) {
			return getAttribute(fileEntryBridgeImpl, BRIDGE_ABSOLUTE_PATH_ATTR_NAME,
					PortletConfigParam.UploadedFilesDir);
		}

		@Override
		/* package-private */ String convertInitParamStringValueToAttribute(String initParamValue) {
			return initParamValue;
		}

		@Override
		/* package-private */ String getOriginalAttribute(FileEntryBridgeImpl fileEntryBridgeImpl) {
			return fileEntryBridgeImpl.getOriginalAbsolutePath();
		}

		@Override
		/* package-private */ boolean useOriginalAttribute(String attr, FileEntryBridgeImpl fileEntryBridgeImpl) {
			return true;
		}
	}

	private static final class AttributeGetterMaxFileSizeImpl extends AttributeGetter<Long> {

		// Private Constants
		private static final String BRIDGE_MAX_FILE_SIZE_ATTR_NAME = AttributeGetterMaxFileSizeImpl.class.getName() +
			".maxFileSize";

		@Override
		public Long getAttribute(FileEntryBridgeImpl fileEntryBridgeImpl) {
			return getAttribute(fileEntryBridgeImpl, BRIDGE_MAX_FILE_SIZE_ATTR_NAME,
					PortletConfigParam.UploadedFileMaxSize);
		}

		@Override
		/* package-private */ Long convertInitParamStringValueToAttribute(String initParamValue) {
			return Long.parseLong(initParamValue);
		}

		@Override
		/* package-private */ Long getOriginalAttribute(FileEntryBridgeImpl fileEntryBridgeImpl) {
			return fileEntryBridgeImpl.getOriginalMaxFileSize();
		}

		@Override
		/* package-private */ boolean useOriginalAttribute(Long attr, FileEntryBridgeImpl fileEntryBridgeImpl) {
			return attr > fileEntryBridgeImpl.getOriginalMaxFileSize();
		}
	}
}
