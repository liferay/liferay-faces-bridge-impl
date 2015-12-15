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
package com.liferay.faces.bridge.tck.common.util.faces.application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.portlet.PortletContext;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * View handler implementation for JSF portlet bridge. The only method we override here is getActionURL(). TODO JSF 1.2
 * note: JSF 1.2 RI implements ViewHandler.renderView() differently in order to handle emitting non-JSF markup that
 * follows the JSF tags after the JSF renders correctly. Unfortunately, the RI handles this by introducing several
 * servlet dependencies. Currently, the bridge handles this by overriding the renderView() and ignoring (not
 * interleafing) the non-JSF markup - see HACK below
 */
public class TestSuiteViewHandlerImpl extends ViewHandlerWrapper {

	// the ViewHandler to delegate to
	private ViewHandler mDelegate;

	public TestSuiteViewHandlerImpl(ViewHandler handler) {
		mDelegate = handler;
	}

	@Override
	public UIViewRoot createView(FacesContext ctx, String viewId) throws IllegalArgumentException,
		NullPointerException {
		ctx.getExternalContext().getRequestMap().put("org.apache.portlet.faces.tck.viewCreated", Boolean.TRUE);

		return super.createView(ctx, viewId);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		String testName = (String) context.getExternalContext().getRequestMap().get(Constants.TEST_NAME);

		// Do nothing when not running in portlet request or not the specific test
		if (!BridgeUtil.isPortletRequest() || (testName == null) || (testName.indexOf("renderPolicyTest") < 0)) {
			super.renderView(context, viewToRender);

			return;
		}
		else {
			ExternalContext extCtx = context.getExternalContext();
			Map m = extCtx.getRequestMap();

			// Check to see what the render rule is
			PortletContext pCtx = (PortletContext) extCtx.getContext();
			String policyStr = pCtx.getInitParameter(Bridge.RENDER_POLICY);
			Bridge.BridgeRenderPolicy policy = (policyStr != null) ? Bridge.BridgeRenderPolicy.valueOf(policyStr)
																   : null;

			if (policy == null) {

				// no policy so we are to do the default
				m.put("javax.portlet.faces.tck.testRenderPolicyPass",
					"Bridge correctly delegated first as no render policy was set.");
				throw new FacesException(
					"Can't do a portlet render -- but we expect the bridge to catch this and complete the render itself");
			}
			else if (policy == Bridge.BridgeRenderPolicy.DEFAULT) {

				// no policy so we are to do the default
				m.put("javax.portlet.faces.tck.testRenderPolicyPass",
					"Bridge correctly delegated first as the render policy was DEFAULT.");
				throw new FacesException(
					"Can't do a portlet render -- but we expect the bridge to catch this and complete the render itself");
			}
			else if (policy == Bridge.BridgeRenderPolicy.ALWAYS_DELEGATE) {
				m.put("javax.portlet.faces.tck.testRenderPolicyPass",
					"Bridge correctly delegated as the render policy is ALWAYS_DELEGATE.");
				renderSelf(context, viewToRender);

				return;
			}
			else if (policy == Bridge.BridgeRenderPolicy.NEVER_DELEGATE) {
				m.put("javax.portlet.faces.tck.testRenderPolicyFail",
					"Bridge incorrectly delegated when render policy was NEVER_DELEGATE.");
				renderSelf(context, viewToRender);
			}
		}

	}

	@Override
	public UIViewRoot restoreView(FacesContext ctx, String viewId) throws FacesException, NullPointerException {
		ctx.getExternalContext().getRequestMap().put("org.apache.portlet.faces.tck.viewCreated", Boolean.FALSE);

		return super.restoreView(ctx, viewId);
	}

	private String appendQueryString(String url, String params) {

		if (url.indexOf('?') < 0) {
			return url + "?" + params;
		}
		else {
			return url + "&" + params;
		}
	}

