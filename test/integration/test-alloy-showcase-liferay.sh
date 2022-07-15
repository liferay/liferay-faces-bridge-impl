#!/bin/sh
mvn verify -P selenium-alloy-showcase,liferay
#mvn verify -P selenium-alloy-showcase,liferay -Dit.test=\*OutputStylesheet\* -Dintegration.browser.headless=false
