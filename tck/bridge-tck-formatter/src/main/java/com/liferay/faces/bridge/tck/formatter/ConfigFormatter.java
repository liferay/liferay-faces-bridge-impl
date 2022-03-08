/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author  Neil Griffin
 */
public class ConfigFormatter {

	// Private Constants
	private static final DecimalFormat LEADING_ZEROS_FORMAT = new DecimalFormat("#000");

	public static void main(String[] args) {

		try {
			String liferayPropertiesPath = "../bridge-tck-main-portlet/src/test/resources/liferay-tests.xml";
			List<Entry> liferayEntries = parsePropertiesFile(liferayPropertiesPath);
			Collections.sort(liferayEntries, EntryComparator.INSTANCE);

			String plutoPropertiesPath = "../bridge-tck-main-portlet/src/test/resources/pluto-tests.xml";
			List<Entry> plutoEntries = parsePropertiesFile(plutoPropertiesPath);
			Collections.sort(plutoEntries, EntryComparator.INSTANCE);

			if (liferayEntries.size() != plutoEntries.size()) {
				throw new IOException("liferay-tests.xml and pluto-tests.xml do not have the same number of tests");
			}
			else if (!liferayEntries.equals(plutoEntries)) {
				throw new IOException(
					"liferay-tests.xml and pluto-tests.xml do not have the same list of portlet names");
			}

			writePropertiesFile(liferayPropertiesPath, liferayEntries, null);

			String comments = "<!--\n" + "NOTE:\n" +
				"* Many event-based tests are disabled because they are not compatible with Pluto's\n" +
				"* implementation of Events IPC. The setRequestCharacterEncodingActionTest is disabled\n" +
				"* because of a known bug in Pluto (according to the TCK User's Guide). The\n" +
				"* scopeNotRestoredResourceTest is disabled because the Pluto ResourceURL#toString()\n" +
				"* method automatically adds public render parameters to ResourceURLs (which includes\n" +
				"* the public render parameter for the bridgeRequestScopeId).\n" + "-->\n";

			writePropertiesFile(plutoPropertiesPath, plutoEntries, comments);
			updatePlutoPortalDriverConfigFile(liferayEntries);
			updateLiferayPortletXmlFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void moveFile(String sourcePath, String destinationPath) throws IOException {

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = new FileInputStream(sourcePath);
			outputStream = new FileOutputStream(destinationPath);

			int length;
			byte[] buffer = new byte[1024];

			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
		}
		finally {

			if (inputStream != null) {
				inputStream.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}
		}

		File sourceFile = new File(sourcePath);
		sourceFile.delete();
	}

	private static List<Entry> parsePropertiesFile(String filePath) throws IOException {

		List<Entry> entries = new ArrayList<Entry>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

		String curLine;
		boolean multiLineComment = false;
		boolean singleLineComment = false;

		while ((curLine = bufferedReader.readLine()) != null) {

			curLine = curLine.trim();

			if (curLine.contains("<!--") && curLine.contains("-->")) {

				if (curLine.contains("AVAILABLE TEST SLOT")) {

					// Ignore
					curLine = "";
				}
				else if (curLine.startsWith("<!--") && curLine.endsWith("-->")) {
					singleLineComment = true;
				}
			}
			else if (curLine.contains("<!--")) {
				multiLineComment = true;
			}
			else if (curLine.contains("-->")) {
				multiLineComment = false;
			}

			if (curLine.contains("entry")) {
				entries.add(new Entry(multiLineComment || singleLineComment, curLine));
			}

			singleLineComment = false;
		}

		return entries;
	}

	private static void updateLiferayPortletXmlFile() throws IOException {

		String inputFilePath = "../bridge-tck-main-portlet/src/main/webapp/WEB-INF/portlet.xml";
		String outputFilePath = "../bridge-tck-main-portlet/src/main/webapp/WEB-INF/liferay-portlet.xml";

		Writer writer = new FileWriter(outputFilePath);

		writer.write("<?xml version=\"1.0\"?>\n");
		writer.write("<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//DTD Portlet Application ");

		String liferayVersionDTD = System.getProperty("liferay.version.dtd");
		String[] versionParts = liferayVersionDTD.split("[.]");
		int liferayMajorVersion = Integer.valueOf(versionParts[0]);
		int liferayMinorVersion = Integer.valueOf(versionParts[1]);
		writer.write(liferayVersionDTD);
		writer.write(".0//EN\" ");
		writer.write("\"http://www.liferay.com/dtd/liferay-portlet-app_");
		writer.write(liferayVersionDTD.replaceAll("[.]", "_"));
		writer.write("_0.dtd\">\n");
		writer.write("<liferay-portlet-app>\n");

		BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));
		String curLine;

		while ((curLine = bufferedReader.readLine()) != null) {

			String portletName = curLine.trim();

			if (portletName.startsWith("<portlet-name>")) {
				portletName = portletName.substring("<portlet-name>".length());

				if (portletName.endsWith("</portlet-name>")) {
					portletName = portletName.substring(0, portletName.indexOf("</portlet-name>"));
				}

				writer.write("\t<portlet>\n");
				writer.write("\t\t<portlet-name>");
				writer.write(portletName);
				writer.write("</portlet-name>\n");
				writer.write("\t\t<requires-namespaced-parameters>false</requires-namespaced-parameters>\n");

				if ((liferayMajorVersion > 7) || ((liferayMajorVersion == 7) && (liferayMinorVersion > 0))) {
					writer.write("\t\t<header-request-attribute-prefix>");
					writer.write("com.liferay.faces.bridge.tck");
					writer.write("</header-request-attribute-prefix>\n");
				}

				writer.write("\t</portlet>\n");
			}
		}

