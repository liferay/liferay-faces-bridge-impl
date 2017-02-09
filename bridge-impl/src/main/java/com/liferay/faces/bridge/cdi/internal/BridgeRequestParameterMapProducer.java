package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.annotation.RequestParameterMap;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;
import java.util.Map;

// alternative to com.sun.faces.cdi.RequestParameterMapProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeRequestParameterMapProducer {

	@Named(value = "param")
	@PortletRequestScoped
	@Produces
	@RequestParameterMap
	public Map<String, String> getRequestParameterMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	}
}

