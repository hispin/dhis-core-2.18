<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:organisationUnit">
    <div class="organisationUnit">
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
          <td>Short Name</td>
          <td> <xsl:value-of select="d:shortName" /> </td>
        </tr>
        <tr>
          <td>Opening Date</td>
          <td> <xsl:value-of select="d:openingDate" /> </td>
        </tr>
        <tr>
          <td>Level</td>
          <td> <xsl:value-of select="d:level" /> </td>
        </tr>
        <tr>
          <td>Active</td>
          <td> <xsl:value-of select="d:active" /> </td>
        </tr>
        <tr>
          <td>Current Parent</td>
          <td> <xsl:value-of select="d:currentParent" /> </td>
        </tr>
        <tr>
          <td>Has Patients</td>
          <td> <xsl:value-of select="d:hasPatients" /> </td>
        </tr>

      </table>

      <xsl:apply-templates select="d:parent|d:organisationUnitGroups|d:dataSets" mode="short" />

    </div>
  </xsl:template>
  
  <xsl:template match="d:organisationUnits" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnits</h3>
      <table border="1">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="d:parent" mode="short">
    <h3>Parent OrganisationUnit</h3>
    <table border="1">
      <xsl:apply-templates select="." mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:dataSets">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table border="1" class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
