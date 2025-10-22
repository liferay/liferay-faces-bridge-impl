#!/bin/sh
mvn verify -P selenium-alloy-showcase,liferay -Dintegration.browser.headless=false
#mvn verify -P selenium-alloy-showcase,liferay -Dit.test=*InputFileGeneralTester -Dintegration.browser.headless=false
