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
package com.liferay.faces.bridge.bean.internal;

/**
 * @author  Neil Griffin
 */
public interface PreDestroyInvoker {

	/**
	 * This method services as a convenience routine for invoking all methods of the specified managed-bean are marked
	 * with the {@link javax.annotation.PreDestroy} or {@link javax.portlet.faces.annotation.BridgePreDestroy}
	 * annotation. The JavaDocs state that if an exception is thrown by any of the pre-destroy annotated methods, they
	 * are required to be caught and NOT re-thrown. Instead, exceptions are logged.
	 *
	 * @param  managedBean       The managed-bean that is to have its {@link javax.annotation.PreDestroy} or {@link
	 *                           javax.portlet.faces.annotation.BridgePreDestroy} annotated method(s) invoked, if any.
	 * @param  preferPreDestroy  Flag indicating that methods annotated with {@link javax.annotation.PreDestroy} should
	 *                           be preferably invoked over those annotated with {@link
	 *                           javax.portlet.faces.annotation.BridgePreDestroy}.
	 */
	public void invokeAnnotatedMethods(Object managedBean, boolean preferPreDestroy);
}
