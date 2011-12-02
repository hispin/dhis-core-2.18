<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" />
  <xsl:template match="/">
    <fo:root>
    <fo:layout-master-set>
      <fo:simple-page-master master-name="A4">
        <fo:region-body margin="1in" />
      </fo:simple-page-master>
    </fo:layout-master-set>

      <fo:page-sequence master-reference="A4">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="20pt" border-bottom="2pt solid black" margin-bottom="20pt">DataElements</fo:block>

          <xsl:apply-templates />

        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

<xsl:template match="dataElements">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="dataElement">
<fo:block font-size="12pt" font-weight="bold" border-bottom="1pt solid black"><xsl:value-of select="@name" /></fo:block>
<fo:table border-top="1pt solid black" margin-bottom="10pt" table-layout="fixed">
  <fo:table-body>
    <fo:table-row>
      <fo:table-cell><fo:block>ShortName</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="@shortName" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>LastUpdated</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="@lastUpdated" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>Active</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="active" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>AggregationOperator</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="aggregationOperator" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>DomainType</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="domainType" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>SortOrder</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="sortOrder" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>Type</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="type" /></fo:block></fo:table-cell>
    </fo:table-row>
    <fo:table-row>
      <fo:table-cell><fo:block>ZeroIsSignificant</fo:block></fo:table-cell>
      <fo:table-cell><fo:block><xsl:value-of select="zeroIsSignificant" /></fo:block></fo:table-cell>
    </fo:table-row>
  </fo:table-body>
</fo:table>
</xsl:template>

</xsl:stylesheet>
