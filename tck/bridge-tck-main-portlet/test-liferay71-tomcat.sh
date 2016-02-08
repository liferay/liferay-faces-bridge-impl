#!/bin/sh
mvn surefire-report:report -P tck,liferay,liferay71,tomcat
