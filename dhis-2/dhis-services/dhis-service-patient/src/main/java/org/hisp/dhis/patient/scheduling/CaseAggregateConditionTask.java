/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient.scheduling;

import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_AGGREGATE_QUERY_BUILDER_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_SCHEDULED_AGGREGATE_QUERY_BUILDER_PERIOD_TYPES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version RunCaseAggregateConditionTask.java 9:52:10 AM Oct 10, 2012 $
 */
public class CaseAggregateConditionTask
    implements Runnable
{
    public static final String STORED_BY_DHIS_SYSTEM = "DHIS-System";

    private OrganisationUnitService organisationUnitService;

    private CaseAggregationConditionService aggregationConditionService;

    private DataValueService dataValueService;

    private SystemSettingManager systemSettingManager;

    private JdbcTemplate jdbcTemplate;

    private DataElementService dataElementService;

    private DataElementCategoryService categoryService;
    
    // -------------------------------------------------------------------------
    // Params
    // -------------------------------------------------------------------------

    private List<Period> periods;

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

    private boolean last6Months;

    public void setLast6Months( boolean last6Months )
    {
        this.last6Months = last6Months;
    }

    private boolean last6To12Months;

    public void setLast6To12Months( boolean last6To12Months )
    {
        this.last6To12Months = last6To12Months;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CaseAggregateConditionTask( OrganisationUnitService organisationUnitService,
        CaseAggregationConditionService aggregationConditionService, DataValueService dataValueService,
        SystemSettingManager systemSettingManager, JdbcTemplate jdbcTemplate, DataElementService dataElementService,
        DataElementCategoryService categoryService )
    {
        this.organisationUnitService = organisationUnitService;
        this.aggregationConditionService = aggregationConditionService;
        this.dataValueService = dataValueService;
        this.systemSettingManager = systemSettingManager;
        this.jdbcTemplate = jdbcTemplate;
        this.dataElementService = dataElementService;
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public void run()
    {
        int level = (Integer) systemSettingManager.getSystemSetting(
            KEY_AGGREGATE_QUERY_BUILDER_ORGUNITGROUPSET_AGG_LEVEL, DEFAULT_ORGUNITGROUPSET_AGG_LEVEL );
        Collection<OrganisationUnit> orgunits = organisationUnitService.getOrganisationUnitsAtLevel( level );

        Collection<CaseAggregationCondition> aggConditions = aggregationConditionService
            .getAllCaseAggregationCondition();

        // ---------------------------------------------------------------------
        // Get Period list in system-setting
        // ---------------------------------------------------------------------

        Set<String> periodTypes = (Set<String>) systemSettingManager.getSystemSetting(
            KEY_SCHEDULED_AGGREGATE_QUERY_BUILDER_PERIOD_TYPES, DEFAULT_SCHEDULED_PERIOD_TYPES );

        List<Period> periods = getPeriods( periodTypes );

        // ---------------------------------------------------------------------
        // Aggregation
        // ---------------------------------------------------------------------

        for ( OrganisationUnit orgUnit : orgunits )
        {
            for ( CaseAggregationCondition aggCondition : aggConditions )
            {
                // -------------------------------------------------------------
                // Get agg-dataelement and option-combo
                // -------------------------------------------------------------

                String sql = "select aggregationdataelementid, optioncomboid from caseaggregationcondition where caseaggregationconditionid="
                    + aggCondition.getId();
                SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
                rs.next();
                int dataelementId = rs.getInt( "aggregationdataelementid" );
                int optionComboId = rs.getInt( "optioncomboid" );

                // -------------------------------------------------------------
                // Get agg-dataelement and option-combo
                // -------------------------------------------------------------

                DataElement dElement = dataElementService.getDataElement( dataelementId );
                DataElementCategoryOptionCombo optionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                for ( Period period : periods )
                {
                    Double resultValue = aggregationConditionService.parseConditition( aggCondition, orgUnit, period );

                    DataValue dataValue = dataValueService.getDataValue( orgUnit, dElement, period, optionCombo );

                    if ( resultValue != null && resultValue != 0.0 )
                    {
                        // -----------------------------------------
                        // Add dataValue
                        // -----------------------------------------
                        
                        if ( dataValue == null )
                        {
                            dataValue = new DataValue( dElement, period, orgUnit, "" + resultValue, "", new Date(),
                                null, optionCombo );
                            dataValueService.addDataValue( dataValue );
                        }
                        // -----------------------------------------
                        // Update dataValue
                        // -----------------------------------------
                        else
                        {
                            dataValue.setValue( "" + resultValue );
                            dataValue.setTimestamp( new Date() );
                            sql = "UPDATE datavalue" + " SET value='" + resultValue + "',lastupdated='" + new Date() + "' where dataelementId="
                                + dataelementId + " and periodid=" + period.getId() + " and sourceid="
                                + orgUnit.getId() + " and categoryoptioncomboid=" + optionComboId + " and storedby='"
                                + STORED_BY_DHIS_SYSTEM + "'";
                            jdbcTemplate.execute( sql );
                        }
                    }

                    // -----------------------------------------
                    // Delete dataValue
                    // -----------------------------------------
                    else if ( dataValue != null )
                    {
                        dataValueService.deleteDataValue( dataValue );
                    }

                }
            }
        }

    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<Period> getPeriods( Set<String> periodTypes )
    {
        if ( periods != null && periods.size() > 0 )
        {
            return periods;
        }

        List<Period> relatives = new ArrayList<Period>();

        if ( last6Months )
        {
            relatives.addAll( new RelativePeriods().getLast6Months( periodTypes ) );
        }

        if ( last6To12Months )
        {
            relatives.addAll( new RelativePeriods().getLast6To12Months( periodTypes ) );
        }

        return relatives;
    }

}
