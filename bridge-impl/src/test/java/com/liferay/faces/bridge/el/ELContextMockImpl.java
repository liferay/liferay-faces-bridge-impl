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
package com.liferay.faces.bridge.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;


/**
 * @author  Kyle Stiemann
 */
public class ELContextMockImpl extends ELContext {

	// Private Final Data Members
	private final ELResolver elResolver;

	// Private Data Members
	boolean resolved;

	public ELContextMockImpl(ELResolver eLResolver) {
		this.elResolver = eLResolver;
	}

	@Override
	public ELResolver getELResolver() {
		return elResolver;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public VariableMapper getVariableMapper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPropertyResolved() {

		boolean resolved = this.resolved;
		this.resolved = false;

		return resolved;
	}

	@Override
	public void setPropertyResolved(boolean resolved) {
		this.resolved = resolved;
	}
}
