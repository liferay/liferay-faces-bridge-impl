package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;

// alternative to com.sun.faces.cdi.FacesContextProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeFacesContextProducer {

	@Named(value = "facesContext")
	@PortletRequestScoped
	@Produces
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
}

