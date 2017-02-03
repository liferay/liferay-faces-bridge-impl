package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.annotation.HeaderValuesMap;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;
import java.util.Map;

// alternative to com.sun.faces.cdi.
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeHeaderValuesMapProducer {

	@HeaderValuesMap
	@Named(value = "headerValues")
	@PortletRequestScoped
	@Produces
	public Map<String, String[]> getHeaderValuesMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderValuesMap();
	}
}