		writer.write("\t<role-mapper>\n");
		writer.write("\t\t<role-name>administrator</role-name>\n");
		writer.write("\t\t<role-link>Administrator</role-link>\n");
		writer.write("\t</role-mapper>\n");
		writer.write("\t<role-mapper>\n");
		writer.write("\t\t<role-name>guest</role-name>\n");
		writer.write("\t\t<role-link>Guest</role-link>\n");
		writer.write("\t</role-mapper>\n");
		writer.write("\t<role-mapper>\n");
		writer.write("\t\t<role-name>power-user</role-name>\n");
		writer.write("\t\t<role-link>Power User</role-link>\n");
		writer.write("\t</role-mapper>\n");
		writer.write("\t<role-mapper>\n");
		writer.write("\t\t<role-name>user</role-name>\n");
		writer.write("\t\t<role-link>User</role-link>\n");
		writer.write("\t</role-mapper>\n");
		writer.write("</liferay-portlet-app>\n");

		writer.flush();
		writer.close();

		System.out.println("Updated: " + outputFilePath);
	}

	private static void updatePlutoPortalDriverConfigFile(List<Entry> entries) throws IOException {

		String inputFilePath = "../bridge-tck-harness/src/main/resources/tomcat_pluto/pluto-portal-driver-config.xml";
		String outputFilePath = inputFilePath + ".txt";
		Writer writer = new FileWriter(outputFilePath);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));
		String curLine;

		boolean ignore = false;

		while ((curLine = bufferedReader.readLine()) != null) {

			if (curLine.contains("<!-- Bridge TCK -->")) {

				writer.write(curLine);
				writer.write("\n");

				ignore = true;

				for (int i = 0; i < entries.size(); i++) {

					Entry entry = entries.get(i);
					writer.write("\t\t<page name=\"TestPage");
					writer.write(LEADING_ZEROS_FORMAT.format(i + 1));
					writer.write("\" uri=\"/WEB-INF/themes/pluto-default-theme.jsp\">\n");

					if (entry.getPortletName().contains("body2")) {
						String portletName = entry.getPortletName();
						portletName = portletName.replaceAll("body2", "body1");
						writer.write("\t\t\t<portlet context=\"");
						writer.write(entry.getContext());
						writer.write("\" name=\"");
						writer.write(portletName);
						writer.write("\"/>\n");
					}

					writer.write("\t\t\t<portlet context=\"");
					writer.write(entry.getContext());
					writer.write("\" name=\"");
					writer.write(entry.getPortletName());
					writer.write("\"/>\n");
					writer.write("\t\t</page>\n");
				}
			}
			else if (curLine.contains("</render-config>")) {
				ignore = false;
			}

			if (!ignore) {
				writer.write(curLine);
				writer.write("\n");
			}
		}

		writer.flush();
		writer.close();

		moveFile(outputFilePath, inputFilePath);

		System.out.println("Updated: " + inputFilePath);
	}

	private static void writePropertiesFile(String filePath, List<Entry> entries, String comments) throws IOException {

		Writer writer = new FileWriter(filePath);
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");

		if (comments != null) {
			writer.write(comments);
		}

		writer.write("<properties>\n");

		for (int i = 0; i < entries.size(); i++) {
			Entry entry = entries.get(i);
			writer.write("\t<entry key=\"TestPage");
			writer.write(LEADING_ZEROS_FORMAT.format(i + 1));
			writer.write("\">");
			writer.write(entry.getPortletName());
			writer.write("|");
			writer.write(String.valueOf(entry.isEnabled()));
			writer.write("|");
			writer.write(entry.getContext());
			writer.write("</entry>\n");
		}

		writer.write("</properties>\n");
		writer.flush();
		writer.close();

		System.out.println("Updated: " + filePath);
	}

	private static class Entry {

		private String context;
		private boolean enabled;
		private String portletName;

		public Entry(boolean commented, String text) {

			this.context = "/com.liferay.faces.test.bridge.tck.main.portlet";
			this.enabled = !commented;

			String[] tokens = text.split("[ =<>/\"|]");

			for (String token : tokens) {

				if (token.startsWith("chapter")) {
					this.portletName = token;
				}
				else if (token.equals("true") && !commented) {
					this.enabled = true;
				}
				else if (token.equals("false") && !commented) {
					this.enabled = false;
				}
				else if (token.startsWith("com.liferay.faces.test.bridge.tck")) {
					this.context = "/" + token;
				}
			}
		}

		@Override
		public boolean equals(Object obj) {

			Entry entry = (Entry) obj;

			boolean equals = portletName.equals(entry.getPortletName());

			if (!equals) {
				System.err.println("\"" + portletName + "\" != \"" + entry.getPortletName() + "\"");
			}

			return equals;
		}

		public String getContext() {
			return context;
		}

		public String getPortletName() {
			return portletName;
		}

		public boolean isEnabled() {
			return enabled;
		}
	}

	private static class EntryComparator implements Comparator<Entry> {

		public static final EntryComparator INSTANCE = new EntryComparator();

		@Override
		public int compare(Entry entry1, Entry entry2) {

			String entry1PortletName = entry1.getPortletName();
			String entry2PortletName = entry2.getPortletName();

			if (entry2PortletName.contains("bridgeVersionTest")) {
				return 1;
			}
			else {

				return entry1PortletName.compareTo(entry2PortletName);
			}
		}
	}
}
