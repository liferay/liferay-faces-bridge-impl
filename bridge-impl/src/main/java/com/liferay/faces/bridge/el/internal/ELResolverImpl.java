/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeUtil;
import javax.servlet.jsp.JspContext;

import com.liferay.faces.bridge.context.internal.LegacyBridgeContext;
import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.preference.internal.MutablePreferenceMap;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * @author  Neil Griffin
 */
public class ELResolverImpl extends ELResolverCompatImpl {

	// Private Constants
	private static final String ACTION_REQUEST = "actionRequest";
	private static final String ACTION_RESPONSE = "actionResponse";
	private static final String BRIDGE_CONFIG = "bridgeConfig";
	private static final String BRIDGE_CONTEXT = "bridgeContext";
	private static final String EVENT_REQUEST = "eventRequest";
	private static final String EVENT_RESPONSE = "eventResponse";
	private static final String FLASH = "bridgeFlash"; // http://java.net/jira/browse/JAVASERVERFACES-1964
	private static final String HTTP_SESSION_SCOPE = "httpSessionScope";
	private static final String MUTABLE_PORTLET_PREFERENCES_VALUES = "mutablePortletPreferencesValues";
	private static final String PORTLET_CONFIG = "portletConfig";
	private static final String PORTLET_SESSION = "portletSession";
	private static final String PORTLET_SESSION_SCOPE = "portletSessionScope";
	private static final String PORTLET_PREFERENCES = "portletPreferences";
	private static final String PORTLET_PREFERENCES_VALUES = "portletPreferencesValues";
	private static final String RENDER_REQUEST = "renderRequest";
	private static final String RENDER_RESPONSE = "renderResponse";
	private static final String RESOURCE_REQUEST = "resourceRequest";
	private static final String RESOURCE_RESPONSE = "resourceResponse";
	private static final List<FeatureDescriptor> FEATURE_DESCRIPTORS;
	private static final Set<String> FACES_CONTEXT_VAR_NAMES;
	private static final Set<String> JSP_CONTEXT_VAR_NAMES;

