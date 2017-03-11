/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ReferenceSyntaxException;


/**
 * @author  Michael Freedman
 */
public class TestSuiteApplicationImpl extends Application {
	private Application mWrapped;

	public TestSuiteApplicationImpl(Application app) {
		mWrapped = app;
	}

	/**
	 * Define a new mapping from a logical "component type" to an actual java class name. This controls what type is
	 * created when method createComponent of this class is called.
	 *
	 * <p>Param componentClass must be the fully-qualified class name of some class extending the UIComponent class. The
	 * class must have a default constructor, as instances of it will be created using Class.newInstance.
	 *
	 * <p>It is permitted to override a previously defined mapping, ie to call this method multiple times with the same
	 * componentType string. The createComponent method will simply use the last defined mapping.
	 */
	public void addComponent(String componentType, String componentClass) {
		mWrapped.addComponent(componentType, componentClass);
	}

	public void addConverter(String converterId, String converterClass) {
		mWrapped.addConverter(converterId, converterClass);
	}

	public void addConverter(Class targetClass, String converterClass) {
		mWrapped.addConverter(targetClass, converterClass);
	}

	public void addELContextListener(ELContextListener listener) {
		mWrapped.addELContextListener(listener);
	}

	// The following concrete methods were added for JSF 1.2.  They supply default
	// implementations that throw UnsupportedOperationException.
	// This allows old Application implementations to still work.
	public void addELResolver(ELResolver resolver) {
		mWrapped.addELResolver(resolver);
	}

	public void addValidator(String validatorId, String validatorClass) {
		mWrapped.addValidator(validatorId, validatorClass);
	}

	/**
	 * Create a new UIComponent subclass, using the mappings defined by previous calls to the addComponent method of
	 * this class.
	 *
	 * <p>
	 *
	 * @throws  FacesException  if there is no mapping defined for the specified componentType, or if an instance of the
	 *                          specified type could not be created for any reason.
	 */
	public javax.faces.component.UIComponent createComponent(String componentType) throws FacesException {
		return mWrapped.createComponent(componentType);
	}

	public UIComponent createComponent(ValueExpression componentExpression, FacesContext facesContext,
		String componentType) throws FacesException, NullPointerException {
		return mWrapped.createComponent(componentExpression, facesContext, componentType);
	}

	/**
	 * Create an object which has an associating "binding" expression tying the component to a user property.
	 *
	 * <p>First the specified value-binding is evaluated; if it returns a non-null value then the component "already
	 * exists" and so the resulting value is simply returned.
	 *
	 * <p>Otherwise a new UIComponent instance is created using the specified componentType, and the new object stored
	 * via the provided value-binding before being returned.
	 *
	 * @deprecated
	 */
	public javax.faces.component.UIComponent createComponent(javax.faces.el.ValueBinding componentBinding,
		javax.faces.context.FacesContext context, String componentType) throws FacesException {
		return mWrapped.createComponent(componentBinding, context, componentType);
	}

	public javax.faces.convert.Converter createConverter(String converterId) {
		return mWrapped.createConverter(converterId);
	}

	public javax.faces.convert.Converter createConverter(Class targetClass) {
		return mWrapped.createConverter(targetClass);
	}

	/**
	 * Create an object which can be used to invoke an arbitrary method via an EL expression at a later time. This is
	 * similar to createValueBinding except that it can invoke an arbitrary method (with parameters) rather than just
	 * get/set a javabean property.
	 *
	 * <p>This is used to invoke ActionListener method, and ValueChangeListener methods.
	 *
	 * @deprecated
	 */
	public javax.faces.el.MethodBinding createMethodBinding(String ref, Class[] params)
		throws ReferenceSyntaxException {
		return mWrapped.createMethodBinding(ref, params);
	}

	public javax.faces.validator.Validator createValidator(String validatorId) throws FacesException {
		return mWrapped.createValidator(validatorId);
	}

