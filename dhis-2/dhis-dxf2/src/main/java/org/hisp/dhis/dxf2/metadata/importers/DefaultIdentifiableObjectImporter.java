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
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.hisp.dhis.dxf2.metadata.ObjectBridge;
import org.hisp.dhis.dxf2.utils.OrganisationUnitUtils;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

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
    private ObjectBridge objectBridge;

    //-------------------------------------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------------------------------------

    public DefaultIdentifiableObjectImporter( Class<T> importerClass )
    {
        this.importerClass = importerClass;
    }

    private final Class<T> importerClass;

    //-------------------------------------------------------------------------------------------------------
    // Current import counts
    //-------------------------------------------------------------------------------------------------------

    protected int totalImported;

    protected int totalUpdated;

    protected int totalIgnored;

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

        log.debug( "Trying to save new object => " + getDisplayName( object ) );

        Map<Field, Set<? extends IdentifiableObject>> identifiableObjectCollections =
            scanIdentifiableObjectCollections( object );

        importConflicts.addAll( updateIdentifiableObjects( object, scanIdentifiableObjects( object ) ) );

        objectBridge.saveObject( object );

        importConflicts.addAll( updateIdentifiableObjectCollections( object, identifiableObjectCollections ) );

        updatePeriodTypes( object );
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

        importConflicts.addAll( updateIdentifiableObjects( object, scanIdentifiableObjects( object ) ) );
        importConflicts.addAll( updateIdentifiableObjectCollections( object, scanIdentifiableObjectCollections( object ) ) );

        oldObject.mergeWith( object );
        updatePeriodTypes( oldObject );

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

        // FIXME a bit too static.. implement "pre handler" for types?
        if ( OrganisationUnit.class.isAssignableFrom( objects.get( 0 ).getClass() ) )
        {
            OrganisationUnitUtils.updateParents( (Collection<OrganisationUnit>) objects );
            Collections.sort( (List<OrganisationUnit>) objects, new OrganisationUnitComparator() );
        }

        for ( T object : objects )
        {
            List<ImportConflict> conflicts = importObjectLocal( object, options );

            if ( !conflicts.isEmpty() )
            {
                importConflicts.addAll( conflicts );
            }
        }

        return importConflicts;
    }

    @Override
    public List<ImportConflict> importObject( T object, ImportOptions options )
    {
        init( options );

        return importObjectLocal( object, options );
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
    protected String getDisplayName( IdentifiableObject object )
    {
        if ( object == null )
        {
            return "[ object is null ]";
        }
        else if ( object.getName() != null && object.getName().length() > 0 )
        {
            return object.getName();
        }
        else if ( object.getUid() != null && object.getName().length() > 0 )
        {
            return object.getUid();
        }
        else if ( object.getCode() != null && object.getName().length() > 0 )
        {
            return object.getCode();
        }

        return object.getClass().getName();
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private void init( ImportOptions options )
    {
        totalImported = 0;
        totalUpdated = 0;
        totalIgnored = 0;
    }

    private List<ImportConflict> importObjectLocal( T object, ImportOptions options )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        ImportConflict importConflict = validateIdentifiableObject( object, options );

        if ( importConflict == null )
        {
            importConflicts.addAll( startImport( object, options ) );
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

    private List<ImportConflict> startImport( T object, ImportOptions options )
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

    private ImportConflict validateIdentifiableObject( T object, ImportOptions options )
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
        // FIXME this is a bit too static ATM, should be refactored out into its own "type handler"
        if ( Period.class.isAssignableFrom( identifiableObject.getClass() ) )
        {
            Period period = (Period) identifiableObject;
            return periodService.reloadPeriod( period );
        }

        return objectBridge.getObject( identifiableObject );
    }

    private Map<Field, IdentifiableObject> scanIdentifiableObjects( IdentifiableObject identifiableObject )
    {
        Map<Field, IdentifiableObject> identifiableObjects = new HashMap<Field, IdentifiableObject>();
        Field[] fields = identifiableObject.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, IdentifiableObject.class ) )
            {
                IdentifiableObject ref = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

                if ( ref != null )
                {
                    identifiableObjects.put( field, ref );
                }
            }
        }

        return identifiableObjects;
    }

    private List<ImportConflict> updateIdentifiableObjects( IdentifiableObject identifiableObject, Map<Field,
        IdentifiableObject> identifiableObjects )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( Field field : identifiableObjects.keySet() )
        {
            IdentifiableObject idObject = identifiableObjects.get( field );
            IdentifiableObject ref = findObjectByReference( idObject );

            if ( ref != null )
            {
                ReflectionUtils.invokeSetterMethod( field.getName(), identifiableObject, ref );
            }
            else
            {
                // FIXME special case, possible forward reference.. so just skip it..
                if ( !OrganisationUnitGroupSet.class.isInstance( idObject ) )
                {
                    log.warn( "Ignored reference " + idObject + " on object " + identifiableObject + "." );

                    ImportConflict importConflict = new ImportConflict( getDisplayName( identifiableObject ),
                        "Unknown reference to " + idObject + " on field " + field.getName() + ", reference has been discarded." );

                    importConflicts.add( importConflict );
                }
            }
        }

        return importConflicts;
    }

    private Map<Field, Set<? extends IdentifiableObject>> scanIdentifiableObjectCollections( IdentifiableObject
        identifiableObject )
    {
        Map<Field, Set<? extends IdentifiableObject>> collected = new HashMap<Field, Set<? extends IdentifiableObject>>();
        Field[] fields = identifiableObject.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            boolean b = ReflectionUtils.isCollection( field.getName(), identifiableObject, IdentifiableObject.class,
                Scanned.class );

            if ( b )
            {
                Collection<IdentifiableObject> objects = ReflectionUtils.invokeGetterMethod( field.getName(),
                    identifiableObject );

                if ( objects != null && !objects.isEmpty() )
                {
                    Set<IdentifiableObject> identifiableObjects = new HashSet<IdentifiableObject>( objects );
                    collected.put( field, identifiableObjects );
                    objects.clear();
                }
            }
        }

        return collected;
    }

    private List<ImportConflict> updateIdentifiableObjectCollections( IdentifiableObject identifiableObject,
        Map<Field, Set<? extends IdentifiableObject>> identifiableObjectCollections )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( Field field : identifiableObjectCollections.keySet() )
        {
            Collection<? extends IdentifiableObject> identifiableObjects = identifiableObjectCollections.get( field );
            Collection<IdentifiableObject> objects;

            if ( List.class.isAssignableFrom( field.getType() ) )
            {
                objects = new ArrayList<IdentifiableObject>();
            }
            else if ( Set.class.isAssignableFrom( field.getType() ) )
            {
                objects = new HashSet<IdentifiableObject>();
            }
            else
            {
                log.warn( "Unknown Collection type." );
                continue;
            }

            for ( IdentifiableObject idObject : identifiableObjects )
            {
                IdentifiableObject ref = findObjectByReference( idObject );

                if ( ref != null )
                {
                    objects.add( ref );
                }
                else
                {
                    log.warn( "Ignored reference " + idObject + " on object " + identifiableObject + "." );

                    ImportConflict importConflict = new ImportConflict( getDisplayName( identifiableObject ),
                        "Unknown reference to " + idObject + " on field " + field.getName() + ", reference has been discarded." );

                    importConflicts.add( importConflict );
                }
            }

            ReflectionUtils.invokeSetterMethod( field.getName(), identifiableObject, objects );
        }

        return importConflicts;
    }
}