	static {

		// Initialize hash set of supported EL variable names when running in a Faces context.
		Set<String> facesContextVarNames = new HashSet<String>();
		facesContextVarNames.add(ACTION_REQUEST);
		facesContextVarNames.add(ACTION_RESPONSE);
		facesContextVarNames.add(BRIDGE_CONTEXT);
		facesContextVarNames.add(EVENT_REQUEST);
		facesContextVarNames.add(EVENT_RESPONSE);
		facesContextVarNames.add(FLASH);
		facesContextVarNames.add(HTTP_SESSION_SCOPE);
		facesContextVarNames.add(MUTABLE_PORTLET_PREFERENCES_VALUES);
		facesContextVarNames.add(PORTLET_CONFIG);
		facesContextVarNames.add(PORTLET_SESSION);
		facesContextVarNames.add(PORTLET_SESSION_SCOPE);
		facesContextVarNames.add(PORTLET_PREFERENCES);
		facesContextVarNames.add(PORTLET_PREFERENCES_VALUES);
		facesContextVarNames.add(RENDER_REQUEST);
		facesContextVarNames.add(RENDER_RESPONSE);
		facesContextVarNames.add(RESOURCE_REQUEST);
		facesContextVarNames.add(RESOURCE_RESPONSE);
		FACES_CONTEXT_VAR_NAMES = Collections.unmodifiableSet(facesContextVarNames);

		// Initialize hash set of supported EL variable names when running in a JSP context.
		Set<String> jspContextVarName = new HashSet<String>();
		jspContextVarName.add(HTTP_SESSION_SCOPE);
		jspContextVarName.add(MUTABLE_PORTLET_PREFERENCES_VALUES);
		JSP_CONTEXT_VAR_NAMES = Collections.unmodifiableSet(jspContextVarName);

		// Initialize the list of static feature descriptors.
		List<FeatureDescriptor> featureDescriptors = new ArrayList<FeatureDescriptor>();
		featureDescriptors.add(getFeatureDescriptor(ACTION_REQUEST, ActionRequest.class));
		featureDescriptors.add(getFeatureDescriptor(ACTION_RESPONSE, ActionResponse.class));
		featureDescriptors.add(getFeatureDescriptor(BRIDGE_CONTEXT, LegacyBridgeContext.class));
		featureDescriptors.add(getFeatureDescriptor(EVENT_REQUEST, EventRequest.class));
		featureDescriptors.add(getFeatureDescriptor(EVENT_RESPONSE, EventResponse.class));
		featureDescriptors.add(getFeatureDescriptor(FLASH, Flash.class));
		featureDescriptors.add(getFeatureDescriptor(HTTP_SESSION_SCOPE, Map.class));
		featureDescriptors.add(getFeatureDescriptor(MUTABLE_PORTLET_PREFERENCES_VALUES, Map.class));
		featureDescriptors.add(getFeatureDescriptor(PORTLET_CONFIG, PortletConfig.class));
		featureDescriptors.add(getFeatureDescriptor(PORTLET_SESSION, PortletSession.class));
		featureDescriptors.add(getFeatureDescriptor(PORTLET_SESSION_SCOPE, Map.class));
		featureDescriptors.add(getFeatureDescriptor(PORTLET_PREFERENCES, PortletPreferences.class));
		featureDescriptors.add(getFeatureDescriptor(PORTLET_PREFERENCES_VALUES, Map.class));
		featureDescriptors.add(getFeatureDescriptor(RENDER_REQUEST, RenderRequest.class));
		featureDescriptors.add(getFeatureDescriptor(RENDER_RESPONSE, RenderResponse.class));
		featureDescriptors.add(getFeatureDescriptor(RESOURCE_REQUEST, ResourceRequest.class));
		featureDescriptors.add(getFeatureDescriptor(RESOURCE_RESPONSE, ResourceResponse.class));
		featureDescriptors.addAll(FEATURE_DESCRIPTORS_COMPAT);
		FEATURE_DESCRIPTORS = Collections.unmodifiableList(featureDescriptors);
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {

		Class<?> commonPropertyType = null;

		if (base == null) {
			commonPropertyType = String.class;
		}

		return commonPropertyType;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {

		if (base != null) {
			return null;
		}
		else {
			return FEATURE_DESCRIPTORS.iterator();
		}
	}

	@Override
	public Class<?> getType(ELContext elContext, Object base, Object property) {

		if (elContext == null) {

			// Throw an exception as directed by the JavaDoc for ELContext.
			throw new NullPointerException("elContext may not be null");
		}

		if ((base == null) && (property == null)) {
			throw new PropertyNotFoundException("Property name is null.");
		}

		if ((base == null) && canHandleVar(property)) {
			elContext.setPropertyResolved(true);
		}

		return null;
	}

	@Override
	public Object getValue(ELContext elContext, Object base, Object property) {

		if (elContext == null) {

			// Throw an exception as directed by the JavaDoc for ELContext.
			throw new NullPointerException();
		}

		if ((base == null) && (property == null)) {
			throw new PropertyNotFoundException("Property name is null.");
		}

		Object value = null;

		if (base == null) {

			// If running inside a JSP context, meaning evaluation of a JSP-syntax (dollar-sign prefixed) EL expression
			// like ${portletConfig} then
			if (elContext.getContext(JspContext.class) != null) {

				// Resolve according to the JSP expression requirements of Section 6.5.2.2 of the JSR 329 Spec.
				value = resolveJspContext(elContext, base, property);
			}

			// Otherwise, must be running inside a Faces context, meaning evaluation of a JSF-syntax (hash/pound
			// prefixed) EL expression like #{portletConfig}
			else {

				// Resolve according to the JSF expression requirements of Section 6.5.2.2 of the JSR 329 Spec.
				value = resolveFacesContext(elContext, base, property);
			}
		}

		return value;
	}

	@Override
	public boolean isReadOnly(ELContext elContext, Object base, Object property) {

		if ((base == null) && (property == null)) {
			throw new PropertyNotFoundException("Property name is null.");
		}

		if ((base == null) && canHandleVar(property)) {
			elContext.setPropertyResolved(true);
		}

		return true;
	}

	@Override
	public void setValue(ELContext elContext, Object base, Object property, Object value) {

		if (elContext == null) {

			// Throw an exception as directed by the JavaDoc for ELContext.
			throw new NullPointerException("elContext may not be null");
		}

		if ((base == null) && (property == null)) {
			throw new PropertyNotFoundException("Property name is null.");
		}

		if ((base == null) && canHandleVar(property)) {
			throw new PropertyNotWritableException(property.toString());
		}
	}

	@Override
	protected PortletRequest getPortletRequest(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Object request = externalContext.getRequest();

		if (request instanceof PortletRequest) {
			return (PortletRequest) request;
		}
		else {
			return null;
		}
	}

	@Override
	protected PortletResponse getPortletResponse(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Object response = externalContext.getResponse();

		if (response instanceof PortletResponse) {
			return (PortletResponse) response;
		}
		else {
			return null;
		}
	}

	@Override
	protected boolean isFacesContextVar(String varName) {
		return FACES_CONTEXT_VAR_NAMES.contains(varName) || super.isFacesContextVar(varName);
	}

	protected Object resolveFacesContext(ELContext elContext, Object base, Object property) {

		Object value = null;

		if (base == null) {

			if (property instanceof String) {
				String varName = (String) property;

				if (isFacesContextVar(varName)) {
					value = resolveVariable(elContext, varName);
				}
			}
		}
		else {

			if (property instanceof String) {
				String propertyName = (String) property;
				value = resolveProperty(elContext, base, propertyName);
			}
		}

		if (value != null) {
			elContext.setPropertyResolved(true);
		}

		return value;
	}

	protected Object resolveJspContext(ELContext elContext, Object base, Object property) {

		Object value = null;

		if (base == null) {

			if (property instanceof String) {
				String varName = (String) property;

				if (JSP_CONTEXT_VAR_NAMES.contains(varName)) {
					value = resolveVariable(elContext, varName);
				}
			}
		}
		else {

			if (property instanceof String) {
				String propertyName = (String) property;
				value = resolveProperty(elContext, base, propertyName);
			}
		}

		if (value != null) {
			elContext.setPropertyResolved(true);
		}

		return value;
	}

	protected Object resolveProperty(ELContext elContext, Object base, String property) {
		return null;
	}

	@Override
	protected Object resolveVariable(ELContext elContext, String varName) {
		Object value = null;

		if (varName != null) {

			if (varName.equals(ACTION_REQUEST)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
					value = getPortletRequest(facesContext);
				}
				else {
					throw new ELException("Unable to get actionRequest during " + portletPhase);
				}
			}
			else if (varName.equals(ACTION_RESPONSE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
					value = getPortletResponse(facesContext);
				}
				else {
					throw new ELException("Unable to get actionResponse during " + portletPhase);
				}
			}
			else if (varName.equals(BRIDGE_CONFIG) || varName.equals(BRIDGE_CONTEXT) ||
					varName.equals(PORTLET_CONFIG)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				BridgeConfig bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);

				if (varName.equals(BRIDGE_CONFIG)) {
					value = bridgeConfig;
				}
				else if (varName.equals(PORTLET_CONFIG)) {
					value = unwrapPortletConfig(RequestMapUtil.getPortletConfig(portletRequest));
				}
				else {
					value = new LegacyBridgeContext(bridgeConfig);
				}
			}
			else if (varName.equals(EVENT_REQUEST)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.EVENT_PHASE) {
					value = getPortletRequest(facesContext);
				}
				else {
					throw new ELException("Unable to get eventRequest during " + portletPhase);
				}
			}
			else if (varName.equals(EVENT_RESPONSE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.EVENT_PHASE) {
					value = getPortletResponse(facesContext);
				}
				else {
					throw new ELException("Unable to get eventResponse during " + portletPhase);
				}
			}
			else if (varName.equals(FLASH)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				value = getFlash(facesContext);
			}
			else if (varName.equals(HTTP_SESSION_SCOPE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				PortletSession portletSession = (PortletSession) externalContext.getSession(true);
				PortletConfig portletConfig = RequestMapUtil.getPortletConfig(portletRequest);
				PortletContext portletContext = portletConfig.getPortletContext();
				boolean preferPreDestroy = PortletConfigParam.PreferPreDestroy.getBooleanValue(portletConfig);
				ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(portletContext,
						ContextMapFactory.class);
				value = contextMapFactory.getSessionScopeMap(portletContext, portletSession,
						PortletSession.APPLICATION_SCOPE, preferPreDestroy);
			}
			else if (varName.equals(MUTABLE_PORTLET_PREFERENCES_VALUES)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				PortletRequest portletRequest = getPortletRequest(facesContext);

				if (portletRequest != null) {
					value = new MutablePreferenceMap(portletRequest.getPreferences());
				}
			}
			else if (varName.equals(PORTLET_SESSION)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				value = facesContext.getExternalContext().getSession(true);
			}
			else if (varName.equals(PORTLET_SESSION_SCOPE)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				value = facesContext.getExternalContext().getSessionMap();
			}
			else if (varName.equals(PORTLET_PREFERENCES)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				PortletRequest portletRequest = getPortletRequest(facesContext);

				if (portletRequest != null) {
					value = portletRequest.getPreferences();
				}
			}
			else if (varName.equals(PORTLET_PREFERENCES_VALUES)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				PortletRequest portletRequest = getPortletRequest(facesContext);

				if (portletRequest != null) {
					value = portletRequest.getPreferences().getMap();
				}
			}
			else if (varName.equals(RENDER_REQUEST)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if ((portletPhase == Bridge.PortletPhase.HEADER_PHASE) ||
						(portletPhase == Bridge.PortletPhase.RENDER_PHASE)) {
					value = getPortletRequest(facesContext);
				}
				else {
					throw new ELException("Unable to get renderRequest during " + portletPhase);
				}
			}
			else if (varName.equals(RENDER_RESPONSE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if ((portletPhase == Bridge.PortletPhase.HEADER_PHASE) ||
						(portletPhase == Bridge.PortletPhase.RENDER_PHASE)) {
					value = getPortletResponse(facesContext);
				}
				else {
					throw new ELException("Unable to get renderResponse during " + portletPhase);
				}
			}
			else if (varName.equals(RESOURCE_REQUEST)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
					value = getPortletRequest(facesContext);
				}
				else {
					throw new ELException("Unable to get resourceRequest during " + portletPhase);
				}
			}
			else if (varName.equals(RESOURCE_RESPONSE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
					value = getPortletResponse(facesContext);
				}
				else {
					throw new ELException("Unable to get renderResponse during " + portletPhase);
				}
			}
			else {
				value = super.resolveVariable(elContext, varName);
			}
		}

		return value;
	}

	private boolean canHandleVar(Object property) {
		return (property instanceof String) &&
			(isFacesContextVar((String) property) || JSP_CONTEXT_VAR_NAMES.contains(property));
	}
}
