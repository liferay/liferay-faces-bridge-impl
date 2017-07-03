#!/bin/sh
mvn verify -P selenium,liferay62,chrome,weblogic "$@"
