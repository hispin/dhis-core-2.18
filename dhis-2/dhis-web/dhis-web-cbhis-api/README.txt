NOTE: WORK IN PROGRESS

This module uses the jsr-311[1] implementation jersey[2] for building
a experimental example of what a web api for the CBHIS module of DHIS2
might look like.

Requests to paths under /api/cbhis/ will be attempted mapped to the
resource classes in the package org.hisp.dhis2.cbhis.api.resources.

The root resource handling /api/cbhis/ will resolve the logged in
users and return an xml (xml produced with JAXB[3]) of the orgunits linked with the user, linking
to urls for the org units activity plan.

An example:

Running this module with mvn jetty:run, assuming a user test_user with
password Trivandrum1 linked to the orgunit with id 2262 in the test
db, this request:

curl -u test_user:Trivandrum1 http://localhost:8080/api/cbhis/v0.1/

should give an xml with links to the orgunits activity plans, while

curl -u test_user:Trivandrum1 http://localhost:8080/api/cbhis/v0.1/orgunits/2262/activityplan

should result in an xml of the activity plan, and

curl -u test_user:Trivandrum1 http://localhost:8080/api/cbhis/v0.1/orgunits/2262/activityplan -H "Accept: applicat
ion/vnd.org.dhis2.casebased.v0.1.activityplan+serialized"

should return a serialized like openxdata uses.


[1] https://jsr311.dev.java.net/
[2] https://jersey.dev.java.net/
[3] http://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding
