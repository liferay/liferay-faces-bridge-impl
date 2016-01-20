#!/bin/sh
mvn surefire-report:report -P tck,liferay,liferay62,websphere
