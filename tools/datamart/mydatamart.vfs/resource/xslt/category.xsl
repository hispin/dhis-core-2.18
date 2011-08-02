<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" xmlns:func="http://exslt.org/functions"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Processes categoryoptioncombo nodes</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:key name="categoryname" match="/dxf:dxf/dxf:categories/dxf:category/dxf:name"
        use="../dxf:id"/>
    <xsl:key name="category"
        match="/dxf:dxf/dxf:categoryCategoryOptionAssociations/dxf:categoryCategoryOptionAssociation/dxf:category"
        use="../dxf:categoryOption"/>
    
    <!--    
        For each categoryOptionCombo:
        1.  insert entry categoryoptioncomboname
        3.  insert entry in category structure table
    -->
    <xsl:template match="dxf:categoryOptionCombo">
        <xsl:variable name="cats" select="dxf:categoryOptions/dxf:categoryOption/dxf:name"/>
        <xsl:variable name="name">
            <xsl:for-each select="$cats">
                <xsl:value-of select="."/>,<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="length" select="string-length($name) - 1"/>
        <xsl:text/>INSERT OR REPLACE INTO _categoryoptioncomboname VALUES (<xsl:value-of
            select="dxf:id"/>,'<xsl:value-of select="concat('[', substring($name,1,$length), ']')"
        />'); <!--
        Populate category structure table
    -->
        <xsl:variable name="fields"> categoryoptioncomboid,<xsl:for-each
                select="dxf:categoryOptions/dxf:categoryOption">
                <xsl:value-of select="dhis:dbencode(key('categoryname',key('category',dxf:id)))"
                />,<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="values">
            <xsl:value-of select="dxf:id"/>,<xsl:for-each
                select="dxf:categoryOptions/dxf:categoryOption">
                <xsl:text/>'<xsl:value-of select="dhis:dbescape(dxf:name)"/>',<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="dhis:insert('_categorystructure', $fields, $values)"
        /><xsl:text>
        </xsl:text>
    </xsl:template>

</xsl:stylesheet>
