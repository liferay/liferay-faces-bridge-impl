#!/bin/sh
>&2 echo "NOTE: There are test failures in the bootsfaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-3570"
>&2 echo "NOTE: There are test failures in the butterfaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-3573"
>&2 echo "NOTE: There are test failures in the icefaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-2867"
>&2 echo "NOTE: There are test failures in the richfaces-applicant-portlet due to https://issues.liferay.com/browse/FACES-3571"
#mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test=\*JSFApplicant\* -Dintegration.browser.headless=false
#mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test='*Boots*Applicant*'
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test='!ADF*ApplicantPortletTester,!*Boots*Applicant*,!*IceFaces*Applicant*,!*RichFaces*Applicant*,*Applicant*'