	private void doRenderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		viewToRender.encodeAll(context);
	}

	private void renderSelf(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		ExternalContext extContext = context.getExternalContext();
		RenderResponse renderResponse = (RenderResponse) extContext.getResponse();

		try {

			// set request attribute indicating we can deal with content
			// that is supposed to be delayed until after JSF tree is ouput.
			extContext.getRequestMap().put(Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);
			// TODO JSF 1.2 - executePageToBuildView() creates ViewHandlerResponseWrapper to handle error page and text
			// that exists after the <f:view> tag among other things which have lots of servlet dependencies - we're
			// skipping this for now for portlet

			// Bridge has had to set this attribute so  Faces RI will skip servlet dependent
			// code when mapping from request paths to viewIds -- however we need to remove it
			// as it screws up the dispatch
			extContext.getRequestMap().remove("javax.servlet.include.servlet_path");
			extContext.dispatch(viewToRender.getViewId());

		}
		catch (IOException e) {
			throw new FacesException(e);
		}

		// set up the ResponseWriter
		RenderKitFactory renderFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
		RenderKit renderKit = renderFactory.getRenderKit(context, viewToRender.getRenderKitId());

		ResponseWriter oldWriter = context.getResponseWriter();
		StringBuilderWriter strWriter = new StringBuilderWriter(context, 4096);
		ResponseWriter newWriter;

		if (null != oldWriter) {
			newWriter = oldWriter.cloneWithWriter(strWriter);
		}
		else {
			newWriter = renderKit.createResponseWriter(strWriter, null, renderResponse.getCharacterEncoding());
		}

		context.setResponseWriter(newWriter);

		newWriter.startDocument();

		doRenderView(context, viewToRender);

		newWriter.endDocument();

		// replace markers in the body content and write it to response.

		ResponseWriter responseWriter;

		// Dispatch may have output to an OutputStream instead of a Writer
		Writer renderResponseWriter = null;

		try {
			renderResponseWriter = renderResponse.getWriter();
		}
		catch (IllegalStateException ise) {

			// got this exception because we've called getOutputStream() previously
			renderResponseWriter = new BufferedWriter(new OutputStreamWriter(renderResponse.getPortletOutputStream(),
						renderResponse.getCharacterEncoding()));
		}

		if (null != oldWriter) {
			responseWriter = oldWriter.cloneWithWriter(renderResponseWriter);
		}
		else {
			responseWriter = newWriter.cloneWithWriter(renderResponseWriter);
		}

		context.setResponseWriter(responseWriter);

		strWriter.write(responseWriter);
		renderResponseWriter.flush();

		if (null != oldWriter) {
			context.setResponseWriter(oldWriter);
		}

		Object content = extContext.getRequestMap().get(Bridge.AFTER_VIEW_CONTENT);

		if (content != null) {

			if (content instanceof char[]) {
				renderResponse.getWriter().write(new String((char[]) content));
			}
			else if (content instanceof byte[]) {
				renderResponse.getWriter().write(new String((byte[]) content));
			}
			else {
				throw new IOException("PortletViewHandlerImpl: invalid" + "AFTER_VIEW_CONTENT buffer type");
			}
		}

		renderResponse.flushBuffer();
	}

	public String getActionURL(FacesContext context, String viewId) {

		// Call super to get the actionURL
		String resultURL = super.getActionURL(context, viewId);

		// Then test to see if we are in a render and this is an encodeActionURL test that
		// tests the render encoding -- if so add the appropriate parameters to test.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RENDER_PHASE) {
			String testName = (String) context.getExternalContext().getRequestMap().get(Constants.TEST_NAME);

			if (testName == null)
				return resultURL;

			if (testName.equals("encodeActionURLWithParamRenderTest")) {
				return appendQueryString(resultURL, "param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithModeRenderTest") ||
					testName.equals("encodeResourceURLWithModeTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.PortletMode=edit&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidModeRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.PortletMode=blue&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithWindowStateRenderTest") ||
					testName.equals("encodeResourceURLWithWindowStateTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.WindowState=maximized&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidWindowStateRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.WindowState=blue&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithSecurityRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.Secure=true&param1=testValue");
			}
			else if (testName.equals("encodeActionURLWithInvalidSecurityRenderTest")) {
				return appendQueryString(resultURL, "javax.portlet.faces.Secure=blue&param1=testValue");
			}
		}

		return resultURL;
	}

	public ViewHandler getWrapped() {
		return mDelegate;
	}

	private static final class StringBuilderWriter extends Writer {

		// TODO: These bridge needs to use it's own constants here. This will
		// confine
		// us to only work with the R.I.
		private static final String RI_SAVESTATE_FIELD_MARKER = "~com.sun.faces.saveStateFieldMarker~";
		private static final String MYFACES_SAVESTATE_FIELD_MARKER = "<!--@@JSF_FORM_STATE_MARKER@@-->";
		private static String sSaveStateFieldMarker = null;
		private StringBuilder mBuilder;
		private FacesContext mContext;

		public StringBuilderWriter(FacesContext context, int initialCapacity) {

			if (initialCapacity < 0) {
				throw new IllegalArgumentException();
			}

			mBuilder = new StringBuilder(initialCapacity);
			mContext = context;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public String toString() {
			return mBuilder.toString();
		}

		/**
		 * Write a string.
		 *
		 * @param  str  String to be written
		 */
		@Override
		public void write(String str) {
			mBuilder.append(str);
		}

		public void write(Writer writer) throws IOException {

			// See if we already have determined the SAVESTATE_FIELD_MARKER in use
			// If not then determine it and set for future use
			if (sSaveStateFieldMarker == null) {
				sSaveStateFieldMarker = determineSaveStateFieldMarker();
			}

			// TODO: Buffer?
			int pos = 0;

			// First we need to make sure we save the view
			StateManager stateManager = mContext.getApplication().getStateManager();
			Object stateToWrite = stateManager.saveView(mContext);

			// If we didn't find a savestate_field_marker don't search to replace for one.
			if (sSaveStateFieldMarker != null) {
				int markLen = sSaveStateFieldMarker.length();
				int tildeIdx = mBuilder.indexOf(sSaveStateFieldMarker);

				while (tildeIdx > 0) {
					writer.write(mBuilder.substring(pos, tildeIdx));
					stateManager.writeState(mContext, stateToWrite);
					pos = tildeIdx + markLen;
					tildeIdx = mBuilder.indexOf(sSaveStateFieldMarker, pos);
				}
			}

			writer.write(mBuilder.substring(pos));
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {

			if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return;
			}

			mBuilder.append(cbuf, off, len);
		}

		@Override
		public void write(String str, int off, int len) {
			write(str.substring(off, off + len));
		}

		private String determineSaveStateFieldMarker() throws IOException {

			// First check to see if there is one set in the configuration - if so test it first
			String marker = ((PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getInitParameter(Bridge.SAVESTATE_FIELD_MARKER);

			if (isMarker(marker)) {
				return marker;
			}

			// wasn't that one so test the Faces RI marker
			else if (isMarker(RI_SAVESTATE_FIELD_MARKER)) {
				return RI_SAVESTATE_FIELD_MARKER;
			}

			// wasn't that one so test the MyFaces marker
			else if (isMarker(MYFACES_SAVESTATE_FIELD_MARKER)) {
				return MYFACES_SAVESTATE_FIELD_MARKER;
			}

			// log that we didn't find a marker
			// However ignore this "exceptional" situation because its not so exceptional
			// MyFaces actually directly writes the state into the response more commonly
			// than it writes the Marker.
			mContext.getExternalContext().log(
				"Unable to locate a SAVESTATE_FIELD_MARKER in response.  This could be because your Faces environment doesn't write such a marker or because the bridge doesn't know the marker in use.  If the later, configure the appropriate application init parameter javax.portlet.faces.SAVESTATE_FIELD_MARKER.");

			return null;
		}

		public StringBuilder getBuffer() {
			return mBuilder;
		}

		private boolean isMarker(String marker) {
			return (marker != null) && (mBuilder.indexOf(marker) >= 0);
		}
	}

}
