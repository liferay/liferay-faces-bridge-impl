/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.el;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.portlet.RenderRequest;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.el.internal.ELResolverImpl;


/**
 * @author  Kyle Stiemann
 */
public class ELResolverImplTest {

	private static final Map<String, Class<?>> EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES;

	static {

		Map<String, Class<?>> expectedElResolverFeatureDescriptorTypes = new HashMap<String, Class<?>>();
		expectedElResolverFeatureDescriptorTypes.put("portletConfig", javax.portlet.PortletConfig.class);
		expectedElResolverFeatureDescriptorTypes.put("actionRequest", javax.portlet.ActionRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("actionResponse", javax.portlet.ActionResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("eventRequest", javax.portlet.EventRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("eventResponse", javax.portlet.EventResponse.class);
		addExpectedFeatureDescriptorTypeIfPossible("headerRequest", "javax.portlet.HeaderRequest",
			expectedElResolverFeatureDescriptorTypes);
		addExpectedFeatureDescriptorTypeIfPossible("headerResponse", "javax.portlet.HeaderResponse",
			expectedElResolverFeatureDescriptorTypes);
		expectedElResolverFeatureDescriptorTypes.put("renderRequest", javax.portlet.RenderRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("renderResponse", javax.portlet.RenderResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("resourceRequest", javax.portlet.ResourceRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("resourceResponse", javax.portlet.ResourceResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("portletSession", javax.portlet.PortletSession.class);
		expectedElResolverFeatureDescriptorTypes.put("portletSessionScope", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("httpSessionScope", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("portletPreferences", javax.portlet.PortletPreferences.class);
		expectedElResolverFeatureDescriptorTypes.put("portletPreferencesValues", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("mutablePortletPreferencesValues", Map.class);
		EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES = Collections.unmodifiableMap(
				expectedElResolverFeatureDescriptorTypes);
	}

	private static void addExpectedFeatureDescriptorTypeIfPossible(String key, String classNameValue,
		Map<String, Class<?>> map) {

		try {
			map.put(key, Class.forName(classNameValue));
		}
		catch (ClassNotFoundException e) {
			// no-op
		}
		catch (NoClassDefFoundError e) {
			// no-op
		}
	}

	private static boolean isFeatureDescriptorValid(String name, FeatureDescriptor featureDescriptor, Class<?> clazz) {

		return (featureDescriptor != null) && clazz.equals(featureDescriptor.getValue(ELResolver.TYPE)) &&
			Boolean.TRUE.equals(featureDescriptor.getValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME)) &&
			name.equals(featureDescriptor.getName()) && name.equals(featureDescriptor.getName()) &&
			(featureDescriptor.getShortDescription() != null) && !featureDescriptor.isExpert() &&
			!featureDescriptor.isHidden() && featureDescriptor.isPreferred();
	}

	/**
	 * See spec section 6.5.2.2.
	 */
	@Test
	public void testELResolverImplGetCommonPropertyType() {

		ELResolver elResolver = new ELResolverImpl();
		ELContext elContext = new ELContextMockImpl(elResolver);
		Assert.assertNull(elResolver.getCommonPropertyType(elContext, "facesContext"));
		Assert.assertEquals(String.class, elResolver.getCommonPropertyType(elContext, null));
	}

	/**
	 * See spec section 6.5.2.2.
	 */
	@Test
	public void testELResolverImplGetFeatureDescriptors() {

		ELResolver elResolver = new ELResolverImpl();
		ELContext elContext = new ELContextMockImpl(elResolver);
		Assert.assertNull(
			"ELResolver.getFeatureDescriptors() did not return null when passed a non-null value for base.",
			elResolver.getFeatureDescriptors(elContext, "facesContext"));

		Iterator<FeatureDescriptor> iterator = elResolver.getFeatureDescriptors(elContext, null);
		Map<String, FeatureDescriptor> featureDescriptors = new HashMap<String, FeatureDescriptor>();

		while (iterator.hasNext()) {

			FeatureDescriptor featureDescriptor = iterator.next();
			featureDescriptors.put(featureDescriptor.getName(), featureDescriptor);
		}

		featureDescriptors = Collections.unmodifiableMap(featureDescriptors);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("The following EL key words had invalid feature descriptors: ");

		boolean first = true;
		Set<Map.Entry<String, Class<?>>> entrySet = EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES.entrySet();

		for (Map.Entry<String, Class<?>> entry : entrySet) {

			String name = entry.getKey();
			FeatureDescriptor featureDescriptor = featureDescriptors.get(name);

			if (!isFeatureDescriptorValid(name, featureDescriptor, entry.getValue())) {

				if (!first) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(name);
				stringBuilder.append(" (expected ELResolver.TYPE \"");
				stringBuilder.append(EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES.get(name));
				stringBuilder.append("\")");
				first = false;
			}
		}

		stringBuilder.append(
			". ELResolver.RESOLVABLE_AT_DESIGN_TIME must be Boolean.TRUE. getName() and getDisplayName() must return the same value. getShortDescription() must not be null. isExpert() and isHidden() must return false. isPreferred() must return true. ");

		Assert.assertTrue(stringBuilder.toString(), first);
	}
}
