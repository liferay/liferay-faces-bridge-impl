/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.config;

/**
 * @author  Neil Griffin
 */
public class BridgeConfigConstants {

	/**
	 * Boolean indicating whether or not methods annotated with the &#064;PreDestroy annotation are preferably invoked
	 * over the &#064;BridgePreDestroy annotation. Default value is true. The reason why, is because local portals like
	 * Liferay don't have a problem with PreDestroy. It really only comes into play for remote portals like Oracle
	 * WebCenter. For more info, see: http://issues.liferay.com/browse/FACES-146
	 */
	public static final String PARAM_PREFER_PRE_DESTROY1 = "com.liferay.faces.bridge.preferPreDestroy";
	public static final String PARAM_PREFER_PRE_DESTROY2 = "org.portletfaces.bridge.preferPreDestroy";

}
