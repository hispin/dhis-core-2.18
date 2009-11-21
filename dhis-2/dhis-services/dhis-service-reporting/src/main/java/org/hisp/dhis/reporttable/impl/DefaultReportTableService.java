package org.hisp.dhis.reporttable.impl;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionOptionElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.RelativePeriodType;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.RelativePeriods;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableData;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.jdbc.ReportTableManager;
import org.hisp.dhis.system.grid.Grid;
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
    
    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
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
        Integer parentOrganisationUnitId, Integer organisationUnitId, I18nFormat format )
    {
        for ( ReportTable reportTable : getReportTables( id, mode ) )
        {
            // -----------------------------------------------------------------
            // Reporting period report parameter / current reporting period
            // -----------------------------------------------------------------

            Date date = null;

            if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamReportingMonth() )
            {
                reportTable.setRelativePeriods( getRelativePeriods( reportTable.getRelatives(), reportingPeriod ) );
                
                date = getDateFromPreviousMonth( reportingPeriod );
                
                log.info( "Reporting period date from report param: " + date );
            }
            else
            {
                reportTable.setRelativePeriods( getRelativePeriods( reportTable.getRelatives(), 1 ) );
                
                date = getDateFromPreviousMonth( 1 );
                
                log.info( "Reporting period date default: " + date );
            }

            String reportingMonthName = format.formatPeriod( new MonthlyPeriodType().createPeriod( date ) );
            
            reportTable.setReportingMonthName( reportingMonthName );

            // -----------------------------------------------------------------
            // Parent organisation unit report parameter
            // -----------------------------------------------------------------

            if ( reportTable.getReportParams() != null && reportTable.getReportParams().isParamParentOrganisationUnit() )
            {
                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( parentOrganisationUnitId );

                reportTable.getRelativeUnits().addAll( new ArrayList<OrganisationUnit>( organisationUnit.getChildren() ) );
                
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
                
                log.info( "Organisation unit: " + organisationUnit.getName() );
            }

            // -----------------------------------------------------------------
            // Set properties and initalize
            // -----------------------------------------------------------------

            reportTable.setI18nFormat( format );
            reportTable.init();

            // -----------------------------------------------------------------
            // Create report table
            // -----------------------------------------------------------------

            createReportTable( reportTable, true );
        }
                
        dataMartStore.deleteRelativePeriods();
    }

    @Transactional
    public void createReportTable( ReportTable reportTable, boolean doDataMart )
    {
        log.info( "Process started for report table: '" + reportTable.getName() + "'" );
        
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
        // Updating existingt table name after deleting the database table
        // ---------------------------------------------------------------------
        
        reportTable.updateExistingTableName();
        
        updateReportTable( reportTable );
        
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

        BatchHandler<Object> batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class );

        batchHandler.setTableName( reportTable.getTableName() );
        
        batchHandler.init();
        
        for ( List<String> row : grid.getRows() )
        {
            batchHandler.addObject( row );
        }
        
        batchHandler.flush();       

        log.info( "Populated report table: '" + reportTable.getTableName() + "'" );
    }

    public void removeReportTable( ReportTable reportTable )
    {
        reportTableManager.removeReportTable( reportTable );
    }
    
    @Transactional
    public List<Period> getRelativePeriods( RelativePeriods relatives, int months )
    {
        List<Period> relativePeriods = new ArrayList<Period>();
        
        Date date = getDateFromPreviousMonth( months );
        
        if ( relatives != null )
        {
            if ( relatives.isReportingMonth() )
            {
                Period period = periodService.getRelativePeriod( date, -1 );
                period.setName( RelativePeriods.REPORTING_MONTH );
                relativePeriods.add( period );
            }
            if ( relatives.isLast3Months() )
            {
                Period period = periodService.getRelativePeriod( date, -3 );
                period.setName( RelativePeriods.LAST_3_MONTHS );
                relativePeriods.add( period );            
            }
            if ( relatives.isLast6Months() )
            {
                Period period = periodService.getRelativePeriod( date, -6 );
                period.setName( RelativePeriods.LAST_6_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isLast9Months() )
            {
                Period period = periodService.getRelativePeriod( date, -9 );
                period.setName( RelativePeriods.LAST_9_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isLast12Months() )
            {
                Period period = periodService.getRelativePeriod( date, -12 );
                period.setName( RelativePeriods.LAST_12_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isSoFarThisYear() )
            {
                MonthlyPeriodType periodType = new MonthlyPeriodType();            
                Period period = new Period();
                
                period.setPeriodType( new RelativePeriodType() );
                period.setStartDate( getStartDateOfYear( date ) );            
                period.setEndDate( periodType.createPeriod( date ).getEndDate() );
                
                period = savePeriod( period );
                period.setName( RelativePeriods.SO_FAR_THIS_YEAR );
                relativePeriods.add( period );
            }
            if ( relatives.isSoFarThisFinancialYear() )
            {
                MonthlyPeriodType periodType = new MonthlyPeriodType();            
                Period period = new Period();
                
                period.setPeriodType( new RelativePeriodType() );
                period.setStartDate( getStartDateOfFinancialYear( date ) );            
                period.setEndDate( periodType.createPeriod( date ).getEndDate() );
                
                period = savePeriod( period );
                period.setName( RelativePeriods.SO_FAR_THIS_FINANCIAL_YEAR );
                relativePeriods.add( period );
            }
            if ( relatives.isLast3To6Months() )
            {
                Period period = periodService.getRelativePeriod( date, -6, -3 );
                period.setName( RelativePeriods.LAST_3_TO_6_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isLast6To9Months() )
            {
                Period period = periodService.getRelativePeriod( date, -9, -6 );
                period.setName( RelativePeriods.LAST_6_TO_9_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isLast9To12Months() )
            {
                Period period = periodService.getRelativePeriod( date, -12, -9 );
                period.setName( RelativePeriods.LAST_9_TO_12_MONTHS );
                relativePeriods.add( period );
            }
            if ( relatives.isLast12IndividualMonths() )
            {
                for ( int i = 0; i < 12; i++ )
                {
                    int periodNumber = i - 12;
                    
                    Period period = periodService.getRelativePeriod( date, periodNumber, periodNumber + 1 );
                    period.setName( RelativePeriods.PREVIOUS_MONTH_NAMES[i] );
                    relativePeriods.add( period );
                }
            }
            if ( relatives.isIndividualMonthsThisYear() )
            {
                MonthlyPeriodType periodType = new MonthlyPeriodType();
                
                Period period = new Period();
                period.setStartDate( date );
                
                List<Period> periods = periodType.generatePeriods( period );
                
                for ( int i = 0; i < 12; i++ )
                {
                    Period month = periods.get( i );
                    month.setPeriodType( new RelativePeriodType() );
                    month = savePeriod( month );
                    month.setName( RelativePeriods.MONTHS_THIS_YEAR[i] );                
                    relativePeriods.add( month );
                }            
            }
            if ( relatives.isIndividualQuartersThisYear() )
            {
                QuarterlyPeriodType periodType = new QuarterlyPeriodType();
                
                Period period = new Period();
                period.setStartDate( date );
                
                List<Period> periods = periodType.generatePeriods( period );
                
                for ( int i = 0; i < 4; i++ )
                {
                    Period quarter = periods.get( i );
                    quarter.setPeriodType( new RelativePeriodType() );
                    quarter = savePeriod( quarter );
                    quarter.setName( RelativePeriods.QUARTERS_THIS_YEAR[i] );
                    relativePeriods.add( quarter );
                }
            }
        }
        
        return relativePeriods;
    }

    public Date getDateFromPreviousMonth( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        
        cal.add( Calendar.MONTH, months * -1 );
        
        return cal.getTime();
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
    public Collection<ReportTable> getReportTables( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllReportTables();
        }
        
        Collection<ReportTable> tables = new ArrayList<ReportTable>();
        
        for ( Integer id : identifiers )
        {
            tables.add( getReportTable( id ) );
        }
        
        return tables;
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
    public ReportTableData getReportTableData( int id, I18nFormat format )
    {
        ReportTable reportTable = getReportTable( id );
        
        reportTable.setI18nFormat( format );
        reportTable.init();
        
        return reportTableManager.getDisplayReportTableData( reportTable );
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
        final Grid grid = new Grid();
        
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

                        Map<String, Double> map = reportTableManager.getAggregatedValueMap( 
                            reportTable, metaObject, (DataElementCategoryOptionCombo) categoryOptionCombo, period, unit );
                        
                        for ( String identifier : reportTable.getCrossTabIdentifiers() )
                        {
                            grid.addValue( parseAndReplaceNull( map.get( identifier ) ) );
                        }

                        // -----------------------------------------------------
                        // Values
                        // -----------------------------------------------------
                        
                        if ( reportTable.doTotal() )
                        {
                            for ( DimensionOption dimensionOption : reportTable.getDimensionOptions() )
                            {
                                grid.addValue( String.valueOf( dataMartStore.
                                    getTotalAggregatedValue( (DataElement) metaObject, (DataElementCategoryOption) dimensionOption, period, unit ) ) );
                            }
                            
                            grid.addValue( String.valueOf( dataMartStore.getTotalAggregatedValue( (DataElement) metaObject, period, unit ) ) );
                        }
                    }
                }
            }
        }
        
        return grid;
    }
    
    private String parseAndReplaceNull( Double value )
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
    
    private Date getStartDateOfFinancialYear( Date date )
    {
        Calendar cal = PeriodType.createCalendarInstance( date );

        cal.set( Calendar.MONTH, 3 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        if ( date.before( cal.getTime() ) )
        {
            cal.add( Calendar.YEAR, -1 );
        }
        
        return cal.getTime();
        
        //TODO Create system setting for start month, this can be different from place to place
    }
    
    private Date getStartDateOfYear( Date date )
    {
        Calendar cal = PeriodType.createCalendarInstance( date );
        
        cal.set( Calendar.MONTH, 0 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        
        return cal.getTime();
    }
    
    private Period savePeriod( Period period )
    {
        Period persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
        
        if ( persistedPeriod == null )
        {
            periodService.addPeriod( period );
        }
        else
        {
            period = persistedPeriod;
        }
        
        return period;
    }
}
