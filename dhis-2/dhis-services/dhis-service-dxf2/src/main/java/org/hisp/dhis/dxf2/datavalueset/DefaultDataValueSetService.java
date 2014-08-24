package org.hisp.dhis.dxf2.datavalueset;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang.StringUtils.trimToNull;
import static org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty.UUID;
import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;
import static org.hisp.dhis.system.util.ConversionUtils.wrap;
import static org.hisp.dhis.system.util.DateUtils.getDefaultDate;
import static org.hisp.dhis.system.util.DateUtils.parseDate;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.factory.XMLFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormUtil;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.DebugUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.csvreader.CsvReader;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataValueSetService
    implements DataValueSetService
{
    private static final Log log = LogFactory.getLog( DefaultDataValueSetService.class );

    private static final String ERROR_INVALID_DATA_SET = "Invalid data set: ";

    private static final String ERROR_INVALID_PERIOD = "Invalid period: ";

    private static final String ERROR_INVALID_ORG_UNIT = "Invalid org unit: ";

    private static final String ERROR_OBJECT_NEEDED_TO_COMPLETE = "Must be provided to complete data set";

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private BatchHandlerFactory batchHandlerFactory;

    @Autowired
    private CompleteDataSetRegistrationService registrationService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataValueSetStore dataValueSetStore;

    @Autowired
    private Notifier notifier;

    //--------------------------------------------------------------------------
    // DataValueSet implementation
    //--------------------------------------------------------------------------

    @Override
    public void writeDataValueSetXml( String dataSet, String period, String orgUnit, OutputStream out )
    {
        DataSet dataSet_ = dataSetService.getDataSet( dataSet );
        Period period_ = PeriodType.getPeriodFromIsoString( period );
        OrganisationUnit orgUnit_ = organisationUnitService.getOrganisationUnit( orgUnit );

        if ( dataSet_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_DATA_SET + dataSet );
        }

        if ( period_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_PERIOD + period );
        }

        if ( orgUnit_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_ORG_UNIT + orgUnit );
        }

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo(); //TODO

        CompleteDataSetRegistration registration = registrationService
            .getCompleteDataSetRegistration( dataSet_, period_, orgUnit_, optionCombo );

        Date completeDate = registration != null ? registration.getDate() : null;

        period_ = periodService.reloadPeriod( period_ );

        dataValueSetStore.writeDataValueSetXml( newHashSet( dataSet_ ), completeDate, period_, orgUnit_, wrap( period_ ),
            wrap( orgUnit_ ), out );
    }

    @Override
    public void writeDataValueSetXml( Set<String> dataSets, Date startDate, Date endDate, Set<String> orgUnits, 
        boolean includeChildren, OutputStream out )
    {
        Set<DataSet> ds = new HashSet<>( dataSetService.getDataSetsByUid( dataSets ) );
        Set<Period> pe = new HashSet<>( periodService.getPeriodsBetweenDates( startDate, endDate ) );
        Set<OrganisationUnit> ou = new HashSet<>( organisationUnitService.getOrganisationUnitsByUid( orgUnits ) );

        if ( ds.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one data set must be specified" );
        }
        
        if ( pe.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one period must be specified" );
        }
        
        if ( ou.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one organisation unit must be specified" );
        }
        
        if ( includeChildren )
        {
            ou = new HashSet<>( organisationUnitService.getOrganisationUnitsWithChildren( IdentifiableObjectUtils.getUids( ou ) ) );
        }
        
        dataValueSetStore.writeDataValueSetXml( ds, null, null, null, pe, ou, out );
    }

    @Override
    public void writeDataValueSetJson( String dataSet, String period, String orgUnit, OutputStream outputStream )
    {
        DataSet dataSet_ = dataSetService.getDataSet( dataSet );
        Period period_ = PeriodType.getPeriodFromIsoString( period );
        OrganisationUnit orgUnit_ = organisationUnitService.getOrganisationUnit( orgUnit );

        if ( dataSet_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_DATA_SET + dataSet );
        }

        if ( period_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_PERIOD + period );
        }

        if ( orgUnit_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_ORG_UNIT + orgUnit );
        }

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo(); //TODO

        CompleteDataSetRegistration registration = registrationService
            .getCompleteDataSetRegistration( dataSet_, period_, orgUnit_, optionCombo );

        Date completeDate = registration != null ? registration.getDate() : null;

        period_ = periodService.reloadPeriod( period_ );

        dataValueSetStore.writeDataValueSetJson( newHashSet( dataSet_ ), completeDate, period_, orgUnit_, wrap( period_ ),
            wrap( orgUnit_ ), outputStream );
    }

    @Override
    public void writeDataValueSetJson( Date lastUpdated, OutputStream outputStream )
    {
        dataValueSetStore.writeDataValueSetJson( lastUpdated, outputStream );
    }

    @Override
    public void writeDataValueSetJson( Set<String> dataSets, Date startDate, Date endDate, Set<String> orgUnits,
        boolean includeChildren, OutputStream outputStream )
    {
        Set<DataSet> ds = new HashSet<>( dataSetService.getDataSetsByUid( dataSets ) );
        Set<Period> pe = new HashSet<>( periodService.getPeriodsBetweenDates( startDate, endDate ) );
        Set<OrganisationUnit> ou = new HashSet<>( organisationUnitService.getOrganisationUnitsByUid( orgUnits ) );

        if ( ds.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one data set must be specified" );
        }
        
        if ( pe.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one period must be specified" );
        }
        
        if ( ou.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one organisation unit must be specified" );
        }
        
        if ( includeChildren )
        {
            ou = new HashSet<>( organisationUnitService.getOrganisationUnitsWithChildren( IdentifiableObjectUtils.getUids( ou ) ) );
        }

        dataValueSetStore.writeDataValueSetJson( ds, null, null, null, pe, ou, outputStream );
    }

    @Override
    public void writeDataValueSetCsv( Set<String> dataSets, Date startDate, Date endDate, Set<String> orgUnits,
        boolean includeChildren, Writer writer )
    {
        Set<DataSet> ds = new HashSet<>( dataSetService.getDataSetsByUid( dataSets ) );
        Set<Period> pe = new HashSet<>( periodService.getPeriodsBetweenDates( startDate, endDate ) );
        Set<OrganisationUnit> ou = new HashSet<>( organisationUnitService.getOrganisationUnitsByUid( orgUnits ) );

        if ( ds.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one data set must be specified" );
        }
        
        if ( pe.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one period must be specified" );
        }
        
        if ( ou.isEmpty() )
        {
            throw new IllegalArgumentException( "At least one organisation unit must be specified" );
        }
        
        if ( includeChildren )
        {
            ou = new HashSet<>( organisationUnitService.getOrganisationUnitsWithChildren( IdentifiableObjectUtils.getUids( ou ) ) );
        }

        dataValueSetStore.writeDataValueSetCsv( ds, pe, ou, writer );
    }

    @Override
    public RootNode getDataValueSetTemplate( DataSet dataSet, Period period, List<String> orgUnits,
        boolean writeComments, String ouScheme, String deScheme )
    {
        RootNode rootNode = new RootNode( "dataValueSet" );
        rootNode.setNamespace( DxfNamespaces.DXF_2_0 );
        rootNode.setComment( "Data set: " + dataSet.getDisplayName() + " (" + dataSet.getUid() + ")" );

        CollectionNode collectionNode = rootNode.addChild( new CollectionNode( "dataValues" ) );
        collectionNode.setWrapping( false );

        if ( orgUnits.isEmpty() )
        {
            for ( DataElement dataElement : dataSet.getDataElements() )
            {
                CollectionNode collection = getDataValueTemplate( dataElement, deScheme, null, ouScheme, period,
                    writeComments );
                collectionNode.addChildren( collection.getChildren() );
            }
        }
        else
        {
            for ( String orgUnit : orgUnits )
            {
                OrganisationUnit organisationUnit = identifiableObjectManager.search( OrganisationUnit.class, orgUnit );

                if ( organisationUnit == null )
                {
                    continue;
                }

                for ( DataElement dataElement : dataSet.getDataElements() )
                {
                    CollectionNode collection = getDataValueTemplate( dataElement, deScheme, organisationUnit, ouScheme,
                        period, writeComments );
                    collectionNode.addChildren( collection.getChildren() );
                }
            }
        }

        return rootNode;
    }

    private CollectionNode getDataValueTemplate( DataElement dataElement, String deScheme,
        OrganisationUnit organisationUnit, String ouScheme, Period period, boolean comment )
    {
        CollectionNode collectionNode = new CollectionNode( "dataValues" );
        collectionNode.setWrapping( false );

        for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getSortedOptionCombos() )
        {
            ComplexNode complexNode = collectionNode.addChild( new ComplexNode( "dataValue" ) );

            String label = dataElement.getDisplayName();

            if ( !categoryOptionCombo.isDefault() )
            {
                label += " " + categoryOptionCombo.getDisplayName();
            }

            if ( comment )
            {
                complexNode.setComment( "Data element: " + label );
            }

            if ( IdentifiableObject.IdentifiableProperty.CODE.toString().toLowerCase()
                .equals( deScheme.toLowerCase() ) )
            {
                SimpleNode simpleNode = complexNode.addChild( new SimpleNode( "dataElement", dataElement.getCode() ) );
                simpleNode.setAttribute( true );
            }
            else
            {
                SimpleNode simpleNode = complexNode.addChild( new SimpleNode( "dataElement", dataElement.getUid() ) );
                simpleNode.setAttribute( true );
            }

            SimpleNode simpleNode = complexNode.addChild( new SimpleNode( "categoryOptionCombo", categoryOptionCombo.getUid() ) );
            simpleNode.setAttribute( true );

            simpleNode = complexNode.addChild( new SimpleNode( "period", period != null ? period.getIsoDate() : "" ) );
            simpleNode.setAttribute( true );

            if ( organisationUnit != null )
            {
                if ( IdentifiableObject.IdentifiableProperty.CODE.toString().toLowerCase().equals( ouScheme.toLowerCase() ) )
                {
                    simpleNode = complexNode.addChild( new SimpleNode( "orgUnit",
                        organisationUnit.getCode() == null ? "" : organisationUnit.getCode() ) );
                    simpleNode.setAttribute( true );
                }
                else
                {
                    simpleNode = complexNode.addChild( new SimpleNode( "orgUnit",
                        organisationUnit.getUid() == null ? "" : organisationUnit.getUid() ) );
                    simpleNode.setAttribute( true );
                }
            }

            simpleNode = complexNode.addChild( new SimpleNode( "value", "" ) );
            simpleNode.setAttribute( true );
        }

        return collectionNode;
    }

    public ImportSummary saveDataValueSet( InputStream in )
    {
        return saveDataValueSet( in, ImportOptions.getDefaultImportOptions(), null );
    }

    public ImportSummary saveDataValueSetJson( InputStream in )
    {
        return saveDataValueSetJson( in, ImportOptions.getDefaultImportOptions(), null );
    }

    public ImportSummary saveDataValueSet( InputStream in, ImportOptions importOptions )
    {
        return saveDataValueSet( in, importOptions, null );
    }

    public ImportSummary saveDataValueSetJson( InputStream in, ImportOptions importOptions )
    {
        return saveDataValueSetJson( in, importOptions, null );
    }

    public ImportSummary saveDataValueSet( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = new StreamingDataValueSet( XMLFactory.getXMLReader( in ) );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetJson( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = JacksonUtils.fromJson( in, DataValueSet.class );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( Exception ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetCsv( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = new StreamingCsvDataValueSet( new CsvReader( in, Charset.forName( "UTF-8" ) ) );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.clear( id ).notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    public ImportSummary saveDataValueSetPdf( InputStream in, ImportOptions importOptions, TaskId id )
    {
        try
        {
            DataValueSet dataValueSet = PdfDataEntryFormUtil.getDataValueSet( in );
            return saveDataValueSet( importOptions, id, dataValueSet );
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.clear( id ).notify( id, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() );
        }
    }

    private ImportSummary saveDataValueSet( ImportOptions importOptions, TaskId id, DataValueSet dataValueSet )
    {
        log.debug( "Import options: " + importOptions );
        notifier.clear( id ).notify( id, "Process started" );

        ImportSummary summary = new ImportSummary();

        importOptions = importOptions != null ? importOptions : ImportOptions.getDefaultImportOptions();

        IdentifiableProperty dataElementIdScheme = dataValueSet.getDataElementIdScheme() != null ?
            IdentifiableProperty.valueOf( dataValueSet.getDataElementIdScheme().toUpperCase() ) :
            importOptions.getDataElementIdScheme();
        IdentifiableProperty orgUnitIdScheme = dataValueSet.getOrgUnitIdScheme() != null ?
            IdentifiableProperty.valueOf( dataValueSet.getOrgUnitIdScheme().toUpperCase() ) :
            importOptions.getOrgUnitIdScheme();
        boolean dryRun = dataValueSet.getDryRun() != null ? dataValueSet.getDryRun() : importOptions.isDryRun();
        ImportStrategy strategy = dataValueSet.getStrategy() != null ?
            ImportStrategy.valueOf( dataValueSet.getStrategy() ) :
            importOptions.getImportStrategy();
        boolean skipExistingCheck = importOptions.isSkipExistingCheck();

        Map<String, DataElement> dataElementMap = identifiableObjectManager.getIdMap( DataElement.class, dataElementIdScheme );
        Map<String, OrganisationUnit> orgUnitMap = orgUnitIdScheme == UUID ?
            getUuidOrgUnitMap() :
            identifiableObjectManager.getIdMap( OrganisationUnit.class, orgUnitIdScheme );
        Map<String, DataElementCategoryOptionCombo> categoryOptionComboMap = identifiableObjectManager
            .getIdMap( DataElementCategoryOptionCombo.class, IdentifiableProperty.UID );
        Map<String, Period> periodMap = new HashMap<>();

        DataSet dataSet = dataValueSet.getDataSet() != null ?
            identifiableObjectManager.getObject( DataSet.class, IdentifiableProperty.UID, dataValueSet.getDataSet() ) :
            null;
        Date completeDate = getDefaultDate( dataValueSet.getCompleteDate() );

        Period outerPeriod = PeriodType.getPeriodFromIsoString( trimToNull( dataValueSet.getPeriod() ) );

        OrganisationUnit outerOrgUnit;

        DataElementCategoryOptionCombo fallbackCategoryOptionCombo = categoryService
            .getDefaultDataElementCategoryOptionCombo();

        if ( orgUnitIdScheme.equals( IdentifiableProperty.UUID ) )
        {
            outerOrgUnit = dataValueSet.getOrgUnit() == null ?
                null :
                organisationUnitService.getOrganisationUnitByUuid( dataValueSet.getOrgUnit() );
        }
        else
        {
            outerOrgUnit = dataValueSet.getOrgUnit() != null ?
                identifiableObjectManager.getObject( OrganisationUnit.class, orgUnitIdScheme, dataValueSet.getOrgUnit() ) : null;
        }

        DataElementCategoryOptionCombo outerAttrOptionCombo = dataValueSet.getAttributeOptionCombo() != null ?
            identifiableObjectManager.getObject( DataElementCategoryOptionCombo.class, IdentifiableProperty.UID,
                trimToNull( dataValueSet.getAttributeOptionCombo() ) ) : null;

        if ( dataSet != null && completeDate != null )
        {
            notifier.notify( id, "Completing data set" );
            handleComplete( dataSet, completeDate, outerPeriod, outerOrgUnit, fallbackCategoryOptionCombo, summary ); //TODO
        }
        else
        {
            summary.setDataSetComplete( Boolean.FALSE.toString() );
        }

        String currentUser = currentUserService.getCurrentUsername();

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class )
            .init();

        int importCount = 0;
        int updateCount = 0;
        int totalCount = 0;

        // ---------------------------------------------------------------------
        // Data values
        // ---------------------------------------------------------------------

        notifier.notify( id, "Importing data values" );
        log.info( "importing data values" );

        while ( dataValueSet.hasNextDataValue() )
        {
            org.hisp.dhis.dxf2.datavalue.DataValue dataValue = dataValueSet.getNextDataValue();

            DataValue internalValue = new DataValue();

            totalCount++;

            DataElement dataElement = dataElementMap.get( trimToNull( dataValue.getDataElement() ) );
            DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboMap
                .get( trimToNull( dataValue.getCategoryOptionCombo() ) );
            Period period = outerPeriod != null ?
                outerPeriod :
                PeriodType.getPeriodFromIsoString( trimToNull( dataValue.getPeriod() ) );
            OrganisationUnit orgUnit =
                outerOrgUnit != null ? outerOrgUnit : orgUnitMap.get( trimToNull( dataValue.getOrgUnit() ) );
            DataElementCategoryOptionCombo attrOptionCombo = outerAttrOptionCombo != null ?
                outerAttrOptionCombo :
                categoryOptionComboMap.get( trimToNull( dataValue.getAttributeOptionCombo() ) );

            if ( dataElement == null )
            {
                summary.getConflicts()
                    .add( new ImportConflict( DataElement.class.getSimpleName(), dataValue.getDataElement() ) );
                continue;
            }

            if ( period == null )
            {
                summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), dataValue.getPeriod() ) );
                continue;
            }

            if ( orgUnit == null )
            {
                summary.getConflicts()
                    .add( new ImportConflict( OrganisationUnit.class.getSimpleName(), dataValue.getOrgUnit() ) );
                continue;
            }

            if ( categoryOptionCombo == null )
            {
                categoryOptionCombo = fallbackCategoryOptionCombo;
            }

            if ( attrOptionCombo == null )
            {
                attrOptionCombo = fallbackCategoryOptionCombo;
            }

            if ( dataValue.getValue() == null && dataValue.getComment() == null )
            {
                continue;
            }

            String valueValid = ValidationUtils.dataValueIsValid( dataValue.getValue(), dataElement );

            if ( valueValid != null )
            {
                summary.getConflicts().add( new ImportConflict( DataValue.class.getSimpleName(), valueValid ) );
                continue;
            }

            String commentValid = ValidationUtils.commentIsValid( dataValue.getComment() );

            if ( commentValid != null )
            {
                summary.getConflicts().add( new ImportConflict( DataValue.class.getSimpleName(), commentValid ) );
                continue;
            }

            if ( periodMap.containsKey( dataValue.getPeriod() ) )
            {
                period = periodMap.get( dataValue.getPeriod() );
            }
            else
            {
                period = periodService.reloadPeriod( period );
                periodMap.put( dataValue.getPeriod(), period );
            }

            internalValue.setDataElement( dataElement );
            internalValue.setPeriod( period );
            internalValue.setSource( orgUnit );
            internalValue.setCategoryOptionCombo( categoryOptionCombo );
            internalValue.setAttributeOptionCombo( attrOptionCombo );
            internalValue.setValue( trimToNull( dataValue.getValue() ) );

            if ( dataValue.getStoredBy() == null || dataValue.getStoredBy().trim().isEmpty() )
            {
                internalValue.setStoredBy( currentUser );
            }
            else
            {
                internalValue.setStoredBy( dataValue.getStoredBy() );
            }

            internalValue.setCreated( parseDate( dataValue.getCreated() ) );
            internalValue.setLastUpdated( parseDate( dataValue.getLastUpdated() ) );
            internalValue.setComment( trimToNull( dataValue.getComment() ) );
            internalValue.setFollowup( dataValue.getFollowup() );

            if ( !skipExistingCheck && batchHandler.objectExists( internalValue ) )
            {
                if ( strategy.isCreateAndUpdate() || strategy.isUpdate() )
                {
                    if ( !dryRun )
                    {
                        batchHandler.updateObject( internalValue );
                    }

                    updateCount++;
                }
            }
            else
            {
                if ( strategy.isCreateAndUpdate() || strategy.isCreate() )
                {
                    if ( !dryRun )
                    {
                        batchHandler.addObject( internalValue );
                    }

                    importCount++;
                }
            }
        }

        batchHandler.flush();

        int ignores = totalCount - importCount - updateCount;

        summary.setDataValueCount( new ImportCount( importCount, updateCount, ignores, 0 ) );
        summary.setStatus( ImportStatus.SUCCESS );
        summary.setDescription( "Import process completed successfully" );

        notifier.notify( id, INFO, "Import done", true ).addTaskSummary( id, summary );

        dataValueSet.close();

        return summary;
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void handleComplete( DataSet dataSet, Date completeDate, Period period, OrganisationUnit orgUnit,
        DataElementCategoryOptionCombo attributeOptionCombo, ImportSummary summary )
    {
        if ( orgUnit == null )
        {
            summary.getConflicts().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }

        if ( period == null )
        {
            summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }

        period = periodService.reloadPeriod( period );

        CompleteDataSetRegistration completeAlready = registrationService
            .getCompleteDataSetRegistration( dataSet, period, orgUnit, attributeOptionCombo );

        String username = currentUserService.getCurrentUsername();

        if ( completeAlready != null )
        {
            completeAlready.setStoredBy( username );
            completeAlready.setDate( completeDate );

            registrationService.updateCompleteDataSetRegistration( completeAlready );
        }
        else
        {
            CompleteDataSetRegistration registration = new CompleteDataSetRegistration( dataSet, period, orgUnit,
                attributeOptionCombo, completeDate, username );

            registrationService.saveCompleteDataSetRegistration( registration );
        }

        summary.setDataSetComplete( DateUtils.getMediumDateString( completeDate ) );
    }

    private Map<String, OrganisationUnit> getUuidOrgUnitMap()
    {
        Map<String, OrganisationUnit> orgUnitMap = new HashMap<>();

        Collection<OrganisationUnit> allOrganisationUnits = organisationUnitService.getAllOrganisationUnits();

        for ( OrganisationUnit organisationUnit : allOrganisationUnits )
        {
            orgUnitMap.put( organisationUnit.getUuid(), organisationUnit );
        }

        return orgUnitMap;
    }
}
