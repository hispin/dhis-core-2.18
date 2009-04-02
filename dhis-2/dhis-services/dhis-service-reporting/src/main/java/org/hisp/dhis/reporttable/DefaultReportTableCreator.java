package org.hisp.dhis.reporttable;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.MetaObject;
import org.hisp.dhis.completeness.DataSetCompletenessExportService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.jdbc.BatchHandlerFactory;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriodType;
import org.hisp.dhis.reporttable.jdbc.ReportTableManager;
import org.hisp.dhis.system.grid.Grid;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultReportTableCreator
    extends ReportTableInternalProcess
{
    private static final Log log = LogFactory.getLog( DefaultReportTableCreator.class );
    
    private static final String NULL_REPLACEMENT = "0.0";
    
    // ---------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------

    private ReportTableManager reportTableManager;
    
    public void setReportTableManager( ReportTableManager reportTableManager )
    {
        this.reportTableManager = reportTableManager;
    }
    
    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
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
    
    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }
    
    private DataSetCompletenessExportService completenessExportService;
    
    public void setCompletenessExportService( DataSetCompletenessExportService completenessExportService )
    {
        this.completenessExportService = completenessExportService;
    }

    public void createReportTable( ReportTable reportTable, boolean doDataMart )
    {
        log.info( "Process started for report table: '" + reportTable.getName() + "'" );
        
        setMessage( "aggregating_data" );
        
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
                completenessExportService.exportDataSetCompleteness( getIdentifiers( DataSet.class, reportTable.getDataSets() ),
                    getIdentifiers( Period.class, reportTable.getAllPeriods() ),
                    getIdentifiers( OrganisationUnit.class, reportTable.getAllUnits() ),
                    reportTable.getId() );
            }
        }
        
        // ---------------------------------------------------------------------
        // Creating report table
        // ---------------------------------------------------------------------
        
        setMessage( "creating_report_datasource" );
        
        reportTableManager.createReportTable( reportTable );

        // ---------------------------------------------------------------------
        // Updating existingt table name after deleting the database table
        // ---------------------------------------------------------------------
        
        reportTable.updateExistingTableName();
        
        reportTableService.updateReportTable( reportTable );
        
        log.info( "Created report table" );

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
        }

        // ---------------------------------------------------------------------
        // Populating report table from grid
        // ---------------------------------------------------------------------

        BatchHandler batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class );

        batchHandler.setTableName( reportTable.getTableName() );
        
        batchHandler.init();
        
        for ( List<String> row : grid.getRows() )
        {
            batchHandler.addObject( row );
        }
        
        batchHandler.flush();       

        setMessage( "process_done" );        
        
        log.info( "Populated report table: '" + reportTable.getTableName() + "'" );
    }

    public void removeReportTable( ReportTable reportTable )
    {
        reportTableManager.removeReportTable( reportTable );
    }
    
    @Override
    public void deleteRelativePeriods()
    {
        dataMartStore.deleteRelativePeriods();
        
        log.info( "Deleted relative periods" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Grid addRegressionToGrid( Grid grid, int startColumnIndex, int numberOfColumns )
    {
        for ( int i = 0; i < numberOfColumns; i++ )
        {
            int columnIndex = i + startColumnIndex;
            
            grid.addRegressionColumn( columnIndex );
        }
        
        return grid;
    }
    
    private Grid getGrid( ReportTable reportTable )
    {
        Grid grid = new Grid();
        
        Map<String, Double> map = null;

        for ( final MetaObject metaObject : reportTable.getReportIndicators() )
        {
            for ( final DataElementCategoryOptionCombo categoryOptionCombo : reportTable.getReportCategoryOptionCombos() )
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
                            grid.addValue( getPeriodName( reportTable, period ) );
                        }
                        
                        if ( reportTable.getIndexNameColumns().contains( ReportTable.ORGANISATIONUNIT_NAME ) )
                        {
                            grid.addValue( unit.getShortName() );
                        }

                        // -----------------------------------------------------
                        // Reporting month name
                        // -----------------------------------------------------

                        grid.addValue( reportTable.getReportingMonthName() );
    
                        // -----------------------------------------------------
                        // Values
                        // -----------------------------------------------------

                        map = reportTableManager.getAggregatedValueMap( reportTable, metaObject, categoryOptionCombo, period, unit );
                        
                        for ( String identifier : reportTable.getCrossTabIdentifiers() )
                        {
                            grid.addValue( parseAndReplaceNull( map.get( identifier ) ) );
                        }
                    }
                }
            }
        }
        
        return grid;
    }
    
    private static String parseAndReplaceNull( Double value )
    {
        return value != null ? String.valueOf( value ) : NULL_REPLACEMENT;
    }
    
    private String getPeriodName( ReportTable reportTable, Period period )
    {
        if ( period.getPeriodType().getName().equals( RelativePeriodType.NAME ) )
        {
            return period.getName();
        }
        else
        {
            return reportTable.getI18nFormat().formatPeriod( period );
        }
    }
}
