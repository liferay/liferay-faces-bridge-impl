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
package com.liferay.faces.bridge.el.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.portlet.faces.annotation.BridgePreDestroy;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ManagedBeanUtil {

	// Private Constants
	private static final String JAVAX_ANNOTATION_PRE_DESTROY = "javax.annotation.PreDestroy";
	private static final String JAVAX_ANNOTATION_BRIDGE_PRE_DESTROY = "javax.portlet.faces.annotation.BridgePreDestroy";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ManagedBeanUtil.class);

	/**
	 * Determines whether or not the specified method is annotated with the {@link BridgePreDestroy} annotation. Note
	 * that the method signature must also have a void return type an zero parameters in order for this method to return
	 * true.
	 *
	 * @param   method  The method to check.
	 *
	 * @return  true if the specified method is annotated with a PreDestroy annotation.
	 */
	public static boolean hasBridgePreDestroyAnnotation(Method method) {

		if (method.getReturnType() == Void.TYPE) {
			Class<?>[] parameterTypes = method.getParameterTypes();

			if ((parameterTypes == null) || (parameterTypes.length == 0)) {
				Annotation[] annotations = method.getAnnotations();

				if (annotations != null) {

					for (Annotation annotation : annotations) {

						if (annotation.annotationType().getName().equals(JAVAX_ANNOTATION_BRIDGE_PRE_DESTROY)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Determines whether or not the specified object is annotated as a JSF managed-bean.
	 *
	 * @param   obj  The object to check.
	 *
	 * @return  true if the specified object is annotated as a JSF managed-bean, otherwise false.
	 */
	public static boolean hasManagedBeanAnnotation(Object object) {

		// no-op for JSF 1.2
		return false;
	}

	/**
	 * Determines whether or not the specified method is annotated with the {@link PreDestroy} annotation. Note that the
	 * method signature must also have a void return type an zero parameters in order for this method to return true.
	 *
	 * @param   method  The method to check.
	 *
	 * @return  true if the specified method is annotated with a PreDestroy annotation.
	 */
	public static boolean hasPreDestroyAnnotation(Method method) {

		if (method.getReturnType() == Void.TYPE) {
			Class<?>[] parameterTypes = method.getParameterTypes();

			if ((parameterTypes == null) || (parameterTypes.length == 0)) {
				Annotation[] annotations = method.getAnnotations();

				if (annotations != null) {

					for (Annotation annotation : annotations) {

						if (annotation.annotationType().getName().equals(JAVAX_ANNOTATION_PRE_DESTROY)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * This method services as a convenience routine for invoking all methods of the specified managed-bean are marked
	 * with the {@link javax.annotation.PreDestroy} annotation. The JavaDocs state that if an exception is thrown by any
	 * of the PreDestroy annotated methods, they are required to be caught and NOT re-thrown. Instead, exceptions may be
	 * logged.
	 *
	 * @param  managedBean       The managed-bean that is to have its PreDestroy annotated method(s) invoked, if any.
	 * @param  preferPreDestroy  Boolean flag indicating that methods annotated with &#064;PreDestroy should be
	 *                           preferably invoked over those annotated with &#064;BridgePreDestroy.
	 */
	public static void invokePreDestroyMethods(Object managedBean, boolean preferPreDestroy) {

		if (managedBean != null) {

			Class<?> clazz = managedBean.getClass();
			Method[] methods = managedBean.getClass().getMethods();

			if (methods != null) {

				for (Method method : methods) {

					if (preferPreDestroy) {

						if (hasPreDestroyAnnotation(method)) {

							try {
								logger.debug("Invoking @PreDestroy method named [{0}] on managedBean class=[{1}]",
									method.getName(), clazz.getName());
								method.invoke(managedBean, new Object[] {});
							}
							catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
					else {

						if (hasBridgePreDestroyAnnotation(method)) {

							try {
								logger.debug("Invoking @BridgePreDestroy method named [{0}] on managedBean class=[{1}]",
									method.getName(), clazz.getName());
								method.invoke(managedBean, new Object[] {});
							}
							catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}

}
