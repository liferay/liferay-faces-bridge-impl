# Checklist For Adding an Issue Tester Portlet

If your issue is related to PrimeFaces, add it to the primefaces-issues-portlet. If your issue is related to the bridge
or one of the JSF implementations, add it to the jsf-issues-portlet. If your issue is related to another component
suite, let us know and we'll consider adding another issues portlet for that component suite.

1. Add whatever views are necessary for your portlet. If your portlet only requires one view, add it to
`src/main/webapp/WEB-INF/views`. The file name should be the issue id followed by ".xhtml". For example, the single 
view required to test FACES-1478 was added as `src/main/webapp/WEB-INF/views/FACES-1478.xhtml`. If your portlet requires
multiple views, create a folder under `src/main/webapp/WEB-INF/views`. The folder name should be the issue id. Add all
the views to the new folder. For example, since multiple views are needed to test FACES-1470, all views were added under
`src/main/webapp/WEB-INF/views/FACES-1470`. Please make sure to include detailed instructions in your view(s) so that
testers can reproduce the results.

2. Add whatever resources are necessary for your portlet. Create a folder under `src/main/webapp/WEB-INF/resources`. The
folder name should be the issue id. Add all the resources to the new folder. For example, since resources are needed to
test FACES-1618, resources were added under `src/main/webapp/WEB-INF/resources/FACES-1618`.

3. Add whatever beans are necessary for your portlet. If your portlet only requires one bean, add it to
`src/main/java/com/liferay/faces/issue`. The file name should be the issue id (with dashes replaced with underscores)
followed by "Bean.java". For example, the single bean required to test FACES-1478 was added as
`src/main/java/com/liferay/faces/issue/FACES_1478Bean.java`. If your portlet requires multiple beans, create a package
under `src/main/java/com/liferay/faces/issue`. The package name should be the issue id (with dashes replaced with
underscores). Add all the beans to the new package. For example, since multiple beans are needed to test FACES-1470, all
beans were added under `src/main/java/com/liferay/faces/issue/FACES_1470`.

4. Add the `<portlet>` to the `src/main/webapp/WEB-INF/portlet.xml`, and add a `javax.portlet.faces.defaultViewId.view`
for the portlet. Examples can be found in the `portlet.xml`.

5. Add the `<portlet>` to the `src/main/webapp/WEB-INF/liferay-portlet.xml`. Examples can be found in the
`liferay-portlet.xml`.

6. Add a `<page>` for the portlet to the
`tck/bridge-tck-harness/src/main/resources/tomcat_pluto/pluto-portal-driver-config.xml` (please add the page even if the
portlet should not be tested on Pluto). Examples can be found in the `pluto-portal-driver-config.xml`.

7. Optionally, add a selenium tester under
`$LIFERAY_FACES_BRIDGE_IMPL_REPO_HOME/test/integration/src/test/java/com/liferay/faces/bridge/test/integration/issue/`.
The file name should be the issue id (with dashes replaced with underscores) followed by "PortletTester.java". For
example, the tester for FACES-1478 was added as
`$LIFERAY_FACES_BRIDGE_IMPL_REPO_HOME/test/integration/src/test/java/com/liferay/faces/bridge/test/integration/issue/FACES_1478PortletTester.java`.
The tester class should extend `BrowserDriverManagingTesterBase` and follow the patterns of other issue tester classes.
