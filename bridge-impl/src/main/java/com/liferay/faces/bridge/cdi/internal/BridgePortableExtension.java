/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.cdi.internal;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType
import javax.portlet.annotations.PortletRequestScoped;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.portlet.faces.annotation.BridgeRequestScoped;
import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author  Neil Griffin
 * @author  Kyle Stiemann
 */
public class BridgePortableExtension implements Extension {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePortableExtension.class);

	public void step1BeforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {

		beforeBeanDiscovery.addScope(BridgeRequestScoped.class, true, false);
	}

	public void step2AfterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery) {

		afterBeanDiscovery.addContext(new BridgeRequestBeanContext());
	}

	private <T> void step3ProcessAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType,
											   @Observes ServletContext servletContext) {

		// Defaults to true.
		if (!"false".equalsIgnoreCase(servletContext.getInitParameter(
				"javax.portlet.faces.CONVERT_CDI_REQUEST_SCOPED_BEANS_TO_BRIDGE_REQUEST_SCOPED_BEANS"))) {
			AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();

			Class<T> javaClass = annotatedType.getJavaClass();

			// Check that the actual class has the @RequestScoped annotation instead of
			// annotatedType.isAnnotationPresent() since the Portlet CDI Portable Extension might have changed
			// @RequestScoped to @PortletRequestScoped.
			if (javaClass.getAnnotation(RequestScoped.class) != null) {

				Set<Annotation> annotations = new HashSet<>(annotatedType.getAnnotations());
				annotations.remove(new RequestScopedAnnotation());
				// Also remove the @PortletRequestScoped annotation if it was added by the Portlet CDI Portable
				// Extension.
				if (javaClass.getAnnotation(PortletRequestScoped.class) == null) {
					annotations.remove(new PortletRequestScopedAnnotation());
				}
				annotations.add(new BridgeRequestScopedAnnotation());

				if (!annotations.equals(annotatedType.getAnnotations())) {

					processAnnotatedType.setAnnotatedType(new ModifiedAnnotatedType<>(annotatedType, annotations,
							annotatedType.getTypeClosure()));
					logger.info("Automatically changed @RequestScoped annotation to @BridgeRequestScoped.");
				}
			}
		}
	}

	private static final class BridgeRequestScopedAnnotation implements Annotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			return BridgeRequestScoped.class;
		}
	}

	private static final class RequestScopedAnnotation implements Annotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			return RequestScoped.class;
		}
	}

	private static final class PortletRequestScopedAnnotation implements Annotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			return PortletRequestScoped.class;
		}
	}

	private static final class ModifiedAnnotatedType<X> implements AnnotatedType<X> {

		private final AnnotatedType<X> annotatedType;
		private final Set<Annotation> annotations;
		private final Set<Type> types;

		public ModifiedAnnotatedType(AnnotatedType<X> annotatedType, Set<Annotation> annotations, Set<Type> types) {

			this.annotatedType = annotatedType;
			this.annotations = Collections.unmodifiableSet(annotations);
			this.types = Collections.unmodifiableSet(types);
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationType) {

			for (Annotation annotation : annotations) {
				Class<? extends Annotation> curAnnotationType = annotation.annotationType();

				if (curAnnotationType.equals(annotationType)) {
					return annotationType.cast(annotation);
				}
			}

			return null;
		}

		@Override
		public Set<Annotation> getAnnotations() {
			return annotations;
		}

		@Override
		public Type getBaseType() {
			return annotatedType.getBaseType();
		}

		@Override
		public Set<AnnotatedConstructor<X>> getConstructors() {
			return annotatedType.getConstructors();
		}

		@Override
		public Set<AnnotatedField<? super X>> getFields() {
			return annotatedType.getFields();
		}

		@Override
		public Class<X> getJavaClass() {
			return annotatedType.getJavaClass();
		}

		@Override
		public Set<AnnotatedMethod<? super X>> getMethods() {
			return annotatedType.getMethods();
		}

		@Override
		public Set<Type> getTypeClosure() {
			return types;
		}

		@Override
		public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {

			for (Annotation annotation : annotations) {
				Class<? extends Annotation> curAnnotationType = annotation.annotationType();

				if (curAnnotationType.equals(annotationType)) {
					return true;
				}
			}

			return false;
		}

	}
}
