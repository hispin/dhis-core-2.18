package org.hisp.dhis.reporttable.impl;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.reporttable.ReportTable.ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME;
import static org.hisp.dhis.reporttable.ReportTable.PARAM_ORGANISATIONUNIT_COLUMN_NAME;
import static org.hisp.dhis.reporttable.ReportTable.PRETTY_COLUMNS;
import static org.hisp.dhis.reporttable.ReportTable.REPORTING_MONTH_COLUMN_NAME;
import static org.hisp.dhis.reporttable.ReportTable.SPACE;
import static org.hisp.dhis.reporttable.ReportTable.getColumnName;
import static org.hisp.dhis.reporttable.ReportTable.getPrettyColumnName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultReportTableService
    implements ReportTableService
{
    private static final Log log = LogFactory.getLog( DefaultReportTableService.class );

    private static final String YES = "Yes";

    private static final String NO = "No";

    // ---------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------

    private AnalyticsService analyticsService;

    public void setAnalyticsService( AnalyticsService analyticsService )
    {
        this.analyticsService = analyticsService;
    }

    private GenericIdentifiableObjectStore<ReportTable> reportTableStore;

    public void setReportTableStore( GenericIdentifiableObjectStore<ReportTable> reportTableStore )
    {
        this.reportTableStore = reportTableStore;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // ReportTableService implementation
    // -------------------------------------------------------------------------

    @Override
    public Grid getReportTableGrid( String uid, I18nFormat format, Date reportingPeriod, String organisationUnitUid )
    {
        ReportTable reportTable = getReportTable( uid );

        return getReportTableGrid( reportTable, format, reportingPeriod, organisationUnitUid, false );
    }

    @Override
    public Grid getReportTableGrid( int reportTableId, I18nFormat format, Date reportingPeriod, String organisationUnitUid )
    {
        ReportTable reportTable = getReportTable( reportTableId );

        return getReportTableGrid( reportTable, format, reportingPeriod, organisationUnitUid, false );
    }

    @Override
    public Grid getReportTableGrid( ReportTable reportTable, I18nFormat format, Date reportingPeriod, String organisationUnitUid, boolean minimal )
    {
        reportTable = initDynamicMetaObjects( reportTable, reportingPeriod, organisationUnitUid, format );

        return getGrid( reportTable, format );
    }

    @Override
    public ReportTable getReportTable( String uid, String mode )
    {
        if ( mode.equals( MODE_REPORT_TABLE ) )
        {
            return getReportTable( uid );
        }
        else if ( mode.equals( MODE_REPORT ) )
        {
            return reportService.getReport( uid ).getReportTable();
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public int saveReportTable( ReportTable reportTable )
    {
        return reportTableStore.save( reportTable );
    }

    public void updateReportTable( ReportTable reportTable )
    {
        reportTableStore.update( reportTable );
    }

    public void deleteReportTable( ReportTable reportTable )
    {
        reportTableStore.delete( reportTable );
    }

    public ReportTable getReportTable( int id )
    {
        return reportTableStore.get( id );
    }

    public ReportTable getReportTable( String uid )
    {
        return reportTableStore.getByUid( uid );
    }

    public ReportTable getReportTableNoAcl( String uid )
    {
        return reportTableStore.getByUidNoAcl( uid );
    }

    public Collection<ReportTable> getReportTables( final Collection<Integer> identifiers )
    {
        Collection<ReportTable> objects = getAllReportTables();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<ReportTable>()
        {
            public boolean retain( ReportTable object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public List<ReportTable> getAllReportTables()
    {
        return reportTableStore.getAll();
    }

    public List<ReportTable> getReportTableByName( String name )
    {
        return reportTableStore.getAllEqName( name );
    }

    public List<ReportTable> getReportTablesBetweenByName( String name, int first, int max )
    {
        return reportTableStore.getAllLikeNameOrderedName( name, first, max );
    }

    public int getReportTableCount()
    {
        return reportTableStore.getCount();
    }

    public int getReportTableCountByName( String name )
    {
        return reportTableStore.getCountLikeName( name );
    }

    public List<ReportTable> getReportTablesBetween( int first, int max )
    {
        return reportTableStore.getAllOrderedName( first, max );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Populates the report table with dynamic meta objects originating from
     * report table parameters.
     *
     * @param reportTable        the report table.
     * @param reportingPeriod    the reporting period start date.
     * @param organisationUnitId the organisation unit identifier.
     * @param format             the I18n format.
     * @return a report table.
     */
    private ReportTable initDynamicMetaObjects( ReportTable reportTable, Date reportingPeriod,
        String organisationUnitUid, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Reporting period report parameter / current reporting period
        // ---------------------------------------------------------------------

        log.info( "Running report table: " + reportTable.getName() );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitUid );
        
        List<OrganisationUnit> relativeOrganisationUnits = new ArrayList<OrganisationUnit>();
        
        if ( reportTable.hasRelativePeriods() && reportTable.getReportParams() != null && reportTable.getReportParams().isParamReportingMonth() )
        {
            reportTable.setReportingPeriodName( reportTable.getRelatives().getReportingPeriodName( reportingPeriod,
                format ) );

            log.info( "Reporting period date from report param: " + reportTable.getReportingPeriodName() );
        }
        else if ( reportTable.hasRelativePeriods() )
        {
            reportTable.setReportingPeriodName( reportTable.getRelatives().getReportingPeriodName( format ) );

            log.info( "Reporting period date default: " + reportTable.getReportingPeriodName() );
        }

        // ---------------------------------------------------------------------
        // Parent organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null &&
            reportTable.getReportParams().isParamParentOrganisationUnit() )
        {
            organisationUnit.setCurrentParent( true );
            reportTable.setParentOrganisationUnit( organisationUnit );
            relativeOrganisationUnits.addAll( organisationUnit.getChildren() );
            relativeOrganisationUnits.add( organisationUnit );

            log.info( "Parent organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null &&
            reportTable.getReportParams().isParamOrganisationUnit() )
        {
            reportTable.setParentOrganisationUnit( organisationUnit );
            relativeOrganisationUnits.add( organisationUnit );

            log.info( "Organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Set properties and initalize
        // ---------------------------------------------------------------------

        reportTable.init( currentUserService.getCurrentUser(), reportingPeriod, relativeOrganisationUnits, format );

        return reportTable;
    }

    /**
     * Generates a grid based on the given report table.
     *
     * @param reportTable the report table.
     * @return a grid.
     */
    private Grid getGrid( ReportTable reportTable, I18nFormat format )
    {
        final String subtitle = StringUtils.trimToEmpty( reportTable.getParentOrganisationUnitName() ) + SPACE
            + StringUtils.trimToEmpty( reportTable.getReportingPeriodName() );

        final Grid grid = new ListGrid().setTitle( reportTable.getName() + " - " + subtitle );

        Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( reportTable, format );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( String row : reportTable.getRowDimensions() )
        {
            String name = StringUtils.defaultIfEmpty( DimensionalObject.PRETTY_NAMES.get( row ), row );
            
            grid.addHeader( new GridHeader( name + " ID", row + "_id", String.class.getName(), true, true ) );
            grid.addHeader( new GridHeader( name, row + "_name", String.class.getName(), false, true ) );
            grid.addHeader( new GridHeader( name + " code", row + "_code", String.class.getName(), true, true ) );
            grid.addHeader( new GridHeader( name + " description", row + "_description", String.class.getName(), true, true ) );
        }
        
        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( REPORTING_MONTH_COLUMN_NAME ), REPORTING_MONTH_COLUMN_NAME,
            String.class.getName(), true, true ) );
        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( PARAM_ORGANISATIONUNIT_COLUMN_NAME ),
            PARAM_ORGANISATIONUNIT_COLUMN_NAME, String.class.getName(), true, true ) );
        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME ),
            ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME, String.class.getName(), true, true ) );

        final int startColumnIndex = grid.getHeaders().size();
        final int numberOfColumns = reportTable.getGridColumns().size();

        for ( List<NameableObject> column : reportTable.getGridColumns() )
        {
            grid.addHeader( new GridHeader( getPrettyColumnName( column ), getColumnName( column ), Double.class
                .getName(), false, false ) );
        }

        // ---------------------------------------------------------------------
        // Values
        // ---------------------------------------------------------------------

        for ( List<NameableObject> row : reportTable.getGridRows() )
        {
            grid.addRow();

            // -----------------------------------------------------------------
            // Row meta data
            // -----------------------------------------------------------------

            for ( NameableObject object : row )
            {
                grid.addValue( object.getUid() );
                grid.addValue( object.getName() );
                grid.addValue( object.getCode() );
                grid.addValue( object.getDescription() );
            }
            
            grid.addValue( reportTable.getReportingPeriodName() );
            grid.addValue( reportTable.getParentOrganisationUnitName() );
            grid.addValue( isCurrentParent( row ) ? YES : NO );

            // -----------------------------------------------------------------
            // Row data values
            // -----------------------------------------------------------------

            for ( List<NameableObject> column : reportTable.getGridColumns() )
            {
                String key = getId( column, row );
                Double value = map.get( key );
                
                grid.addValue( value );
            }

            //TODO totals and sub totals
        }

        if ( reportTable.isRegression() )
        {
            addRegressionToGrid( grid, startColumnIndex, numberOfColumns );
        }

        if ( reportTable.isCumulative() )
        {
            addCumulativesToGrid( grid, startColumnIndex, numberOfColumns );
        }

        // ---------------------------------------------------------------------
        // Sort and limit
        // ---------------------------------------------------------------------

        if ( reportTable.sortOrder() != ReportTable.NONE )
        {
            grid.sortGrid( grid.getWidth(), reportTable.sortOrder() );
        }

        if ( reportTable.topLimit() > 0 )
        {
            grid.limitGrid( reportTable.topLimit() );
        }

        return grid;
    }
    
    private String getId( List<NameableObject> column, List<NameableObject> row )
    {
        StringBuilder id = new StringBuilder();
        
        for ( NameableObject item : column )
        {
            id.append( item.getUid() ).append( "-" );
        }
        
        for ( NameableObject item : row )
        {
            id.append( item.getUid() ).append( "-" );
        }
        
        return id.substring( 0, id.length() - 1 );
    }

    /**
     * Adds columns with regression values to the given grid.
     *
     * @param grid             the grid.
     * @param startColumnIndex the index of the first data column.
     * @param numberOfColumns  the number of data columns.
     */
    private Grid addRegressionToGrid( Grid grid, int startColumnIndex, int numberOfColumns )
    {
        for ( int i = 0; i < numberOfColumns; i++ )
        {
            int columnIndex = i + startColumnIndex;

            grid.addRegressionColumn( columnIndex, true );
        }

        return grid;
    }

    /**
     * Adds columns with cumulative values to the given grid.
     *
     * @param grid             the grid.
     * @param startColumnIndex the index of the first data column.
     * @param numberOfColumns  the number of data columns.
     */
    private Grid addCumulativesToGrid( Grid grid, int startColumnIndex, int numberOfColumns )
    {
        for ( int i = 0; i < numberOfColumns; i++ )
        {
            int columnIndex = i + startColumnIndex;

            grid.addCumulativeColumn( columnIndex, true );
        }

        return grid;
    }

    /**
     * Checks whether the given List of IdentifiableObjects contains an object
     * which is an OrganisationUnit and has the currentParent property set to
     * true.
     *
     * @param objects the List of IdentifiableObjects.
     */
    private boolean isCurrentParent( List<? extends IdentifiableObject> objects )
    {
        for ( IdentifiableObject object : objects )
        {
            if ( object != null && object instanceof OrganisationUnit && ((OrganisationUnit) object).isCurrentParent() )
            {
                return true;
            }
        }

        return false;
    }
}
