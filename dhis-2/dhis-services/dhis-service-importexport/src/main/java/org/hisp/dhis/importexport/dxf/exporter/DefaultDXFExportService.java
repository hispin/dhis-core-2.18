package org.hisp.dhis.importexport.dxf.exporter;

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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hibernate.SessionFactory;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportPipeThread;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.importexport.dxf.converter.AggregatedDataValueConverter;
import org.hisp.dhis.importexport.dxf.converter.CalculatedDataElementConverter;
import org.hisp.dhis.importexport.dxf.converter.CategoryCategoryOptionAssociationConverter;
import org.hisp.dhis.importexport.dxf.converter.CategoryComboCategoryAssociationConverter;
import org.hisp.dhis.importexport.dxf.converter.CompleteDataSetRegistrationConverter;
import org.hisp.dhis.importexport.dxf.converter.DataDictionaryConverter;
import org.hisp.dhis.importexport.dxf.converter.DataDictionaryDataElementConverter;
import org.hisp.dhis.importexport.dxf.converter.DataDictionaryIndicatorConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementCategoryComboConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementCategoryConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementCategoryOptionComboConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementCategoryOptionConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementGroupConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementGroupMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.DataSetConverter;
import org.hisp.dhis.importexport.dxf.converter.DataSetMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.DataSetSourceAssociationConverter;
import org.hisp.dhis.importexport.dxf.converter.DataValueConverter;
import org.hisp.dhis.importexport.dxf.converter.ExtendedDataElementConverter;
import org.hisp.dhis.importexport.dxf.converter.ExtendedIndicatorConverter;
import org.hisp.dhis.importexport.dxf.converter.GroupSetConverter;
import org.hisp.dhis.importexport.dxf.converter.GroupSetMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.IndicatorConverter;
import org.hisp.dhis.importexport.dxf.converter.IndicatorGroupConverter;
import org.hisp.dhis.importexport.dxf.converter.IndicatorGroupMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.IndicatorTypeConverter;
import org.hisp.dhis.importexport.dxf.converter.OlapUrlConverter;
import org.hisp.dhis.importexport.dxf.converter.OrganisationUnitConverter;
import org.hisp.dhis.importexport.dxf.converter.OrganisationUnitGroupConverter;
import org.hisp.dhis.importexport.dxf.converter.OrganisationUnitGroupMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.OrganisationUnitLevelConverter;
import org.hisp.dhis.importexport.dxf.converter.OrganisationUnitRelationshipConverter;
import org.hisp.dhis.importexport.dxf.converter.PeriodConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableCategoryOptionComboConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableDataElementConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableDataSetConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableIndicatorConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTableOrganisationUnitConverter;
import org.hisp.dhis.importexport.dxf.converter.ReportTablePeriodConverter;
import org.hisp.dhis.importexport.dxf.converter.ValidationRuleConverter;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.olap.OlapURLService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.validation.ValidationRuleService;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDXFExportService.java 5960 2008-10-17 14:07:50Z larshelg $
 */
