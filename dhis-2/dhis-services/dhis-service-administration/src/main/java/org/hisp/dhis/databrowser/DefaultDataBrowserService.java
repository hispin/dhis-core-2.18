package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-2008, University of Oslo
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author joakibj, briane, eivinhb, jetonm
 * @version $Id$
 */
public class DefaultDataBrowserService 
    implements DataBrowserService
{
    private static final String STARTDATE = "1900-01-01";
    private static final String ENDDATE = "3000-01-01";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataBrowserStore dataBrowserStore;

    public void setDataBrowserStore( DataBrowserStore dataBrowserStore )
    {
        this.dataBrowserStore = dataBrowserStore;
    }

    // -------------------------------------------------------------------------
    // DataBrowserService implementation
    // -------------------------------------------------------------------------

    public DataBrowserTable getDataSetsInPeriod( String startDate, String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );

        return dataBrowserStore.getDataSetsBetweenPeriods( betweenPeriodIds );
    }

    public DataBrowserTable getDataElementGroupsInPeriod( String startDate, String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );

        return dataBrowserStore.getDataElementGroupsBetweenPeriods( betweenPeriodIds );
    }

    public DataBrowserTable getOrgUnitGroupsInPeriod( String startDate, String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );

        return dataBrowserStore.getOrgUnitGroupsBetweenPeriods( betweenPeriodIds );
    }    
    
    public DataBrowserTable getOrgUnitsInPeriod( Integer orgUnitParent, String startDate, String endDate,
        PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );
        DataBrowserTable table = new DataBrowserTable();

        dataBrowserStore.setStructureForOrgUnitBetweenPeriods( table, orgUnitParent, betweenPeriodIds );

        Integer numResults = dataBrowserStore.setCountOrgUnitsBetweenPeriods( table, orgUnitParent, betweenPeriodIds );
        if ( numResults == 0 )
            table.addZeroColumn();

        return table;
    }

    public DataBrowserTable getCountDataElementsForDataSetInPeriod( Integer dataSetId, String startDate,
        String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );
        DataBrowserTable table = new DataBrowserTable();

        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetId, betweenPeriodIds );

        dataBrowserStore.setCountDataElementsForDataSetBetweenPeriods( table, dataSetId, betweenPeriodIds );

        return table;
    }

    public DataBrowserTable getCountDataElementsForDataElementGroupInPeriod( Integer dataElementGroupId,
        String startDate, String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );
        DataBrowserTable table = new DataBrowserTable();

        dataBrowserStore.setDataElementStructureForDataElementGroupBetweenPeriods( table, dataElementGroupId,
            betweenPeriodIds );

        dataBrowserStore.setCountDataElementsForDataElementGroupBetweenPeriods( table, dataElementGroupId,
            betweenPeriodIds );

        return table;
    }

    public DataBrowserTable getCountDataElementGroupsForOrgUnitGroupInPeriod( Integer orgUnitGroupId, String startDate,
        String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );

        DataBrowserTable table = new DataBrowserTable();

        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( table, orgUnitGroupId,
            betweenPeriodIds );

        dataBrowserStore.setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( table, orgUnitGroupId,
            betweenPeriodIds );

        return table;
    }

    public DataBrowserTable getCountDataElementsForOrgUnitInPeriod( Integer orgUnitGroupId, String startDate,
        String endDate, PeriodType periodType )
    {
        if ( startDate == null || startDate.length() == 0 )
            startDate = STARTDATE;
        if ( endDate == null || endDate.length() == 0 )
            endDate = ENDDATE;

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType );

        DataBrowserTable table = new DataBrowserTable();

        dataBrowserStore.setDataElementStructureForOrgUnitBetweenPeriods( table, orgUnitGroupId, betweenPeriodIds );

        int numRows = dataBrowserStore.setCountDataElementsForOrgUnitBetweenPeriods( table, orgUnitGroupId,
            betweenPeriodIds );
        if ( numRows == 0 )
            table.addZeroColumn();

        return table;
    }

    /**
     * Helper-method that finds all PeriodIds between a given period. Uses
     * functionality already in the DHIS. Returns a list with all id's that was
     * found.
     * 
     * @param startDate
     * @param endDate
     * @param periodType
     * @return List<Integer>
     */
    private List<Integer> getAllPeriodIdsBetweenDatesOnPeriodType( String startDate, String endDate,
        PeriodType periodType )
    {
        String formatString = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( formatString );
        Date date1 = new Date();
        Date date2 = new Date();

        try
        {
            date1 = sdf.parse( startDate );
            date2 = sdf.parse( endDate );
        }
        catch ( ParseException e )
        {
            e.printStackTrace();
        }

        Collection<Period> pp = periodService.getPeriodsBetweenDates( periodType, date1, date2 );
        Iterator<Period> it = pp.iterator();
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();

        while ( it.hasNext() )
        {
            Period p = it.next();
            betweenPeriodIds.add( p.getId() );
        }

        if ( betweenPeriodIds.size() <= 0 )
        {
            betweenPeriodIds.add( -1 );
        }
        
        return betweenPeriodIds;
    }
}
