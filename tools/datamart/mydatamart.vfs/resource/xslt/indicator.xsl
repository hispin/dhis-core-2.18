<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" 
    xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" 
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Processes indicators</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:key name="indid_by_indgroup" 
        match="/dxf:dxf/dxf:indicatorGroupMembers/dxf:indicatorGroupMember/dxf:indicator"
        use="../dxf:indicatorGroup" />    
    <xsl:key name="indgroupsetid_by_indgroup"
        match="/dxf:dxf/dxf:indicatorGroupSetMembers/dxf:indicatorGroupSetMember/dxf:indicatorGroupSet"
        use="../dxf:indicatorGroup"/>
    
    <!--    
        For each indicator:
        1.  insert entry in indicator table
        3.  insert entry in indicator groupset structure table
    -->
    <xsl:template match="dxf:indicators/dxf:indicator">
        <!--
            Populate indicator table
        -->
        <xsl:text/>INSERT OR REPLACE INTO indicator (indicatorid, name, shortname, annualized ) <xsl:text/>
        <xsl:text/>VALUES (<xsl:value-of select="dxf:id"/>, '<xsl:value-of
            select="dhis:dbescape(dxf:name)"/>', '<xsl:value-of
                select="dhis:dbescape(dxf:shortName)"/>', <xsl:value-of
                    select="dhis:boolean(dxf:annualized)"/>);
        <!--
            Populate indicator groupset structure
        -->
        <!-- Groupsets .... -->
        <xsl:variable name="groupsets" select="/dxf:dxf/dxf:indicatorGroupSets/dxf:indicatorGroupSet"/>
        <xsl:variable name="allgroups" select="/dxf:dxf/dxf:indicatorGroups/dxf:indicatorGroup"/>
        
        <xsl:variable name="fields">
            <xsl:text/>indicatorid,<xsl:text/>
            <xsl:for-each select="$groupsets">
                <xsl:variable name="groupsetname"
                    select="./dxf:name"/>
                <xsl:text/><xsl:value-of select="dhis:dbencode($groupsetname)"/>,<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="ind_id" select="dxf:id"/>
        <!-- this is the set of groups which a dataelement is part of -->
        <xsl:variable name="indgroups"
            select="/dxf:dxf/dxf:indicatorGroups/dxf:indicatorGroup[key('indid_by_indgroup',dxf:id)=$ind_id]"/>
        <xsl:variable name="values">
            <xsl:value-of select="dxf:id"/>,<xsl:text/>
            <xsl:for-each select="$groupsets">
                <xsl:variable name="groupsetid" select="./dxf:id"/>
                <!-- this is the set of groups in a groupset -->
                <xsl:variable name="groupsetgroups"
                    select="$allgroups[key('indgroupsetid_by_indgroup',dxf:id)=$groupsetid]"/>
                <!-- the intersection represents the group(s) from the groupset which dataelement is part of -->
                <!-- [1] ensures that only the first is returned if there are multiple nodes matching -->
                <xsl:variable name="group" select="set:intersection($indgroups,$groupsetgroups)[1]"/>
                <xsl:choose>
                    <xsl:when test="count($group)=1">'<xsl:value-of select="$group/dxf:name"/>',</xsl:when>
                    <xsl:otherwise>NULL,</xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="dhis:insert('_indicatorgroupsetstructure',$fields,$values)"/>
        <xsl:text>
        </xsl:text>
        
    </xsl:template>
    
    <!--
        Populate indicatortype table
    -->
    <xsl:template match="dxf:indicatorType">
        <xsl:text/>INSERT OR REPLACE INTO indicatortype (indicatortypeid, name, indicatorfactor ) <xsl:text/>
        <xsl:text/>VALUES (<xsl:value-of select="dxf:id"/>, '<xsl:value-of
            select="dhis:dbescape(dxf:name)"/>', <xsl:value-of select="dxf:factor"/>);
    </xsl:template>
    
</xsl:stylesheet>
