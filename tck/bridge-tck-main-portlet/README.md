# Running the Liferay Faces Bridge TCK Tests

The Liferay Faces Bridge TCK tests can be run from the command line. The test framework expects that the TCK portlets have already been deployed to a running portal instance and have already been added to the appropriate pages.

Before running the tests from the command line, you must navigate into the `tck/bridge-tck-main-portlet` directory:

	cd tck/bridge-tck-main-portlet

The tests can be activated by using the `selenium` maven profile. To run all the tests (HTMLUnit and Liferay are the default browser and container properties respectively for tck tests):

	mvn verify -P selenium

Different browsers can be activated via the `chrome`, `firefox`, `phantomjs`, and `jbrowser` maven profiles. For example, to run the tests on Firefox:

	mvn verify -P selenium,firefox

**Note:** HTMLUnit and [JBrowser](https://github.com/MachinePublishers/jBrowserDriver) may fail to open web pages with complex JavaScript due to their experimental/buggy JavaScript support. PhantomJS is recommended for testing complex pages in a headless environment. Chrome (or the slightly slower Firefox) is recommended for testing complex pages in a normal desktop environment. See the root `pom.xml` file dependencies section for the required versions of each browser.

Single tests and groups of tests can be selected via the the `integration.test.filter` property. The `integration.test.filter` property is a regular expression which checked against TCK test names. Matching tests are run. For example, to run only the "Destroy" tests:

	mvn verify -P selenium -Dintegration.test.filter=.*Destroy.*

The tests can also be run on Pluto Portal with the `pluto` profile:

    mvn verify -P selenium,pluto

The `integration.port` property controls which port the browser will navigate to in order to run the tests. For example, if the portal is running on port `4000`, then the following command would be needed to test the portlets:

    mvn verify -P selenium -Dintegration.port=4000

All of the above properties and profiles can be combined to run tests in more complex scenarios. Here are some examples:

- Run the "Destroy" tests with Chrome against a Pluto Portal instance running on port 4000.

		mvn verify -P selenium,pluto,chrome -Dintegration.port=4000 -Dintegration.test.filter=.*Destroy.*

- Run the tests on Firefox against a Liferay 6.2 Portal instance:

		mvn verify -P selenium,liferay62,firefox

- Run the getRequestParameterNamesCoreTest test on PhantomJS against a Liferay Portal 7 instance:

		mvn verify -P selenium,phantomjs -Dintegration.test.filter=getRequestParameterNamesCoreTest
