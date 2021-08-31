/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import com.liferay.faces.bridge.annotation.BridgeRequestScoped;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is an optional CDI {@link Extension} that causes CDI beans that are annotated with {@link RequestScoped}
 * to instead be annotated with {@link BridgeRequestScoped}.
 *
 * @author  Neil Griffin
 * @author  Kyle Stiemann
 */
public class BridgeRequestScopeExtension implements Extension {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeExtension.class);

	private static final Annotation _bridgeRequestScoped = new BridgeRequestScoped() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return BridgeRequestScoped.class;
			}

		};

	private <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		Class<? extends Annotation> portletRequestScopedClass = null;

		try {
			portletRequestScopedClass = (Class<? extends Annotation>) Class.forName(
					"javax.portlet.annotations.PortletRequestScoped");
		}
		catch (ClassNotFoundException classNotFoundException) {
			// Ignore, likely running on Pluto
		}

		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();

		Set<Annotation> annotations = new HashSet<>(annotatedType.getAnnotations());

		if (annotatedType.isAnnotationPresent(RequestScoped.class)) {
			annotations.remove(annotatedType.getAnnotation(RequestScoped.class));
			annotations.add(_bridgeRequestScoped);
			logger.info("Automatically changed @RequestScoped to @BridgeRequestScoped for [{0}].",
				annotatedType.getJavaClass());
		}
		else if (annotatedType.isAnnotationPresent(portletRequestScopedClass)) {
			annotations.remove(annotatedType.getAnnotation(portletRequestScopedClass));
			annotations.add(_bridgeRequestScoped);
			logger.info("Automatically changed @PortletRequestScoped to @BridgeRequestScoped for [{0}].",
				annotatedType.getJavaClass());
		}

		if (!annotations.equals(annotatedType.getAnnotations())) {

			processAnnotatedType.setAnnotatedType(new ModifiedAnnotatedType<>(annotatedType, annotations,
					annotatedType.getTypeClosure()));
		}
	}

	private static final class BridgeRequestScopedAnnotation implements Annotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			return BridgeRequestScoped.class;
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
