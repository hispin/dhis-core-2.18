<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:dataValueSets">


    <pre style="font-size: 0.9em;">
    <![CDATA[
The DataValueSet resource gives you a mean to POST dataValues into DHIS 2.

an optional phoneNumber query parameter can be provided and will override the organisation unit identifier of the
data value set, this will be matched against the phoneNumber for the users in the system, and the dataValues will
be added to their orgUnit.

The supported format is XML, and the root dataValueSet is declared with the following attributes

dataSet     - The UID of the dataset to report on
period      - The period to report on
orgUnit     - The UID of organisation unit to report on

The actual dataValues are sent with a format matching this:

<dataValues>
    <dataValue dataElement="dataElementUid" value="1" />
    <dataValue dataElement="dataElementUid" value="2" />
    <dataValue dataElement="dataElementUid" value="3" />
    <dataValue dataElement="dataElementUid" value="4" />
    <dataValue dataElement="dataElementUid" value="5" />
</dataValues>

So, a complete example would be:

<dataValueSet dataSet="dataSetUid" period="2012-01-01" orgUnit="orgUnitUid">
    <dataValues>
        <dataValue dataElement="dataElementUid" value="1" />
        <dataValue dataElement="dataElementUid" value="2" />
        <dataValue dataElement="dataElementUid" value="3" />
        <dataValue dataElement="dataElementUid" value="4" />
        <dataValue dataElement="dataElementUid" value="5" />
    </dataValues>
</dataValueSet>
    ]]>
    </pre>

  </xsl:template>

</xsl:stylesheet>
