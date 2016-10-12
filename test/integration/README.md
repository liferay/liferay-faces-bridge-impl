# Running the Liferay Faces Bridge Integration Tests

The Liferay Faces Bridge integration tests can be run from an IDE (such as Eclipse) or the command line. The test framework expects that the tests have already been deployed to a running portal instance and have already been added to the appropriate pages.

Before running the tests from the command line, you must navigate into the `test/integration` directory:

    cd test/integration

The tests can be activated by using the `integration` maven profile. To run all the tests:

    mvn test -P integration

Different browsers can be activated via the `chrome`, `firefox`, and `phantomjs` maven profiles. For example, to run the tests on Firefox:

    mvn test -P integration,firefox

Single tests and groups of tests can be selected via the the `test` property. The `test` property uses wildcards to select tests from their fully qualified class names. For example, to run only the issue portlet tests:

    mvn test -P integration -Dtest=*issue.*Test*

Likewise, `-Dtest=*Applicant*` would run only the applicant portlet tests, and `-Dtest=*demo.JSF*` would run only the non-applicant demo portlet tests.

The tests can also be run on Pluto Portal with the `pluto` profile:

    mvn test -P integration,pluto

The log level of the tests can be configured to be more or less verbose with the `integration.log.level` property:

    mvn test -P integration -Dintegration.log.level=INFO

The `integration.port` property controls which port the browser will navigate to in order to run the tests. For example, if the portal is running on port `4000`, then the following command would be needed to test the portlets:

    mvn test -P integration -Dintegration.port=4000

All of the above properties and profiles can be combined to run tests in more complex scenarios. Here are some examples:

- Run the issue tests with Chrome against a Pluto Portal instance running on port 4000.

        mvn test -P integration,pluto,chrome -Dintegration.port=4000 -Dtest=*issue.*Test*

- Run the tests on Firefox with more verbose logs:

        mvn test -P integration,firefox -Dintegration.log.level=INFO

- Run the FACES-1635 test on Chrome:

        mvn test -P integration,chrome -Dtest=*FACES_1635*
