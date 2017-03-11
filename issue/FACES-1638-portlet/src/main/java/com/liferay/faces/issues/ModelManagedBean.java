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
package com.liferay.faces.issues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


/**
 * @author  Neil Griffin
 */
@ManagedBean
@ViewScoped
public class ModelManagedBean implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 2958957985108536731L;

	private List<Item> items;

	public List<Item> getItems() {

		if (items == null) {
			items = new ArrayList<Item>();
			items.add(new Item(1, "First-Item"));
			items.add(new Item(2, "Second-Item"));
			items.add(new Item(3, "Third-Item"));
		}

		return items;
	}

}
