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
import org.hisp.dhis.common.*;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.hisp.dhis.dxf2.utils.OrganisationUnitUtils;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
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
    protected IdentifiableObjectManager manager;

    @Autowired
    private PeriodService periodService;

    //-------------------------------------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------------------------------------

    public DefaultIdentifiableObjectImporter( Class<T> importerClass )
    {
        this.importerClass = importerClass;
        this.nameable = NameableObject.class.isAssignableFrom( importerClass );
    }

    private final Class<T> importerClass;

    private final boolean nameable;

    //-------------------------------------------------------------------------------------------------------
    // Current import counts
    //-------------------------------------------------------------------------------------------------------

    protected int imported;

    protected int updated;

    protected int ignored;

    //-------------------------------------------------------------------------------------------------------
    // Mappings from identifier (uid, name, code) to a db object.
    //
    // WARNING: These maps might be out-of-date, depending on if new inserts has been made after the were
    //          fetched.
    //-------------------------------------------------------------------------------------------------------

    protected Map<String, T> uidMap;

    protected Map<String, T> codeMap;

    protected Map<String, T> nameMap;

    protected Map<String, T> shortNameMap;

    //-------------------------------------------------------------------------------------------------------
    // Generic implementations of newObject and updatedObject
    //-------------------------------------------------------------------------------------------------------

    /**
     * Called every time a new object is to be imported.
     *
     * @param object  Object to import
     * @param options Current import options
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected ImportConflict newObject( T object, ImportOptions options )
    {
        if ( options.isDryRun() )
        {
            return null;
        }

        // make sure that the internalId is 0, so that the system will generate a ID
        object.setId( 0 );
        object.setUid( CodeGenerator.generateCode() );

        log.info( "Trying to save new object with UID: " + object.getUid() );

        saveOrUpdateObjectWithReferences( object, false );

        manager.update( object );
        updateIdMaps( object );

        log.info( "Save successful." );
        log.info( object );

        return null;
    }

    /**
     * Update object from old => new.
     *
     * @param object    Object to import
     * @param oldObject The current version of the object
     * @param options   Current import options
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected ImportConflict updatedObject( T object, T oldObject, ImportOptions options )
    {
        if ( options.isDryRun() )
        {
            return null;
        }

        log.info( "Starting update of object " + getDisplayName( oldObject ) + " (" + oldObject.getClass().getSimpleName() + ")" );

        saveOrUpdateObjectWithReferences( object, true );

        oldObject.mergeWith( object );
        manager.update( oldObject );

        log.info( "Update successful." );
        log.info( oldObject );

        return null;
    }

    //-------------------------------------------------------------------------------------------------------
    // Importer<T> Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public List<ImportConflict> importObjects( List<T> objects, ImportOptions options )
    {
        List<ImportConflict> conflicts = new ArrayList<ImportConflict>();

        if ( objects.isEmpty() )
        {
            return conflicts;
        }

        reset();

        if ( OrganisationUnit.class.isAssignableFrom( objects.get( 0 ).getClass() ) )
        {
            OrganisationUnitUtils.updateParents( (Collection<OrganisationUnit>) objects );
            Collections.sort( (List<OrganisationUnit>) objects, new OrganisationUnitComparator() );
        }

        for ( T object : objects )
        {
            ImportConflict importConflict = importObjectLocal( object, options );

            if ( importConflict != null )
            {
                conflicts.add( importConflict );
            }
        }

        return conflicts;
    }

    @Override
    public ImportConflict importObject( T object, ImportOptions options )
    {
        reset();

        return importObjectLocal( object, options );
    }

    @Override
    public ImportCount getCurrentImportCount()
    {
        return new ImportCount( imported, updated, ignored );
    }

    @Override
    public boolean canHandle( Class<?> clazz )
    {
        return importerClass.equals( clazz );
    }

    //-------------------------------------------------------------------------------------------------------
    // Protected methods
    //-------------------------------------------------------------------------------------------------------

    protected void updateIdMaps( T object )
    {
        if ( object.getUid() != null )
        {
            uidMap.put( object.getUid(), object );
        }

        if ( object.getCode() != null )
        {
            codeMap.put( object.getCode(), object );
        }

        if ( object.getName() != null )
        {
            nameMap.put( object.getName(), object );
        }

        if ( nameable )
        {
            NameableObject nameableObject = (NameableObject) object;

            if ( nameableObject.getShortName() != null )
            {
                shortNameMap.put( nameableObject.getShortName(), object );
            }
        }
    }

    /**
     * @param object Object to get display name for
     * @return A usable display name
     */
    protected String getDisplayName( IdentifiableObject object )
    {
        if ( object.getUid() != null )
        {
            return object.getUid();
        }
        else if ( object.getCode() != null )
        {
            return object.getCode();
        }
        else if ( object.getName() != null )
        {
            return object.getName();
        }

        return object.getClass().getName();
    }

    /**
     * Current object name, used to fill name part of a ImportConflict
     *
     * @return Name of object
     */
    protected String getClassName()
    {
        return importerClass.getSimpleName();
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private void reset()
    {
        imported = 0;
        updated = 0;
        ignored = 0;

        uidMap = manager.getIdMap( importerClass, IdentifiableObject.IdentifiableProperty.UID );
        codeMap = manager.getIdMap( importerClass, IdentifiableObject.IdentifiableProperty.CODE );
        nameMap = manager.getIdMap( importerClass, IdentifiableObject.IdentifiableProperty.NAME );

        if ( nameable )
        {
            shortNameMap = (Map<String, T>) manager.getIdMap( (Class<? extends NameableObject>) importerClass, NameableObject.NameableProperty.SHORT_NAME );
        }
    }

    private ImportConflict importObjectLocal( T object, ImportOptions options )
    {
        ImportConflict conflict = validateIdentifiableObject( object, options );

        if ( conflict == null )
        {
            conflict = startImport( object, options );
        }

        if ( conflict != null )
        {
            ignored++;
        }

        return conflict;
    }

    private ImportConflict startImport( T object, ImportOptions options )
    {
        T oldObject = getObject( object );
        ImportConflict conflict;

        if ( ImportStrategy.NEW.equals( options.getImportStrategy() ) )
        {
            conflict = newObject( object, options );

            if ( conflict != null )
            {
                return conflict;
            }

            imported++;
        }
        else if ( ImportStrategy.UPDATES.equals( options.getImportStrategy() ) )
        {
            conflict = updatedObject( object, oldObject, options );

            if ( conflict != null )
            {
                return conflict;
            }

            updated++;
        }
        else if ( ImportStrategy.NEW_AND_UPDATES.equals( options.getImportStrategy() ) )
        {
            if ( oldObject != null )
            {
                conflict = updatedObject( object, oldObject, options );

                if ( conflict != null )
                {
                    return conflict;
                }

                updated++;
            }
            else
            {
                conflict = newObject( object, options );

                if ( conflict != null )
                {
                    return conflict;
                }

                imported++;
            }
        }

        return null;
    }

    private ImportConflict validateIdentifiableObject( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T codeObject = codeMap.get( object.getCode() );
        T nameObject = nameMap.get( object.getName() );

        T shortNameObject = null;
        T alternativeNameObject = null;

        if ( nameable )
        {
            NameableObject nameableObject = (NameableObject) object;

            shortNameObject = shortNameMap.get( nameableObject.getShortName() );
        }

        ImportConflict conflict = null;

        if ( ImportStrategy.NEW.equals( options.getImportStrategy() ) )
        {
            conflict = validateForNewStrategy( object, options );
        }
        else if ( ImportStrategy.UPDATES.equals( options.getImportStrategy() ) )
        {
            conflict = validateForUpdatesStrategy( object, options );
        }
        else if ( ImportStrategy.NEW_AND_UPDATES.equals( options.getImportStrategy() ) )
        {
            // if we have a match on at least one of the objects, then assume update
            if ( uidObject != null || codeObject != null || nameObject != null || shortNameObject != null || alternativeNameObject != null )
            {
                conflict = validateForUpdatesStrategy( object, options );
            }
            else
            {
                conflict = validateForNewStrategy( object, options );
            }
        }

        return conflict;
    }

    private ImportConflict validateForUpdatesStrategy( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T codeObject = codeMap.get( object.getCode() );
        T nameObject = nameMap.get( object.getName() );

        T shortNameObject = null;
        T alternativeNameObject = null;

        if ( nameable )
        {
            NameableObject nameableObject = (NameableObject) object;

            shortNameObject = shortNameMap.get( nameableObject.getShortName() );
        }

        ImportConflict conflict = null;

        Set<T> nonNullObjects = new HashSet<T>();

        if ( uidObject != null )
        {
            nonNullObjects.add( uidObject );
        }

        if ( codeObject != null )
        {
            nonNullObjects.add( codeObject );
        }

        if ( nameObject != null )
        {
            nonNullObjects.add( nameObject );
        }

        if ( shortNameObject != null )
        {
            nonNullObjects.add( shortNameObject );
        }

        if ( alternativeNameObject != null )
        {
            nonNullObjects.add( alternativeNameObject );
        }

        if ( nonNullObjects.isEmpty() )
        {
            conflict = reportLookupConflict( object, options );
        }
        else if ( nonNullObjects.size() > 1 )
        {
            conflict = reportMoreThanOneConflict( object, options );
        }

        return conflict;
    }

    private ImportConflict validateForNewStrategy( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T codeObject = codeMap.get( object.getCode() );
        T nameObject = nameMap.get( object.getName() );

        T shortNameObject = null;
        T alternativeNameObject = null;

        if ( nameable )
        {
            NameableObject nameableObject = (NameableObject) object;

            shortNameObject = shortNameMap.get( nameableObject.getShortName() );
        }

        ImportConflict conflict = null;

        if ( uidObject != null || codeObject != null || nameObject != null || shortNameObject != null || alternativeNameObject != null )
        {
            conflict = reportConflict( object, options );
        }

        return conflict;
    }

    private ImportConflict reportLookupConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object ), "Object does not exist." );
    }

    private ImportConflict reportMoreThanOneConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object ), "More than one object matches identifiers." );
    }

    private ImportConflict reportConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object ), "Object already exists." );
    }

    private T getObject( T object )
    {
        T matchedObject = uidMap.get( object.getUid() );

        if ( matchedObject != null )
        {
            return matchedObject;
        }

        matchedObject = codeMap.get( object.getCode() );

        if ( matchedObject != null )
        {
            return matchedObject;
        }

        matchedObject = nameMap.get( object.getName() );

        if ( matchedObject != null )
        {
            return matchedObject;
        }

        if ( nameable )
        {
            NameableObject nameableObject = (NameableObject) object;

            matchedObject = shortNameMap.get( nameableObject.getShortName() );

            if ( matchedObject != null )
            {
                return matchedObject;
            }
        }

        return matchedObject;
    }

    // FIXME slow! some kind of global idMap is needed here, that will also update itself from several importers
    private IdentifiableObject findObjectByReference( IdentifiableObject identifiableObject )
    {
        IdentifiableObject match = null;

        // FIXME this is a bit too static ATM, should be refactored out into its own "type handler"
        if ( Period.class.isAssignableFrom( identifiableObject.getClass() ) )
        {
            Period period = (Period) identifiableObject;
            match = periodService.reloadPeriod( period );
        }
        else if ( identifiableObject.getUid() != null )
        {
            match = manager.get( identifiableObject.getClass(), identifiableObject.getUid() );
        }
        else if ( identifiableObject.getCode() != null )
        {
            match = manager.getByCode( identifiableObject.getClass(), identifiableObject.getCode() );
        }
        else if ( identifiableObject.getName() != null )
        {
            match = manager.getByName( identifiableObject.getClass(), identifiableObject.getName() );
        }

        return match;
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

    private void updateIdentifiableObjects( IdentifiableObject identifiableObject, Map<Field, IdentifiableObject> identifiableObjects )
    {
        for ( Field field : identifiableObjects.keySet() )
        {
            IdentifiableObject ref = findObjectByReference( identifiableObject );

            if ( ref != null )
            {
                ReflectionUtils.invokeSetterMethod( field.getName(), identifiableObject, ref );
            }
            else
            {
                log.info( "--> Ignored reference " + getDisplayName( identifiableObject ) + "." );
            }
        }
    }

    private Map<Field, Set<? extends IdentifiableObject>> scanIdentifiableObjectCollections( IdentifiableObject identifiableObject )
    {
        Map<Field, Set<? extends IdentifiableObject>> collected = new HashMap<Field, Set<? extends IdentifiableObject>>();
        Field[] fields = identifiableObject.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            boolean b = ReflectionUtils.isCollection( field.getName(), identifiableObject, IdentifiableObject.class, Scanned.class );

            if ( b )
            {
                Collection<IdentifiableObject> objects = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

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

    private void updateIdentifiableObjectCollections( IdentifiableObject identifiableObject,
                                                      Map<Field, Set<? extends IdentifiableObject>> identifiableObjectCollections )
    {
        for ( Field field : identifiableObjectCollections.keySet() )
        {
            Collection<? extends IdentifiableObject> identifiableObjects = identifiableObjectCollections.get( field );
            Collection<IdentifiableObject> objects = null;

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
                log.warn( "Unknown Collection type!" );
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
                    log.info( "--> Ignored reference " + getDisplayName( identifiableObject ) + "." );
                }
            }

            ReflectionUtils.invokeSetterMethod( field.getName(), identifiableObject, objects );
        }
    }

    private void saveOrUpdateObjectWithReferences( T object, boolean update )
    {
        log.info( "-> Finding and updating references." );

        Map<Field, IdentifiableObject> identifiableObjects = scanIdentifiableObjects( object );
        Map<Field, Set<? extends IdentifiableObject>> identifiableObjectCollections = scanIdentifiableObjectCollections( object );

        updateIdentifiableObjects( object, identifiableObjects );

        if ( !update )
        {
            manager.save( object );
        }

        updateIdentifiableObjectCollections( object, identifiableObjectCollections );
    }
}
