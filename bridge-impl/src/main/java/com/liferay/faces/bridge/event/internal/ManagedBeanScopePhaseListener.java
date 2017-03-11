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
package com.liferay.faces.bridge.event.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.bean.internal.BeanManager;
import com.liferay.faces.bridge.bean.internal.BeanManagerFactory;
import com.liferay.faces.util.config.ApplicationConfig;


/**
 * <p>According to the JSF 2.0 JavaDocs for {@link ExternalContext#getApplicationMap}, before a managed-bean is removed
 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be called
 * first. This would be equally true of any custom JSF 2.0 scope, such as the bridgeRequestScope. This class is a JSF
 * PhaseListener that listens after the RENDER_RESPONSE phase completes. Its purpose is to force the managed-beans in
 * bridgeRequestScope and requestScope to go out-of-scope which will in turn cause any annotated PreDestroy methods to
 * be called.</p>
 *
 * <p>Note that this functionality is implemented as a PhaseListener because I couldn't get it to work after the
 * lifecycle terminated. My suspicion is that Mojarra has some servlet dependency stuff going on. Specifically,
 * Mojarra's WebappLifecycleListener might be getting invoked or something. The strange thing is that it appears that
 * Mojarra makes managed-beans go away, but not by calling the map.clear() or map.remove() methods. Mojarra apparently
 * handles things with a listener that captures ServletContext attribute events, which may also be playing a role.
 * Anyway, doing this with a PhaseListener seems to work.</p>
 *
 * @author  Neil Griffin
 */
public class ManagedBeanScopePhaseListener implements PhaseListener {

	// serialVersionUID
	private static final long serialVersionUID = 1713704308484763548L;

	@Override
	public void afterPhase(PhaseEvent phaseEvent) {

		if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {

			FacesContext facesContext = phaseEvent.getFacesContext();

			PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

			if ((portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) ||
					(portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

				// Remove any managed-beans in request scope. According to the JSF 2.0 JavaDocs for {@link
				// ExternalContext.getRequestMap}, before a managed-bean is removed from the map, any public no-argument
				// void return methods annotated with javax.annotation.PreDestroy must be called first. Note that the
				// bridge {@link RequestAttributeMap.remove(Object)} method will ensure that any @PreDestroy method(s)
				// are called. The JavaDocs also state that this should only be the case for objects that are actually
				// managed-beans.
				ExternalContext externalContext = facesContext.getExternalContext();
				Map<String, Object> requestScope = externalContext.getRequestMap();
				List<String> managedBeanKeysToRemove = new ArrayList<String>();
				Set<Map.Entry<String, Object>> mapEntries = requestScope.entrySet();
				String appConfigAttrName = ApplicationConfig.class.getName();
				Map<String, Object> applicationMap = externalContext.getApplicationMap();
				ApplicationConfig applicationConfig = (ApplicationConfig) applicationMap.get(appConfigAttrName);
				BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(
						BeanManagerFactory.class);
				BeanManager beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());

				for (Map.Entry<String, Object> mapEntry : mapEntries) {
					String potentialManagedBeanName = mapEntry.getKey();
					Object potentialManagedBeanValue = mapEntry.getValue();

					// Note that the request attribute name will not have a namespace prefix, so it is fine to
					// simply pass the attribute name.
					if (beanManager.isManagedBean(potentialManagedBeanName, potentialManagedBeanValue)) {
						managedBeanKeysToRemove.add(potentialManagedBeanName);
					}
				}

				for (String managedBeanKey : managedBeanKeysToRemove) {
					requestScope.remove(managedBeanKey);
				}
			}
		}
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {
		// This method is required by the PhaseListener interface but is not used.
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
