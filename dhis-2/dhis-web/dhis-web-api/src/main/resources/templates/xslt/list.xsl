<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:include href="identifiable-row.xsl"/>

  <!-- match all plural elements -->
  <xsl:template match="charts|dataElements|indicators|organisationUnits|categories|categoryCombos|categoryOptions|dataElementGroups|dataElementGroupSets">
    <xsl:variable name="elements" select="local-name()" />
    <h3>
      <xsl:value-of select="$elements"/>
    </h3>

    <table border="1">
        <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

</xsl:stylesheet>
