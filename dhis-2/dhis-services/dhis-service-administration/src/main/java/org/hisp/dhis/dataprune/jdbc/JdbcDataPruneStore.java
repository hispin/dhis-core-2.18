package org.hisp.dhis.dataprune.jdbc;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataprune.DataPruneStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Quang Nguyen
 * @version Apr 6, 2010 5:48:15 PM
 */
public class JdbcDataPruneStore 
    implements DataPruneStore
{
    private static final Log log = LogFactory.getLog( JdbcDataPruneStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // DataPruneService implementation
    // -------------------------------------------------------------------------

    public void deleteMultiOrganisationUnit(List<OrganisationUnit> orgUnits) {
        
        String orgUnitIds = TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( OrganisationUnit.class, orgUnits )) ;
        
        String sql = "delete from datasetlocksource where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );
        
        sql = "delete from completedatasetregistration where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from datasetsource where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from frequencyoverrideassociation where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from minmaxdataelement where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from orgunitgroupmembers where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from usermembership where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from datamartexportorgunits where orgunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from excelgroup_associations where organisationid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from reportexcel_associations where organisationid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from maporganisationunitrelation where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from maporganisationunitrelation where mapid in (select mapid from map where organisationunitid in ("
            + orgUnitIds + "));";
        jdbcTemplate.execute( sql );

        sql = "delete from map where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from patientidentifier where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from program_organisationunits where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from chart_organisationunits where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from reporttable_organisationunits where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from datavalue_audit where (dataelementid, periodid, sourceid, categoryoptioncomboid) in (select dataelementid, periodid, sourceid, categoryoptioncomboid from datavalue where sourceid in ("
            + orgUnitIds + "));";
        jdbcTemplate.execute( sql );

        sql = "delete from datavalue where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from mapfile where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );
        
        sql = "delete from feature where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "update organisationunit set parentid=null where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from organisationunit where organisationunitid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );

        sql = "delete from source where sourceid in (" + orgUnitIds + ");";
        jdbcTemplate.execute( sql );
        
        log.info( "Deleting " + orgUnits.size() + " organisations units sucessfully" );
    }
}
