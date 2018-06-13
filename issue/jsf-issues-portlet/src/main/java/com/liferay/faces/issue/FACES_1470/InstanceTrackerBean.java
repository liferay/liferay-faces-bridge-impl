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
package com.liferay.faces.issue.FACES_1470;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "instanceTrackerBean")
@ApplicationScoped
public class InstanceTrackerBean {

	// Private Data Members
	private CopyOnWriteArrayList<WeakReference> as7LeakInstances = new CopyOnWriteArrayList<WeakReference>();

	public static <T> void trackAS7LeakInstance(T t) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> applicationMap = externalContext.getApplicationMap();
		InstanceTrackerBean instanceTrackerBean = (InstanceTrackerBean) applicationMap.get("instanceTrackerBean");
		List<WeakReference> aS7LeakInstances = instanceTrackerBean.getAS7LeakInstances();
		aS7LeakInstances.add(new WeakReference<T>(t));
	}

	public List<WeakReference> getAS7LeakInstances() {
		return as7LeakInstances;
	}

	public void performGarbageCollection(ActionEvent actionEvent) {

		System.gc();

		// Remove garbage collected instances.
		List<WeakReference> as7LeakInstances = getAS7LeakInstances();
		List<WeakReference> as7LeakInstancesToRemove = new ArrayList<WeakReference>();

		for (WeakReference weakReference : as7LeakInstances) {

			if (weakReference.get() == null) {
				as7LeakInstancesToRemove.add(weakReference);
			}
		}

		as7LeakInstances.removeAll(as7LeakInstancesToRemove);
	}
}
