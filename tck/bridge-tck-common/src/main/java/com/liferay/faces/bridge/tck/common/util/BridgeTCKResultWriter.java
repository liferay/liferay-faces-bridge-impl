/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util;

import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.common.util.tags.TCKSpanTag;


/**
 * This class provides a way to put test results calculated in portlets/servlets in the HTTP response body These test
 * results are delimited by portlet custom tags.
 */
public class BridgeTCKResultWriter {

	public static boolean PASS = true;
	public static boolean FAIL = false;

	private static String LINE_BREAK = "<p>";

	private String mTestName = null;
	private TCKSpanTag mTestNameTag;
	private TCKSpanTag mDetailTag;
	private TCKSpanTag mStatusTag;

	/**
	 * Constructor
	 *
	 * @param  testName  name of the test
	 */
	public BridgeTCKResultWriter(String testName) {
		super();
		mTestName = testName;
		mTestNameTag = new TCKSpanTag(mTestName + "-test-name");
		mTestNameTag.setTagContent(mTestName);
		mStatusTag = new TCKSpanTag(mTestName + "-result-status");
		mDetailTag = new TCKSpanTag(mTestName + "-result-detail");
	}

	/**
	 * Returns the fail string. Also used on the client side to provide failure critieria for a test.
	 *
	 * @param  testName  name of the test
	 */
	public static String getFailedString(String testName) {
		return Constants.TEST_FAILED;
	}

	/**
	 * Returns the pass string. Also used on the client side, for specifying the success criteria.
	 *
	 * @param  testName  name of the test
	 */
	public static String getPassedString(String testName) {

		return Constants.TEST_SUCCESS;
	}

	/**
	 * Let's the tests add details about the test run.
	 *
	 * @param  detailLine  line containing detail information about the test run.
	 */
	public void addDetail(String detailLine) {
		mDetailTag.appendTagContent(LINE_BREAK + detailLine);
	}

	/**
	 * Let's the tests add details about the test run.
	 *
	 * @param  detailLine  line containing detail information about the test run.
	 */
	public void setDetail(String detailLine) {
		mDetailTag.appendTagContent(detailLine);
	}

	/**
	 * Sets the success/failure status for the test.
	 *
	 * @param  status  the success/failure status for the test.
	 */
	public void setStatus(boolean status) {
		mStatusTag.setTagContent(getStatusString(status));
	}

	/**
	 * Returns the string to be embedded in the Http Response.
	 */

	public String toString() {
		mDetailTag.appendTagContent(LINE_BREAK);

		return "Test: " + mTestNameTag.toString() + LINE_BREAK + "Status: " + mStatusTag.toString() + LINE_BREAK +
			"Detail: " + mDetailTag.toString();
	}

	/**
	 * Returns the string stating the status of the test.
	 *
	 * @param  status
	 */
	private String getStatusString(boolean status) {

		if (status) {
			return getPassedString(mTestName);
		}
		else {
			return getFailedString(mTestName);
		}
	}

}
