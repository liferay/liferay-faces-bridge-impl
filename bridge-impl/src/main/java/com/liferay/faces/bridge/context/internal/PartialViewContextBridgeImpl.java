/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
import java.util.List;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

import com.liferay.faces.bridge.client.internal.BridgeScriptUtil;
import com.liferay.faces.util.client.Script;
import com.liferay.faces.util.context.FacesRequestContext;
import com.liferay.faces.util.context.PartialResponseWriterWrapper;


/**
 * This class is a wrapper around the {@link PartialViewContext}. Its purpose is to wrap the {@link
 * PartialResponseWriter} with a {@link PartialResponseWriterAlloyImpl} which writes {@link Script}s from {@link
 * FacesRequestContext} to the &lt;eval&gt; section of the partial response.
 *
 * @author  Neil Griffin
 */
public class PartialViewContextBridgeImpl extends PartialViewContextWrapper {

	// Private Data Members
	private PartialResponseWriter partialResponseWriter;
	private PartialViewContext wrappedPartialViewContext;

	public PartialViewContextBridgeImpl(PartialViewContext partialViewContext) {
		this.wrappedPartialViewContext = partialViewContext;
	}

	/**
	 * This method is missing from the {@link PartialViewContextWrapper} class so it must be implemented here.
	 */
	@Override
	public void setPartialRequest(boolean isPartialRequest) {
		wrappedPartialViewContext.setPartialRequest(isPartialRequest);
	}

	@Override
	public PartialResponseWriter getPartialResponseWriter() {

		if (partialResponseWriter == null) {
			partialResponseWriter = new PartialResponseWriterBridgeImpl(super.getPartialResponseWriter());
		}

		return partialResponseWriter;
	}

	@Override
	public PartialViewContext getWrapped() {
		return wrappedPartialViewContext;
	}

	/**
	 * This class serves as a wrapper around the {@link PartialResponseWriter} that will encode JavaScript within an
	 * <eval>...</eval> section just before the end of the partial-response document.
	 *
	 * @author  Kyle Stiemann
	 */
	protected class PartialResponseWriterBridgeImpl extends PartialResponseWriterWrapper {

		// Private Data Members
		private boolean wroteEval;

		public PartialResponseWriterBridgeImpl(PartialResponseWriter partialResponseWriter) {
			super(partialResponseWriter);
		}

		@Override
		public void endDocument() throws IOException {

			if (!wroteEval) {

				FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
				List<Script> scripts = facesRequestContext.getScripts();

				if (!scripts.isEmpty()) {

					super.startEval();
					BridgeScriptUtil.writeScripts(partialResponseWriter, scripts);
					super.endEval();
				}
			}

			super.endDocument();
		}

		@Override
		public void endEval() throws IOException {

			FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
			List<Script> scripts = facesRequestContext.getScripts();

			if (!scripts.isEmpty()) {
				BridgeScriptUtil.writeScripts(partialResponseWriter, scripts);
			}

			super.endEval();
			wroteEval = true;
		}
	}
}
