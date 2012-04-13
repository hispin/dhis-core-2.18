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
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Importer that can handle IdentifiableObject and NameableObject.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultImporter<T extends BaseIdentifiableObject>
    implements Importer<T>
{
    private static final Log log = LogFactory.getLog( DefaultImporter.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    //-------------------------------------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------------------------------------

    public DefaultImporter( Class<T> importerClass )
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

    protected Map<String, T> alternativeNameMap;

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

        log.info( "Trying to save new object with UID: " + object.getUid() );

        findAndUpdateReferences( object );
        //manager.save( object );
        //updateIdMaps( object );

        log.info( "Save successful." );

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

        log.info( "Trying to update object with UID: " + oldObject.getUid() );

        findAndUpdateReferences( object );
        // oldObject.mergeWith( object );
        // manager.update( oldObject );

        log.info( "Update successful." );

        return null;
    }

    private void findAndUpdateReferences( T object )
    {
        Field[] fields = object.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, IdentifiableObject.class ) )
            {
                IdentifiableObject identifiableObject = ReflectionUtils.invokeGetterMethod( field.getName(), object );

                if ( identifiableObject != null )
                {
                    log.info( "VERIFYING: " + identifiableObject );
                }
            }
            else
            {
                boolean b = ReflectionUtils.isCollection( field.getName(), object, IdentifiableObject.class );

                if ( b )
                {
                    Collection<IdentifiableObject> identifiableObjects = ReflectionUtils.invokeGetterMethod( field.getName(), object );

                    if ( !identifiableObjects.isEmpty() )
                    {
                        log.info( "VERIFYING: " + identifiableObjects );
                    }
                }
            }
        }
    }

    /**
     * Current object name, used to fill name part of a ImportConflict
     *
     * @return Name of object
     */
    protected String getObjectName()
    {
        return importerClass.getSimpleName();
    }

    @Override
    public boolean canHandle( Class<?> clazz )
    {
        return importerClass.equals( clazz );
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

    //-------------------------------------------------------------------------------------------------------
    // Internal methods
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
            alternativeNameMap = (Map<String, T>) manager.getIdMap( (Class<? extends NameableObject>) importerClass, NameableObject.NameableProperty.ALTERNATIVE_NAME );
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
            prepareIdentifiableObject( object );
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
                prepareIdentifiableObject( object );
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
            alternativeNameObject = alternativeNameMap.get( nameableObject.getAlternativeName() );
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
            alternativeNameObject = alternativeNameMap.get( nameableObject.getAlternativeName() );
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
            alternativeNameObject = alternativeNameMap.get( nameableObject.getAlternativeName() );
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

            matchedObject = alternativeNameMap.get( nameableObject.getAlternativeName() );

            if ( matchedObject != null )
            {
                return matchedObject;
            }
        }

        return matchedObject;
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

            if ( nameableObject.getAlternativeName() != null )
            {
                alternativeNameMap.put( nameableObject.getAlternativeName(), object );
            }
        }
    }

    protected void prepareIdentifiableObject( BaseIdentifiableObject object )
    {
        if ( object.getUid() == null && object.getLastUpdated() == null )
        {
            object.setAutoFields();
        }
        else if ( object.getUid() == null )
        {
            object.setUid( CodeGenerator.generateCode() );
        }
    }

    /**
     * @param object Object to get display name for
     * @return A usable display name
     */
    protected String getDisplayName( IdentifiableObject object )
    {
        return object.getClass().getName();
    }
}
