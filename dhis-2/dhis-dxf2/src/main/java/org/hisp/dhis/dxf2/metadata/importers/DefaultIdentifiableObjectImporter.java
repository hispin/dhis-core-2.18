package org.hisp.dhis.dxf2.metadata.importers;

/*
 * Copyright (c) 2012, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.hisp.dhis.dxf2.metadata.ObjectBridge;
import org.hisp.dhis.dxf2.metadata.handlers.FieldHandler;
import org.hisp.dhis.dxf2.metadata.handlers.ObjectHandler;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.FunctionalUtils;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.system.util.functional.Function1;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

import static org.hisp.dhis.system.util.PredicateUtils.idObjectCollectionsWithScanned;
import static org.hisp.dhis.system.util.PredicateUtils.idObjects;

/**
 * Importer that can handle IdentifiableObject and NameableObject.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultIdentifiableObjectImporter<T extends BaseIdentifiableObject>
    implements Importer<T>
{
    private static final Log log = LogFactory.getLog( DefaultIdentifiableObjectImporter.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private PeriodService periodService;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private DataElementOperandService dataElementOperandService;

    @Autowired
    private ObjectBridge objectBridge;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired( required = false )
    private List<ObjectHandler> objectHandlers;

    @Autowired( required = false )
    private List<FieldHandler> fieldHandlers;

    //-------------------------------------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------------------------------------

    public DefaultIdentifiableObjectImporter( Class<T> importerClass )
    {
        this.importerClass = importerClass;
    }

    private final Class<T> importerClass;

    //-------------------------------------------------------------------------------------------------------
    // Internal state
    //-------------------------------------------------------------------------------------------------------

    protected int totalImported;

    protected int totalUpdated;

    protected int totalIgnored;

    protected ImportOptions options;

    //-------------------------------------------------------------------------------------------------------
    // Generic implementations of newObject and updatedObject
    //-------------------------------------------------------------------------------------------------------

    /**
     * Called every time a new object is to be imported.
     *
     * @param object Object to import
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected List<ImportConflict> newObject( T object )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        // make sure that the internalId is 0, so that the system will generate a ID
        object.setId( 0 );

        // FIXME add uidValidator.. part of bean validation impl?
        // object.setUid( CodeGenerator.generateCode() );

        log.debug( "Trying to save new object => " + getDisplayName( object ) + " (" + object.getClass().getSimpleName() + ")" );

        Map<Field, Object> fields = detachFields( object );
        importConflicts.addAll( reattachFields( object, fields ) );

        Map<Field, Collection<Object>> collectionFields = detachCollectionFields( object );

        objectBridge.saveObject( object );

        updatePeriodTypes( object );
        importConflicts.addAll( reattachCollectionFields( object, collectionFields ) );

        objectBridge.updateObject( object );

        log.debug( "Save successful." );

        return importConflicts;
    }

    /**
     * Update object from old => new.
     *
     * @param object    Object to import
     * @param oldObject The current version of the object
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected List<ImportConflict> updatedObject( T object, T oldObject )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        log.debug( "Starting update of object " + getDisplayName( oldObject ) + " (" + oldObject.getClass()
            .getSimpleName() + ")" );

        Map<Field, Object> fields = detachFields( object );
        importConflicts.addAll( reattachFields( object, fields ) );

        Map<Field, Collection<Object>> collectionFields = detachCollectionFields( object );

        oldObject.mergeWith( object );
        updatePeriodTypes( oldObject );

        importConflicts.addAll( reattachCollectionFields( oldObject, collectionFields ) );

        objectBridge.updateObject( oldObject );

        log.debug( "Update successful." );

        return importConflicts;
    }

    // FIXME to static ATM, should be refactor out.. "type handler", not idObject
    private void updatePeriodTypes( T object )
    {
        for ( Field field : object.getClass().getDeclaredFields() )
        {
            if ( PeriodType.class.isAssignableFrom( field.getType() ) )
            {
                PeriodType periodType = ReflectionUtils.invokeGetterMethod( field.getName(), object );
                periodType = objectBridge.getObject( periodType );
                ReflectionUtils.invokeSetterMethod( field.getName(), object, periodType );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // Importer<T> Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ImportConflict> importObjects( List<T> objects, ImportOptions options )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        if ( objects.isEmpty() )
        {
            return importConflicts;
        }

        init( options );

        preObjectsHandlers( objects );

        for ( T object : objects )
        {
            preObjectHandlers( object );

            // FIXME add to scanner, and use field handlers for this
            Set<AttributeValue> attributeValues = getAndClearAttributeValues( object );
            Set<DataElementOperand> compulsoryDataElementOperands = getAndClearDataElementOperands( object, "compulsoryDataElementOperands" );
            Set<DataElementOperand> greyedFields = getAndClearDataElementOperands( object, "greyedFields" );
            Expression leftSide = getAndClearExpression( object, "leftSide" );
            Expression rightSide = getAndClearExpression( object, "rightSide" );

            List<ImportConflict> conflicts = importObjectLocal( object );
            importConflicts.addAll( conflicts );

            if ( !options.isDryRun() )
            {
                sessionFactory.getCurrentSession().flush();

                newAttributeValues( object, attributeValues );
                newExpression( object, "leftSide", leftSide );
                newExpression( object, "rightSide", rightSide );
                // newDataElementOperands( object, "compulsoryDataElementOperands", compulsoryDataElementOperands );
                newDataElementOperands( object, "greyedFields", greyedFields );

                sessionFactory.getCurrentSession().flush();
            }

            postObjectHandlers( object );
        }

        postObjectsHandlers( objects );

        return importConflicts;
    }


    @SuppressWarnings( "unchecked" )
    private void preObjectHandlers( T object )
    {
        for ( ObjectHandler objectHandler : objectHandlers )
        {
            if ( objectHandler.canHandle( object.getClass() ) )
            {
                objectHandler.preImportObject( object );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void postObjectHandlers( T object )
    {
        for ( ObjectHandler objectHandler : objectHandlers )
        {
            if ( objectHandler.canHandle( object.getClass() ) )
            {
                objectHandler.postImportObject( object );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void preObjectsHandlers( List<T> objects )
    {
        if ( objects.size() > 0 )
        {
            for ( ObjectHandler objectHandler : objectHandlers )
            {
                if ( objectHandler.canHandle( objects.get( 0 ).getClass() ) )
                {
                    objectHandler.preImportObjects( objects );
                }
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void postObjectsHandlers( List<T> objects )
    {
        if ( objects.size() > 0 )
        {
            for ( ObjectHandler objectHandler : objectHandlers )
            {
                if ( objectHandler.canHandle( objects.get( 0 ).getClass() ) )
                {
                    objectHandler.postImportObjects( objects );
                }
            }
        }
    }

    // FIXME add type check
    private Expression getAndClearExpression( T object, String fieldName )
    {
        Expression expression = null;

        if ( ReflectionUtils.findGetterMethod( fieldName, object ) != null )
        {
            expression = ReflectionUtils.invokeGetterMethod( fieldName, object );

            if ( expression != null )
            {
                ReflectionUtils.invokeSetterMethod( fieldName, object, new Object[] { null } );
            }
        }

        return expression;
    }

    // FIXME add type check
    private Set<DataElementOperand> getAndClearDataElementOperands( T object, String fieldName )
    {
        Set<DataElementOperand> dataElementOperands = new HashSet<DataElementOperand>();

        if ( ReflectionUtils.findGetterMethod( fieldName, object ) != null )
        {
            Set<DataElementOperand> detachedDataElementOperands = ReflectionUtils.invokeGetterMethod( fieldName, object );
            dataElementOperands = new HashSet<DataElementOperand>( detachedDataElementOperands );

            if ( detachedDataElementOperands.size() > 0 )
            {
                detachedDataElementOperands.clear();
                ReflectionUtils.invokeSetterMethod( fieldName, object, new HashSet<DataElementOperand>() );
            }
        }

        return dataElementOperands;
    }

    // FIXME add type check
    private Set<AttributeValue> getAndClearAttributeValues( T object )
    {
        Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();

        if ( ReflectionUtils.findGetterMethod( "attributeValues", object ) != null )
        {
            attributeValues = ReflectionUtils.invokeGetterMethod( "attributeValues", object );

            if ( attributeValues.size() > 0 )
            {
                ReflectionUtils.invokeSetterMethod( "attributeValues", object, new HashSet<AttributeValue>() );
            }
        }

        return attributeValues;
    }

    private void newExpression( T object, String field, Expression expression )
    {
        if ( expression != null )
        {
            Map<Field, Collection<Object>> identifiableObjectCollections = detachCollectionFields( expression );
            reattachCollectionFields( expression, identifiableObjectCollections );

            expression.setId( 0 );
            expressionService.addExpression( expression );

            sessionFactory.getCurrentSession().flush();

            ReflectionUtils.invokeSetterMethod( field, object, expression );
        }
    }

    private void newDataElementOperands( T object, String field, Set<DataElementOperand> dataElementOperands )
    {
        if ( dataElementOperands.size() > 0 )
        {
            for ( DataElementOperand dataElementOperand : dataElementOperands )
            {
                Map<Field, Object> identifiableObjects = detachFields( dataElementOperand );
                reattachFields( dataElementOperand, identifiableObjects );

                dataElementOperand.setId( 0 );
                dataElementOperandService.addDataElementOperand( dataElementOperand );
            }

            ReflectionUtils.invokeSetterMethod( field, object, dataElementOperands );
        }
    }

    private void newAttributeValues( T object, Set<AttributeValue> attributeValues )
    {
        if ( attributeValues.size() > 0 )
        {
            for ( AttributeValue attributeValue : attributeValues )
            {
                Attribute attribute = objectBridge.getObject( attributeValue.getAttribute() );

                if ( attribute == null )
                {
                    log.warn( "Unknown reference to " + attributeValue.getAttribute() + " on object " + attributeValue );
                    continue;
                }

                attributeValue.setId( 0 );
                attributeValue.setAttribute( attribute );
            }

            for ( AttributeValue attributeValue : attributeValues )
            {
                attributeService.addAttributeValue( attributeValue );
            }

            ReflectionUtils.invokeSetterMethod( "attributeValues", object, attributeValues );
        }
    }

    @Override
    public List<ImportConflict> importObject( T object, ImportOptions options )
    {
        init( options );

        return importObjectLocal( object );
    }

    @Override
    public ImportCount getCurrentImportCount()
    {
        return new ImportCount( totalImported, totalUpdated, totalIgnored );
    }

    @Override
    public boolean canHandle( Class<?> clazz )
    {
        return importerClass.equals( clazz );
    }

    //-------------------------------------------------------------------------------------------------------
    // Protected methods
    //-------------------------------------------------------------------------------------------------------

    /**
     * @param object Object to get display name for
     * @return A usable display name
     */
    protected String getDisplayName( Object object )
    {
        if ( object == null )
        {
            return "[ object is null ]";
        }
        else if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getName() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getName();
            }
            else if ( identifiableObject.getUid() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getUid();
            }
            else if ( identifiableObject.getCode() != null && identifiableObject.getName().length() > 0 )
            {
                return identifiableObject.getCode();
            }

        }

        return object.getClass().getName();
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private void init( ImportOptions options )
    {
        this.options = options;
        totalImported = 0;
        totalUpdated = 0;
        totalIgnored = 0;
    }

    private List<ImportConflict> importObjectLocal( T object )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        ImportConflict importConflict = validateIdentifiableObject( object );

        if ( importConflict == null )
        {
            importConflicts.addAll( startImport( object ) );
        }
        else
        {
            importConflicts.add( importConflict );
        }

        if ( importConflicts.isEmpty() )
        {
            totalIgnored++;
        }

        return importConflicts;
    }

    private List<ImportConflict> startImport( T object )
    {
        T oldObject = objectBridge.getObject( object );
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        if ( ImportStrategy.NEW.equals( options.getImportStrategy() ) )
        {
            importConflicts.addAll( newObject( object ) );

            if ( importConflicts.isEmpty() )
            {
                totalImported++;
            }
        }
        else if ( ImportStrategy.UPDATES.equals( options.getImportStrategy() ) )
        {
            importConflicts.addAll( updatedObject( object, oldObject ) );

            if ( importConflicts.isEmpty() )
            {
                totalUpdated++;
            }
        }
        else if ( ImportStrategy.NEW_AND_UPDATES.equals( options.getImportStrategy() ) )
        {
            if ( oldObject != null )
            {
                importConflicts.addAll( updatedObject( object, oldObject ) );

                if ( importConflicts.isEmpty() )
                {
                    totalUpdated++;
                }
            }
            else
            {
                importConflicts.addAll( newObject( object ) );

                if ( importConflicts.isEmpty() )
                {
                    totalImported++;
                }
            }
        }

        return importConflicts;
    }

    private ImportConflict validateIdentifiableObject( T object )
    {
        ImportConflict conflict = null;

        // FIXME add bean validation for this
        if ( object.getName() == null || object.getName().length() == 0 )
        {
            return new ImportConflict( getDisplayName( object ), "Empty name for object " + object );
        }

        if ( NameableObject.class.isInstance( object ) )
        {
            NameableObject nameableObject = (NameableObject) object;

            if ( nameableObject.getShortName() == null || nameableObject.getShortName().length() == 0 )
            {
                return new ImportConflict( getDisplayName( object ), "Empty shortName for object " + object );
            }
        }
        // end

        if ( ImportStrategy.NEW.equals( options.getImportStrategy() ) )
        {
            conflict = validateForNewStrategy( object );
        }
        else if ( ImportStrategy.UPDATES.equals( options.getImportStrategy() ) )
        {
            conflict = validateForUpdatesStrategy( object );
        }
        else if ( ImportStrategy.NEW_AND_UPDATES.equals( options.getImportStrategy() ) )
        {
            // if we have a match on at least one of the objects, then assume update
            if ( objectBridge.getObjects( object ).size() > 0 )
            {
                conflict = validateForUpdatesStrategy( object );
            }
            else
            {
                conflict = validateForNewStrategy( object );
            }
        }

        return conflict;
    }

    private ImportConflict validateForUpdatesStrategy( T object )
    {
        ImportConflict conflict = null;
        Collection<T> objects = objectBridge.getObjects( object );

        if ( objects.isEmpty() )
        {
            conflict = reportLookupConflict( object );
        }
        else if ( objects.size() > 1 )
        {
            conflict = reportMoreThanOneConflict( object );
        }

        return conflict;
    }

    private ImportConflict validateForNewStrategy( T object )
    {
        ImportConflict conflict = null;
        Collection<T> objects = objectBridge.getObjects( object );

        if ( objects.size() > 0 )
        {
            conflict = reportConflict( object );
        }

        return conflict;
    }

    private ImportConflict reportLookupConflict( IdentifiableObject object )
    {
        return new ImportConflict( getDisplayName( object ), "Object does not exist." );
    }

    private ImportConflict reportMoreThanOneConflict( IdentifiableObject object )
    {
        return new ImportConflict( getDisplayName( object ), "More than one object matches identifiers." );
    }

    private ImportConflict reportConflict( IdentifiableObject object )
    {
        return new ImportConflict( getDisplayName( object ), "Object already exists." );
    }

    private IdentifiableObject findObjectByReference( IdentifiableObject identifiableObject )
    {
        if ( identifiableObject == null )
        {
            return null;
        }
        // FIXME this is a bit too static ATM, should be refactored out into its own "type handler"
        else if ( Period.class.isAssignableFrom( identifiableObject.getClass() ) )
        {
            Period period = (Period) identifiableObject;
            period = periodService.reloadPeriod( period );

            if ( !options.isDryRun() )
            {
                sessionFactory.getCurrentSession().flush();
            }

            return period;
        }

        return objectBridge.getObject( identifiableObject );
    }

    private Map<Field, Object> detachFields( final Object object )
    {
        final Map<Field, Object> fieldMap = new HashMap<Field, Object>();
        final Collection<Field> fieldCollection = ReflectionUtils.collectFields( object.getClass(), idObjects );

        FunctionalUtils.forEach( fieldCollection, new Function1<Field>()
        {
            @Override
            public void apply( Field field )
            {
                Object ref = ReflectionUtils.invokeGetterMethod( field.getName(), object );

                if ( ref != null )
                {
                    fieldMap.put( field, ref );
                    ReflectionUtils.invokeSetterMethod( field.getName(), object, new Object[] { null } );
                }
            }
        } );

        return fieldMap;
    }

    private List<ImportConflict> reattachFields( Object object, Map<Field, Object> fields )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( Field field : fields.keySet() )
        {
            IdentifiableObject idObject = (IdentifiableObject) fields.get( field );
            IdentifiableObject ref = findObjectByReference( idObject );

            if ( ref == null )
            {
                reportReferenceError(object, importConflicts, idObject);
            }

            if ( !options.isDryRun() )
            {
                ReflectionUtils.invokeSetterMethod( field.getName(), object, ref );
            }
        }

        return importConflicts;
    }

    private Map<Field, Collection<Object>> detachCollectionFields( final Object object )
    {
        final Map<Field, Collection<Object>> collectionFields = new HashMap<Field, Collection<Object>>();
        final Collection<Field> fieldCollection = ReflectionUtils.collectFields( object.getClass(), idObjectCollectionsWithScanned );

        FunctionalUtils.forEach( fieldCollection, new Function1<Field>()
        {
            @Override
            public void apply( Field field )
            {
                Collection<Object> objects = ReflectionUtils.invokeGetterMethod( field.getName(), object );

                if ( objects != null && !objects.isEmpty() )
                {
                    collectionFields.put( field, objects );
                    Collection<Object> emptyCollection = ReflectionUtils.newCollectionInstance( field.getType() );
                    ReflectionUtils.invokeSetterMethod( field.getName(), object, emptyCollection );
                }
            }
        } );

        return collectionFields;
    }

    private List<ImportConflict> reattachCollectionFields( Object object, Map<Field, Collection<Object>> collectionFields )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( Field field : collectionFields.keySet() )
        {
            Collection<Object> collection = collectionFields.get( field );
            Collection<Object> objects = ReflectionUtils.newCollectionInstance( field.getType() );

            for ( Object idObject : collection )
            {
                IdentifiableObject ref = findObjectByReference( (IdentifiableObject) idObject );

                if ( ref != null )
                {
                    objects.add( ref );
                }
                else
                {
                    reportReferenceError( object, importConflicts, idObject );
                }
            }

            if ( !options.isDryRun() )
            {
                ReflectionUtils.invokeSetterMethod( field.getName(), object, objects );
            }
        }

        return importConflicts;
    }

    private void reportReferenceError( Object object, List<ImportConflict> importConflicts, Object idObject )
    {
        String referenceName = idObject != null ? idObject.getClass().getSimpleName() : "null";
        String objectName = object != null ? object.getClass().getSimpleName() : "null";

        String logMsg = "Unknown reference to " + idObject + " (" + referenceName + ")" +
            " on object " + object + " (" + objectName + ").";

        log.warn( logMsg );

        ImportConflict importConflict = new ImportConflict( getDisplayName( object ), logMsg );
        importConflicts.add( importConflict );
    }
}
