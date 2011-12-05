<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:template match="map">
        <div class="map">
            <h2>Map <xsl:value-of select="@name"/></h2>
            <table border="1">
                <tr>
                    <td>Map rendering</td>
                    <td>
                        <a href="{@link}.png">PNG</a>
                    </td>
                </tr>
                <tr>
                    <td>ID</td>
                    <td>
                        <xsl:value-of select="@id"/>
                    </td>
                </tr>
                <tr>
                    <td>Last Updated</td>
                    <td>
                        <xsl:value-of select="@lastUpdated"/>
                    </td>
                </tr>
            </table>

        </div>
    </xsl:template>

</xsl:stylesheet>
