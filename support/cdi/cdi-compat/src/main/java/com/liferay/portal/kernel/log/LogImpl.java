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
package com.liferay.portal.kernel.log;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Since the Liferay CDI Portlet Bridge has a dependency on the Liferay Portal logging API, this class is necessary to
 * provide a compatibility layer with the Liferay Faces logging API.
 *
 * @author  Neil Griffin
 */
public class LogImpl implements Log {

	// Private Data Members
	private Logger logger;

	public LogImpl(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
	}

	public LogImpl(String className) {
		logger = LoggerFactory.getLogger(className);
	}

	public void debug(Object msg) {
		logger.debug(msg.toString());
	}

	public void debug(Throwable t) {
		logger.debug(t.getMessage(), t);
	}

	public void debug(Object msg, Throwable t) {
		logger.debug(msg.toString(), t);
	}

	public void error(Object msg) {
		logger.debug(msg.toString());
	}

	public void error(Throwable t) {
		logger.error(t);
	}

	public void error(Object msg, Throwable t) {
		logger.error(msg.toString(), t);
	}

	public void fatal(Object msg) {
		logger.error(msg.toString());
	}

	public void fatal(Throwable t) {
		logger.error(t);
	}

	public void fatal(Object msg, Throwable t) {
		logger.error(t);
	}

	public void info(Object msg) {
		logger.info(msg.toString());
	}

	public void info(Throwable t) {
		logger.info(t.getMessage());
	}

	public void info(Object msg, Throwable t) {
		logger.info(msg.toString(), t);
	}

	public void trace(Object msg) {
		logger.trace(msg.toString());
	}

	public void trace(Throwable t) {
		logger.trace(t.getMessage());
	}

	public void trace(Object msg, Throwable t) {
		logger.trace(msg.toString());
	}

	public void warn(Object msg) {
		logger.warn(msg.toString());
	}

	public void warn(Throwable t) {
		logger.warn(t.getMessage());
	}

	public void warn(Object msg, Throwable t) {
		logger.warn(msg.toString());
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return logger.isErrorEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

}
