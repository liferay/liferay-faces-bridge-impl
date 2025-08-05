/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.test.showcase.bridge;

import java.io.IOException;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.test.showcase.inputfile.InputFileTester;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class BridgeInputFileInstantAjaxTester extends InputFileTester {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeInputFileInstantAjaxTester.class);

	@Test
	public void runBridgeInputFileInstantAjaxTest() throws IOException {

		logger.warn(
			"File Upload Instant Ajax use-case does not exist for Liferay 6.2, so this test will always fail on Liferay 6.2. See FACES-3199 for more details.");

		runInputFileTest("bridge", "instant-ajax");
	}
}
