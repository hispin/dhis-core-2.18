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
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.IdScheme;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractImporter<T extends BaseIdentifiableObject>
    implements Importer<T>
{
    private static final Log log = LogFactory.getLog( AbstractImporter.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    //-------------------------------------------------------------------------------------------------------
    // Current import counts
    //-------------------------------------------------------------------------------------------------------

    protected int imports;

    protected int updates;

    protected int ignores;

    //-------------------------------------------------------------------------------------------------------
    // Mappings from identifier (uid, name, code) to a db object.
    //
    // WARNING: These maps might be out-of-date, depending on if new inserts has been made after the were
    //          fetched.
    //-------------------------------------------------------------------------------------------------------

    protected Map<String, T> uidMap;

    protected Map<String, T> nameMap;

    protected Map<String, T> codeMap;

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
        if ( !options.isDryRun() )
        {
            log.info( "Trying to save new object with UID: " + object.getUid() );
            manager.save( object );
            log.info( "Save successful." );
            updateIdMaps( object );
        }

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
        oldObject.mergeWith( object );

        if ( !options.isDryRun() )
        {
            log.info( "Trying to update object with UID: " + oldObject.getUid() );
            manager.update( oldObject );
            log.info( "Update successful." );

        }

        return null;
    }

    /**
     * Current object name, used to fill name part of a ImportConflict
     *
     * @return Name of object
     */
    protected abstract String getObjectName();

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

        reset( objects.get( 0 ) );

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
        if ( object != null )
        {
            reset( object );
        }

        return importObjectLocal( object, options );
    }

    @Override
    public ImportCount getCurrentImportCount()
    {
        ImportCount importCount = new ImportCount( getObjectName() );

        importCount.setImports( imports );
        importCount.setUpdates( updates );
        importCount.setIgnores( ignores );

        return importCount;
    }

    //-------------------------------------------------------------------------------------------------------
    // Internal methods
    //-------------------------------------------------------------------------------------------------------

    private void reset( T type )
    {
        imports = 0;
        updates = 0;
        ignores = 0;

        uidMap = manager.getIdMap( (Class<T>) type.getClass(), IdentifiableObject.IdentifiableProperty.UID );
        nameMap = manager.getIdMap( (Class<T>) type.getClass(), IdentifiableObject.IdentifiableProperty.NAME );
        codeMap = manager.getIdMap( (Class<T>) type.getClass(), IdentifiableObject.IdentifiableProperty.CODE );
    }

    protected void updateIdMaps( T object )
    {
        if ( object.getUid() != null )
        {
            uidMap.put( object.getUid(), object );
        }

        if ( object.getName() != null )
        {
            nameMap.put( object.getName(), object );
        }

        if ( object.getCode() != null )
        {
            codeMap.put( object.getCode(), object );
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
            ignores++;
        }

        return conflict;
    }

    private ImportConflict startImport( T object, ImportOptions options )
    {
        T oldObject = getObject( object, options.getIdScheme() );
        ImportConflict conflict;

        if ( options.getImportStrategy().isNewStrategy() )
        {
            prepareIdentifiableObject( object );
            conflict = newObject( object, options );

            if ( conflict != null )
            {
                return conflict;
            }

            imports++;
        }
        else if ( options.getImportStrategy().isUpdatesStrategy() )
        {
            conflict = updatedObject( object, oldObject, options );

            if ( conflict != null )
            {
                return conflict;
            }

            updates++;
        }
        else if ( options.getImportStrategy().isNewAndUpdatesStrategy() )
        {
            if ( oldObject != null )
            {
                conflict = updatedObject( object, oldObject, options );

                if ( conflict != null )
                {
                    return conflict;
                }

                updates++;
            }
            else
            {
                prepareIdentifiableObject( object );
                conflict = newObject( object, options );

                if ( conflict != null )
                {
                    return conflict;
                }

                imports++;
            }
        }

        return null;
    }

    private ImportConflict validateIdentifiableObject( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T nameObject = nameMap.get( object.getName() );
        T codeObject = codeMap.get( object.getCode() );

        ImportConflict conflict = null;

        if ( options.getImportStrategy().isNewStrategy() )
        {
            conflict = validateForNewStrategy( object, options );
        }
        else if ( options.getImportStrategy().isUpdatesStrategy() )
        {
            conflict = validateForUpdatesStrategy( object, options );
        }
        else if ( options.getImportStrategy().isNewAndUpdatesStrategy() )
        {
            if ( options.getIdScheme().isUidScheme() )
            {
                if ( uidObject == null )
                {
                    conflict = validateForNewStrategy( object, options );
                }
                else
                {
                    conflict = validateForUpdatesStrategy( object, options );
                }
            }
            else if ( options.getIdScheme().isNameScheme() )
            {
                if ( nameObject == null )
                {
                    conflict = validateForNewStrategy( object, options );
                }
                else
                {
                    conflict = validateForUpdatesStrategy( object, options );
                }
            }
            else if ( options.getIdScheme().isCodeScheme() )
            {
                if ( codeObject == null )
                {
                    conflict = validateForNewStrategy( object, options );
                }
                else
                {
                    conflict = validateForUpdatesStrategy( object, options );
                }
            }
        }

        return conflict;
    }

    private ImportConflict validateForUpdatesStrategy( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T nameObject = nameMap.get( object.getName() );
        T codeObject = codeMap.get( object.getCode() );

        ImportConflict conflict = null;

        if ( options.getIdScheme().isUidScheme() )
        {
            if ( uidObject == null )
            {
                conflict = reportUidLookupConflict( object, options );
            }
            else if ( nameObject != null && nameObject != uidObject )
            {
                conflict = reportNameConflict( object, options );
            }
            else if ( codeObject != null && codeObject != uidObject )
            {
                conflict = reportCodeConflict( object, options );
            }
        }
        else if ( options.getIdScheme().isNameScheme() )
        {
            if ( nameObject == null )
            {
                conflict = reportNameLookupConflict( object, options );
            }
            else if ( uidObject != null && uidObject != nameObject )
            {
                conflict = reportUidConflict( object, options );
            }
            else if ( codeObject != null && codeObject != nameObject )
            {
                conflict = reportCodeConflict( object, options );
            }
        }
        else if ( options.getIdScheme().isCodeScheme() )
        {
            if ( codeObject == null )
            {
                conflict = reportCodeLookupConflict( object, options );
            }
            else if ( uidObject != null && uidObject != codeObject )
            {
                conflict = reportUidConflict( object, options );
            }
            else if ( nameObject != null && nameObject != codeObject )
            {
                conflict = reportNameConflict( object, options );
            }
        }

        return conflict;
    }

    private ImportConflict validateForNewStrategy( T object, ImportOptions options )
    {
        T uidObject = uidMap.get( object.getUid() );
        T nameObject = nameMap.get( object.getName() );
        T codeObject = codeMap.get( object.getCode() );

        ImportConflict conflict = null;

        if ( uidObject != null )
        {
            conflict = reportUidConflict( object, options );
        }
        else if ( nameObject != null )
        {
            conflict = reportNameConflict( object, options );
        }
        else if ( codeObject != null )
        {
            conflict = reportCodeConflict( object, options );
        }

        return conflict;
    }

    private ImportConflict reportUidLookupConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, lookup done using UID." );
    }

    private ImportConflict reportNameLookupConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, lookup done using NAME." );
    }

    private ImportConflict reportCodeLookupConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, lookup done using CODE." );
    }

    private ImportConflict reportUidConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object already exists, lookup done using UID." );
    }

    private ImportConflict reportNameConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object already exists, lookup done using NAME." );
    }

    private ImportConflict reportCodeConflict( IdentifiableObject object, ImportOptions options )
    {
        return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object already exists, lookup done using CODE." );
    }

    private T getObject( T object, IdScheme scheme )
    {
        if ( scheme.isUidScheme() )
        {
            return uidMap.get( object.getUid() );
        }
        else if ( scheme.isNameScheme() )
        {
            return nameMap.get( object.getName() );
        }
        else if ( scheme.isCodeScheme() )
        {
            return codeMap.get( object.getCode() );
        }

        return null;
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

    //-------------------------------------------------------------------------------------------------------
    // Protected methods
    //-------------------------------------------------------------------------------------------------------

    /**
     * Try to get a usable display based on current idScheme, mainly used for error-reporting
     * but can also be use elsewhere. Falls back to the name of the class, if no other alternative
     * is available.
     *
     * @param object Object to get display name for
     * @param scheme Current idScheme
     * @return A usable display name
     */
    protected String getDisplayName( IdentifiableObject object, IdScheme scheme )
    {
        if ( scheme.isUidScheme() )
        {
            if ( object.getUid() != null )
            {
                return object.getUid();
            }
        }
        else if ( scheme.isNameScheme() )
        {
            if ( object.getName() != null )
            {
                return object.getName();
            }
        }
        else if ( scheme.isCodeScheme() )
        {
            if ( object.getCode() != null )
            {
                return object.getCode();
            }
        }

        return object.getClass().getName();
    }

    protected String generateUid()
    {
        return CodeGenerator.generateCode();
    }
}
