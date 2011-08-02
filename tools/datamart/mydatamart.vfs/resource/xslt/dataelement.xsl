<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:dxf="http://dhis2.org/schema/dxf/1.0"
    xmlns:dhis="http://dhis2.org/funcs" 
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings" extension-element-prefixes="func" version="1.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Processes dataelements</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:key name="deid_by_degroup" 
        match="/dxf:dxf/dxf:dataElementGroupMembers/dxf:dataElementGroupMember/dxf:dataElement"
        use="../dxf:dataElementGroup" />    
    <xsl:key name="degroupsetid_by_degroup"
        match="/dxf:dxf/dxf:dataElementGroupSetMembers/dxf:dataElementGroupSetMember/dxf:dataElementGroupSet"
        use="../dxf:dataElementGroup"/>
    
    <!--    
        For each dataelement:
        1.  insert entry in dataelement table
        3.  insert entry in dataelement groupset structure table
    -->
    <xsl:template match="dxf:dataElements/dxf:dataElement">
        <!--
            Populate dataelement table
        -->
        <xsl:text/>INSERT OR REPLACE INTO dataelement (dataelementid, name, shortname,aggregationtype) <xsl:text/>
        <xsl:text/>VALUES (<xsl:value-of select="dxf:id"/>, '<xsl:value-of
            select="dhis:dbescape(dxf:name)"/>', '<xsl:value-of
            select="dhis:dbescape(dxf:shortName)"/>', '<xsl:value-of
            select="dhis:dbescape(dxf:aggregationOperator)"/>');
        <xsl:text/>

        <!-- Groupsets .... -->
        <xsl:variable name="groupsets" select="/dxf:dxf/dxf:dataElementGroupSets/dxf:dataElementGroupSet"/>
        <xsl:variable name="allgroups" select="/dxf:dxf/dxf:dataElementGroups/dxf:dataElementGroup"/>

        <xsl:variable name="fields">
            <xsl:text/>dataelementid,<xsl:text/>
            <xsl:for-each select="$groupsets">
                <xsl:variable name="groupsetname"
                    select="./dxf:name"/>
                <xsl:text/><xsl:value-of select="dhis:dbencode($groupsetname)"/>,<xsl:text/>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="de_id" select="dxf:id"/>
        <!-- this is the set of groups which a dataelement is part of -->
        <xsl:variable name="degroups"
            select="/dxf:dxf/dxf:dataElementGroups/dxf:dataElementGroup[key('deid_by_degroup',dxf:id)=$de_id]"/>
        <xsl:variable name="values">
            <xsl:value-of select="dxf:id"/>,<xsl:text/>
            <xsl:for-each select="$groupsets">
                <xsl:variable name="groupsetid" select="./dxf:id"/>
                <!-- this is the set of groups in a groupset -->
                <xsl:variable name="groupsetgroups"
                    select="$allgroups[key('degroupsetid_by_degroup',dxf:id)=$groupsetid]"/>
                <!-- the intersection represents the group(s) from the groupset which dataelement is part of -->
                <!-- [1] ensures that only the first is returned if there are multiple nodes matching -->
                <xsl:variable name="group" select="set:intersection($degroups,$groupsetgroups)[1]"/>
                <xsl:choose>
                    <xsl:when test="count($group)=1">'<xsl:value-of select="$group/dxf:name"/>',</xsl:when>
                    <xsl:otherwise>NULL,</xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="dhis:insert('_dataelementgroupsetstructure',$fields,$values)"/>
        <xsl:text>
        </xsl:text>
        
    </xsl:template>

</xsl:stylesheet>
