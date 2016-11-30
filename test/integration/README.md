# Running the Liferay Faces Bridge Integration Tests

The Liferay Faces Bridge integration tests can be run from an IDE (such as Eclipse) or the command line. The test framework expects that the tests have already been deployed to a running portal instance and have already been added to the appropriate pages.

Before running the tests from the command line, you must navigate into the `test/integration` directory:

	cd test/integration

The tests can be activated by using the `integration` maven profile. To run all the tests:

	mvn verify -P selenium

Different browsers can be activated via the `chrome`, `firefox`, and `phantomjs` maven profiles. For example, to run the tests on Firefox:

	mvn verify -P selenium,liferay,firefox

Single tests and groups of tests can be selected via the the `it.test` property. The `it.test` property uses wildcards to select tests from their fully qualified class names. For example, to run only the issue portlet tests:

	mvn verify -P selenium,liferay -Dit.test=*issue.*Test*

Unfortunately the bridge tests will be executed twice when run with the `it.test` property since there are multiple `maven-failsafe-plugin` `<execution>` elements. To avoid this behavior, you can temporarily comment out the `alloy-showcase-selenium-tests` `<execution>` in the `pom.xml` file.

Likewise, `-Dit.test=*Applicant*` would run only the applicant portlet tests, and `-Dit.test=*demo.JSF*` would run only the non-applicant demo portlet tests.

The tests can also be run on Pluto Portal with the `pluto` profile:

    mvn verify -P selenium,pluto

The log level of the tests can be configured to be more or less verbose with the `integration.log.level` property:

    mvn verify -P selenium,liferay -Dintegration.log.level=INFO

The `integration.port` property controls which port the browser will navigate to in order to run the tests. For example, if the portal is running on port `4000`, then the following command would be needed to test the portlets:

    mvn verify -P selenium,liferay -Dintegration.port=4000

All of the above properties and profiles can be combined to run tests in more complex scenarios. Here are some examples:

- Run the issue tests with Chrome against a Pluto Portal instance running on port 4000.
 
		mvn verify -P selenium,pluto,chrome -Dintegration.port=4000 -Dit.test=*issue.*Test*

- Run the tests on Firefox with more verbose logs:

		mvn verify -P selenium,liferay,firefox -Dintegration.log.level=INFO

- Run the FACES-1635 test on Chrome:

		mvn verify -P selenium,liferay,chrome -Dit.test=*FACES_1635* 
