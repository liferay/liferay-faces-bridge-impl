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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

/**
 * This is a utility class that acts as a compatibility layer in order to minimize diffs across branches.
 *
 * @author  Neil Griffin
 */
class EncodeURLTestUtil {

	static boolean isStrictXhtmlEncoded(String url) {

		// check for use of &amp; in query string
		int currentPos = 0;
		boolean isStrict = false;

		while (true) {
			int ampPos = url.indexOf('&', currentPos);
			int xhtmlAmpPos = url.indexOf("&amp;", currentPos);

			// no more & to process -- so return current value of isStrict
			if (ampPos == -1) {
				return isStrict;
			}

			// if the amp we found doesn't start an &amp; then its not strict
			if (ampPos != xhtmlAmpPos) {
				return false;
			}

			isStrict = true;
			currentPos = ampPos + 1;
		}
	}
}
