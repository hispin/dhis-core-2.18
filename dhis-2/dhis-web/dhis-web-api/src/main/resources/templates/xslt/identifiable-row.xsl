<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >
    
    <xsl:template match="*" mode="row">
        <xsl:variable name="root" select="concat('/api/',local-name(.),'s')" />
        <tr>
            <td><xsl:value-of select="@name"/></td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href"><xsl:value-of select="concat($root,'/',@id)"/></xsl:attribute>
                    <xsl:text>html</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href"><xsl:value-of select="concat($root,'/',@id,'.xml')"/></xsl:attribute>
                    <xsl:text>xml</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href"><xsl:value-of select="concat($root,'/',@id,'.json')"/></xsl:attribute>
                    <xsl:text>json</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href"><xsl:value-of select="concat($root,'/',@id,'.jsonp')"/></xsl:attribute>
                    <xsl:text>jsonp</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href"><xsl:value-of select="concat($root,'/',@id,'.pdf')"/></xsl:attribute>
                    <xsl:text>pdf</xsl:text>
                </xsl:element>
            </td>
        </tr>
    </xsl:template>
    
    <!-- special case - TODO handle this better -->
    <xsl:template match="d:parent" mode="row">
        <xsl:variable name="root">/api/organisationUnits</xsl:variable>
        <tr>
            <td>
                <xsl:value-of select="@name"/>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="concat($root,'/',@id)"/>
                    </xsl:attribute>
                    <xsl:text>html</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="concat($root,'/',@id,'.xml')"/>
                    </xsl:attribute>
                    <xsl:text>xml</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="concat($root,'/',@id,'.json')"/>
                    </xsl:attribute>
                    <xsl:text>json</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="concat($root,'/',@id,'.jsonp')"/>
                    </xsl:attribute>
                    <xsl:text>jsonp</xsl:text>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="concat($root,'/',@id,'.pdf')"/>
                    </xsl:attribute>
                    <xsl:text>pdf</xsl:text>
                </xsl:element>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
