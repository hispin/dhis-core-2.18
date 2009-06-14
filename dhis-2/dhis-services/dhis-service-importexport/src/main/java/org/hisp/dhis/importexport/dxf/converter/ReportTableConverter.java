package org.hisp.dhis.importexport.dxf.converter;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractReportTableConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.reporttable.RelativePeriods;
import org.hisp.dhis.reporttable.ReportParams;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableConverter
    extends AbstractReportTableConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "reportTables";
    public static final String ELEMENT_NAME = "reportTable";

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TABLE_NAME = "tableName";
    private static final String FIELD_EXISTING_TABLE_NAME = "existingTableName";
    private static final String FIELD_MODE = "mode";
    private static final String FIELD_REGRESSION = "regression";
            
    private static final String FIELD_DO_INDICATORS = "doIndicators";
    private static final String FIELD_DO_CATEGORY_OPTION_COMBOS = "doCategoryOptionCombos";
    private static final String FIELD_DO_PERIODS = "doPeriods";
    private static final String FIELD_DO_ORGANISATION_UNITS = "doOrganisationUnits";
    
    private static final String FIELD_REPORTING_MONTH = "reportingMonth";
    private static final String FIELD_LAST_3_MONTHS = "last3Months";
    private static final String FIELD_LAST_6_MONTHS = "last6Months";
    private static final String FIELD_LAST_9_MONTHS = "last9Months";
    private static final String FIELD_LAST_12_MONTHS = "last12Months";
    private static final String FIELD_SO_FAR_THIS_YEAR = "soFarThisYear";
    private static final String FIELD_SO_FAR_THIS_FINANCIAL_YEAR = "soFarThisFinancialYear";
    private static final String FIELD_LAST_3_TO_6_MONTHS = "last3To6Months";
    private static final String FIELD_LAST_6_TO_9_MONTHS = "last6To9Months";
    private static final String FIELD_LAST_9_TO_12_MONTHS = "last9To12Months";
    private static final String FIELD_LAST_12_INDIVIDUAL_MONHTS = "last12IndividualMonths";
    private static final String FIELD_INDIVIDUAL_MONTHS_THIS_YEAR = "individualMonthsThisYear";
    private static final String FIELD_INDIVIDUAL_QUARTERS_THIS_YEAR = "individualQuartersThisYear";
    
    private static final String FIELD_PARAM_REPORTING_MONTH = "paramReportingMonth";
    private static final String FIELD_PARAM_PARENT_ORG_UNIT = "paramParentOrganisationUnit";
    private static final String FIELD_PARAM_ORG_UNIT = "paramOrganisationUnit";
        
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public ReportTableConverter( ReportTableService reportTableService )
    {   
        this.reportTableService = reportTableService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param batchHandler BatchHandler
     * @param reportTableStore ReportTableStore
     * @param importObjectService ImportObjectService
     */
    public ReportTableConverter( BatchHandler batchHandler, 
        ReportTableService reportTableService,
        ImportObjectService importObjectService )
    {
        this.batchHandler = batchHandler;
        this.reportTableService = reportTableService;
        this.importObjectService = importObjectService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<ReportTable> reportTables = reportTableService.getReportTables( params.getReportTables() );
        
        if ( reportTables != null && reportTables.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( ReportTable reportTable : reportTables )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( reportTable.getId() ) );
                writer.writeElement( FIELD_NAME, reportTable.getName() );
                writer.writeElement( FIELD_TABLE_NAME, reportTable.getTableName() );
                writer.writeElement( FIELD_EXISTING_TABLE_NAME, reportTable.getExistingTableName() );
                writer.writeElement( FIELD_MODE, reportTable.getMode() );
                writer.writeElement( FIELD_REGRESSION, String.valueOf( reportTable.isRegression() ) );
                
                writer.writeElement( FIELD_DO_INDICATORS, String.valueOf( reportTable.isDoIndicators() ) );
                writer.writeElement( FIELD_DO_CATEGORY_OPTION_COMBOS, String.valueOf( reportTable.isDoCategoryOptionCombos() ) );
                writer.writeElement( FIELD_DO_PERIODS, String.valueOf( reportTable.isDoPeriods() ) );
                writer.writeElement( FIELD_DO_ORGANISATION_UNITS, String.valueOf( reportTable.isDoUnits() ) );
                
                writer.writeElement( FIELD_REPORTING_MONTH, String.valueOf( reportTable.getRelatives().isReportingMonth() ) );
                writer.writeElement( FIELD_LAST_3_MONTHS, String.valueOf( reportTable.getRelatives().isLast3Months() ) );
                writer.writeElement( FIELD_LAST_6_MONTHS, String.valueOf( reportTable.getRelatives().isLast6Months() ) );
                writer.writeElement( FIELD_LAST_9_MONTHS, String.valueOf( reportTable.getRelatives().isLast9Months() ) );
                writer.writeElement( FIELD_LAST_12_MONTHS, String.valueOf( reportTable.getRelatives().isLast12Months() ) );
                writer.writeElement( FIELD_SO_FAR_THIS_YEAR, String.valueOf( reportTable.getRelatives().isSoFarThisYear() ) );
                writer.writeElement( FIELD_SO_FAR_THIS_FINANCIAL_YEAR, String.valueOf( reportTable.getRelatives().isSoFarThisFinancialYear() ) );
                writer.writeElement( FIELD_LAST_3_TO_6_MONTHS, String.valueOf( reportTable.getRelatives().isLast3To6Months() ) );
                writer.writeElement( FIELD_LAST_6_TO_9_MONTHS, String.valueOf( reportTable.getRelatives().isLast6To9Months() ) );
                writer.writeElement( FIELD_LAST_9_TO_12_MONTHS, String.valueOf( reportTable.getRelatives().isLast9To12Months() ) );
                writer.writeElement( FIELD_LAST_12_INDIVIDUAL_MONHTS, String.valueOf( reportTable.getRelatives().isLast12IndividualMonths() ) );
                writer.writeElement( FIELD_INDIVIDUAL_MONTHS_THIS_YEAR, String.valueOf( reportTable.getRelatives().isIndividualMonthsThisYear() ) );
                writer.writeElement( FIELD_INDIVIDUAL_QUARTERS_THIS_YEAR, String.valueOf( reportTable.getRelatives().isIndividualQuartersThisYear() ) );
                
                writer.writeElement( FIELD_PARAM_REPORTING_MONTH, String.valueOf( reportTable.getReportParams().isParamReportingMonth() ) );
                writer.writeElement( FIELD_PARAM_PARENT_ORG_UNIT, String.valueOf( reportTable.getReportParams().isParamParentOrganisationUnit() ) );
                writer.writeElement( FIELD_PARAM_ORG_UNIT, String.valueOf( reportTable.getReportParams().isParamOrganisationUnit() ) ); 
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final ReportTable reportTable = new ReportTable();
            
            final RelativePeriods relatives = new RelativePeriods();
            reportTable.setRelatives( relatives );
            
            final ReportParams reportParams = new ReportParams();
            reportTable.setReportParams( reportParams );
            
            reportTable.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            reportTable.setName( values.get( FIELD_NAME ) );
            reportTable.setTableName( values.get( FIELD_TABLE_NAME ) );
            reportTable.setExistingTableName( values.get( FIELD_EXISTING_TABLE_NAME ) );
            reportTable.setMode( values.get( FIELD_MODE ) );
            reportTable.setRegression( Boolean.parseBoolean( values.get( FIELD_REGRESSION ) ) );
            
            reportTable.setDoIndicators( Boolean.parseBoolean( values.get( FIELD_DO_INDICATORS ) ) );
            reportTable.setDoCategoryOptionCombos( Boolean.parseBoolean( values.get( FIELD_DO_CATEGORY_OPTION_COMBOS ) ) );
            reportTable.setDoPeriods( Boolean.parseBoolean( values.get( FIELD_DO_PERIODS ) ) );
            reportTable.setDoUnits( Boolean.parseBoolean( values.get( FIELD_DO_ORGANISATION_UNITS ) ) );
            
            reportTable.getRelatives().setReportingMonth( Boolean.parseBoolean( values.get( FIELD_REPORTING_MONTH ) ) );            
            reportTable.getRelatives().setLast3Months( Boolean.parseBoolean( values.get( FIELD_LAST_3_MONTHS ) ) );
            reportTable.getRelatives().setLast6Months( Boolean.parseBoolean( values.get( FIELD_LAST_6_MONTHS ) ) );
            reportTable.getRelatives().setLast9Months( Boolean.parseBoolean( values.get( FIELD_LAST_9_MONTHS ) ) );
            reportTable.getRelatives().setLast12Months( Boolean.parseBoolean( values.get( FIELD_LAST_12_MONTHS ) ) );
            reportTable.getRelatives().setSoFarThisYear( Boolean.parseBoolean( values.get( FIELD_SO_FAR_THIS_YEAR ) ) );
            reportTable.getRelatives().setSoFarThisFinancialYear( Boolean.parseBoolean( values.get( FIELD_SO_FAR_THIS_FINANCIAL_YEAR ) ) );
            reportTable.getRelatives().setLast3To6Months( Boolean.parseBoolean( values.get( FIELD_LAST_3_TO_6_MONTHS ) ) );
            reportTable.getRelatives().setLast6To9Months( Boolean.parseBoolean( values.get( FIELD_LAST_6_TO_9_MONTHS ) ) );
            reportTable.getRelatives().setLast9To12Months( Boolean.parseBoolean( values.get( FIELD_LAST_9_TO_12_MONTHS ) ) );
            reportTable.getRelatives().setLast12IndividualMonths( Boolean.parseBoolean( values.get( FIELD_LAST_12_INDIVIDUAL_MONHTS ) ) );
            reportTable.getRelatives().setIndividualMonthsThisYear( Boolean.parseBoolean( values.get( FIELD_INDIVIDUAL_MONTHS_THIS_YEAR ) ) );
            reportTable.getRelatives().setIndividualQuartersThisYear( Boolean.parseBoolean( values.get( FIELD_INDIVIDUAL_QUARTERS_THIS_YEAR ) ) );
            
            reportTable.getReportParams().setParamReportingMonth( Boolean.parseBoolean( values.get( FIELD_PARAM_REPORTING_MONTH ) ) );
            reportTable.getReportParams().setParamParentOrganisationUnit( Boolean.parseBoolean( values.get( FIELD_PARAM_PARENT_ORG_UNIT ) ) );
            reportTable.getReportParams().setParamOrganisationUnit( Boolean.parseBoolean( values.get( FIELD_PARAM_ORG_UNIT ) ) );
            
            NameMappingUtil.addReportTableMapping( reportTable.getId(), reportTable.getName() );
            
            read( reportTable, GroupMemberType.NONE, params );
        }        
    }
}
