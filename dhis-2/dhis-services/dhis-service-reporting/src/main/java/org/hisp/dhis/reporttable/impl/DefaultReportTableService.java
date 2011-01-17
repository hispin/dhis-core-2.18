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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionOptionElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.jdbc.ReportTableManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultReportTableService
    implements ReportTableService
{
    private static final Log log = LogFactory.getLog( DefaultReportTableService.class );
    
    private static final String NULL_REPLACEMENT = "0.0";
    private static final String MODE_REPORT = "report";
    private static final String MODE_REPORT_TABLE = "table";
    
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

    private BatchHandlerFactory batchHandlerFactory;
    
    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private DataMartService dataMartService;
    
    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    private AggregatedDataValueService aggregatedDataValueService;
    
    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private DataSetCompletenessService completenessService;

    public void setCompletenessService( DataSetCompletenessService completenessService )
    {
        this.completenessService = completenessService;
    }

    // -------------------------------------------------------------------------
    // ReportTableService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void createReportTables( int id, String mode, Integer reportingPeriod, 
        Integer organisationUnitId, boolean doDataMart, I18nFormat format )
    {
        for ( ReportTable reportTable : getReportTables( id, mode ) )
        {
            reportTable = initDynamicMetaObjects( reportTable, reportingPeriod, organisationUnitId, format );

            createReportTable( reportTable, doDataMart );
        }
    }
    
    @Transactional
    public void createReportTable( ReportTable reportTable, boolean doDataMart )
    {
        log.info( "Process started for report table: " + reportTable.getName() );
        
        // ---------------------------------------------------------------------
        // Exporting relevant data to data mart
        // ---------------------------------------------------------------------

        if ( doDataMart )
        {
            String mode = reportTable.getMode();
            
            if ( mode.equals( ReportTable.MODE_DATAELEMENTS ) || mode.equals( ReportTable.MODE_INDICATORS ) )
            {
                dataMartService.export( getIdentifiers( DataElement.class, reportTable.getDataElements() ),
                    getIdentifiers( Indicator.class, reportTable.getIndicators() ),
                    getIdentifiers( Period.class, reportTable.getAllPeriods() ),
                    getIdentifiers( OrganisationUnit.class, reportTable.getAllUnits() ) );
            }
            else if ( mode.equals( ReportTable.MODE_DATASETS ) )
            {
                completenessService.exportDataSetCompleteness( getIdentifiers( DataSet.class, reportTable.getDataSets() ),
                    getIdentifiers( Period.class, reportTable.getAllPeriods() ),
                    getIdentifiers( OrganisationUnit.class, reportTable.getAllUnits() ),
                    reportTable.getId() );
            }
        }
        
        // ---------------------------------------------------------------------
        // Creating report table
        // ---------------------------------------------------------------------
        
        reportTableManager.createReportTable( reportTable );

        // ---------------------------------------------------------------------
        // Updating existing table name after deleting the database table
        // ---------------------------------------------------------------------
        
        reportTable.updateExistingTableName();
        
        updateReportTable( reportTable );
        
        log.info( "Created report table: " + reportTable.getName() );

        // ---------------------------------------------------------------------
        // Creating grid
        // ---------------------------------------------------------------------

        Grid grid = getGrid( reportTable );

        if ( reportTable.isRegression() )
        {
            // -----------------------------------------------------------------
            // The start index of the crosstab columns is derived by
            // subtracting the total number of columns with the number of
            // crosstab columns, since they come last in the report table.
            // -----------------------------------------------------------------

            int numberOfColumns = reportTable.getCrossTabIdentifiers().size();
            int startColumnIndex = grid.getWidth() - numberOfColumns;
        
            addRegressionToGrid( grid, startColumnIndex, numberOfColumns );
            
            log.info( "Added regression to report table: " + reportTable.getName() );
        }

        // ---------------------------------------------------------------------
        // Populating report table from grid
        // ---------------------------------------------------------------------
        
        BatchHandler<Object> batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class );

        batchHandler.setTableName( reportTable.getTableName() );        
        batchHandler.init();
        
        for ( List<String> row : grid.getRows() )
        {
            batchHandler.addObject( row );
        }
        
        batchHandler.flush();       

        log.info( "Populated report table: " + reportTable.getTableName() );
    }

    public void removeReportTable( ReportTable reportTable )
    {
        reportTableManager.removeReportTable( reportTable );
    }
    
    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    @Transactional
    public int saveReportTable( ReportTable reportTable )
    {
        return reportTableStore.save( reportTable );
    }

    @Transactional
    public void updateReportTable( ReportTable reportTable )
    {
        reportTableStore.update( reportTable );
    }

    @Transactional
    public void deleteReportTable( ReportTable reportTable )
    {
        reportTableStore.delete( reportTable );
    }

    @Transactional
    public ReportTable getReportTable( int id )
    {
        return reportTableStore.get( id );
    }

    @Transactional
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

    @Transactional
    public Collection<ReportTable> getAllReportTables()
    {
        return reportTableStore.getAll();
    }

    @Transactional
    public ReportTable getReportTableByName( String name )
    {
        return reportTableStore.getByName( name );
    }

    @Transactional
    public Grid getReportTableGrid( int id, I18nFormat format, Integer reportingPeriod, Integer organisationUnitId )
    {
        ReportTable reportTable = getReportTable( id );
        
        reportTable = initDynamicMetaObjects( reportTable, reportingPeriod, organisationUnitId, format );
        
        return reportTableManager.getReportTableGrid( reportTable );
    }
    
    @Transactional
    public Collection<ReportTable> getReportTablesBetweenByName( String name, int first, int max )
    {
        return reportTableStore.getBetweenByName( name, first, max );
    }

    @Transactional
    public int getReportTableCount()
    {
        return reportTableStore.getCount();
    }

    @Transactional
    public int getReportTableCountByName( String name )
    {
        return reportTableStore.getCountByName( name );
    }

    @Transactional
    public Collection<ReportTable> getReportTablesBetween( int first, int max )
    {
        return reportTableStore.getBetween( first, max );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Populates the report table with dynamic meta objects originating from report
     * table parameters.
     * 
     * @param reportTable the report table.
     * @param reportingPeriod the reporting period number.
     * @param organisationUnitId the organisation unit identifier.
     * @param format the I18n format.
     * @return a report table.
     */
    private ReportTable initDynamicMetaObjects( ReportTable reportTable, Integer reportingPeriod, 
        Integer organisationUnitId, I18nFormat format )
    {
        // -----------------------------------------------------------------
        // Reporting period report parameter / current reporting period
        // -----------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamReportingMonth() )
        {             
            reportTable.setRelativePeriods( periodService.reloadPeriods( reportTable.getRelatives().getRelativePeriods( reportingPeriod, format, !reportTable.isDoPeriods() ) ) );                                
            reportTable.setReportingMonthName( reportTable.getRelatives().getReportingMonthName( reportingPeriod, format ) );
            
            log.info( "Reporting period date from report param: " + reportTable.getReportingMonthName() );
        }
        else
        {
            reportTable.setRelativePeriods( periodService.reloadPeriods( reportTable.getRelatives().getRelativePeriods( 1, format, !reportTable.isDoPeriods() ) ) );                
            reportTable.setReportingMonthName( reportTable.getRelatives().getReportingMonthName( 1, format ) );
            
            log.info( "Reporting period date default: " + reportTable.getReportingMonthName() );
        }

        // -----------------------------------------------------------------
        // Parent organisation unit report parameter
        // -----------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamParentOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            organisationUnit.setCurrentParent( true );
            reportTable.getRelativeUnits().add( organisationUnit );
            reportTable.getRelativeUnits().addAll( new ArrayList<OrganisationUnit>( organisationUnit.getChildren() ) );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );
            
            log.info( "Parent organisation unit: " + organisationUnit.getName() );
        }

        // -----------------------------------------------------------------
        // Organisation unit report parameter
        // -----------------------------------------------------------------

        if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
            
            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
            organisationUnits.add( organisationUnit );
            reportTable.getRelativeUnits().addAll( organisationUnits );
            reportTable.setOrganisationUnitName( organisationUnit.getName() );
            
            log.info( "Organisation unit: " + organisationUnit.getName() );
        }

        // -----------------------------------------------------------------
        // Set properties and initalize
        // -----------------------------------------------------------------

        reportTable.setI18nFormat( format );
        reportTable.init();
        
        return reportTable;
    }

    /**
     * Adds columns with regression values to the given grid.
     * 
     * @param grid the grid.
     * @param startColumnIndex the start column index.
     * @param numberOfColumns the number of columns.
     * @return a grid.
     */
    private Grid addRegressionToGrid( Grid grid, int startColumnIndex, int numberOfColumns )
    {
        for ( int i = 0; i < numberOfColumns; i++ )
        {
            int columnIndex = i + startColumnIndex;
            
            grid.addRegressionColumn( columnIndex );
        }
        
        return grid;
    }
    
    /**
     * Creates a grid representing the data in the report table.
     * 
     * @param reportTable the report table.
     * @return a grid.
     */
    private Grid getGrid( ReportTable reportTable )
    {
        final Grid grid = new ListGrid();
        
        for ( final IdentifiableObject metaObject : reportTable.getReportIndicators() )
        {
            for ( final DimensionOptionElement categoryOptionCombo : reportTable.getReportCategoryOptionCombos() )
            {
                for ( final Period period : reportTable.getReportPeriods() )
                {
                    for ( final OrganisationUnit unit : reportTable.getReportUnits() )
                    {
                        grid.nextRow();
                        
                        // -----------------------------------------------------
                        // Identifier
                        // -----------------------------------------------------
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.INDICATOR_ID ) )
                        {
                            grid.addValue( String.valueOf( metaObject.getId() ) );
                        }
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.DATAELEMENT_ID ) )
                        {
                            grid.addValue( String.valueOf( metaObject.getId() ) );
                        }
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.DATASET_ID ) )
                        {
                            grid.addValue( String.valueOf( metaObject.getId() ) );
                        }
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.CATEGORYCOMBO_ID ) )
                        {
                            grid.addValue( String.valueOf( categoryOptionCombo.getId() ) );
                        }
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.PERIOD_ID ) )
                        {
                            grid.addValue( String.valueOf( period.getId() ) );
                        }
                        
                        if ( reportTable.getIndexColumns().contains( ReportTable.ORGANISATIONUNIT_ID ) )
                        {
                            grid.addValue( String.valueOf( unit.getId() ) );
                        }
                        
                        // -----------------------------------------------------
                        // Name
                        // -----------------------------------------------------
    
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.INDICATOR_NAME ) )
                        {
                            grid.addValue( metaObject.getShortName() );
                        }
                        
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.DATAELEMENT_NAME ) )
                        {
                            grid.addValue( metaObject.getShortName() );
                        }

                        if ( reportTable.getIndexNameColumns().contains( ReportTable.DATASET_NAME ) )
                        {
                            grid.addValue( metaObject.getShortName() );
                        }
                        
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.CATEGORYCOMBO_NAME ) )
                        {
                            grid.addValue( categoryOptionCombo.getShortName() );
                        }
                        
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.PERIOD_NAME ) )
                        {
                            grid.addValue( period.getName() );
                        }
                        
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.ORGANISATIONUNIT_NAME ) )
                        {
                            grid.addValue( unit.getShortName() );
                        }
                        
                        // -----------------------------------------------------
                        // Param reporting month name
                        // -----------------------------------------------------

                        grid.addValue( reportTable.getReportingMonthName() );

                        // -----------------------------------------------------
                        // Param organisation unit name
                        // -----------------------------------------------------

                        grid.addValue( reportTable.getOrganisationUnitName() );

                        // ---------------------------------------------------------------------
                        // Organisation unit is parent
                        // ---------------------------------------------------------------------

                        grid.addValue( unit != null && unit.isCurrentParent() ? String.valueOf( 1 ) : String.valueOf( 0 ) );
                        
                        // -----------------------------------------------------
                        // Values
                        // -----------------------------------------------------

                        Map<String, Double> map = reportTableManager.getAggregatedValueMap( 
                            reportTable, metaObject, (DataElementCategoryOptionCombo) categoryOptionCombo, period, unit );
                        
                        for ( String identifier : reportTable.getCrossTabIdentifiers() )
                        {
                            grid.addValue( parseAndReplaceNull( map.get( identifier ) ) );
                        }
                        
                        // -----------------------------------------------------
                        // Total values
                        // -----------------------------------------------------
                        
                        if ( reportTable.doTotal() )
                        {
                            for ( DimensionOption dimensionOption : reportTable.getDimensionOptions() )
                            {
                                grid.addValue( String.valueOf( aggregatedDataValueService.
                                    getAggregatedValue( (DataElement) metaObject, dimensionOption, period, unit ) ) );
                            }
                            
                            grid.addValue( String.valueOf( aggregatedDataValueService.getAggregatedValue( (DataElement) metaObject, period, unit ) ) );
                        }
                    }
                }
            }
        }
        
        return grid;
    }
    
    /**
     * Converts the given Double to String or replaces with default value if null.
     * 
     * @param value the Double.
     * @return the String.
     */
    private String parseAndReplaceNull( Double value )
    {
        return value != null ? String.valueOf( value ) : NULL_REPLACEMENT;
    }
    
    /**
     * If report table mode, this method will return the report table with the
     * given identifier. If report mode, this method will return the report
     * tables associated with the report.
     * 
     * @param id the identifier.
     * @param mode the mode.
     */
    private Collection<ReportTable> getReportTables( Integer id, String mode )
    {
        Collection<ReportTable> reportTables = new ArrayList<ReportTable>();

        if ( mode.equals( MODE_REPORT_TABLE ) )
        {
            reportTables.add( getReportTable( id ) );
        }
        else if ( mode.equals( MODE_REPORT ) )
        {
            reportTables = reportService.getReport( id ).getReportTables();
        }

        return reportTables;
    }
}
