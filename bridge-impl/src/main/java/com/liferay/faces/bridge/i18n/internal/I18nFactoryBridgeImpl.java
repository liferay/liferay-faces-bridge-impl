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
package com.liferay.faces.bridge.i18n.internal;

import java.io.Serializable;

import com.liferay.faces.util.i18n.I18n;
import com.liferay.faces.util.i18n.I18nFactory;


/**
 * @author  Neil Griffin
 */
public class I18nFactoryBridgeImpl extends I18nFactory implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 612047590542268570L;

	// Private Data Members
	private I18nFactory wrappedI18nFactory;
	private I18n i18n;

	public I18nFactoryBridgeImpl(I18nFactory i18nFactory) {
		I18n wrappedI18n = i18nFactory.getI18n();
		this.i18n = new I18nBridgeImpl(wrappedI18n);
		this.wrappedI18nFactory = i18nFactory;
	}

	@Override
	public I18n getI18n() {
		return i18n;
	}

	@Override
	public I18nFactory getWrapped() {
		return wrappedI18nFactory;
	}
}
