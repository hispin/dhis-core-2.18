<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:organisationUnitGroup">
    <div class="organisationUnitGroup">
      <h2> <xsl:value-of select="@name" /> </h2>

      <table border="1">
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:organisationUnits"/>
    </div>
  </xsl:template>

  <xsl:template match="d:organisationUnits">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnits</h3>
      <table border="1" class="organisationUnits">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
