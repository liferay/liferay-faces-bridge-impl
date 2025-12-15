/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.support.thick.cdi.internal;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.portlet.annotations.ContextPath;
import jakarta.portlet.annotations.Namespace;
import jakarta.portlet.annotations.PortletName;
import jakarta.portlet.annotations.PortletRequestScoped;
import jakarta.portlet.annotations.WindowId;


/**
 * Note: The source for class is mostly taken from the {@link
 * com.liferay.bean.portlet.cdi.extension.internal.CDIBeanPortletExtension} class.
 *
 * @author  Neil Griffin
 */
public class ThickExtension implements Extension {

	private static final Annotation portletRequestScoped = new PortletRequestScoped() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return PortletRequestScoped.class;
			}

		};

	public void step1BeforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {

		beforeBeanDiscovery.addQualifier(ContextPath.class);
		beforeBeanDiscovery.addQualifier(Namespace.class);
		beforeBeanDiscovery.addQualifier(PortletName.class);
		beforeBeanDiscovery.addQualifier(WindowId.class);
		beforeBeanDiscovery.addScope(PortletRequestScoped.class, true, false);
	}

	public <T> void step2ProcessAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();

		Set<Annotation> annotations = new HashSet<>(annotatedType.getAnnotations());

		if (annotations.remove(annotatedType.getAnnotation(RequestScoped.class)) &&
				!annotatedType.isAnnotationPresent(PortletRequestScoped.class)) {

			annotations.add(portletRequestScoped);
		}

	}

	public void step3AfterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery) {

		afterBeanDiscovery.addContext(new PortletRequestBeanContext());
	}
}
