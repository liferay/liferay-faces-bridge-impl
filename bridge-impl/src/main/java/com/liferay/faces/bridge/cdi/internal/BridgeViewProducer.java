package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.annotations.PortletRequestScoped;

// alternative to com.sun.faces.cdi.ViewProducer
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION+10)
public class BridgeViewProducer {

	@Named(value = "view")
	@PortletRequestScoped
	@Produces
	public UIViewRoot getView() {
		return FacesContext.getCurrentInstance().getViewRoot();
	}
}

