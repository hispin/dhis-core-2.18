package org.hisp.dhis.importexport.dxf.importer;

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

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.datadictionary.ExtendedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.XMLConverter;
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
import org.hisp.dhis.importexport.dxf.converter.DataElementGroupSetConverter;
import org.hisp.dhis.importexport.dxf.converter.DataElementGroupSetMemberConverter;
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
import org.hisp.dhis.importexport.dxf.converter.IndicatorGroupSetConverter;
import org.hisp.dhis.importexport.dxf.converter.IndicatorGroupSetMemberConverter;
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
import org.hisp.dhis.importexport.invoker.ConverterInvoker;
import org.hisp.dhis.importexport.locking.LockingManager;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.CategoryCategoryOptionAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.CategoryComboCategoryAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.CompleteDataSetRegistrationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryDataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryIndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryComboBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryOptionBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetSourceAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ExtendedDataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ImportDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableCategoryOptionComboBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableDataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableDataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableIndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableOrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTablePeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.SourceBatchHandler;
import org.hisp.dhis.olap.OlapURLService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.importexport.dxf.importer.V2Parser;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDXFImportService.java 6425 2008-11-22 00:08:57Z larshelg
 *          $
 */
public class DefaultDXFImportService
    implements ImportService
{
    public static final String ROOT_NAME = "dxf";

    public static final String DXF2_NAMESPACE_URI = "http://dhis2.org/ns/schema/dxf2";

    private final Log log = LogFactory.getLog( DefaultDXFImportService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
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

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
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

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private ObjectMappingGenerator objectMappingGenerator;

    public void setObjectMappingGenerator( ObjectMappingGenerator objectMappingGenerator )
    {
        this.objectMappingGenerator = objectMappingGenerator;
    }

    @SuppressWarnings( "unused" )
    private LockingManager lockingManager;

    public void setLockingManager( LockingManager lockingManager )
    {
        this.lockingManager = lockingManager;
    }

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    private ConverterInvoker converterInvoker;

    public void setConverterInvoker( ConverterInvoker converterInvoker )
    {
        this.converterInvoker = converterInvoker;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DefaultDXFImportService()
    {
        super();
    }

    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------

    public void importData( ImportParams params, InputStream inputStream )
    {
        if ( params.isPreview() )
        {
            importObjectService.deleteImportObjects();
        }

        ZipInputStream zipIn = new ZipInputStream( inputStream );

        StreamUtils.getNextZipEntry( zipIn );

        XMLReader reader = XMLFactory.getXMLReader( zipIn );

        // assume default version 1
        int dxfVersion = 1;

        // move to root element
        while ( !reader.isStartElement( ROOT_NAME ) )
        {
            reader.next();
        }

        if ( reader.getXmlStreamReader().getNamespaceURI() == DXF2_NAMESPACE_URI )
        {
            dxfVersion = 2;
            log.info( "parsing dxf version " + dxfVersion );

            try
            {
                V2Parser v2parser = new V2Parser();
                v2parser.getMetadata( reader.getXmlStreamReader() );
                v2parser.getDataValues( reader.getXmlStreamReader() );
            }
            catch ( javax.xml.bind.JAXBException ex )
            {
                log.info( "Parsing error: " + ex );
                // report something to GUI ....
            }
        }
        else
        {
            dxfVersion = 1;
            log.info( "parsing dxf version " + dxfVersion );
            parseDXFv1( params, reader );
        }

        // setMessage( "import_process_done" );

        StreamUtils.closeInputStream( zipIn );

        reader.closeReader();

        NameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }

    private void parseDXFv1( ImportParams params, XMLReader reader )
    {
        while ( reader.next() )
        {
            if ( reader.isStartElement( DataElementCategoryOptionConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_category_options" );

                BatchHandler<DataElementCategoryOption> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementCategoryOptionBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementCategoryOptionConverter( batchHandler, importObjectService,
                    categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategoryOptions" );
            }
            else if ( reader.isStartElement( DataElementCategoryConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_categories" );

                BatchHandler<DataElementCategory> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementCategoryBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementCategoryConverter( batchHandler, importObjectService,
                    categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategories" );
            }
            else if ( reader.isStartElement( DataElementCategoryComboConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_category_combos" );

                BatchHandler<DataElementCategoryCombo> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementCategoryComboBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementCategoryComboConverter( batchHandler, importObjectService,
                    categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategoryCombos" );
            }
            else if ( reader.isStartElement( DataElementCategoryOptionComboConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_category_option_combos"
                // );

                XMLConverter converter = new DataElementCategoryOptionComboConverter( importObjectService,
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), objectMappingGenerator
                        .getCategoryOptionMapping( params.skipMapping() ), categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported DataElementCategoryOptionCombos" );
            }
            // dxfv1 only
            else if ( reader.isStartElement( CategoryCategoryOptionAssociationConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_category_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( CategoryCategoryOptionAssociationBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new CategoryCategoryOptionAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getCategoryMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryOptionMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported CategoryCategoryOption associations" );
            }
            // dxfv1 only
            else if ( reader.isStartElement( CategoryComboCategoryAssociationConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_category_combo_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( CategoryComboCategoryAssociationBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new CategoryComboCategoryAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported CategoryComboCategory associations" );
            }
            else if ( reader.isStartElement( DataElementConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_elements" );

                BatchHandler<DataElement> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), dataElementService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElements" );
            }
            else if ( reader.isStartElement( ExtendedDataElementConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_elements" );

                BatchHandler<DataElement> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementBatchHandler.class );

                BatchHandler<ExtendedDataElement> extendedDataElementBatchHandler = batchHandlerFactory
                    .createBatchHandler( ExtendedDataElementBatchHandler.class );

                extendedDataElementBatchHandler.init();

                batchHandler.init();

                XMLConverter converter = new ExtendedDataElementConverter( batchHandler,
                    extendedDataElementBatchHandler, importObjectService, objectMappingGenerator
                        .getCategoryComboMapping( params.skipMapping() ), dataElementService );

                converterInvoker.invokeRead( converter, reader, params );

                extendedDataElementBatchHandler.flush();

                batchHandler.flush();

                log.info( "Imported ExtendedDataElements" );
            }
            else if ( reader.isStartElement( CalculatedDataElementConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_calculated_data_elements" );

                XMLConverter converter = new CalculatedDataElementConverter( importObjectService, dataElementService,
                    expressionService, objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), objectMappingGenerator
                        .getCategoryOptionComboMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported CalculatedDataElements" );
            }
            else if ( reader.isStartElement( DataElementGroupConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_groups" );

                BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementGroupBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementGroupConverter( batchHandler, importObjectService,
                    dataElementService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroups" );
            }
            else if ( reader.isStartElement( DataElementGroupMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataElementGroupMemberBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataElementGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroup members" );
            }
            else if ( reader.isStartElement( DataElementGroupSetConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_group_sets" );
                
                BatchHandler<DataElementGroupSet> batchHandler = batchHandlerFactory.
                    createBatchHandler( DataElementGroupSetBatchHandler.class );
                
                batchHandler.init();
                
                XMLConverter converter = new DataElementGroupSetConverter( batchHandler, importObjectService, dataElementService );
                
                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                
                log.info( "Imported DataElementGroupSets" );
            }
            else if ( reader.isStartElement( DataElementGroupSetMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_element_group_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.
                    createBatchHandler( DataElementGroupSetMemberBatchHandler.class );
                
                batchHandler.init();
                
                XMLConverter converter = new DataElementGroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementGroupMapping( params.skipMapping() ), 
                    objectMappingGenerator.getDataElementGroupSetMapping( params.skipMapping() ) );
                
                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                
                log.info( "Imported DataElementGroupSet members" );
            }
            else if ( reader.isStartElement( IndicatorTypeConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicator_types" );

                BatchHandler<IndicatorType> batchHandler = batchHandlerFactory
                    .createBatchHandler( IndicatorTypeBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new IndicatorTypeConverter( batchHandler, importObjectService,
                    indicatorService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorTypes" );
            }
            else if ( reader.isStartElement( IndicatorConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicators" );

                BatchHandler<Indicator> batchHandler = batchHandlerFactory
                    .createBatchHandler( IndicatorBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new IndicatorConverter( batchHandler, importObjectService, indicatorService,
                    expressionService, objectMappingGenerator.getIndicatorTypeMapping( params.skipMapping() ),
                    objectMappingGenerator.getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getCategoryOptionComboMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Indicators" );
            }
            else if ( reader.isStartElement( ExtendedIndicatorConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicators" );

                BatchHandler<Indicator> batchHandler = batchHandlerFactory
                    .createBatchHandler( IndicatorBatchHandler.class );

                BatchHandler<ExtendedDataElement> extendedDataElementBatchHandler = batchHandlerFactory
                    .createBatchHandler( ExtendedDataElementBatchHandler.class );

                extendedDataElementBatchHandler.init();

                batchHandler.init();

                XMLConverter converter = new ExtendedIndicatorConverter( batchHandler, extendedDataElementBatchHandler,
                    importObjectService, indicatorService, expressionService, objectMappingGenerator
                        .getIndicatorTypeMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getCategoryOptionComboMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                extendedDataElementBatchHandler.flush();

                batchHandler.flush();

                log.info( "Imported ExtendedIndicators" );
            }
            else if ( reader.isStartElement( IndicatorGroupConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicator_groups" );

                BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory
                    .createBatchHandler( IndicatorGroupBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new IndicatorGroupConverter( batchHandler, importObjectService,
                    indicatorService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroups" );
            }
            else if ( reader.isStartElement( IndicatorGroupMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicator_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( IndicatorGroupMemberBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new IndicatorGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getIndicatorMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroup members" );
            }
            else if ( reader.isStartElement( IndicatorGroupSetConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicator_group_sets" );
                
                BatchHandler<IndicatorGroupSet> batchHandler = batchHandlerFactory.
                    createBatchHandler( IndicatorGroupSetBatchHandler.class );
                
                batchHandler.init();
                
                XMLConverter converter = new IndicatorGroupSetConverter( batchHandler, importObjectService, indicatorService );
                
                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                
                log.info( "Imported IndicatorGroupSets" );
            }
            else if ( reader.isStartElement( IndicatorGroupSetMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_indicator_group_set_members" );
                
                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.
                    createBatchHandler( IndicatorGroupSetMemberBatchHandler.class );
                
                batchHandler.init();
                
                XMLConverter converter = new IndicatorGroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getIndicatorGroupMapping( params.skipMapping() ),
                    objectMappingGenerator.getIndicatorGroupSetMapping( params.skipMapping() ) );
                
                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                
                log.info( "Imported IndicatorGroupSet members" );
            }
            else if ( reader.isStartElement( DataDictionaryConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_dictionaries" );

                BatchHandler<DataDictionary> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataDictionaryBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataDictionaryConverter( batchHandler, importObjectService,
                    dataDictionaryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionaries" );
            }
            else if ( reader.isStartElement( DataDictionaryDataElementConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_dictionary_data_elements" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataDictionaryDataElementBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataDictionaryDataElementConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataDictionaryMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionary DataElements" );
            }
            else if ( reader.isStartElement( DataDictionaryIndicatorConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_dictionary_indicators" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataDictionaryIndicatorBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataDictionaryIndicatorConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataDictionaryMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionary Indicators" );
            }
            else if ( reader.isStartElement( DataSetConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_sets" );

                BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataSetConverter( batchHandler, importObjectService, dataSetService,
                    objectMappingGenerator.getPeriodTypeMapping() );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSets" );
            }
            else if ( reader.isStartElement( DataSetMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataSetMemberBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSet members" );
            }
            else if ( reader.isStartElement( OrganisationUnitConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_units" );

                BatchHandler<Source> sourceBatchHandler = batchHandlerFactory
                    .createBatchHandler( SourceBatchHandler.class );
                BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory
                    .createBatchHandler( OrganisationUnitBatchHandler.class );

                sourceBatchHandler.init();
                batchHandler.init();

                XMLConverter converter = new OrganisationUnitConverter( batchHandler, sourceBatchHandler,
                    importObjectService, organisationUnitService );

                converterInvoker.invokeRead( converter, reader, params );

                sourceBatchHandler.flush();
                batchHandler.flush();

                log.info( "Imported OrganisationUnits" );
            }
            else if ( reader.isStartElement( OrganisationUnitRelationshipConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_relationships" );

                BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory
                    .createBatchHandler( OrganisationUnitBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new OrganisationUnitRelationshipConverter( batchHandler, importObjectService,
                    organisationUnitService, objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnit relationships" );
            }
            else if ( reader.isStartElement( OrganisationUnitGroupConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_groups" );

                BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory
                    .createBatchHandler( OrganisationUnitGroupBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new OrganisationUnitGroupConverter( batchHandler, importObjectService,
                    organisationUnitGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroups" );
            }
            else if ( reader.isStartElement( OrganisationUnitGroupMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( OrganisationUnitGroupMemberBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new OrganisationUnitGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroup members" );
            }
            else if ( reader.isStartElement( GroupSetConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_group_sets" );

                BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory
                    .createBatchHandler( GroupSetBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new GroupSetConverter( batchHandler, importObjectService,
                    organisationUnitGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroupSets" );
            }
            else if ( reader.isStartElement( GroupSetMemberConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_group_set_members"
                // );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( GroupSetMemberBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new GroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
                    objectMappingGenerator.getOrganisationUnitGroupSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroupSet members" );
            }
            else if ( reader.isStartElement( OrganisationUnitLevelConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_organisation_unit_levels" );

                XMLConverter converter = new OrganisationUnitLevelConverter( organisationUnitService,
                    importObjectService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported OrganisationUnitLevels" );
            }
            else if ( reader.isStartElement( DataSetSourceAssociationConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_data_set_source_associations" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( DataSetSourceAssociationBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new DataSetSourceAssociationConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataSetMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSet Source associations" );
            }
            else if ( reader.isStartElement( ValidationRuleConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_validation_rules" );

                XMLConverter converter = new ValidationRuleConverter( importObjectService, validationRuleService,
                    expressionService, objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryOptionComboMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported ValidationRules" );
            }
            else if ( reader.isStartElement( PeriodConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_periods" );

                BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new PeriodConverter( batchHandler, importObjectService, periodService,
                    objectMappingGenerator.getPeriodTypeMapping() );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Periods" );
            }
            else if ( reader.isStartElement( ReportTableConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_tables" );

                BatchHandler<ReportTable> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableConverter( batchHandler, reportTableService,
                    importObjectService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTables" );
            }
            else if ( reader.isStartElement( ReportTableDataElementConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_dataelements" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableDataElementBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableDataElementConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getReportTableMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable DataElements" );
            }
            else if ( reader.isStartElement( ReportTableCategoryOptionComboConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_category_option_combos" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableCategoryOptionComboBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableCategoryOptionComboConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getReportTableMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryOptionComboMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable CategoryOptionCombos" );
            }
            else if ( reader.isStartElement( ReportTableIndicatorConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_indicators" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableIndicatorBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableIndicatorConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getReportTableMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable Indicators" );
            }
            else if ( reader.isStartElement( ReportTableDataSetConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_datasets" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableDataSetBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableDataSetConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getReportTableMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable DataSets" );
            }
            else if ( reader.isStartElement( ReportTablePeriodConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_periods" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTablePeriodBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTablePeriodConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getReportTableMapping( params.skipMapping() ), objectMappingGenerator
                        .getPeriodMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable Periods" );
            }
            else if ( reader.isStartElement( ReportTableOrganisationUnitConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_report_table_organisation_units" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
                    .createBatchHandler( ReportTableOrganisationUnitBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new ReportTableOrganisationUnitConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getReportTableMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTable OrganisationUnits" );
            }
            else if ( reader.isStartElement( OlapUrlConverter.COLLECTION_NAME ) )
            {
                // setMessage( "importing_olap_urls" );

                XMLConverter converter = new OlapUrlConverter( importObjectService, olapURLService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported OlapURLs" );
            }
            else if ( reader.isStartElement( CompleteDataSetRegistrationConverter.COLLECTION_NAME )
                && params.isDataValues() )
            {
                // setMessage( "importing_complete_data_set_registrations" );

                BatchHandler<CompleteDataSetRegistration> batchHandler = batchHandlerFactory
                    .createBatchHandler( CompleteDataSetRegistrationBatchHandler.class );

                batchHandler.init();

                XMLConverter converter = new CompleteDataSetRegistrationConverter( batchHandler, importObjectService,
                    params, objectMappingGenerator.getDataSetMapping( params.skipMapping() ), objectMappingGenerator
                        .getPeriodMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported CompleteDataSetRegistrations" );
            }
            else if ( reader.isStartElement( DataValueConverter.COLLECTION_NAME ) && params.isDataValues() )
            {
                /*if ( params.skipMapping() == false && lockingManager.currentImportContainsLockedData() )
                {
                    // setMessage( "import_contains_data_for_locked_periods" );

                    log.warn( "Skipped DataValues because import file contained DataValues for locked Period, Organisation Unit and DataSet combinations" );
                }
                else */
                {
                    BatchHandler<DataValue> batchHandler = batchHandlerFactory
                        .createBatchHandler( DataValueBatchHandler.class );

                    BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory
                        .createBatchHandler( ImportDataValueBatchHandler.class );

                    batchHandler.init();

                    importDataValueBatchHandler.init();

                    XMLConverter converter = new DataValueConverter( batchHandler, importDataValueBatchHandler,
                        dataValueService, dataMartStore, importObjectService, params, objectMappingGenerator
                            .getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                            .getPeriodMapping( params.skipMapping() ), objectMappingGenerator
                            .getOrganisationUnitMapping( params.skipMapping() ), objectMappingGenerator
                            .getCategoryOptionComboMapping( params.skipMapping() ) );

                    converterInvoker.invokeRead( converter, reader, params );

                    batchHandler.flush();

                    importDataValueBatchHandler.flush();

                    log.info( "Imported DataValues" );
                }
            }
        }
    }
}
