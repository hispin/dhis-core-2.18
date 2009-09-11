@echo off

if not "%JAVA_HOME%" == "" goto startup
:abort
echo DHIS 2 requires a Java Runtime Environment to be installed
pause
goto end
:startup
echo Starting DHIS 2...
java -jar dhis2-lite.jar
goto end
:end
