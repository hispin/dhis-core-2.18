<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Nov 28, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Generic rendering of table rows of identifiable objects</xd:p>
        </xd:desc>
    </xd:doc>

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
    <xsl:template match="parent" mode="row">
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
