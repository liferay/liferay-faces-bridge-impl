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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.Writer;


/**
 * @author  Neil Griffin
 */
public class BufferedRenderWriterCompatImpl extends BufferedRenderWriterImpl {

	// Private Data Members
	private Writer wrappedWriter;

	public BufferedRenderWriterCompatImpl(Writer writer) {
		super();
		this.wrappedWriter = writer;
	}

	/**
	 * Renders the buffered response output to the wrapped {@link Writer}.
	 *
	 * @throws  IOException
	 */
	public void render() throws IOException {

		for (OutputOperation outputOperation : outputOperationList) {
			outputOperation.invoke(wrappedWriter);
		}
	}
}
