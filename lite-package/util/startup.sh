#!/bin/sh

#if [ "${JAVA_HOME:+1}" = "1" ]
if true
then
  echo "Starting DHIS 2..."
  java -jar dhis2-lite.jar
else
  echo "DHIS 2 requires a Java Runtime Environment to be installed"
fi
