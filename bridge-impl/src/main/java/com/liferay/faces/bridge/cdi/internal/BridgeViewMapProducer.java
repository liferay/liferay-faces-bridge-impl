package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.annotation.ViewMap;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;
import java.util.Map;

// alternative to com.sun.faces.cdi.ViewMapProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeViewMapProducer {

	@Named(value = "viewScope")
	@PortletRequestScoped
	@Produces
	@ViewMap
	public Map<String, Object> getViewMap() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
	}
}

