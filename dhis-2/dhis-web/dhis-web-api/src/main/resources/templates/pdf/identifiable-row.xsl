<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns="http://www.w3.org/1999/xhtml">

  <xsl:template match="*" mode="row">
    <fo:block font-size="10pt" font-weight="bold" border-bottom="1pt solid black"><xsl:value-of select="@name" /></fo:block>

    <fo:table border-top="1pt solid black" margin-bottom="9pt" table-layout="fixed">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell><fo:block>ID</fo:block></fo:table-cell>
          <fo:table-cell><fo:block><xsl:value-of select="@id" /></fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell><fo:block>Code</fo:block></fo:table-cell>
          <fo:table-cell><fo:block><xsl:value-of select="@code" /></fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell><fo:block>Last Updated</fo:block></fo:table-cell>
          <fo:table-cell><fo:block><xsl:value-of select="@lastUpdated" /></fo:block></fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

  </xsl:template>

</xsl:stylesheet>
