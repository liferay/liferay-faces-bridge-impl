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
package com.liferay.faces.bridge.component.icefaces;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;


/**
 * This class is part of a workaround for <a href="http://jira.icesoft.org/browse/ICE-6398">ICE-6398</a>.
 *
 * @author  Neil Griffin
 */
public abstract class DataPaginator extends HtmlPanelGroup implements ActionSource {

	// Public Constants
	public static final String COMPONENT_TYPE = "com.icesoft.faces.DataScroller";
	public static final String RENDERER_TYPE = "com.icesoft.faces.DataScroller";

	public abstract void addActionListener(ActionListener listener);

	@Override
	public abstract void broadcast(FacesEvent event) throws AbortProcessingException;

	public abstract UIData findUIData(FacesContext facesContext) throws Exception;

	@SuppressWarnings("deprecation")
	public abstract javax.faces.el.MethodBinding getAction();

	@SuppressWarnings("deprecation")
	public abstract javax.faces.el.MethodBinding getActionListener();

	public abstract ActionListener[] getActionListeners();

	public abstract String getBaseStyleClass();

	public abstract String getComponentType();

	public abstract String getDisplayedRowsCountVar();

	public abstract String getEnabledOnUserRole();

	@Override
	public abstract String getFamily();

	public abstract UIComponent getFastForward();

	public abstract UIComponent getFastRewind();

	public abstract int getFastStep();

	public abstract UIComponent getFirst();

	public abstract int getFirstRow();

	public abstract String getFirstRowIndexVar();

	public abstract String getFor();

	public abstract UIComponent getLast();

	public abstract String getLastRowIndexVar();

	public abstract UIComponent getNext();

	public abstract int getPageCount();

	public abstract String getPageCountVar();

	public abstract int getPageIndex();

	public abstract String getPageIndexVar();

	public abstract String getPaginatorActiveColumnClass();

	public abstract String getPaginatorColumnClass();

	public abstract int getPaginatorMaxPages();

	public abstract String getPaginatorTableClass();

	public abstract UIComponent getPrevious();

	public abstract String getRenderedOnUserRole();

	@Override
	public abstract String getRendererType();

	@Override
	public abstract boolean getRendersChildren();

	public abstract int getRowCount();

	public abstract int getRows();

	public abstract String getRowsCountVar();

	public abstract String getscrollButtonCellClass();

	@Override
	public abstract String getStyle();

	@Override
	public abstract String getStyleClass();

	public abstract int getTabindex();

	public abstract UIData getUIData() throws Exception;

	public abstract void gotoFastForward();

	public abstract void gotoFastRewind();

	public abstract void gotoFirstPage();

	public abstract void gotoLastPage();

	public abstract void gotoNextPage();

	public abstract void gotoPreviousPage();

	public abstract boolean isDisabled();

	public abstract boolean isImmediate();

	public abstract boolean isKeyboardNavigationEnabled();

	public abstract boolean isLastPage();

	public abstract boolean isModelResultSet();

	public abstract boolean isPaginator();

	@Override
	public abstract boolean isRendered();

	public abstract boolean isRenderFacetsIfSinglePage();

	public abstract boolean isVertical();

	@Override
	public abstract void queueEvent(FacesEvent event);

	public abstract void removeActionListener(ActionListener listener);

	@Override
	public abstract void restoreState(FacesContext context, Object state);

	@Override
	public abstract Object saveState(FacesContext context);

	@SuppressWarnings("deprecation")
	public abstract void setAction(javax.faces.el.MethodBinding action);

	@SuppressWarnings("deprecation")
	public abstract void setActionListener(javax.faces.el.MethodBinding actionListener);

	public abstract void setDisabled(boolean disabled);

	public abstract void setDisplayedRowsCountVar(String displayedRowsCountVar);

	public abstract void setEnabledOnUserRole(String enabledOnUserRole);

	public abstract void setFastForward(UIComponent previous);

	public abstract void setFastRewind(UIComponent previous);

	public abstract void setFastStep(int fastStep);

	public abstract void setFirst(UIComponent first);

	public abstract void setFirstRowIndexVar(String firstRowIndexVar);

	public abstract void setFor(String forValue);

	public abstract void setImmediate(boolean immediate);

	public abstract void setKeyboardNavigationEnabled(boolean keyboardNavigationEnabled);

	public abstract void setLast(UIComponent last);

	public abstract void setLastRowIndexVar(String lastRowIndexVar);

	public abstract void setNext(UIComponent next);

	public abstract void setPageCountVar(String pageCountVar);

	public abstract void setPageIndexVar(String pageIndexVar);

	public abstract void setPaginator(boolean paginator);

	public abstract void setPaginatorMaxPages(int paginatorMaxPages);

	public abstract void setPrevious(UIComponent previous);

	public abstract void setRenderedOnUserRole(String renderedOnUserRole);

	public abstract void setRenderFacetsIfSinglePage(boolean renderFacetsIfSinglePage);

	public abstract void setRowsCountVar(String rowsCountVar);

	@Override
	public abstract void setStyle(String style);

	@Override
	public abstract void setStyleClass(String styleClass);

	public abstract void setTabindex(int tabindex);

	public abstract void setUIData(UIData uiData) throws Exception;

	public abstract void setVertical(boolean vertical);

}
