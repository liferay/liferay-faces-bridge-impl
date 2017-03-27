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
package com.liferay.faces.bridge.context.flash.internal;

import java.lang.reflect.Method;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * <p>This class provides a factory-style way of obtaining the JSF {@link Flash} scope instance provided by the JSF
 * runtime (Mojarra or MyFaces).</p>
 *
 * <p>Background: The JSF 2.0/2.1 API does not currently provide a factory-style way of obtaining {@link Flash} scope
 * instances. Instead, the {@link ExternalContext#getFlash()} method inside the JSF runtime is responsible for acting as
 * a pseudo-factory for creating instances. If the bridge were to use that approach for obtaining the {@link Flash}
 * scope instance, it would require the bridge to create instances of the JSF runtime's {@link ExternalContext}. Such an
 * operation would impose a big cost/overhead/expense, and since there are Servlet-API dependencies involved, it would
 * require the bridge to provide hack implementations of the {@link javax.servlet.ServletContext}, {@link
 * javax.servlet.http.HttpServletRequest}, and {@link javax.servlet.http.HttpServletResponse}. All this would be
 * required, simply to call the {@link ExternalContext#getFlash()} method.</p>
 *
 * <p>As an alternative approach, this class simply uses Java reflection to obtain the JSF {@link Flash} scope instance
 * from the {@link ClassLoader}. While it is true that Java reflection is expensive, it is certainly less expensive than
 * the {@link ExternalContext#getFlash()} approach described above. Hopefully we can get a factory-style way of
 * obtaining {@link Flash} scope instances in the JSF 2.2 API, which would make this class obsolete.</p>
 *
 * @author  Neil Griffin
 */
public class BridgeFlashFactoryImpl extends BridgeFlashFactory {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeFlashFactory.class);

	// Private Constants
	private static final String MOJARRA_FLASH_FQCN = "com.sun.faces.context.flash.ELFlash";
	private static final String MOJARRA_GET_FLASH_METHOD_NAME = "getFlash";
	private static final String MYFACES_FLASH_FQCN_NEW = "org.apache.myfaces.shared.context.flash.FlashImpl";
	private static final String MYFACES_FLASH_FQCN_OLD = "org.apache.myfaces.shared_impl.context.flash.FlashImpl";
	private static final String MYFACES_GET_FLASH_METHOD_NAME = "getCurrentInstance";
	private static final String WARNING_MSG = "Unable to create an instance of [{0}]; falling back to [{1}]";
	private static final Method mojarraGetFlashMethod;
	private static final Method myFacesGetFlashMethod;

	static {

		Method mojarraMethod = null;
		Method myFacesMethod = null;

		try {
			Class<?> mojarraFlashClass = Class.forName(MOJARRA_FLASH_FQCN);
			mojarraMethod = mojarraFlashClass.getMethod(MOJARRA_GET_FLASH_METHOD_NAME, new Class[] {});
		}
		catch (Exception e1) {

			try {
				Class<?> myFacesFlashClass = Class.forName(MYFACES_FLASH_FQCN_NEW);
				myFacesMethod = myFacesFlashClass.getMethod(MYFACES_GET_FLASH_METHOD_NAME,
						new Class[] { ExternalContext.class });
			}
			catch (Exception e2) {

				try {
					Class<?> myFacesFlashClass = Class.forName(MYFACES_FLASH_FQCN_OLD);
					myFacesMethod = myFacesFlashClass.getMethod(MYFACES_GET_FLASH_METHOD_NAME,
							new Class[] { ExternalContext.class });
				}
				catch (Exception e3) {
					logger.error("Classloader unable to find either the Mojarra or MyFaces Flash implementations");
				}
			}
		}

		mojarraGetFlashMethod = mojarraMethod;
		myFacesGetFlashMethod = myFacesMethod;
	}

	public BridgeFlashFactoryImpl() {
	}

	@Override
	public BridgeFlash getBridgeFlash() {

		BridgeFlash bridgeFlash = null;

		if (mojarraGetFlashMethod != null) {
			Flash mojarraFlash;

			try {
				mojarraFlash = (Flash) mojarraGetFlashMethod.invoke(null, new Object[] {});
				bridgeFlash = new BridgeFlashMojarraImpl(mojarraFlash);
			}
			catch (Exception e) {
				logger.warn(WARNING_MSG, MOJARRA_FLASH_FQCN, BridgeFlashFallbackImpl.class.getName());
				bridgeFlash = new BridgeFlashFallbackImpl();
			}
		}
		else if (myFacesGetFlashMethod != null) {
			Flash myFacesFlash;

			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				myFacesFlash = (Flash) myFacesGetFlashMethod.invoke(null, externalContext);
				bridgeFlash = new BridgeFlashMyFacesImpl(myFacesFlash);
			}
			catch (Exception e) {
				logger.warn(WARNING_MSG, MYFACES_FLASH_FQCN_NEW, BridgeFlashFallbackImpl.class.getName());
				bridgeFlash = new BridgeFlashFallbackImpl();
			}
		}

		return bridgeFlash;
	}

	public BridgeFlashFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}