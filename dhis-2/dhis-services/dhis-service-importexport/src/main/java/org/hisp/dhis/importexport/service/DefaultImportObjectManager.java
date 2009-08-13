package org.hisp.dhis.importexport.service;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.ExtendedDataElement;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObject;
import org.hisp.dhis.importexport.ImportObjectManager;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.importexport.ImportObjectStore;
import org.hisp.dhis.importexport.locking.LockingManager;
import org.hisp.dhis.importexport.mapping.GroupMemberAssociationVerifier;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
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
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetSourceAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ExtendedDataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupMemberBatchHandler;
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
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.olap.OlapURLService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultImportObjectManager
    implements ImportObjectManager
{
    private static final Log log = LogFactory.getLog( DefaultImportObjectManager.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private ImportObjectStore importObjectStore;

    public void setImportObjectStore( ImportObjectStore importObjectStore )
    {
        this.importObjectStore = importObjectStore;
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private OlapURLService olapURLService;

    public void setOlapURLService( OlapURLService olapURLService )
    {
        this.olapURLService = olapURLService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private ImportDataValueService importDataValueService;

    public void setImportDataValueService( ImportDataValueService importDataValueService )
    {
        this.importDataValueService = importDataValueService;
    }

    private LockingManager lockingManager;

    public void setLockingManager( LockingManager lockingManager )
    {
        this.lockingManager = lockingManager;
    }

    // -------------------------------------------------------------------------
    // ImportObjectManager implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void importCategoryOptions()
    {
        BatchHandler<DataElementCategoryOption> batchHandler = batchHandlerFactory.createBatchHandler( DataElementCategoryOptionBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategoryOption.class );
        
        for ( ImportObject importObject : importObjects )
        {
            DataElementCategoryOption object = (DataElementCategoryOption) importObject.getObject();
            
            NameMappingUtil.addCategoryOptionMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementCategoryOption compareObject = (DataElementCategoryOption) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataElementCategoryOption.class );
        
        log.info( "Imported DataElementCategoryOptions" );
    }

    @Transactional
    public void importCategories()
    {
        BatchHandler<DataElementCategory> batchHandler = batchHandlerFactory.createBatchHandler( DataElementCategoryBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategory.class );

        for ( ImportObject importObject : importObjects )
        {
            DataElementCategory object = (DataElementCategory) importObject.getObject();
            
            NameMappingUtil.addCategoryMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementCategory compareObject = (DataElementCategory) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }            

            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }

        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataElementCategory.class );
        
        log.info( "Imported DataElementCategories" );
    }

    @Transactional
    public void importCategoryCombos()
    {
        BatchHandler<DataElementCategoryCombo> batchHandler = batchHandlerFactory.createBatchHandler( DataElementCategoryComboBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategoryCombo.class );

        for ( ImportObject importObject : importObjects )
        {
            DataElementCategoryCombo object = (DataElementCategoryCombo) importObject.getObject();
            
            NameMappingUtil.addCategoryComboMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementCategoryCombo compareObject = (DataElementCategoryCombo) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementCategoryCombo.class );
        
        log.info( "Imported DataElementCategoryCombos" );
    }

    @Transactional
    public void importCategoryOptionCombos()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategoryOptionCombo.class );

        Map<Object, Integer> categoryComboMapping = objectMappingGenerator.getCategoryComboMapping( false );
        Map<Object, Integer> categoryOptionMapping = objectMappingGenerator.getCategoryOptionMapping( false );
        
        for ( ImportObject importObject : importObjects )
        {
            DataElementCategoryOptionCombo object = (DataElementCategoryOptionCombo) importObject.getObject();
            
            int categoryOptionComboId = object.getId();
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementCategoryOptionCombo compareObject = (DataElementCategoryOptionCombo) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            int categoryComboId = categoryComboMapping.get( object.getCategoryCombo().getId() );
            
            object.setCategoryCombo( categoryComboService.getDataElementCategoryCombo( categoryComboId ) );
            
            Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();
            
            for ( DataElementCategoryOption categoryOption : object.getCategoryOptions() )
            {
                int categoryOptionId = categoryOptionMapping.get( categoryOption.getId() );
                
                categoryOptions.add( categoryOptionService.getDataElementCategoryOption( categoryOptionId ) );
            }
            
            object.setCategoryOptions( categoryOptions );

            NameMappingUtil.addCategoryOptionComboMapping( categoryOptionComboId, object );
            
            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                categoryOptionComboService.addDataElementCategoryOptionCombo( object );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                categoryOptionComboService.updateDataElementCategoryOptionCombo( object );
            }
        }

        importObjectStore.deleteImportObjects( DataElementCategoryOptionCombo.class );
        
        log.info( "Imported DataElementCategoryOptionCombos" );
    }

    @Transactional
    public void importCategoryCategoryOptionAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( CategoryCategoryOptionAssociationBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.CATEGORY_CATEGORYOPTION, 
            objectMappingGenerator.getCategoryMapping( false ), 
            objectMappingGenerator.getCategoryOptionMapping( false ) );
        
        log.info( "Imported CategoryCategoryOption associations" );
    }

    @Transactional
    public void importCategoryComboCategoryAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( CategoryComboCategoryAssociationBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.CATEGORYCOMBO_CATEGORY, 
            objectMappingGenerator.getCategoryComboMapping( false ),
            objectMappingGenerator.getCategoryMapping( false ) );
        
        log.info( "Imported CategoryComboCategory associations" );
    }

    @Transactional
    public void importDataElements()
    {
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );
        BatchHandler<ExtendedDataElement> extendedDataElementBatchHandler = 
            batchHandlerFactory.createBatchHandler( ExtendedDataElementBatchHandler.class );
        
        Map<Object, Integer> categoryComboMapping = objectMappingGenerator.getCategoryComboMapping( false );
        
        batchHandler.init();
        extendedDataElementBatchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElement.class );
        
        for ( ImportObject importObject : importObjects )
        {
            DataElement object = (DataElement) importObject.getObject();

            NameMappingUtil.addDataElementMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                ExtendedDataElement extendedObject = object.getExtended();
                
                if ( extendedObject != null )
                {
                    int id = extendedDataElementBatchHandler.insertObject( extendedObject, true );
                    
                    extendedObject.setId( id );
                    
                    object.setExtended( extendedObject );
                }
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )//TODO
            {
                ExtendedDataElement extendedObject = object.getExtended();
                
                if ( extendedObject != null )
                {
                    ExtendedDataElement extendedCompareObject = ((DataElement)importObject.getCompareObject()).getExtended();
                    
                    extendedObject.setId( extendedCompareObject.getId() );
                    
                    extendedDataElementBatchHandler.updateObject( extendedObject );
                    
                    object.setExtended( extendedObject );
                }
                
                DataElement compareObject = (DataElement) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            object.getCategoryCombo().setId( categoryComboMapping.get( object.getCategoryCombo().getId() ) );
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        extendedDataElementBatchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataElement.class );
        
        log.info( "Imported DataElements" );
    }

    @Transactional
    public void importCalculatedDataElements()
    {
        Map<Object, Integer> categoryComboMapping = objectMappingGenerator.getCategoryComboMapping( false );
        Map<Object, Integer> dataElementMapping = objectMappingGenerator.getDataElementMapping( false );
        Map<Object, Integer> categoryOptionComboMapping = objectMappingGenerator.getCategoryOptionComboMapping( false );

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( CalculatedDataElement.class );

        for ( ImportObject importObject : importObjects )
        {
            CalculatedDataElement object = (CalculatedDataElement) importObject.getObject();

            NameMappingUtil.addDataElementMapping( object.getId(), object.getName() );

            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElement compareObject = (DataElement) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }

            object.getCategoryCombo().setId( categoryComboMapping.get( object.getCategoryCombo().getId() ) );
            object.getExpression().setExpression( expressionService.convertExpression( 
                object.getExpression().getExpression(), dataElementMapping, categoryOptionComboMapping ) );
            
            importObject.setObject( object );
            
            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                dataElementService.addDataElement( object );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                dataElementService.updateDataElement( object );
            }
        }
        
        importObjectStore.deleteImportObjects( CalculatedDataElement.class );
        
        log.info( "Imported CalculatedDataElements" );
    }

    @Transactional
    public void importDataElementGroups()
    {
        BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupBatchHandler.class );

        batchHandler.init();
            
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementGroup.class );

        for ( ImportObject importObject : importObjects )
        {
            DataElementGroup object = (DataElementGroup) importObject.getObject();

            NameMappingUtil.addDataElementGroupMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementGroup compareObject = (DataElementGroup) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataElementGroup.class );
        
        log.info( "Imported DataElementGroups" );
    }

    @Transactional
    public void importDataElementGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATAELEMENTGROUP,
            objectMappingGenerator.getDataElementGroupMapping( false ),
            objectMappingGenerator.getDataElementMapping( false ) );
        
        log.info( "Imported DataElementGroup members" );
    }

    @Transactional
    public void importIndicatorTypes()
    {
        BatchHandler<IndicatorType> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorTypeBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( IndicatorType.class );

        for ( ImportObject importObject : importObjects )
        {   
            IndicatorType object = (IndicatorType) importObject.getObject();

            NameMappingUtil.addIndicatorTypeMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                IndicatorType compareObject = (IndicatorType) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( IndicatorType.class );
        
        log.info( "Imported IndicatorTypes" );
    }    

    @Transactional
    public void importIndicators()
    {
        BatchHandler<Indicator> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class );
        BatchHandler<ExtendedDataElement> extendedDataElementBatchHandler = 
            batchHandlerFactory.createBatchHandler( ExtendedDataElementBatchHandler.class );
        
        Map<Object, Integer> indicatorTypeMapping = objectMappingGenerator.getIndicatorTypeMapping( false );
        Map<Object, Integer> dataElementMapping = objectMappingGenerator.getDataElementMapping( false );
        Map<Object, Integer> categoryOptionComboMapping = objectMappingGenerator.getCategoryOptionComboMapping( false );
        
        batchHandler.init();
        extendedDataElementBatchHandler.init();
                
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Indicator.class );

        for ( ImportObject importObject : importObjects )
        {
            Indicator object = (Indicator) importObject.getObject();

            NameMappingUtil.addIndicatorMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                ExtendedDataElement extendedIndicator = object.getExtended();
                
                if ( extendedIndicator != null )
                {
                    int id = extendedDataElementBatchHandler.insertObject( extendedIndicator, true );
                    
                    extendedIndicator.setId( id );
                    
                    object.setExtended( extendedIndicator );
                }
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                ExtendedDataElement extendedObject = object.getExtended();
                
                if ( extendedObject != null )
                {
                    ExtendedDataElement extendedCompareObject = ((Indicator)importObject.getCompareObject()).getExtended();
                    
                    extendedObject.setId( extendedCompareObject.getId() );
                    
                    extendedDataElementBatchHandler.updateObject( extendedObject );
                    
                    object.setExtended( extendedObject );
                }
                
                Indicator compareObject = (Indicator) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            object.getIndicatorType().setId( indicatorTypeMapping.get( object.getIndicatorType().getId() ) );
            object.setNumerator( expressionService.convertExpression( object.getNumerator(), dataElementMapping, categoryOptionComboMapping ) );
            object.setDenominator( expressionService.convertExpression( object.getDenominator(), dataElementMapping, categoryOptionComboMapping ) );
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        extendedDataElementBatchHandler.flush();
        
        importObjectStore.deleteImportObjects( Indicator.class );
        
        log.info( "Imported Indicators" );
    }    

    @Transactional
    public void importIndicatorGroups()
    {
        BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( IndicatorGroup.class );

        for ( ImportObject importObject : importObjects )
        {   
            IndicatorGroup object = (IndicatorGroup) importObject.getObject();

            NameMappingUtil.addIndicatorGroupMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                IndicatorGroup compareObject = (IndicatorGroup) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );          
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( IndicatorGroup.class );
        
        log.info( "Imported IndicatorGroups" );
    }

    @Transactional
    public void importIndicatorGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.INDICATORGROUP,
            objectMappingGenerator.getIndicatorGroupMapping( false ),
            objectMappingGenerator.getIndicatorMapping( false ) );
        
        log.info( "Imported IndicatorGroup members" );
    }

    @Transactional
    public void importDataDictionaries()
    {
        BatchHandler<DataDictionary> batchHandler = batchHandlerFactory.createBatchHandler( DataDictionaryBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataDictionary.class );
        
        for ( ImportObject importObject : importObjects )
        {
            DataDictionary object = (DataDictionary) importObject.getObject();
            
            NameMappingUtil.addDataDictionaryMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataDictionary compareObject = (DataDictionary) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataDictionary.class );
        
        log.info( "Imported DataDictionaries" );
    }

    @Transactional
    public void importDataSets()
    {
        BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class );

        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataSet.class );
        
        for ( ImportObject importObject : importObjects )
        {
            DataSet object = (DataSet) importObject.getObject();

            NameMappingUtil.addDataSetMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataSet compareObject = (DataSet) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );         
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( DataSet.class );
        
        log.info( "Imported DataSets" );
    }

    @Transactional
    public void importDataDictionaryDataElements()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataDictionaryDataElementBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.DATADICTIONARY_DATAELEMENT, 
            objectMappingGenerator.getDataDictionaryMapping( false ),
            objectMappingGenerator.getDataElementMapping( false ) );
        
        log.info( "Imported DataDictionary DataElements" );
    }

    @Transactional
    public void importDataDictionaryIndicators()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataDictionaryIndicatorBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.DATADICTIONARY_INDICATOR, 
            objectMappingGenerator.getDataDictionaryMapping( false ),
            objectMappingGenerator.getIndicatorMapping( false ) );
        
        log.info( "Imported DataDictionary Indicators" );
    }

    @Transactional
    public void importDataSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATASET,
            objectMappingGenerator.getDataSetMapping( false ), 
            objectMappingGenerator.getDataElementMapping( false ) );
        
        log.info( "Imported DataSet members" );
    }

    @Transactional
    public void importOrganisationUnits()
    {
        BatchHandler<Source> sourceBatchHandler = batchHandlerFactory.createBatchHandler( SourceBatchHandler.class );
        BatchHandler<OrganisationUnit> organisationUnitBatchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );

        sourceBatchHandler.init();
        organisationUnitBatchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnit.class );
        
        for ( ImportObject importObject : importObjects )
        {
            OrganisationUnit object = (OrganisationUnit) importObject.getObject();

            NameMappingUtil.addOrganisationUnitMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                int id = sourceBatchHandler.insertObject( object, true );
                
                object.setId( id );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                OrganisationUnit compareObject = (OrganisationUnit) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( organisationUnitBatchHandler, importObject );
        }
        
        sourceBatchHandler.flush();
        organisationUnitBatchHandler.flush();
        
        importObjectStore.deleteImportObjects( OrganisationUnit.class );
        
        log.info( "Imported OrganisationUnits" );
    }

    @Transactional
    public void importOrganisationUnitRelationships()
    {
        Map<Object, Integer> organisationUnitMapping = objectMappingGenerator.getOrganisationUnitMapping( false );
        
        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );

        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( GroupMemberType.ORGANISATIONUNITRELATIONSHIP );
        
        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation object = (GroupMemberAssociation) importObject.getObject();
            
            OrganisationUnit child = organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( object.getMemberId() ) );
            
            OrganisationUnit parent = organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( object.getGroupId() ) );
            
            child.setParent( parent );
            
            batchHandler.updateObject( child );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITRELATIONSHIP );
        
        log.info( "Imported OrganisationUnit relationships" );
    }

    @Transactional
    public void importOrganisationUnitGroups()
    {
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupBatchHandler.class );

        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitGroup.class );
        
        for ( ImportObject importObject : importObjects )
        {
            OrganisationUnitGroup object = (OrganisationUnitGroup) importObject.getObject();

            NameMappingUtil.addOrganisationUnitGroupMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                OrganisationUnitGroup compareObject = (OrganisationUnitGroup) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( OrganisationUnitGroup.class );
        
        log.info( "Imported OrganisationUnitGroups" );
    }

    @Transactional
    public void importOrganisationUnitGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupMemberBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.ORGANISATIONUNITGROUP,
            objectMappingGenerator.getOrganisationUnitGroupMapping( false ),
            objectMappingGenerator.getOrganisationUnitMapping( false ) );
        
        log.info( "Imported OrganissationUnitGroup members" );
    }

    @Transactional
    public void importOrganisationUnitGroupSets()
    {
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetBatchHandler.class );

        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitGroupSet.class );
        
        for ( ImportObject importObject : importObjects )
        {
            OrganisationUnitGroupSet object = (OrganisationUnitGroupSet) importObject.getObject();

            NameMappingUtil.addGroupSetMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                OrganisationUnitGroupSet compareObject = (OrganisationUnitGroupSet) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );            
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( OrganisationUnitGroupSet.class );
        
        log.info( "Imported OrganisationUnitGroupSets" );
    }

    @Transactional
    public void importOrganisationUnitGroupSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetMemberBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.ORGANISATIONUNITGROUPSET,
            objectMappingGenerator.getOrganisationUnitGroupSetMapping( false ),
            objectMappingGenerator.getOrganisationUnitGroupMapping( false ) );
        
        log.info( "Imported OrganisationUnitGroupSet members" );
    }

    @Transactional
    public void importOrganisationUnitLevels()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitLevel.class );
        
        for ( ImportObject importObject : importObjects )
        {
            OrganisationUnitLevel object = (OrganisationUnitLevel) importObject.getObject();
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                OrganisationUnitLevel compare = (OrganisationUnitLevel) importObject.getCompareObject();
                
                object = updateOrganisationUnitLevel( compare, object );
                
                organisationUnitService.updateOrganisationUnitLevel( object );                
            }
            else if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                organisationUnitService.addOrganisationUnitLevel( object );
            }
        }
        
        importObjectStore.deleteImportObjects( OrganisationUnitLevel.class );
        
        log.info( "Imported OrganisationUnitLevels" );            
    }

    @Transactional
    public void importDataSetSourceAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetSourceAssociationBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATASET_SOURCE,
            objectMappingGenerator.getDataSetMapping( false ), 
            objectMappingGenerator.getOrganisationUnitMapping( false ) );
        
        log.info( "Imported DataSet Source associations" );
    }

    @Transactional
    public void importValidationRules()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( ValidationRule.class );
        
        for ( ImportObject importObject : importObjects )
        {
            ValidationRule object = (ValidationRule) importObject.getObject();
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                ValidationRule compare = (ValidationRule) importObject.getCompareObject();
                
                validationRuleService.updateValidationRule( compare ); // Reload because of Expression
                
                object = updateValidationRule( compare, object );
                
                expressionService.updateExpression( object.getLeftSide() );
                expressionService.updateExpression( object.getRightSide() );
                                
                validationRuleService.updateValidationRule( object );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                expressionService.addExpression( object.getLeftSide() );
                expressionService.addExpression( object.getRightSide() );
                                
                validationRuleService.addValidationRule( object );
            }
        }
        
        importObjectStore.deleteImportObjects( ValidationRule.class );
        
        log.info( "Imported ValidationRules" );
    }

    @Transactional
    public void importPeriods()
    {
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Period.class );
        
        for ( ImportObject importObject : importObjects )
        {
            Period period = (Period) importObject.getObject();
            
            NameMappingUtil.addPeriodMapping( period.getId(), period );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( Period.class );
        
        log.info( "Imported Periods" );
    }

    @Transactional
    public void importReportTables()
    {
        BatchHandler<ReportTable> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableBatchHandler.class );
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( ReportTable.class );
        
        for ( ImportObject importObject : importObjects )
        {
            ReportTable object = (ReportTable) importObject.getObject();
            
            NameMappingUtil.addReportTableMapping( object.getId(), object.getName() );
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                ReportTable compareObject = (ReportTable) importObject.getCompareObject();
                
                object.setId( compareObject.getId() );
            }
            
            importObject.setObject( object );
            
            addOrUpdateObject( batchHandler, importObject );
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( ReportTable.class );
        
        log.info( "Imported ReportTables" );
    }

    @Transactional
    public void importReportTableDataElements()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableDataElementBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_DATAELEMENT,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getDataElementMapping( false ) );
        
        log.info( "Imported ReportTable DataElements" );
    }

    @Transactional
    public void importReportTableCategoryOptionCombos()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableCategoryOptionComboBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_CATEGORY_OPTION_COMBO,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getCategoryOptionComboMapping( false ) );
        
        log.info( "Imported ReportTable CategoryOptionCombos" );
    }

    @Transactional
    public void importReportTableIndicators()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableIndicatorBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_INDICATOR,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getIndicatorMapping( false ) );
        
        log.info( "Imported ReportTable Indicators" );
    }

    @Transactional
    public void importReportTableDataSets()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableDataSetBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_DATASET,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getDataSetMapping( false ) );
        
        log.info( "Imported ReportTable DataSets" );
    }

    @Transactional
    public void importReportTablePeriods()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTablePeriodBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_PERIOD,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getPeriodMapping( false ) );
        
        log.info( "Imported ReportTable Periods" );
    }

    @Transactional
    public void importReportTableOrganisationUnits()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableOrganisationUnitBatchHandler.class );
        
        importGroupMemberAssociation( batchHandler, GroupMemberType.REPORTTABLE_ORGANISATIONUNIT,
            objectMappingGenerator.getReportTableMapping( false ),
            objectMappingGenerator.getOrganisationUnitMapping( false ) );
        
        log.info( "Imported ReportTable OrganisationUnits" );
    }

    @Transactional
    public void importOlapURLs()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OlapURL.class );
        
        for ( ImportObject importObject : importObjects )
        {
            OlapURL object = (OlapURL) importObject.getObject();
            
            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                OlapURL compare = (OlapURL) importObject.getObject();
                
                object = updateOlapURL( compare, object );
                
                olapURLService.updateOlapURL( object );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                olapURLService.saveOlapURL( object );
            }            
        }
        
        importObjectStore.deleteImportObjects( OlapURL.class );
        
        log.info( "Imported OlapURLs" );
    }

    @Transactional
    public void importCompleteDataSetRegistrations()
    {
        BatchHandler<CompleteDataSetRegistration> batchHandler = batchHandlerFactory.createBatchHandler( CompleteDataSetRegistrationBatchHandler.class );

        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( CompleteDataSetRegistration.class );

        Map<Object, Integer> dataSetMapping = objectMappingGenerator.getDataSetMapping( false );
        Map<Object, Integer> periodMapping = objectMappingGenerator.getPeriodMapping( false );
        Map<Object, Integer> sourceMapping = objectMappingGenerator.getOrganisationUnitMapping( false );
        
        for ( ImportObject importObject : importObjects )
        {
            CompleteDataSetRegistration registration = (CompleteDataSetRegistration) importObject.getObject();
            
            registration.getDataSet().setId( dataSetMapping.get( registration.getDataSet().getId() ) );
            registration.getPeriod().setId( periodMapping.get( registration.getPeriod().getId() ) );
            registration.getSource().setId( sourceMapping.get( registration.getSource().getId() ) );
            
            // -----------------------------------------------------------------
            // Must check for existing registrations since this cannot be done
            // during preview
            // -----------------------------------------------------------------
            
            if ( !batchHandler.objectExists( registration ) )
            {
                batchHandler.addObject( registration );
            }
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( CompleteDataSetRegistration.class );
        
        log.info( "Imported CompleteDataSetRegistrations" );
    }

    @Transactional
    public void importDataValues()
    {
        if ( lockingManager.currentImportContainsLockedData() )
        {
            log.warn( "Import file contained DataValues for locked periods" );
        }
        else
        {
            BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class );
            
            batchHandler.init();
            
            Map<Object, Integer> dataElementMapping = objectMappingGenerator.getDataElementMapping( false );
            Map<Object, Integer> periodMapping = objectMappingGenerator.getPeriodMapping( false );
            Map<Object, Integer> sourceMapping = objectMappingGenerator.getOrganisationUnitMapping( false );
            Map<Object, Integer> categoryOptionComboMapping = objectMappingGenerator.getCategoryOptionComboMapping( false );
            
            Collection<ImportDataValue> importValues = importDataValueService.getImportDataValues( ImportObjectStatus.NEW );
            
            for ( ImportDataValue importValue : importValues )
            {
                DataValue value = importValue.getDataValue();
                
                value.getDataElement().setId( dataElementMapping.get( value.getDataElement().getId() ) );
                value.getPeriod().setId( periodMapping.get( value.getPeriod().getId() ) );
                value.getSource().setId( sourceMapping.get( value.getSource().getId() ) );
                value.getOptionCombo().setId( categoryOptionComboMapping.get( value.getOptionCombo().getId() ) );
                
                // -------------------------------------------------------------
                // Must check for existing datavalues since this cannot be done
                // during preview
                // -------------------------------------------------------------
                
                if ( !batchHandler.objectExists( value ) )
                {
                    batchHandler.addObject( value );
                }
            }
            
            batchHandler.flush();
            
            importDataValueService.deleteImportDataValues();
            
            log.info( "Imported DataValues" );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private void addOrUpdateObject( BatchHandler batchHandler, ImportObject importObject )
    {
        if ( importObject.getStatus() == ImportObjectStatus.NEW )
        {
            batchHandler.addObject( importObject.getObject() );
        }
        else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
        {
            batchHandler.updateObject( importObject.getObject() );
        }

        // ---------------------------------------------------------------------
        // Ignoring ImportObjects of type MATCH
        // ---------------------------------------------------------------------
    }

    @SuppressWarnings( "unchecked" )
    private void importGroupMemberAssociation( BatchHandler batchHandler, GroupMemberType type,
        Map<Object, Integer> groupMapping, Map<Object, Integer> memberMapping )
    {
        GroupMemberAssociationVerifier.clear();
        
        batchHandler.init();
        
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( type );
        
        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation object = (GroupMemberAssociation) importObject.getObject();

            object.setGroupId( groupMapping.get( object.getGroupId() ) );
            object.setMemberId( memberMapping.get( object.getMemberId() ) );
            
            if ( GroupMemberAssociationVerifier.isUnique( object, type ) && !batchHandler.objectExists( object ) )
            {
                batchHandler.addObject( object );
            }            
        }
        
        batchHandler.flush();
        
        importObjectStore.deleteImportObjects( type );
    }

    private OrganisationUnitLevel updateOrganisationUnitLevel( OrganisationUnitLevel original, OrganisationUnitLevel update )
    {
        original.setLevel( update.getLevel() );
        original.setName( update.getName() );
        
        return original;
    }
    
    private ValidationRule updateValidationRule( ValidationRule original, ValidationRule update )
    {
        original.setName( update.getName() );
        original.setDescription( update.getDescription() );
        original.setType( update.getType() );
        original.setOperator( update.getOperator() );
        original.getLeftSide().setExpression( update.getLeftSide().getExpression() );
        original.getLeftSide().setDescription( update.getLeftSide().getDescription()  );
        original.getLeftSide().setDataElementsInExpression( update.getLeftSide().getDataElementsInExpression() );
        original.getRightSide().setExpression( update.getRightSide().getExpression() );
        original.getRightSide().setDescription( update.getRightSide().getDescription() );
        original.getRightSide().setDataElementsInExpression( update.getRightSide().getDataElementsInExpression() );
        
        return original;
    }

    private OlapURL updateOlapURL( OlapURL original, OlapURL update )
    {
        original.setName( update.getName() );
        original.setUrl( update.getUrl() );
        
        return original;
    }
}
