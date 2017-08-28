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
package com.liferay.faces.bridge.internal;

import javax.portlet.PortletConfig;
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.config.ConfigParam;
import com.liferay.faces.util.helper.BooleanHelper;


/**
 * This enumeration contains constant names for various implementation-specific context-param entries that portlet
 * developers can use in the WEB-INF/web.xml descriptor.
 *
 * @author  Neil Griffin
 */
public enum PortletConfigParam implements ConfigParam<PortletConfig> {

	/**
	 * Flag indicating whether or not the bridge request scope is preserved after the RENDER_PHASE completes. Default
	 * value is false. Set value to true in order to enable JSR 329 default behavior.
	 */
	BridgeRequestScopeActionEnabled("javax.portlet.faces.BRIDGE_REQUEST_SCOPE_ACTION_ENABLED",
		"com.liferay.faces.bridge.bridgeRequestScopePreserved", false),

	/**
	 * Flag indicating whether or not the bridge should manage BridgeRequestScope during the RESOURCE_PHASE of the
	 * portlet lifecycle when the {@link com.liferay.faces.bridge.internal.BridgeExt#FACES_AJAX_PARAMETER} resource
	 * request parameter is "true". Default value is false.
	 */
	BridgeRequestScopeAjaxEnabled("javax.portlet.faces.BRIDGE_REQUEST_SCOPE_AJAX_ENABLED",
		"com.liferay.faces.bridge.bridgeRequestScopeAjaxEnabled", false),

	/**
	 * Integer indicating the initial cache capacity for the Bridge Request Scope. The default value of this param is
	 * 16. For more details, see {@link com.liferay.faces.util.cache.CacheFactory#getConcurrentCache(int)} and {@link
	 * java.util.HashMap#HashMap()}.
	 *
	 * @since  1.1
	 * @since  2.1
	 * @since  3.1
	 */
	BridgeRequestScopeInitialCacheCapacity("com.liferay.faces.bridge.INITIAL_MANAGED_REQUEST_SCOPES", 16),

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
	BridgeRequestScopeMaxCacheCapacity(Bridge.MAX_MANAGED_REQUEST_SCOPES, 100),

	/**
	 * Flag indicating whether or not the portlet container has the ability to set the HTTP status code for resources.
	 * Default value is false.
	 */
	ContainerAbleToSetHttpStatusCode("com.liferay.faces.bridge.containerAbleToSetHttpStatusCode",
		"org.portletfaces.bridge.containerAbleToSetHttpStatusCode", false),

	DefaultRenderKitId("javax.portlet.faces.defaultRenderKitId", null),

	/**
	 * Flag indicating whether or not the bridge should manage incongruities between the JSF lifecycle and the Portlet
	 * lifecycle. The default is true.
	 */
	ManageIncongruities("com.liferay.faces.bridge.manageIncongruities", true),

	/**
	 * Flag indicating whether or not methods annotated with the &#064;PreDestroy annotation are preferably invoked over
	 * the &#064;BridgePreDestroy annotation. Default value is true.For more info, see:
	 * http://issues.liferay.com/browse/FACES-146
	 */
	PreferPreDestroy("com.liferay.faces.bridge.preferPreDestroy", "org.portletfaces.bridge.preferPreDestroy", true),

	/** Flag indicating the value of the "javax.portlet.faces.preserveActionParams" init-param. The default is false. */
	PreserveActionParams("javax.portlet.faces.preserveActionParams", false),

	/**
	 * Flag indicating whether or not the render-redirect standard feature is enabled. Default value is false for the
	 * sake of performance.
	 */
	RenderRedirectEnabled("com.liferay.faces.bridge.renderRedirectEnabled", false),

	/** Size in bytes for the buffer that is used to deliver resources back to the browser. Default value is 1024. */
	ResourceBufferSize("com.liferay.faces.bridge.resourceBufferSize", "org.portletfaces.bridge.resourceBufferSize",
		1024),

	/**
	 * Absolute path to a directory (folder) in which the uploaded file data should be written to. Default value is the
	 * value of the system property "java.io.tmpdir".
	 */
	UploadedFilesDir("com.liferay.faces.bridge.uploadedFilesDir", "javax.faces.UPLOADED_FILES_DIR",
		System.getProperty("java.io.tmpdir")),

	/** Maximum file size for an uploaded file. Default is 104,857,600 (~100MB), upper limit is 2,147,483,647 (~2GB) */
	UploadedFileMaxSize("com.liferay.faces.bridge.uploadedFileMaxSize", "javax.faces.UPLOADED_FILE_MAX_SIZE",
		104857600L),

	/** Name of the render parameter used to encode the viewId. Default value is "_facesViewIdRender". */
	ViewIdRenderParameterName("com.liferay.faces.bridge.viewIdRenderParameterName", "_facesViewIdRender"),

	/** Name of the resource request parameter used to encode the viewId Default value is "_facesViewIdResource" */
	ViewIdResourceParameterName("com.liferay.faces.bridge.viewIdResourceParameterName", "_facesViewIdResource"),

	/** Flag indicating whether or not the JSF 2 "View Parameters" feature is enabled. Default value is true. */
	ViewParametersEnabled("com.liferay.faces.bridge.viewParametersEnabled", true);

	// Private Data Members
	private String alternateName;
	private boolean defaultBooleanValue;
	private String defaultStringValue;
	private int defaultIntegerValue;
	private long defaultLongValue;
	private String name;

	PortletConfigParam(String name, int defaultIntegerValue) {
		this(name, null, defaultIntegerValue);
	}

	PortletConfigParam(String name, String defaultStringValue) {
		this(name, null, defaultStringValue);
	}

	PortletConfigParam(String name, boolean defaultBooleanValue) {
		this(name, null, defaultBooleanValue);
	}

	PortletConfigParam(String name, String alternateName, int defaultIntegerValue) {
		this.name = name;
		this.alternateName = alternateName;
		this.defaultBooleanValue = (defaultIntegerValue != 0);
		this.defaultIntegerValue = defaultIntegerValue;
		this.defaultLongValue = defaultIntegerValue;
		this.defaultStringValue = Integer.toString(defaultIntegerValue);
	}

	PortletConfigParam(String name, String alternateName, long defaultLongValue) {
		this.name = name;
		this.alternateName = alternateName;
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

	PortletConfigParam(String name, String alternateName, String defaultStringValue) {
		this.name = name;
		this.alternateName = alternateName;

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

	PortletConfigParam(String name, String alternateName, boolean defaultBooleanValue) {
		this.name = name;
		this.alternateName = alternateName;
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
		return PortletConfigParamUtil.getBooleanValue(portletConfig, name, alternateName, defaultBooleanValue);
	}

	@Override
	public String getConfiguredValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getConfiguredValue(portletConfig, name, alternateName);
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
		return PortletConfigParamUtil.getIntegerValue(portletConfig, name, alternateName, defaultIntegerValue);
	}

	@Override
	public long getLongValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getLongValue(portletConfig, name, alternateName, defaultLongValue);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getStringValue(PortletConfig portletConfig) {
		return PortletConfigParamUtil.getStringValue(portletConfig, name, alternateName, defaultStringValue);
	}

	@Override
	public boolean isConfigured(PortletConfig portletConfig) {
		return PortletConfigParamUtil.isSpecified(portletConfig, name, alternateName);
	}
}
