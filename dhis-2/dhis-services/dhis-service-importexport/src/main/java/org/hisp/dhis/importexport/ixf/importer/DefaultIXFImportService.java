package org.hisp.dhis.importexport.ixf.importer;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.invoker.ConverterInvoker;
import org.hisp.dhis.importexport.ixf.converter.IndicatorConverter;
import org.hisp.dhis.importexport.ixf.converter.SourceConverter;
import org.hisp.dhis.importexport.ixf.converter.TimePeriodConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.SourceBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultIXFImportService.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public class DefaultIXFImportService
    implements ImportService
{
    private static final Log log = LogFactory.getLog( DefaultIXFImportService.class );
    
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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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

    public DefaultIXFImportService()
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
        
        ZipInputStream zipIn = new ZipInputStream ( inputStream );
        
        StreamUtils.getNextZipEntry( zipIn );
        
        XMLReader reader = XMLFactory.getXMLReader( zipIn );
                
        while ( reader.next() )
        {
            if ( reader.isStartElement( TimePeriodConverter.COLLECTION_NAME ) )
            {
                //setMessage( "importing_periods" );
                
                BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );
                
                batchHandler.init();
                
                XMLConverter converter = new TimePeriodConverter( batchHandler, 
                    periodService, importObjectService, objectMappingGenerator.getPeriodTypeMapping() );
                
                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                
                log.info( "Imported Periods" );
            }
            else if ( reader.isStartElement( SourceConverter.COLLECTION_NAME ) )
            {
                //setMessage( "importing_organisation_units" );
                
                BatchHandler<Source> sourceBatchHandler = batchHandlerFactory.createBatchHandler( SourceBatchHandler.class );
                BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );
                
                sourceBatchHandler.init();
                batchHandler.init();
                
                XMLConverter converter = new SourceConverter( batchHandler,
                    sourceBatchHandler, importObjectService, organisationUnitService );

                converterInvoker.invokeRead( converter, reader, params );
                
                sourceBatchHandler.flush();
                batchHandler.flush();
                
                log.info( "Imported OrganisationUnits" );
            }
            else if ( reader.isStartElement( IndicatorConverter.COLLECTION_NAME ) )
            {
                //setMessage( "importing_data_elements" );

                BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );                
                BatchHandler<DataValue> dataValueBatchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class );
                
                DataElementCategoryCombo categoryCombo = categoryComboService.
                    getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
                
                DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboService.
                    getDefaultDataElementCategoryOptionCombo();
                
                batchHandler.init();
                dataValueBatchHandler.init();
                
                XMLConverter converter = new IndicatorConverter( batchHandler, 
                    dataElementService, 
                    importObjectService,
                    dataMartStore,
                    categoryCombo, 
                    dataValueBatchHandler,
                    categoryOptionCombo,
                    objectMappingGenerator.getPeriodMapping( params.skipMapping() ),
                    objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );
                
                batchHandler.flush();
                dataValueBatchHandler.flush();
                
                log.info( "Imported DataElements" );
            }
        }
        
        //setMessage( "import_process_done" );
        
        StreamUtils.closeInputStream( zipIn );
        
        reader.closeReader();
        
        NameMappingUtil.clearMapping();
        
        cacheManager.clearCache();
    }
}