	/**
	 * Create an object which can be used to invoke an arbitrary method via an EL expression at a later time. This is
	 * similar to createValueBinding except that it can invoke an arbitrary method (with parameters) rather than just
	 * get/set a javabean property.
	 *
	 * <p>This is used to invoke ActionListener method, and ValueChangeListener methods.
	 *
	 * @deprecated
	 */
	public javax.faces.el.ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return mWrapped.createValueBinding(ref);
	}

	public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType)
		throws ELException {
		return mWrapped.evaluateExpressionGet(context, expression, expectedType);
	}

	public javax.faces.event.ActionListener getActionListener() {
		return mWrapped.getActionListener();
	}

	public Iterator<String> getComponentTypes() {
		return mWrapped.getComponentTypes();
	}

	public Iterator<String> getConverterIds() {
		return mWrapped.getConverterIds();
	}

	public Iterator<Class<?>> getConverterTypes() {
		return mWrapped.getConverterTypes();
	}

	public Locale getDefaultLocale() {
		return mWrapped.getDefaultLocale();
	}

	public String getDefaultRenderKitId() {
		return mWrapped.getDefaultRenderKitId();
	}

	public ELContextListener[] getELContextListeners() {
		return mWrapped.getELContextListeners();
	}

	public ELResolver getELResolver() {
		return mWrapped.getELResolver();
	}

	public ExpressionFactory getExpressionFactory() {
		return mWrapped.getExpressionFactory();
	}

	public String getMessageBundle() {
		return mWrapped.getMessageBundle();
	}

	/**
	 * Return the NavigationHandler object which is responsible for mapping from a logical (viewid, fromAction, outcome)
	 * to the URL of a view to be rendered.
	 */
	public javax.faces.application.NavigationHandler getNavigationHandler() {
		return mWrapped.getNavigationHandler();
	}

	/**
	 * Get the object used by the VariableResolver to read and write named properties on java beans, Arrays, Lists and
	 * Maps. This object is used by the ValueBinding implementation, and during the process of configuring "managed
	 * bean" properties.
	 *
	 * @deprecated
	 */
	public javax.faces.el.PropertyResolver getPropertyResolver() {
		return mWrapped.getPropertyResolver();
	}

	public ResourceBundle getResourceBundle(FacesContext ctx, String name) throws FacesException, NullPointerException {
		return mWrapped.getResourceBundle(ctx, name);
	}

	public javax.faces.application.StateManager getStateManager() {
		return mWrapped.getStateManager();
	}

	public Iterator<Locale> getSupportedLocales() {
		return mWrapped.getSupportedLocales();
	}

	public Iterator<String> getValidatorIds() {
		return mWrapped.getValidatorIds();
	}

	/**
	 * Get the object used to resolve expressions of form "#{...}".
	 *
	 * @deprecated
	 */
	public javax.faces.el.VariableResolver getVariableResolver() {
		return mWrapped.getVariableResolver();
	}

	public javax.faces.application.ViewHandler getViewHandler() {
		return mWrapped.getViewHandler();
	}

	public void removeELContextListener(ELContextListener listener) {
		mWrapped.removeELContextListener(listener);
	}

	public void setActionListener(javax.faces.event.ActionListener listener) {
		mWrapped.setActionListener(listener);
	}

	public void setDefaultLocale(Locale locale) {
		mWrapped.setDefaultLocale(locale);
	}

	public void setDefaultRenderKitId(String renderKitId) {
		mWrapped.setDefaultRenderKitId(renderKitId);
	}

	public void setMessageBundle(String bundle) {
		mWrapped.setMessageBundle(bundle);
	}

	public void setNavigationHandler(javax.faces.application.NavigationHandler handler) {
		mWrapped.setNavigationHandler(handler);
	}

	/**
	 * @deprecated
	 */
	public void setPropertyResolver(javax.faces.el.PropertyResolver resolver) {
		mWrapped.setPropertyResolver(resolver);
	}

	public void setStateManager(javax.faces.application.StateManager manager) {
		mWrapped.setStateManager(manager);
	}

	public void setSupportedLocales(Collection<Locale> locales) {
		mWrapped.setSupportedLocales(locales);
	}

	/**
	 * @deprecated
	 */
	public void setVariableResolver(javax.faces.el.VariableResolver resolver) {
		mWrapped.setVariableResolver(resolver);
	}

	public void setViewHandler(javax.faces.application.ViewHandler viewHandler) {

		if (viewHandler instanceof ViewHandlerWrapper) {

			// Create a new chain-of-delegation with the TCK's ViewHandler decorating the ViewHandler from the Faces
			// implementation (which does not extend ViewHandlerWrapper).
			Stack<ViewHandler> viewHandlerStack = new Stack<ViewHandler>();

			while (viewHandler instanceof ViewHandlerWrapper) {

				viewHandlerStack.push(viewHandler);

				ViewHandlerWrapper viewHandlerWrapper = (ViewHandlerWrapper) viewHandler;
				viewHandler = viewHandlerWrapper.getWrapped();
			}

			viewHandler = new TestSuiteViewHandlerImpl(viewHandler);

			while (!viewHandlerStack.isEmpty()) {

				ViewHandler poppedViewHandler = viewHandlerStack.pop();

				if (!(poppedViewHandler instanceof TestSuiteViewHandlerImpl)) {
					Class<ViewHandler> poppedViewHandlerClass = (Class<ViewHandler>) poppedViewHandler.getClass();

					try {
						Constructor<ViewHandler> constructor = poppedViewHandlerClass.getConstructor(ViewHandler.class);
						viewHandler = constructor.newInstance(viewHandler);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {

			// Decorate the specified ViewHandler with the TCK's ViewHandler.
			viewHandler = new TestSuiteViewHandlerImpl(viewHandler);
		}

		mWrapped.setViewHandler(viewHandler);
	}
}
