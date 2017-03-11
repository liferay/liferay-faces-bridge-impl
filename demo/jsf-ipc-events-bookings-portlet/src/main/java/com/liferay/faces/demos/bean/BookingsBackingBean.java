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
package com.liferay.faces.demos.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.faces.annotation.BridgePreDestroy;
import javax.portlet.faces.annotation.BridgeRequestScopeAttributeAdded;
import javax.xml.namespace.QName;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "bookingsBackingBean")
@RequestScoped
public class BookingsBackingBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2920712441012786321L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BookingsBackingBean.class);

	// Injections
	@ManagedProperty(name = "bookingsModelBean", value = "#{bookingsModelBean}")
	private BookingsModelBean bookingsModelBean;

	// Private Data Members
	private boolean okToHandlePreDestroy = true;

	@BridgePreDestroy
	public void bridgePreDestroy() {
		logger.trace(
			"@BridgePreDestroy annotation worked -- should only be called if com.liferay.faces.bridge.preferPreDestroy init param is false in portlet.xml");
	}

	@BridgeRequestScopeAttributeAdded
	public void bridgeRequestScopeAttributeAdded() {
		okToHandlePreDestroy = false;
		logger.trace(
			"@BridgeRequestScopeAttributeAdded annotation worked -- should only be called for remote WSRP portlets");
	}

	@PostConstruct
	public void postConstruct() {
		logger.trace("@PostConstruct annotation worked");
	}

	@PreDestroy
	public void preDestroy() {

		// For more info, see comments here: http://issues.liferay.com/browse/FACES-146
		if (okToHandlePreDestroy) {
			logger.trace("@PreDestroy annotation worked, okToHandlePreDestroy=true");
		}
		else {
			logger.trace("@PreDestroy annotation worked, okToHandlePreDestroy=false");
		}
	}

	public void setBookingsModelBean(BookingsModelBean bookingsModelBean) {

		// Injected via ManagedProperty annotation
		this.bookingsModelBean = bookingsModelBean;
	}

	public void submit() {
		logger.debug("Submitting booking changes.");

		QName qName = new QName("http://liferay.com/events", "ipc.customerEdited");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		ActionResponse actionResponse = (ActionResponse) externalContext.getResponse();
		actionResponse.setEvent(qName, bookingsModelBean.getCustomer());
	}
}
