<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:dashboard">
    <div class="dashboard">
      <h2> <xsl:value-of select="@name" /> </h2>

      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="@created" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Item Count</td>
          <td> <xsl:value-of select="d:itemCount" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:dashboardItems|d:user" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:dashboardItems" mode="short">
    <h3>Dashboard Items</h3>
    <xsl:apply-templates select="d:dashboardItem" mode="short" />
  </xsl:template>

  <xsl:template match="d:dashboardItem" mode="short">
    <table>
      <tr>
        <td>Type</td>
        <td><xsl:value-of select="d:type" /></td>
      </tr>
    </table>

    <xsl:apply-templates select="d:users|d:documents|d:reportTables|d:reports" mode="short" />
  </xsl:template>

</xsl:stylesheet>
