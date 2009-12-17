#!/bin/sh

DHIS2_LIVE_HOME=~/development/dhis2-live
DHIS2_SOURCE_HOME=~/development/src/dhis2

rm -rf ${DHIS2_LIVE_HOME}/webapps/dhis
mkdir ${DHIS2_LIVE_HOME}/webapps/dhis
unzip ${DHIS2_SOURCE_HOME}/dhis-2/dhis-web/dhis-web-portal/target/dhis.war -d ${DHIS2_LIVE_HOME}/webapps/dhis
java -jar ${DHIS2_LIVE_HOME}/dhis2-live.jar

