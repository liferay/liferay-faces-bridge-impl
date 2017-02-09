package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;

// alternative to com.sun.faces.cdi.ExternalContextProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeExternalContextProducer {

	@Named(value = "externalContext")
	@PortletRequestScoped
	@Produces
	public ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
}

