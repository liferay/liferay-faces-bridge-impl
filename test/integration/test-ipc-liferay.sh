#!/bin/sh
mvn verify -P selenium,liferay -Dit.test=\*IPC\* -Dintegration.browser.headless=false
#mvn verify -P selenium,liferay -Dit.test=\*IPC\*
