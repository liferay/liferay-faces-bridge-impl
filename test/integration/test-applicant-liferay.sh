#!/bin/sh
mvn verify -P selenium,liferay -Dit.test='!ADF*ApplicantPortletTester,*Applicant*' -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Alloy\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Prime\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Ice\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Boots\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Butter\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*Applicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*JSFApplicant\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*ADF\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*CDI\* -Dintegration.browser.headless=false
