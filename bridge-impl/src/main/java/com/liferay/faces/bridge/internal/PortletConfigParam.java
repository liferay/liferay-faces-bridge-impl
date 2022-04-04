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
package com.liferay.faces.bridge.internal;

import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.config.ConfigParam;
import com.liferay.faces.util.config.WebConfigParam;
import com.liferay.faces.util.helper.BooleanHelper;


/**
 * This enumeration contains constant names for various implementation-specific context-param entries that portlet
 * developers can use in the WEB-INF/web.xml and WEB-INF/portlet.xml descriptors.
 *
 * @author  Neil Griffin
 */
public enum PortletConfigParam implements ConfigParam<PortletConfig> {

	/**
	 * Flag indicating whether or not the bridge request scope is preserved after the RENDER_PHASE completes. Default
	 * value is false. Set value to true in order to enable JSR 329 default behavior.
	 */
	BridgeRequestScopeActionEnabled(false, Bridge.BRIDGE_REQUEST_SCOPE_ACTION_ENABLED,
		"com.liferay.faces.bridge.bridgeRequestScopePreserved"),

	/**
	 * Flag indicating whether or not the bridge should manage BridgeRequestScope during the RESOURCE_PHASE of the
	 * portlet lifecycle when the {@link javax.portlet.faces.Bridge#FACES_AJAX_PARAMETER} resource request parameter is
	 * "true". Default value is false.
	 */
	BridgeRequestScopeAjaxEnabled(false, Bridge.BRIDGE_REQUEST_SCOPE_AJAX_ENABLED,
		"com.liferay.faces.bridge.bridgeRequestScopeAjaxEnabled"),

	/**
	 * Integer indicating the initial cache capacity for the Bridge Request Scope. The default value of this param is
	 * 16. For more details, see {@link com.liferay.faces.util.cache.CacheFactory#getConcurrentCache(int)} and {@link
	 * java.util.HashMap#HashMap()}.
	 *
	 * @since  1.1
	 * @since  2.1
	 * @since  3.1
	 */
	BridgeRequestScopeInitialCacheCapacity(16, "com.liferay.faces.bridge.INITIAL_MANAGED_REQUEST_SCOPES"),

	/**
	 * Integer indicating the maximum cache capacity for the Bridge Request Scope. According to Section 3.2 of the
	 * FacesBridge Spec, "If not set the bridge provides an implementation dependent default maximum." The default value
	 * is 100, which is the default value also used by the Apache MyFaces Portlet Bridge (the original FacesBridge
	 * Reference Implementation developed under JSR 301/329).
	 *
	 * @since  1.1
	 * @since  2.1
	 * @since  3.1
	 */
	BridgeRequestScopeMaxCacheCapacity(100, Bridge.MAX_MANAGED_REQUEST_SCOPES),

	/**
	 * Flag indicating whether or not the portlet container has the ability to set the HTTP status code for resources.
	 * Default value is false.
	 */
	ContainerAbleToSetHttpStatusCode(false, "com.liferay.faces.bridge.containerAbleToSetHttpStatusCode",
		"org.portletfaces.bridge.containerAbleToSetHttpStatusCode"),

	DefaultRenderKitId(null, "javax.portlet.faces.defaultRenderKitId"),

	/**
	 * Flag indicating whether or not the bridge should manage incongruities between the JSF lifecycle and the Portlet
	 * lifecycle. The default is true.
	 */
	ManageIncongruities(true, "com.liferay.faces.bridge.manageIncongruities"),

	/**
	 * Flag indicating whether or not methods annotated with the &#064;PreDestroy annotation are preferably invoked over
	 * the &#064;BridgePreDestroy annotation. Default value is true. For more info, see:
	 * http://issues.liferay.com/browse/FACES-146
	 */
	PreferPreDestroy(true, "com.liferay.faces.bridge.preferPreDestroy", "org.portletfaces.bridge.preferPreDestroy"),

	/** Flag indicating the value of the "javax.portlet.faces.preserveActionParams" init-param. The default is false. */
	PreserveActionParams(false, "javax.portlet.faces.preserveActionParams"),

	/**
	 * Flag indicating whether or not the render-redirect standard feature is enabled. Default value is false for the
	 * sake of performance.
	 */
	RenderRedirectEnabled(false, "com.liferay.faces.bridge.renderRedirectEnabled"),

	/** Size in bytes for the buffer that is used to deliver resources back to the browser. Default value is 1024. */
	ResourceBufferSize(1024, "com.liferay.faces.bridge.resourceBufferSize",
		"org.portletfaces.bridge.resourceBufferSize"),

	/**
	 * Absolute path to a directory (folder) in which the uploaded file data should be written to. Default value is the
	 * value of the system property "java.io.tmpdir".
	 */
	UploadedFilesDir(WebConfigParam.UploadedFilesDir.getDefaultStringValue(), WebConfigParam.UploadedFilesDir.getName(),
		"com.liferay.faces.bridge.uploadedFilesDir", "javax.faces.UPLOADED_FILES_DIR"),

	/** Maximum file size for an uploaded file. Default is 104,857,600 (~100MB), upper limit is 2,147,483,647 (~2GB) */
	UploadedFileMaxSize(WebConfigParam.UploadedFileMaxSize.getDefaultLongValue(),
		WebConfigParam.UploadedFileMaxSize.getName(), "com.liferay.faces.bridge.uploadedFileMaxSize",
		"javax.faces.UPLOADED_FILE_MAX_SIZE"),

	/**
	 * Flag indicating whether or not the {@link Bridge#FACES_VIEW_ID_PARAMETER} value will be consulted when
	 * determining the JSF viewId. Default value is true.
	 */
	ViewIdParameterEnabled(true, "com.liferay.faces.bridge.jsfBridgeViewIdParameterEnabled"),

