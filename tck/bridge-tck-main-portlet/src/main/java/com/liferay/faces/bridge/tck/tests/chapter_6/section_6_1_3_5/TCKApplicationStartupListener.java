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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_5;

import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.liferay.faces.bridge.tck.beans.TestBean;


/**
 * @author  Neil Griffin
 */
public class TCKApplicationStartupListener implements SystemEventListener {

	@Override
	public boolean isListenerForSource(Object source) {
		return ((source != null) && (source instanceof Application));
	}

	/**
	 * @see  {@link Tests#getApplicationContextPathTest(TestBean)}
	 */
	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {
		FacesContext initFacesContext = FacesContext.getCurrentInstance();
		ExternalContext initExternalContext = initFacesContext.getExternalContext();
		Map<String, Object> applicationMap = initExternalContext.getApplicationMap();
		applicationMap.put("tckInitApplicationContextPath", initExternalContext.getApplicationContextPath());
	}
}
