/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
@SuppressWarnings("deprecation")
public abstract class ApplicationWrapper extends Application implements Wrapper<Application> {

	public abstract Application getWrapped();

	@Override
	public void addComponent(String componentType, String componentClass) {
		getWrapped().addComponent(componentType, componentClass);
	}

	@Override
	public void addConverter(String converterId, String converterClass) {
		getWrapped().addConverter(converterId, converterClass);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addConverter(Class targetClass, String converterClass) {
		getWrapped().addConverter(targetClass, converterClass);
	}

	@Override
	public void addELContextListener(ELContextListener listener) {
		getWrapped().addELContextListener(listener);
	}

	@Override
	public void addELResolver(ELResolver resolver) {
		getWrapped().addELResolver(resolver);
	}

	@Override
	public void addValidator(String validatorId, String validatorClass) {
		getWrapped().addValidator(validatorId, validatorClass);
	}

	@Override
	public UIComponent createComponent(String componentType) throws FacesException {
		return getWrapped().createComponent(componentType);
	}

	@Override
	public UIComponent createComponent(ValueExpression componentExpression, FacesContext context, String componentType)
		throws FacesException {
		return getWrapped().createComponent(componentExpression, context, componentType);
	}

	@Override
	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
		throws FacesException {
		return getWrapped().createComponent(componentType);
	}

	@Override
	public Converter createConverter(String converterId) {
		return getWrapped().createConverter(converterId);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Converter createConverter(Class targetClass) {
		return getWrapped().createConverter(targetClass);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return getWrapped().createMethodBinding(ref, params);
	}

	@Override
	public Validator createValidator(String validatorId) throws FacesException {
		return getWrapped().createValidator(validatorId);
	}

	@Override
	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return getWrapped().createValueBinding(ref);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType)
		throws ELException {
		return getWrapped().evaluateExpressionGet(context, expression, expectedType);
	}

	@Override
	public ActionListener getActionListener() {
		return getWrapped().getActionListener();
	}

	@Override
	public Iterator<String> getComponentTypes() {
		return getWrapped().getComponentTypes();
	}

	@Override
	public Iterator<String> getConverterIds() {
		return getWrapped().getConverterIds();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Iterator<Class> getConverterTypes() {
		return getWrapped().getConverterTypes();
	}

	@Override
	public Locale getDefaultLocale() {
		return getWrapped().getDefaultLocale();
	}

	@Override
	public String getDefaultRenderKitId() {
		return getWrapped().getDefaultRenderKitId();
	}

	@Override
	public ELContextListener[] getELContextListeners() {
		return getWrapped().getELContextListeners();
	}

	@Override
	public ELResolver getELResolver() {
		return getWrapped().getELResolver();
	}

	@Override
	public ExpressionFactory getExpressionFactory() {
		return getWrapped().getExpressionFactory();
	}

	@Override
	public String getMessageBundle() {
		return getWrapped().getMessageBundle();
	}

	@Override
	public NavigationHandler getNavigationHandler() {
		return getWrapped().getNavigationHandler();
	}

	@Override
	public PropertyResolver getPropertyResolver() {
		return getWrapped().getPropertyResolver();
	}

	@Override
	public ResourceBundle getResourceBundle(FacesContext ctx, String name) {
		return getWrapped().getResourceBundle(ctx, name);
	}

	@Override
	public StateManager getStateManager() {
		return getWrapped().getStateManager();
	}

	@Override
	public Iterator<Locale> getSupportedLocales() {
		return getWrapped().getSupportedLocales();
	}

	@Override
	public Iterator<String> getValidatorIds() {
		return getWrapped().getValidatorIds();
	}

	@Override
	public VariableResolver getVariableResolver() {
		return getWrapped().getVariableResolver();
	}

	@Override
	public ViewHandler getViewHandler() {
		return getWrapped().getViewHandler();
	}

	@Override
	public void removeELContextListener(ELContextListener listener) {
		getWrapped().removeELContextListener(listener);
	}

	@Override
	public void setActionListener(ActionListener listener) {
		getWrapped().setActionListener(listener);
	}

	@Override
	public void setDefaultLocale(Locale locale) {
		getWrapped().setDefaultLocale(locale);
	}

	@Override
	public void setDefaultRenderKitId(String renderKitId) {
		getWrapped().setDefaultRenderKitId(renderKitId);
	}

	@Override
	public void setMessageBundle(String bundle) {
		getWrapped().setMessageBundle(bundle);
	}

	@Override
	public void setNavigationHandler(NavigationHandler handler) {
		getWrapped().setNavigationHandler(handler);
	}

	@Override
	public void setPropertyResolver(PropertyResolver resolver) {
		getWrapped().setPropertyResolver(resolver);
	}

	@Override
	public void setStateManager(StateManager manager) {
		getWrapped().setStateManager(manager);
	}

	@Override
	public void setSupportedLocales(Collection<Locale> locales) {
		getWrapped().setSupportedLocales(locales);
	}

	@Override
	public void setVariableResolver(VariableResolver resolver) {
		getWrapped().setVariableResolver(resolver);
	}

	@Override
	public void setViewHandler(ViewHandler handler) {
		getWrapped().setViewHandler(handler);
	}

}
