package org.hisp.dhis.reporttable.impl;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.*;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableGroup;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.jdbc.ReportTableManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.hisp.dhis.reporttable.ReportTable.*;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

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

    private ReportTableManager reportTableManager;

    public void setReportTableManager( ReportTableManager reportTableManager )
    {
        this.reportTableManager = reportTableManager;
    }

    private GenericIdentifiableObjectStore<ReportTable> reportTableStore;

    public void setReportTableStore( GenericIdentifiableObjectStore<ReportTable> reportTableStore )
    {
        this.reportTableStore = reportTableStore;
    }

    private GenericIdentifiableObjectStore<ReportTableGroup> reportTableGroupStore;

    public void setReportTableGroupStore( GenericIdentifiableObjectStore<ReportTableGroup> reportTableGroupStore )
    {
        this.reportTableGroupStore = reportTableGroupStore;
    }

    protected ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    protected OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataMartService dataMartService;

    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    private DataSetCompletenessService completenessService;

    public void setCompletenessService( DataSetCompletenessService completenessService )
    {
        this.completenessService = completenessService;
    }

    // -------------------------------------------------------------------------
    // ReportTableService implementation
    // -------------------------------------------------------------------------

    public void populateReportTableDataMart( int id, String mode, Date reportingPeriod, Integer organisationUnitId,
                                             I18nFormat format )
    {
        ReportTable reportTable = getReportTable( id, mode );

        reportTable = initDynamicMetaObjects( reportTable, reportingPeriod, organisationUnitId, format );

        if ( reportTable.hasDataElements() || reportTable.hasIndicators() )
        {
            dataMartService.export( getIdentifiers( DataElement.class, reportTable.getDataElements() ), getIdentifiers(
                Indicator.class, reportTable.getIndicators() ), getIdentifiers( Period.class, reportTable
                .getAllPeriods() ), getIdentifiers( OrganisationUnit.class, reportTable.getAllUnits() ) );
        }

        if ( reportTable.hasDataSets() )
        {
            completenessService.exportDataSetCompleteness( getIdentifiers( DataSet.class, reportTable.getDataSets() ),
                getIdentifiers( Period.class, reportTable.getAllPeriods() ), getIdentifiers( OrganisationUnit.class,
                reportTable.getAllUnits() ) );
        }
    }

    public Grid getReportTableGrid( String uid, I18nFormat format, Date reportingPeriod, String organisationUnitUid )
    {
        ReportTable reportTable = getReportTable( uid );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitUid );

        return getReportTableGrid( reportTable.getId(), format, reportingPeriod, organisationUnit.getId() );
    }

    public Grid getReportTableGrid( int id, I18nFormat format, Date reportingPeriod, Integer organisationUnitId )
    {
        ReportTable reportTable = getReportTable( id );

        reportTable = initDynamicMetaObjects( reportTable, reportingPeriod, organisationUnitId, format );

        return getGrid( reportTable );
    }

    public ReportTable getReportTable( Integer id, String mode )
    {
        if ( mode.equals( MODE_REPORT_TABLE ) )
        {
            return getReportTable( id );
        }
        else if ( mode.equals( MODE_REPORT ) )
        {
            return reportService.getReport( id ).getReportTable();
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

    public Collection<ReportTable> getAllReportTables()
    {
        return reportTableStore.getAll();
    }

    public ReportTable getReportTableByName( String name )
    {
        return reportTableStore.getByName( name );
    }

    public Collection<ReportTable> getReportTablesBetweenByName( String name, int first, int max )
    {
        return reportTableStore.getBetweenByName( name, first, max );
    }

    public int getReportTableCount()
    {
        return reportTableStore.getCount();
    }

    public int getReportTableCountByName( String name )
    {
        return reportTableStore.getCountByName( name );
    }

    public Collection<ReportTable> getReportTablesBetween( int first, int max )
    {
        return reportTableStore.getBetween( first, max );
    }

    // -------------------------------------------------------------------------
    // ReportTableGroup
    // -------------------------------------------------------------------------

    public int addReportTableGroup( ReportTableGroup reportTableGroup )
    {
        return reportTableGroupStore.save( reportTableGroup );
    }

    public void updateReportTableGroup( ReportTableGroup reportTableGroup )
    {
        reportTableGroupStore.update( reportTableGroup );
    }

    public void deleteReportTableGroup( ReportTableGroup reportTableGroup )
    {
        reportTableGroupStore.delete( reportTableGroup );
    }

    public ReportTableGroup getReportTableGroup( int id )
    {
        return reportTableGroupStore.get( id );
    }

    public ReportTableGroup getReportTableGroup( String uid )
    {
        return reportTableGroupStore.getByUid( uid );
    }

    public ReportTableGroup getReportTableGroupByName( String name )
    {
        return reportTableGroupStore.getByName( name );
    }

    public Collection<ReportTableGroup> getAllReportTableGroups()
    {
        return reportTableGroupStore.getAll();
    }

    public Collection<ReportTableGroup> getReportTableGroups( final Collection<Integer> identifiers )
    {
        Collection<ReportTableGroup> groups = getAllReportTableGroups();

        return identifiers == null ? groups : FilterUtils.filter( groups, new Filter<ReportTableGroup>()
        {
            public boolean retain( ReportTableGroup object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public Collection<ReportTableGroup> getGroupsContainingReportTable( ReportTable reportTable )
    {
        Collection<ReportTableGroup> groups = getAllReportTableGroups();

        Iterator<ReportTableGroup> iterator = groups.iterator();

        while ( iterator.hasNext() )
        {
            ReportTableGroup group = iterator.next();

            if ( !group.getMembers().contains( reportTable ) )
            {
                iterator.remove();
            }
        }

        return groups;
    }

    public int getReportTableGroupCount()
    {
        return reportTableGroupStore.getCount();
    }

    public int getReportTableGroupCountByName( String name )
    {
        return reportTableGroupStore.getCountByName( name );
    }

    public Collection<ReportTableGroup> getReportTableGroupsBetween( int first, int max )
    {
        return reportTableGroupStore.getBetween( first, max );
    }

    public Collection<ReportTableGroup> getReportTableGroupsBetweenByName( String name, int first, int max )
    {
        return reportTableGroupStore.getBetweenByName( name, first, max );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Populates the report table with dynamic meta objects originating from
     * report table parameters.
     *
     * @param reportTable        the report table.
     * @param reportingPeriod    the reporting period number.
     * @param organisationUnitId the organisation unit identifier.
     * @param format             the I18n format.
     * @return a report table.
     */
    private ReportTable initDynamicMetaObjects( ReportTable reportTable, Date reportingPeriod,
                                                Integer organisationUnitId, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Reporting period report parameter / current reporting period
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamReportingMonth() )
        {
            reportTable.setRelativePeriods( periodService.reloadPeriods( reportTable.getRelatives().getRelativePeriods(
                reportingPeriod, format, !reportTable.isDoPeriods() ) ) );
            reportTable.setReportingPeriodName( reportTable.getRelatives().getReportingPeriodName( reportingPeriod,
                format ) );

            log.info( "Reporting period date from report param: " + reportTable.getReportingPeriodName() );
        }
        else
        {
            reportTable.setRelativePeriods( periodService.reloadPeriods( reportTable.getRelatives().getRelativePeriods(
                format, !reportTable.isDoPeriods() ) ) );
            reportTable.setReportingPeriodName( reportTable.getRelatives().getReportingPeriodName( format ) );

            log.info( "Reporting period date default: " + reportTable.getReportingPeriodName() );
        }

        // ---------------------------------------------------------------------
        // Leaf parent organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null &&
            reportTable.getReportParams().isParamLeafParentOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            reportTable.getRelativeUnits().addAll(
                new ArrayList<OrganisationUnit>( organisationUnitService.getLeafOrganisationUnits( organisationUnitId ) ) );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );

            log.info( "Leaf parent organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Grand parent organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null &&
            reportTable.getReportParams().isParamGrandParentOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            organisationUnit.setCurrentParent( true );
            reportTable.getRelativeUnits().addAll(
                new ArrayList<OrganisationUnit>( organisationUnit.getGrandChildren() ) );
            reportTable.getRelativeUnits().add( organisationUnit );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );

            log.info( "Grand parent organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Parent organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamParentOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            organisationUnit.setCurrentParent( true );
            reportTable.getRelativeUnits().addAll( new ArrayList<OrganisationUnit>( organisationUnit.getChildren() ) );
            reportTable.getRelativeUnits().add( organisationUnit );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );

            log.info( "Parent organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Organisation unit report parameter
        // ---------------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            reportTable.getRelativeUnits().add( organisationUnit );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );

            log.info( "Organisation unit: " + organisationUnit.getName() );
        }

        // ---------------------------------------------------------------------
        // Set properties and initalize
        // ---------------------------------------------------------------------

        reportTable.setI18nFormat( format );
        reportTable.init();

        return reportTable;
    }

    /**
     * Generates a grid based on the given report table.
     *
     * @param reportTable the report table.
     * @return a grid.
     */
    private Grid getGrid( ReportTable reportTable )
    {
        String subtitle = StringUtils.trimToEmpty( reportTable.getOrganisationUnitName() ) + SPACE
            + StringUtils.trimToEmpty( reportTable.getReportingPeriodName() );

        Grid grid = new ListGrid().setTitle( reportTable.getName() ).setSubtitle( subtitle );

        final Map<String, Double> map = reportTableManager.getAggregatedValueMap( reportTable );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( String column : reportTable.getIndexColumns() ) // Index columns
        {
            grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( column ), column, Integer.class.getName(), true, true ) );
        }

        for ( String column : reportTable.getIndexNameColumns() ) // Index name columns
        {
            grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( column ), column, String.class.getName(), false, true ) );
        }

        for ( String column : reportTable.getIndexCodeColumns() ) // Index code columns
        {
            grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( column ), column, String.class.getName(), true, true ) );
        }

        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( REPORTING_MONTH_COLUMN_NAME ), REPORTING_MONTH_COLUMN_NAME,
            String.class.getName(), true, true ) );
        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( PARAM_ORGANISATIONUNIT_COLUMN_NAME ),
            PARAM_ORGANISATIONUNIT_COLUMN_NAME, String.class.getName(), true, true ) );
        grid.addHeader( new GridHeader( PRETTY_COLUMNS.get( ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME ),
            ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME, String.class.getName(), true, true ) );

        for ( List<NameableObject> column : reportTable.getColumns() )
        {
            grid.addHeader( new GridHeader( getPrettyColumnName( column ), getColumnName( column ), Double.class
                .getName(), false, false ) );
        }

        if ( reportTable.doSubTotals() )
        {
            for ( DataElementCategoryOption categoryOption : reportTable.getCategoryCombo().getCategoryOptions() )
            {
                grid.addHeader( new GridHeader( categoryOption.getShortName(), columnEncode( categoryOption
                    .getShortName() ), Double.class.getName(), false, false ) );
            }
        }

        if ( reportTable.doTotal() )
        {
            grid.addHeader( new GridHeader( TOTAL_COLUMN_PRETTY_NAME, TOTAL_COLUMN_NAME, Double.class.getName(), false,
                false ) );
        }

        // ---------------------------------------------------------------------
        // Values
        // ---------------------------------------------------------------------

        for ( List<NameableObject> row : reportTable.getRows() )
        {
            grid.addRow();

            for ( NameableObject object : row ) // Index columns
            {
                grid.addValue( object.getId() );
            }

            for ( NameableObject object : row ) // Index name columns
            {
                grid.addValue( object.getName() );
            }

            for ( NameableObject object : row ) // Index code columns
            {
                grid.addValue( object.getCode() );
            }

            grid.addValue( reportTable.getReportingPeriodName() );
            grid.addValue( reportTable.getOrganisationUnitName() );
            grid.addValue( isCurrentParent( row ) ? YES : NO );

            for ( List<NameableObject> column : reportTable.getColumns() ) // Values
            {
                grid.addValue( map.get( getIdentifier( row, column ) ) );
            }

            if ( reportTable.doSubTotals() )
            {
                for ( DataElementCategoryOption categoryOption : reportTable.getCategoryCombo().getCategoryOptions() )
                {
                    grid.addValue( map
                        .get( getIdentifier( row, DataElementCategoryOption.class, categoryOption.getId() ) ) );
                }
            }

            if ( reportTable.doTotal() )
            {
                // -------------------------------------------------------------
                // Only category option combo is crosstab when total, row
                // identifier will return total
                // -------------------------------------------------------------

                grid.addValue( map.get( getIdentifier( row ) ) );
            }
        }

        if ( reportTable.isRegression() && !reportTable.doTotal() )
        {
            addRegressionToGrid( grid, reportTable.getColumns().size() );
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

    /**
     * Adds columns with regression values to the given grid.
     *
     * @param grid            the grid.
     * @param numberOfColumns the number of columns.
     */
    private Grid addRegressionToGrid( Grid grid, int numberOfColumns )
    {
        int startColumnIndex = grid.getWidth() - numberOfColumns;

        for ( int i = 0; i < numberOfColumns; i++ )
        {
            int columnIndex = i + startColumnIndex;

            grid.addRegressionColumn( columnIndex, true );
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
