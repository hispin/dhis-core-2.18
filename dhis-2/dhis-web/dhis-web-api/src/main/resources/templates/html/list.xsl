<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:include href="identifiable-row.xsl"/>

  <!-- match all plural elements -->
  <xsl:template match="d:maps|d:charts|d:categories|d:categoryCombos|
    d:categoryOptions|d:categoryOptionCombos|d:dataElements|d:indicators|
    d:organisationUnits|d:dataElementGroups|d:dataElementGroupSets|
    d:indicatorGroups|d:indicatorGroupSets|d:organisationUnitGroups|
    d:organisationUnitGroupSets|d:indicatorTypes|d:attributeTypes|d:reports|
    d:sqlViews|d:validationRules|d:validationRuleGroups">
    <h3> <xsl:value-of select="local-name()"/> </h3>

    <table border="1">
      <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

</xsl:stylesheet>
