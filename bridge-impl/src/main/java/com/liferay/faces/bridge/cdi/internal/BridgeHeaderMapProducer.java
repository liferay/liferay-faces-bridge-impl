package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.annotation.HeaderMap;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;
import java.util.Map;

// alternative to com.sun.faces.cdi.HeaderMapProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeHeaderMapProducer {

	@HeaderMap
	@Named(value = "header")
	@PortletRequestScoped
	@Produces
	public Map<String, String> getHeaderMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
	}
}

