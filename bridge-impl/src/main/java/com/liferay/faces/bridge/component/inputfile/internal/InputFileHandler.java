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
package com.liferay.faces.bridge.component.inputfile.internal;

import java.lang.reflect.Method;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

import com.liferay.faces.bridge.component.inputfile.InputFile;
import com.liferay.faces.bridge.event.FileUploadEvent;
import com.liferay.faces.util.view.facelets.MethodMetadata;


/**
 * @author  Neil Griffin
 */
public class InputFileHandler extends ComponentHandler {

	// Private Constants
	private static final String FILE_UPLOAD_LISTENER = "fileUploadListener";

	public InputFileHandler(ComponentConfig componentConfig) {
		super(componentConfig);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset metaRuleset = super.createMetaRuleset(type);
		metaRuleset.addRule(new HtmlInputFileMethodRule());

		return metaRuleset;
	}

	private static class HtmlInputFileMethodRule extends MetaRule {

		@Override
		public Metadata applyRule(String name, TagAttribute tagAttribute, MetadataTarget metadataTarget) {

			Metadata metadata = null;

			if ((metadataTarget != null) && (metadataTarget.isTargetInstanceOf(InputFile.class))) {

				if (FILE_UPLOAD_LISTENER.equals(name)) {
					Method writeMethod = metadataTarget.getWriteMethod(name);
					Class<?>[] args = new Class[] { FileUploadEvent.class };
					metadata = new MethodMetadata(tagAttribute, writeMethod, args);
				}
			}

			return metadata;
		}
	}
}