public class DefaultDXFExportService
    implements ExportService
{
    private static final String ZIP_ENTRY_NAME = "Export.xml";
    private static final String ROOT_NAME = "dxf";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private DataElementCategoryOptionService categoryOptionService;

    public void setCategoryOptionService( DataElementCategoryOptionService categoryOptionService )
    {
        this.categoryOptionService = categoryOptionService;
    }

    private DataElementCategoryComboService categoryComboService;

    public void setCategoryComboService( DataElementCategoryComboService categoryComboService )
    {
        this.categoryComboService = categoryComboService;
    }

    private DataElementCategoryOptionComboService categoryOptionComboService;

    public void setCategoryOptionComboService( DataElementCategoryOptionComboService categoryOptionComboService )
    {
        this.categoryOptionComboService = categoryOptionComboService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }
    
    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }
    
    private OlapURLService olapURLService;

    public void setOlapURLService( OlapURLService olapURLService )
    {
        this.olapURLService = olapURLService;
    }

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    public void setCompleteDataSetRegistrationService( CompleteDataSetRegistrationService completeDataSetRegistrationService )
    {
        this.completeDataSetRegistrationService = completeDataSetRegistrationService;
    }
    
    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // ExportService implementation
    // -------------------------------------------------------------------------

    public InputStream exportData( ExportParams params )
    {
        try
        {
            // -----------------------------------------------------------------
            // Pipes are input/output pairs. Data written on the output stream 
            // shows up on the input stream at the other end of the pipe. 
            // -----------------------------------------------------------------
            
            PipedOutputStream out = new PipedOutputStream();
            
            PipedInputStream in = new PipedInputStream( out );
            
            ZipOutputStream zipOut = new ZipOutputStream( out );
            
            zipOut.putNextEntry( new ZipEntry( ZIP_ENTRY_NAME ) );

            XMLWriter writer = XMLFactory.getPlainXMLWriter( zipOut );
            
            // -----------------------------------------------------------------
            // Writes to one end of the pipe 
            // -----------------------------------------------------------------
            
            ExportPipeThread thread = new ExportPipeThread( sessionFactory );
            
            thread.setZipOutputStream( zipOut );
            thread.setParams( params );
            thread.setWriter( writer );
            thread.setRootName( ROOT_NAME );
            
            thread.registerXMLConverter( new DataElementCategoryOptionConverter( categoryOptionService ) );
            thread.registerXMLConverter( new DataElementCategoryConverter( categoryService ) );
            thread.registerXMLConverter( new DataElementCategoryComboConverter( categoryComboService ) );
            thread.registerXMLConverter( new DataElementCategoryOptionComboConverter( categoryOptionComboService ) );
            
            thread.registerXMLConverter( new CategoryCategoryOptionAssociationConverter( categoryService, categoryOptionService ) );
            thread.registerXMLConverter( new CategoryComboCategoryAssociationConverter( categoryComboService, categoryService ) );
            
            thread.registerXMLConverter( params.isExtendedMode() ? 
                new ExtendedDataElementConverter( dataElementService ) : new DataElementConverter( dataElementService ) );            
            thread.registerXMLConverter( new CalculatedDataElementConverter( dataElementService ) );
            thread.registerXMLConverter( new DataElementGroupConverter( dataElementService ) );
            thread.registerXMLConverter( new DataElementGroupMemberConverter( dataElementService ) );
            
            thread.registerXMLConverter( new IndicatorTypeConverter( indicatorService ) );
            thread.registerXMLConverter( params.isExtendedMode() ? 
                new ExtendedIndicatorConverter( indicatorService ) : new IndicatorConverter( indicatorService ) );
            thread.registerXMLConverter( new IndicatorGroupConverter( indicatorService ) );
            thread.registerXMLConverter( new IndicatorGroupMemberConverter( indicatorService ) );
            
            thread.registerXMLConverter( new DataDictionaryConverter( dataDictionaryService ) );
            thread.registerXMLConverter( new DataDictionaryDataElementConverter( dataDictionaryService ) );
            thread.registerXMLConverter( new DataDictionaryIndicatorConverter( dataDictionaryService ) );
                        
            thread.registerXMLConverter( new DataSetConverter( dataSetService ) );
            thread.registerXMLConverter( new DataSetMemberConverter( dataSetService, dataElementService ) );
            
            thread.registerXMLConverter( new OrganisationUnitConverter( organisationUnitService ) );
            thread.registerXMLConverter( new OrganisationUnitRelationshipConverter( organisationUnitService ) );
            thread.registerXMLConverter( new OrganisationUnitGroupConverter( organisationUnitGroupService ) );
            thread.registerXMLConverter( new OrganisationUnitGroupMemberConverter( organisationUnitGroupService, organisationUnitService ) );
            
            thread.registerXMLConverter( new GroupSetConverter( organisationUnitGroupService ) );
            thread.registerXMLConverter( new GroupSetMemberConverter( organisationUnitGroupService ) );
            thread.registerXMLConverter( new OrganisationUnitLevelConverter( organisationUnitService ) );
            
            thread.registerXMLConverter( new DataSetSourceAssociationConverter( dataSetService, organisationUnitService ) );
            
            thread.registerXMLConverter( new ValidationRuleConverter( validationRuleService ) );
            
            thread.registerXMLConverter( new PeriodConverter( periodService ) );
            
            thread.registerXMLConverter( new ReportTableConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTableDataElementConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTableCategoryOptionComboConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTableIndicatorConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTableDataSetConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTablePeriodConverter( reportTableService ) );
            thread.registerXMLConverter( new ReportTableOrganisationUnitConverter( reportTableService ) );
            
            thread.registerXMLConverter( new OlapUrlConverter( olapURLService ) );
            
            thread.registerXMLConverter( new CompleteDataSetRegistrationConverter( 
                completeDataSetRegistrationService, dataSetService, organisationUnitService, periodService ) );
            
            thread.registerXMLConverter( params.isAggregatedData() ? 
                new AggregatedDataValueConverter( dataMartStore, dataSetService, periodService ) : 
                new DataValueConverter( dataMartStore, statementManager, periodService ) );
            
            thread.start();
            
            // -----------------------------------------------------------------
            // Reads at the other end of the pipe 
            // -----------------------------------------------------------------
            
            InputStream bis = new BufferedInputStream( in );
            
            return bis;
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Error occured during export to stream", ex );
        }
    }
}
