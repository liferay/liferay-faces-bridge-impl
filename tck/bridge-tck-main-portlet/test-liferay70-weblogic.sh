#!/bin/sh
mvn surefire-report:report -P tck,liferay,liferay70,weblogic