	/**
	 * Flag indicating whether or not the render parameter value will be consulted when determining the JSF viewId.
	 * Default value is true.
	 */
	ViewIdRenderParameterEnabled(true, "com.liferay.faces.bridge.viewIdRenderParameterEnabled"),

	/** Name of the render parameter used to encode the viewId. Default value is "_facesViewIdRender". */
	ViewIdRenderParameterName("_facesViewIdRender", "com.liferay.faces.bridge.viewIdRenderParameterName"),

	/**
	 * Flag indicating whether or not the resource parameter value will be consulted when determining the JSF viewId.
	 * Default value is true.
	 */
	ViewIdResourceParameterEnabled(true, "com.liferay.faces.bridge.viewIdResourceParameterEnabled"),

	/** Name of the resource request parameter used to encode the viewId Default value is "_facesViewIdResource" */
	ViewIdResourceParameterName("_facesViewIdResource", "com.liferay.faces.bridge.viewIdResourceParameterName"),

	/** Flag indicating whether or not the JSF 2 "View Parameters" feature is enabled. Default value is true. */
	ViewParametersEnabled(true, "com.liferay.faces.bridge.viewParametersEnabled"),

	/**
	 * Flag indicating whether or not the {@link Bridge#FACES_VIEW_PATH_PARAMETER} value will be consulted when
	 * determining the JSF viewId. Default value is true.
	 */
	ViewPathParameterEnabled(true, "com.liferay.faces.bridge.jsfBridgeViewPathParameterEnabled");

	// Private Data Members
	private final String name;
	private final String alternateName;
	private final Set<String> names;
	private final boolean defaultBooleanValue;
	private final String defaultStringValue;
	private final int defaultIntegerValue;
	private final long defaultLongValue;

	PortletConfigParam(int defaultIntegerValue, String... names) {
		this.name = names[0];
		this.alternateName = PortletConfigParamUtil.getAlternateName(names);
		this.names = PortletConfigParamUtil.asInsertionOrderedSet(names);
		this.defaultBooleanValue = (defaultIntegerValue != 0);
		this.defaultIntegerValue = defaultIntegerValue;
		this.defaultLongValue = defaultIntegerValue;
		this.defaultStringValue = Integer.toString(defaultIntegerValue);
	}

	PortletConfigParam(long defaultLongValue, String... names) {
		this.name = names[0];
		this.alternateName = PortletConfigParamUtil.getAlternateName(names);
		this.names = PortletConfigParamUtil.asInsertionOrderedSet(names);
		this.defaultBooleanValue = (defaultLongValue != 0);

		if (defaultLongValue < Integer.MIN_VALUE) {
			this.defaultIntegerValue = Integer.MIN_VALUE;
		}
		else if (defaultLongValue > Integer.MAX_VALUE) {
			this.defaultIntegerValue = Integer.MAX_VALUE;
		}
		else {
			this.defaultIntegerValue = (int) defaultLongValue;
		}

		this.defaultLongValue = defaultLongValue;
		this.defaultStringValue = Long.toString(defaultLongValue);
	}

	PortletConfigParam(String defaultStringValue, String... names) {
		this.name = names[0];
		this.alternateName = PortletConfigParamUtil.getAlternateName(names);
		this.names = PortletConfigParamUtil.asInsertionOrderedSet(names);

		if (BooleanHelper.isTrueToken(defaultStringValue)) {
			this.defaultBooleanValue = true;
			this.defaultIntegerValue = 1;
			this.defaultLongValue = 1L;
		}
		else {
			this.defaultBooleanValue = false;
			this.defaultIntegerValue = 0;
			this.defaultLongValue = 0L;
		}

		this.defaultStringValue = defaultStringValue;
	}

	PortletConfigParam(boolean defaultBooleanValue, String... names) {
		this.name = names[0];
		this.alternateName = PortletConfigParamUtil.getAlternateName(names);
		this.names = PortletConfigParamUtil.asInsertionOrderedSet(names);
		this.defaultBooleanValue = defaultBooleanValue;

		if (defaultBooleanValue) {
			this.defaultIntegerValue = 1;
			this.defaultLongValue = 1L;
			this.defaultStringValue = Boolean.TRUE.toString();
		}
		else {
			this.defaultIntegerValue = 0;
			this.defaultLongValue = 0L;
			this.defaultStringValue = Boolean.FALSE.toString();
		}
	}

	@Override
	public String getAlternateName() {
		return alternateName;
	}

	@Override
	public boolean getBooleanValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getBooleanValue(portletConfig, this);
	}

	@Override
	public String getConfiguredValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getConfiguredValue(portletConfig, this);
	}

	@Override
	public boolean getDefaultBooleanValue() {
		return defaultBooleanValue;
	}

	@Override
	public int getDefaultIntegerValue() {
		return defaultIntegerValue;
	}

	@Override
	public long getDefaultLongValue() {
		return defaultLongValue;
	}

	@Override
	public String getDefaultStringValue() {
		return defaultStringValue;
	}

	@Override
	public int getIntegerValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getIntegerValue(portletConfig, this);
	}

	@Override
	public long getLongValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getLongValue(portletConfig, this);
	}

	@Override
	public String getName() {
		return name;
	}

	public Set<String> getNames() {
		return names;
	}

	@Override
	public String getStringValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getStringValue(portletConfig, this);
	}

	@Override
	public boolean isConfigured(PortletConfig portletConfig) {
		return PortletConfigParamUtil.isSpecified(portletConfig, this);
	}
}
