/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.faces.issue;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;

/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author Neil Griffin
 */
public class ApplicationCompat extends ApplicationWrapper {

	private Application application;

	public ApplicationCompat(Application application) {
		this.application = application;
	}

	@Override
	public Application getWrapped() {
		return application;
	}
}
