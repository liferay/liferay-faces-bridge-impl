/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.map.internal;

/**
 * This class provides a compatibility layer that isolates differences between JSF 2.3 and earlier versions of JSF.
 *
 * @author  Neil Griffin
 */
public abstract class ContextMapFactoryCompatImpl extends ContextMapFactory {

	protected String getSeparatorChar() {

		// JSF 2.3 introduced usage of the separator char to delimit the namespace prefix, but this is not necessary
		// in earlier version fo JSF, so simply return an empty string.
		return "";
	}
}
