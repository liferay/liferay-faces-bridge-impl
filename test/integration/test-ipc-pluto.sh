#!/bin/sh
mvn verify -Dintegration.port=9080 -P selenium,pluto -Dit.test=\*IPC\*
