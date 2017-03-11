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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.el.ELResolver;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


/**
 * <p>This class is a JSF {@link ViewScoped} {@link ManagedBean} that maintains a list of JavaScript and/or CSS
 * resources that have been added to the &lt;head&gt; section of the portal page. Along with {@link HeadPhaseListener}
 * and {@link HeadRendererBridgeImpl}, this class helps provides a solution to an issue regarding Ajax-initiated
 * execution of navigation-rules in a portlet. See the class-level comments in the {@link HeadPhaseListener} for more
 * details.</p>
 *
 * @author  Neil Griffin
 */
@ManagedBean
@ViewScoped
public class HeadManagedBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 3829127137783852729L;

	// Private Data Members
	private Set<String> headResourceIds = new HashSet<String>();

	public static HeadManagedBean getInstance(FacesContext facesContext) {
		String elExpression = "headManagedBean";
		ELResolver elResolver = facesContext.getApplication().getELResolver();

		return (HeadManagedBean) elResolver.getValue(facesContext.getELContext(), null, elExpression);
	}

	public Set<String> getHeadResourceIds() {
		return headResourceIds;
	}

}
