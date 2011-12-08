<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:resources">
    <h1>Resources</h1>
    <ul class="resources">
      <xsl:for-each select="child::*">
        <li>
          <h2><xsl:value-of select="@name"/></h2>
          <xsl:variable name="link" select="@link"/>
          <table border="1">
          <tr>
            <xsl:for-each select="d:mediaTypes/d:mediaType">
                <td>
                  <xsl:choose>
                    <xsl:when test="text()='text/html'">
                      <xsl:element name="a">
                        <xsl:attribute name="href">
                          <xsl:value-of select="$link"/>
                        </xsl:attribute>
                        <xsl:text>text/html</xsl:text>
                      </xsl:element>
                    </xsl:when>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="text()='application/xml'">
                      <xsl:element name="a">
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat($link,'.xml')"/>
                        </xsl:attribute>
                        <xsl:text>application/xml</xsl:text>
                      </xsl:element>
                    </xsl:when>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="text()='application/json'">
                      <xsl:element name="a">
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat($link,'.json')"/>
                        </xsl:attribute>
                        <xsl:text>application/json</xsl:text>
                      </xsl:element>
                    </xsl:when>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="text()='application/javascript'">
                      <xsl:element name="a">
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat($link,'.jsonp')"/>
                        </xsl:attribute>
                        <xsl:text>application/javascript</xsl:text>
                      </xsl:element>
                    </xsl:when>
                    <xsl:when test="text()='application/pdf'">
                      <xsl:element name="a">
                        <xsl:attribute name="href">
                          <xsl:value-of select="concat($link,'.pdf')"/>
                        </xsl:attribute>
                        <xsl:text>application/pdf</xsl:text>
                      </xsl:element>
                    </xsl:when>
                  </xsl:choose>
                </td>
            </xsl:for-each>
          </tr>
          </table>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>

</xsl:stylesheet>