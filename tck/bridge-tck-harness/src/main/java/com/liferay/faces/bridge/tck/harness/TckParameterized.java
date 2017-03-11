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
package com.liferay.faces.bridge.tck.harness;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;


/**
 * <p>This version of the Paramaterized class returns an actual test name instead of an indexed test method name.</p>
 *
 * @author  Michael Freedman
 */
public class TckParameterized extends Suite {

	private final List<Runner> runners = new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public TckParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());

		List<Object[]> parametersList = getParametersList(getTestClass());

		for (int i = 0; i < parametersList.size(); i++) {
			runners.add(new TckTestClassRunnerForParameters(getTestClass().getJavaClass(), parametersList, i));
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass) throws Throwable {
		System.out.println(klass.getName());

		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(null);
	}

	private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {

		for (Annotation a : testClass.getAnnotations()) {
			System.out.println(a.toString());
		}

		List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);

		for (FrameworkMethod each : methods) {
			System.out.println(each.getName());

			int modifiers = each.getMethod().getModifiers();

			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class " + testClass.getName());
	}

	/**
	 * Annotation for a method which provides parameters to be injected into the test class constructor by <code>
	 * TckParameterized</code>
	 */
	private class TckTestClassRunnerForParameters extends BlockJUnit4ClassRunner {
		private final int fParameterSetNumber;

		private final List<Object[]> fParameterList;
		private final String fTestName;

		TckTestClassRunnerForParameters(Class<?> type, List<Object[]> parameterList, int i) throws InitializationError {
			super(type);
			fParameterList = parameterList;
			fParameterSetNumber = i;

			// The test name will be the second parameter
			fTestName = (String) parameterList.get(i)[1];
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(computeParams());
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {

			// return String.format("%s[%s]", method.getName(), fParameterSetNumber);
			return fTestName;
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		private Object[] computeParams() throws Exception {

			try {
				return fParameterList.get(fParameterSetNumber);
			}
			catch (ClassCastException e) {
				throw new Exception(String.format("%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod(getTestClass()).getName()));
			}
		}
	}
}
