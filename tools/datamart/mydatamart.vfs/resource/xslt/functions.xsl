<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" 
    xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" 
    xmlns:func="http://exslt.org/functions"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>exslt functions</xd:p>
        </xd:desc>
    </xd:doc>


    <!--  Try and reasonably sanitize strings used for field names (not perfect) -->
    <!--  TODO:  use concept for this-->
    <func:function name="dhis:dbencode">
        <xsl:param name="raw"/>
        <xsl:variable name="tick">'</xsl:variable>
        <xsl:variable name="clean1"            
            select="translate($raw,' .,-+:!','_______')" />
        <xsl:variable name="clean2"            
            select="translate($clean1,'/()','')" />
        <xsl:variable name="clean3" select="str:replace($clean2,'&lt;','_lt_')"/>
        <xsl:variable name="clean4" select="str:replace($clean3,'&gt;','_gt_')"/>
        <xsl:variable name="clean5" select="str:replace($clean4,'&amp;','_and_')"/>
        <xsl:variable name="clean6" select="str:replace($clean5,$tick,'')"/>
        <xsl:variable name="clean7"
            select="translate($clean6, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
        <xsl:variable name="clean8">
            <xsl:choose>
                <xsl:when test="string(number(substring($clean7,1,1)))!='NaN'"><xsl:value-of select="concat('_',$clean7)"/></xsl:when>
                <xsl:when test="$clean7='default'">_default</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$clean7"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <func:result>
            <xsl:value-of select="$clean8"/>
        </func:result>
    </func:function>
    
    <!--  Replace tick (') marks with double tick ('')-->
    <!--  Note:  mysql requires a \ escape instead-->
    <func:function name="dhis:dbescape">
        <xsl:param name="raw"/>
        <xsl:variable name="tick">'</xsl:variable>
        <xsl:variable name="ticktick">''</xsl:variable>
        <xsl:variable name="clean1" select="str:replace($raw,$tick,$ticktick)"/>
        <func:result>
            <xsl:value-of select="$clean1"/>
        </func:result>
    </func:function>
    
    <!--  Remove last character from string (used to knock off trailng commas in lists)-->
    <func:function name="dhis:truncateright">
        <xsl:param name="full"/>
        <xsl:param name="numchars"/>
        <xsl:variable name="length" select="string-length($full) - $numchars"/>
        <func:result>
            <xsl:value-of select="substring($full, 1, $length)"/>
        </func:result>
    </func:function>
    
    <!--  Replace boolean true/false with 1/0 - sqlite deficiency :-( -->
    <func:function name="dhis:boolean">
        <xsl:param name="strbool"/>
        <func:result>
            <xsl:choose>
                <xsl:when test="$strbool='true'">1</xsl:when>
                <xsl:when test="$strbool='false'">0</xsl:when>
                <xsl:otherwise>NULL</xsl:otherwise>
            </xsl:choose>
        </func:result>
    </func:function>
    
    <!-- convenience function for inserting groupsets -->
    <func:function name="dhis:insert">
        <xsl:param name="table"/>
        <xsl:param name="fields"/>
        <xsl:param name="values"/>
        <func:result>
            <xsl:text/>INSERT OR REPLACE INTO <xsl:value-of select="$table"/> (<xsl:text/>
            <xsl:value-of select="dhis:truncateright($fields,1)"/>) VALUES (<xsl:value-of
                select="dhis:truncateright($values,1)"/>);<xsl:text/>
        </func:result>
    </func:function>
    

</xsl:stylesheet>
