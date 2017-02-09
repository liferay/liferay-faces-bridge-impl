package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;

// alternative to com.sun.faces.cdi.FlashProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeFlashProducer {

	@Named(value = "flash")
	@PortletRequestScoped
	@Produces
	public Flash getFlash() {
		return FacesContext.getCurrentInstance().getExternalContext().getFlash();
	}
}

