<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:dataValueSets">


    <pre style="font-size: 0.9em;">
    <![CDATA[
The DataValueSet resource gives you a mean to POST data values into DHIS 2.

Period values are given in ISO format. A complete example of a data value set:

<dataValueSet xmlns="http://dhis2.org/schema/dxf/2.0" period="periodISODate" dataSet="dataSetID" orgUnit="orgUnitID">
  <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="1" />
  <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="2" />
  <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="3" />
</dataValueSet>
    ]]>
    </pre>

  </xsl:template>

</xsl:stylesheet>
