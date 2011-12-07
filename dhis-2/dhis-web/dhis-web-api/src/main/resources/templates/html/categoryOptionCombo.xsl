<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:categoryOptionCombo">
    <div class="categoryOptionCombo">
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
      </table>

      <xsl:apply-templates select="d:categoryOptions|d:categoryCombo"/>
    </div>
  </xsl:template>

  <xsl:template match="d:categoryCombo">
    <h3>CategoryCombo</h3>
    <table border="1" class="categoryCombo">
      <xsl:apply-templates select="child::*" mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:categoryOptions">
    <xsl:if test="count(child::*) > 0">
      <h3>CategoryOptions</h3>
      <table border="1" class="categoryOptions">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
