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
package com.liferay.faces.demos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.liferay.faces.demos.dto.Title;


/**
 * @author  Neil Griffin
 */
@Named("titleService")
@ApplicationScoped
public class TitleServiceMockImpl implements TitleService {

	// Private Data Members
	private List<Title> titles;

	@Override
	public List<Title> getTitles() {
		return titles;
	}

	@PostConstruct
	public void postConstruct() {
		titles = new ArrayList<Title>();
		titles.add(new Title(1, "Mr."));
		titles.add(new Title(2, "Mrs."));
		titles.add(new Title(3, "Ms."));
		titles.add(new Title(4, "Dr."));
		titles = Collections.unmodifiableList(titles);
	}

}
