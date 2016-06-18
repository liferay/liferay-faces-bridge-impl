/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.portlet.PortletException;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.GenericFacesPortlet;

import com.liferay.faces.bridge.BridgeFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeFactoryImpl extends BridgeFactory {

	// Java 1.6+ @Override
	@Override
	public Bridge getBridge() throws PortletException {

		String bridgeClassName = getClassPathResourceAsString(GenericFacesPortlet.BRIDGE_SERVICE_CLASSPATH);

		return getBridge(bridgeClassName);
	}

	// Java 1.6+ @Override
	@Override
	public Bridge getBridge(String bridgeClassName) throws PortletException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try {
			Class<?> bridgeClass = classLoader.loadClass(bridgeClassName);

			return (Bridge) bridgeClass.newInstance();
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	// Java 1.6+ @Override
	@Override
	public BridgeFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}

	protected String getClassPathResourceAsString(String resourcePath) {
		String classPathResourceAsString = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader != null) {
			InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				try {
					classPathResourceAsString = bufferedReader.readLine();
				}
				catch (IOException e) {

					// Since the API can't use a logging system like SLF4J the best we can do is print to stderr.
					System.err.println("Unable to read contents of resourcePath=[" + resourcePath + "]");
				}
				finally {

					try {
						bufferedReader.close();
						inputStreamReader.close();
						inputStream.close();
					}
					catch (IOException e) {
						// ignore
					}
				}
			}
		}

		return classPathResourceAsString;
	}
}
