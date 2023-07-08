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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.render.ResponseStateManager;

import com.liferay.faces.bridge.internal.BridgeExt;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class FacesRequestParameterMapImpl implements FacesRequestParameterMap {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FacesRequestParameterMapImpl.class);

	// Private Constants
	private static final String COM_LIFERAY_FACES_BRIDGE = "com.liferay.faces.bridge";
	private static final String JAVAX_FACES = "javax.faces";
	private static final String PRIMEFACES_DYNAMIC_CONTENT_PARAM = "pfdrid";

	// Private Data Members
	private BridgeRequestScope bridgeRequestScope;
	private String defaultRenderKitId;
	private Map<String, String> facesViewParameterMap;
	private String parameterPrefix;
	private String namespace;
	private boolean namespaceViewState;
	private boolean separatorCharEnabled;
	private boolean strictParameterNamespacing;
	private Map<String, String[]> wrappedParameterMap;

	public FacesRequestParameterMapImpl(String namespace, BridgeRequestScope bridgeRequestScope,
		Map<String, String> facesViewParameterMap, String defaultRenderKitId, String separatorChar,
		boolean strictParameterNamespacing, boolean namespaceViewState) {
		this(new HashMap<String, String[]>(), namespace, bridgeRequestScope, facesViewParameterMap, defaultRenderKitId,
			separatorChar, strictParameterNamespacing, namespaceViewState);
	}

	public FacesRequestParameterMapImpl(Map<String, String[]> parameterMap, String namespace,
		BridgeRequestScope bridgeRequestScope, Map<String, String> facesViewParameterMap, String defaultRenderKitId,
		String separatorChar, boolean strictParameterNamespacing, boolean namespaceViewState) {

		this.wrappedParameterMap = parameterMap;
		this.namespace = namespace;
		this.bridgeRequestScope = bridgeRequestScope;
		this.facesViewParameterMap = facesViewParameterMap;
		this.defaultRenderKitId = defaultRenderKitId;
		this.separatorCharEnabled = ((separatorChar != null) && (separatorChar.length() > 0));
		this.strictParameterNamespacing = strictParameterNamespacing;
		this.namespaceViewState = namespaceViewState;

		if (this.separatorCharEnabled) {
			this.parameterPrefix = namespace + separatorChar;
		}
		else {
			this.parameterPrefix = namespace;
		}
	}

	@Override
	public void addValue(String key, String value) {

		boolean namespacedKey = ((key != null) && key.startsWith(namespace));

		String[] values = get(key);

		if (values == null) {
			values = new String[] { value };
		}
		else {
			values = Arrays.copyOf(values, values.length + 1);
			values[values.length - 1] = value;
		}

		if (namespacedKey) {
			wrappedParameterMap.put(key, values);
		}
		else {

			if (strictParameterNamespacing) {
				wrappedParameterMap.put(namespace + key, values);
			}
			else {
				wrappedParameterMap.put(key, values);
			}
		}
	}

	@Override
	public void clear() {
		wrappedParameterMap.clear();
	}

	/**
	 * This method provides an optimized lookup to see if a key is within the specified request parameter values map.
	 */
	@Override
	public boolean containsKey(Object key) {

		// Assume that they key is not found.
		boolean found = false;

		// If the specified key has a valid value, then
		if (key != null) {

			// Determine whether or not the key is present in the parameter-map within the PortletRequest. This should
			// be a quick lookup (minimal performance impact).
			found = wrappedParameterMap.containsKey(key);

			if (!found) {
				found = wrappedParameterMap.containsKey(namespace + key);
			}

			if (!found && separatorCharEnabled) {
				found = wrappedParameterMap.containsKey(parameterPrefix + key);
			}

			if (!found) {

				// NOTE: If the valuesMap.containsKey(String) method call returned true, then trust that fact and let
				// this method return true as well. Otherwise, don't trust it! This might be a Liferay WSRP producer
				// portlet in which NamespaceServletRequest.getParameterMap().containsKey(String) erroneously returns
				// false. Just in case, try again by seeing if the parameter has a value. If it does, then let this
				// method return true.
				String[] values = wrappedParameterMap.get(key.toString());

				if (values != null) {

					for (String value : values) {

						if ((value != null) && (value.length() > 0)) {
							found = true;

							break;
						}
					}
				}
			}

			// If the key was not present in the quick lookup, then
			if (!found) {

				String keyAsString = (String) key;

				// If the key is "javax.faces.ViewState" then avoid the performance impact of the superclass delegation
				// by handling this special case here.
				if (keyAsString.endsWith(ResponseStateManager.VIEW_STATE_PARAM)) {

					String viewStateParam = getFirst(key);

					if (viewStateParam == null) {

						if (bridgeRequestScope != null) {
							viewStateParam = bridgeRequestScope.getPreservedViewStateParam();

							if (viewStateParam != null) {
								found = true;
							}
						}
					}
					else {
						found = true;
					}
				}

				// Otherwise,
				else {

					// If the key starts with "javax.faces" then the previous lookup in the parameter-map within the
					// PortletRequest is good enough. The JSF implementation (and also the PrimeFaces
					// PrimePartialViewContext) will sometimes ask for request parameters with the "javax.faces" prefix
					// in the name. This is especially the case when a ResourceRequest is looking for JSF2 resources.
					if (keyAsString.startsWith(JAVAX_FACES)) {
						// nothing to do -- just here for comments readability.
					}

					// Otherwise, iterate through the parameter names to see if the name is present. This is done as a
					// last resort since getting the parameter names is an expensive operation.
					else {

						Set<String> parameterNames = keySet();

						for (String parameterName : parameterNames) {
							found = parameterName.equals(keyAsString);

							if (found) {
								break;
							}
						}
					}
				}
			}
		}

		return found;
	}

	@Override
	public boolean containsValue(Object value) {
		return wrappedParameterMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, String[]>> entrySet() {
		return wrappedParameterMap.entrySet();
	}

	@Override
	public String[] get(Object key) {

		String[] values = wrappedParameterMap.get(key);

		if (values == null) {
			values = wrappedParameterMap.get(namespace + key);
		}

		if ((values == null) && separatorCharEnabled) {
			values = wrappedParameterMap.get(parameterPrefix + key);
		}

		if (values == null) {

			String specialParameterValue = getSpecialParameterValue(key.toString());

			if (specialParameterValue != null) {
				values = new String[] { specialParameterValue };
			}
		}

		return values;
	}

	@Override
	public String getFirst(Object key) {

		String firstValue = null;

		if (key != null) {

			String[] values = get(key);

			if ((values != null) && (values.length > 0)) {
				firstValue = values[0];
			}
		}

		logger.trace("{0}=[{1}]", key, firstValue);

		return firstValue;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public boolean isEmpty() {
		return wrappedParameterMap.isEmpty();
	}

	@Override
	public Set<String> keySet() {

		// Note: This can't be cached because the caller basically wants a new enumeration to iterate over each time.
		Set<String> keyNames = new HashSet<String>();

		String renderKitIdParam = getFirst(ResponseStateManager.RENDER_KIT_ID_PARAM);

		if (renderKitIdParam == null) {
			renderKitIdParam = defaultRenderKitId;
		}

		List<String> requestParameterNameList = new ArrayList<String>();
		requestParameterNameList.addAll(wrappedParameterMap.keySet());

		// Section 6.9 of the Bridge spec requires that a parameter name be added to the return value of
		// ExternalContext.getRequestParameterNames() for ResponseStateManager.RENDER_KIT_ID_PARAM.
		if (renderKitIdParam != null) {
			requestParameterNameList.add(ResponseStateManager.RENDER_KIT_ID_PARAM);
		}

		// If the "javax.faces.ViewState" parameter was preserved in the BridgeRequestScope, then add it to the return
		// value list of names.
		String viewStateParam = getFirst(ResponseStateManager.VIEW_STATE_PARAM);

		if ((viewStateParam == null) && (bridgeRequestScope != null)) {
			viewStateParam = bridgeRequestScope.getPreservedViewStateParam();
		}

		if (viewStateParam != null) {

			if (separatorCharEnabled || namespaceViewState) {
				requestParameterNameList.add(parameterPrefix + ResponseStateManager.VIEW_STATE_PARAM);
			}
			else {
				requestParameterNameList.add(ResponseStateManager.VIEW_STATE_PARAM);
			}
		}

		if (bridgeRequestScope != null) {
			Map<String, String> preservedActionParamMap = bridgeRequestScope.getPreservedActionParameterMap();

			if (preservedActionParamMap != null) {
				Set<String> keySet = preservedActionParamMap.keySet();

				for (String key : keySet) {
					requestParameterNameList.add(key);
				}
			}
		}

		Set<String> keySet = facesViewParameterMap.keySet();

		for (String key : keySet) {
			requestParameterNameList.add(key);
		}

		keyNames.addAll(requestParameterNameList);

		return keyNames;
	}

	@Override
	public String[] put(String key, String[] value) {
		return wrappedParameterMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String[]> m) {
		wrappedParameterMap.putAll(m);
	}

	@Override
	public String[] remove(Object key) {
		return wrappedParameterMap.remove(key);
	}

	@Override
	public int size() {
		return wrappedParameterMap.size();
	}

	@Override
	public Collection<String[]> values() {
		return wrappedParameterMap.values();
	}

	protected String getSpecialParameterValue(String parameterName) {

		String specialParameterValue = null;
		String nonPrefixedParameterName = "";

		if (parameterName != null) {

			if (parameterName.startsWith(parameterPrefix)) {
				nonPrefixedParameterName = parameterName.substring(parameterPrefix.length());
			}
			else if (parameterName.startsWith(namespace)) {
				nonPrefixedParameterName = parameterName.substring(namespace.length());
			}
			else {
				nonPrefixedParameterName = parameterName;
			}
		}

		if (ResponseStateManager.RENDER_KIT_ID_PARAM.equals(nonPrefixedParameterName)) {

			// If not found in the request, Section 6.9 of the Bridge spec requires that the value of the
			// ResponseStateManager.RENDER_KIT_ID_PARAM request parameter be set to the value of the
			// "javax.portlet.faces.<portletName>.defaultRenderKitId" PortletContext attribute.
			specialParameterValue = defaultRenderKitId;
		}
		else if (ResponseStateManager.VIEW_STATE_PARAM.equals(nonPrefixedParameterName)) {

			if (bridgeRequestScope != null) {
				specialParameterValue = bridgeRequestScope.getPreservedViewStateParam();
			}
		}
		else if (nonPrefixedParameterName.startsWith(COM_LIFERAY_FACES_BRIDGE) ||
				BridgeExt.FACES_AJAX_PARAMETER.equals(nonPrefixedParameterName) ||
				PRIMEFACES_DYNAMIC_CONTENT_PARAM.equals(nonPrefixedParameterName)) {

			// For the sake of performance, this case is a no-op. If the value wasn't found in the PortletRequest, then
			// it won't be found in the preserved action parameter map or the faces view parameter map.
		}
		else {

			if (bridgeRequestScope != null) {
				Map<String, String> preservedActionParamMap = bridgeRequestScope.getPreservedActionParameterMap();

				if (preservedActionParamMap != null) {
					specialParameterValue = preservedActionParamMap.get(parameterName);
				}

				if (specialParameterValue == null) {
					specialParameterValue = facesViewParameterMap.get(parameterName);
				}
			}
		}

		return specialParameterValue;
	}
}
