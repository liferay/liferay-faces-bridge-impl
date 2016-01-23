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

/**
 * Since the Liferay CDI Portlet Bridge has a dependency on the Liferay Portal logging API, this class has been copied
 * here from the Liferay Portal API so that CDI portlets can be tested in Pluto.
 *
 * @author  Brian Wing Shun Chan
 * @author  Neil Griffin
 */
public interface Log {

	public void debug(Object msg);

	public void debug(Throwable t);

	public void debug(Object msg, Throwable t);

	public void error(Object msg);

	public void error(Throwable t);

	public void error(Object msg, Throwable t);

	public void fatal(Object msg);

	public void fatal(Throwable t);

	public void fatal(Object msg, Throwable t);

	public void info(Object msg);

	public void info(Throwable t);

	public void info(Object msg, Throwable t);

	public void trace(Object msg);

	public void trace(Throwable t);

	public void trace(Object msg, Throwable t);

	public void warn(Object msg);

	public void warn(Throwable t);

	public void warn(Object msg, Throwable t);

	public boolean isDebugEnabled();

	public boolean isErrorEnabled();

	public boolean isFatalEnabled();

	public boolean isInfoEnabled();

	public boolean isTraceEnabled();

	public boolean isWarnEnabled();

}
