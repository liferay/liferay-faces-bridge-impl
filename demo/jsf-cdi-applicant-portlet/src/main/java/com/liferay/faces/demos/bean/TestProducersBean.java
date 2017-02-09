package com.liferay.faces.demos.bean;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.*;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@FacesConfig(version = JSF_2_3)
@Named
@RequestScoped
public class TestProducersBean {

    @Inject
    private ExternalContext externalContext;

    @Inject
    private FacesContext facesContext;

    @Inject
    private Flash flash;

    @HeaderMap
    @Inject
    private Map<String, String> headerMap;

    @HeaderValuesMap
    @Inject
    private Map<String, String[]> headerValuesMap;

    @InitParameterMap
    @Inject
    private Map<String, String> initParameterMap;

    @Inject
    @RequestCookieMap
    private Map<String, Object> requestCookieMap;

    @Inject
    @RequestMap
    private Map<String, Object> requestMap;

    @Inject
    @RequestParameterMap
    private Map<String, String> requestParameterMap;

    @Inject
    @RequestParameterValuesMap
    private Map<String, String[]> requestParameterValuesMap;

    @Inject
    private ResourceHandler resourceHandler;

    @Inject
    @SessionMap
    private Map<String, Object> sessionMap;

    @Inject
    @ViewMap
    private Map<String, Object> viewMap;

    @Inject
    private UIViewRoot view;

    public String getResults() {
        String returnValue = "success";
        if (externalContext == null) { returnValue = "failure ... externalContext == null"; }
        if (facesContext == null) { returnValue = "failure ... facesContext == null"; }
        if (flash == null) { returnValue = "failure ... flash == null"; }
        if (headerMap == null) { returnValue = "failure ... headerMap == null"; }
        if (headerValuesMap == null) { returnValue = "failure ... headerValuesMap == null"; }
        if (initParameterMap == null) { returnValue = "failure ... initParameterMap == null"; }
        if (requestCookieMap == null) { returnValue = "failure ... requestCookieMap == null"; }
        if (requestMap == null) { returnValue = "failure ... requestMap == null"; }
        if (requestParameterMap == null) { returnValue = "failure ... requestParameterMap == null"; }
        if (requestParameterValuesMap == null) { returnValue = "failure ... requestParameterValuesMap == null"; }
        if (resourceHandler == null) { returnValue = "failure ... resourceHandler == null"; }
        if (sessionMap == null) { returnValue = "failure ... sessionMap == null"; }
        if (view == null) { returnValue = "failure ... view == null"; }
        return returnValue;
    }
}
