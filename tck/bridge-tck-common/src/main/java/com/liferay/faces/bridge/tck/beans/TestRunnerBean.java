/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.beans;

import java.lang.IllegalStateException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class TestRunnerBean extends Object {

	// The portlet's name is testGroup-testName-portlet.  For example encodeActionURL_absoluteURLTest-portlet.
	// testGroup corresponds to the managedBean name that holds the object containing the test.
	// testName corresponds to the name of the Test that is the value of the
	// test method annotation we look for to determine which method is called to run the test.
	private String mTestGroup;
	private String mTestName;

	private Object mTest;

	private BridgeTCKResultWriter mResultWriter;
	private boolean mStatus = false;
	private boolean mTestComplete = false;
	private Method mTestMethod;
	private Method mTestActionListener;

	public TestRunnerBean() {

		// Get the Test information from the request (attributes)
		// Placed into the request by the GenericFacesTestSuitePortlet
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> m = context.getExternalContext().getRequestMap();
		mTestGroup = (String) m.get(Constants.TEST_BEAN_NAME);
		mTestName = (String) m.get(Constants.TEST_NAME);

		if ((mTestGroup == null) || (mTestName == null)) {
			throw new IllegalStateException(
				"Couldn't locate the TestGroup or TestName in the request.  Did you use the GenericFacesTestSuitePortlet?");
		}

		// Now lookup the object that implements the test.  There should be a managedBean with the same name as
		// the testGroup.
		mTest = context.getELContext().getELResolver().getValue(context.getELContext(), null, mTestGroup);

		if (mTest == null) {
			throw new IllegalStateException("Couldn't locate managed bean containing the test: " + mTestGroup);
		}

		// The test is annotated by a method annotation with a parameter value = mTestName -- so
		// look it up.
		mTestMethod = getAnnotatedTestMethod(mTestName, mTest);
		mTestActionListener = getAnnotatedTestActionListener(mTestName, mTest);

		if (mTestMethod == null)
			throw new IllegalStateException("Couldn't locate (annotation for) test: " + mTestName +
				"Test in the test object.");

		// Its not often we use the listener
	}

	public void appendTestDetail(String detail) {

		if (mResultWriter != null) {
			mResultWriter.addDetail(detail);
		}
	}

	public String getRedisplayCommandName() {
		// For use with resource redisplay tests -- to kick off the PPR -- The button starts with Run Test which causes
		// the PPR -- it then changes its name to "" so the GoLink can be invoked

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		// Still running test is test is neither marked complete in the bean itself or in the session attribute
		// Later exists for tests that span mode changes/scope changes where the test bean is lost.
		if (!isTestComplete() && (ec.getSessionMap().get("com.liferay.faces.bridge.tck.testCompleted") == null)) {

			// For tests that use partial page refresh technology -- disable the button after its invoked
			if (mTestName.contains("PPR") &&
					(ec.getRequestMap().get("com.liferay.faces.bridge.tck.pprSubmitted") != null)) {
				return "";
			}
			else {
				return "Run Test";
			}
		}
		else {
			return "Test Complete";
		}
	}

	public String getRedisplayLinkName() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		// Still running test is test is neither marked complete in the bean itself or in the session attribute
		// Later exists for tests that span mode changes/scope changes where the test bean is lost.
		if (!isTestComplete() && (ec.getSessionMap().get("com.liferay.faces.bridge.tck.testCompleted") == null)) {

			// For tests that use partial page refresh technology with a follow on render redisplay -- disable the
			// link until PPR invoked
			if (mTestName.contains("PPR") &&
					(ec.getRequestMap().get("com.liferay.faces.bridge.tck.pprSubmitted") == null)) {
				return "";
			}
			else {
				return "Run Test";
			}
		}
		else {
			return "Test Complete";
		}
	}

	// Run the test and renders the result
	public String getRenderTestResult() {
		return getTestResult(runTest());
	}

	public String getTestName() {
		return mTestName;
	}

	public String getTestResourceResult() {

		if (mResultWriter != null) {
			return mResultWriter.toString();
		}
		else {
			String result = runTest();

			if (mResultWriter != null) {
				return mResultWriter.toString();
			}
			else {
				return result;
			}
		}
	}

	public String getTestResult() {

		if (mResultWriter == null) {
			setTestResult(false,
				"Test failed: A test result hasn't been set! Usually this indicates the request flow broken due to bridge failure.");
		}

		return mResultWriter.toString();
	}

	// Merely renders the test result that was previously set.
	public String getTestResult(String s) {

		if (mResultWriter == null) {

			if ((s != null) && !s.equals(Constants.TEST_SUCCESS) && !s.equals(Constants.TEST_FAILED)) {
				return s;
			}
			else {
				setTestResult(false,
					"Test failed: A test result hasn't been set! Usually this indicates the request flow broken due to bridge failure.");
			}
		}

		return mResultWriter.toString();
	}

	public boolean getTestStatus() {
		return mStatus;
	}

	public boolean isTestComplete() {
		return mTestComplete;
	}

	public void runActionListener(ActionEvent action) {

		// run the test and return the result.
		if (mTestActionListener != null) {

			try {
				mTestActionListener.invoke(mTest, new Object[] { this, action });
			}
			catch (Exception e) {

				// do nothing
				return;
			}
		}
	}

	public String runActionTest() {

		// Called either because the action is submitted
		return runTest();
	}

	public void runActionTest(ActionEvent action) {

		// In resource case we use an action listener
		runActionListener(action);
	}

	public void setTestComplete(boolean complete) {
		mTestComplete = complete;
	}

	public void setTestResult(boolean passed, String detail) {
		mResultWriter = new BridgeTCKResultWriter(mTestName);
		mResultWriter.setStatus(passed);
		mResultWriter.addDetail(detail);

		mStatus = passed;
	}

	private Method getAnnotatedTestActionListener(String testName, Object testObj) {

		// search methods for one annotated by the BridgeTest annotation suffixed by 'ActionListener' and
		// with a test (parameter) value = portletNameTest
		String listenerName = testName.concat("ActionListener");

		for (Method method : testObj.getClass().getMethods()) {
			Annotation[] annotations = method.getAnnotations();

			if (annotations != null) {

				for (Annotation annotation : annotations) {
					Class<? extends Annotation> annotationType = annotation.annotationType();

					if (BridgeTest.class.equals(annotationType)) {
						String annotatedTestName = ((BridgeTest) annotation).test();

						if ((annotatedTestName != null) && (annotatedTestName.length() > 0) &&
								annotatedTestName.equalsIgnoreCase(listenerName))
							return method;
					}
				}
			}
		}

		return null;
	}

	private Method getAnnotatedTestMethod(String testName, Object testObj) {

		// search methods for one annotated by the BridgeTest annotation and
		// with a test (parameter) value = portletNameTest
		for (Method method : testObj.getClass().getMethods()) {
			Annotation[] annotations = method.getAnnotations();

			if (annotations != null) {

				for (Annotation annotation : annotations) {
					Class<? extends Annotation> annotationType = annotation.annotationType();

					if (BridgeTest.class.equals(annotationType)) {
						String annotatedTestName = ((BridgeTest) annotation).test();

						if ((annotatedTestName != null) && (annotatedTestName.length() > 0) &&
								annotatedTestName.equalsIgnoreCase(testName))
							return method;
					}
				}
			}
		}

		return null;
	}

	private String runTest() {

		// Only run the test once
		if (mTestComplete) {
			return (mStatus) ? Constants.TEST_SUCCESS : Constants.TEST_FAILED;
		}

		// run the test and return the result.
		if (mTestMethod != null) {

			try {
				return (String) mTestMethod.invoke(mTest, this);
			}
			catch (Exception e) {
				setTestResult(false, "Test failed: " + e.toString() + " thrown during invocation");

				return Constants.TEST_FAILED;
			}
		}
		else {
			setTestResult(false,
				"Test failed: unable to determine test method to call.  Is the portletName properly encoded?  Syntax: testBean-testMethodName-portlet");

			return Constants.TEST_FAILED;
		}
	}
}
