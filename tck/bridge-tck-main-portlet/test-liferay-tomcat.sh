#!/bin/sh
mvn -Dintegration.browser.headless=false verify -P selenium,liferay,chrome,tomcat "$@"
#mvn verify -P selenium,liferay,chrome,tomcat "$@"
