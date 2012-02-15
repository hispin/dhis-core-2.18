<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:dataValueSets">


    <pre style="font-size: 0.9em;">
    <![CDATA[
The DataValueSet resource gives you a mean to POST dataValues into DHIS 2.

An optional phoneNumber query parameter can be provided and will override the organisation unit identifier of the
data value set, this will be matched against the phoneNumber for the users in the system, and the dataValues will
be added to their orgUnit.

Period values are given in ISO format. A complete example of a data value set would be:

<dataValueSet xmlns="http://dhis2.org/schema/dxf/2.0" period="periodISODate" dataSet="dataSetID" orgUnit="orgUnitID">
  <dataValue dataElement="dataElementId" categoryOptionCombo="categoryOptionComboId" value="1" />
  <dataValue dataElement="dataElementId" categoryOptionCombo="categoryOptionComboId" value="2" />
  <dataValue dataElement="dataElementId" categoryOptionCombo="categoryOptionComboId" value="3" />
</dataValueSet>
    ]]>
    </pre>

  </xsl:template>

</xsl:stylesheet>
