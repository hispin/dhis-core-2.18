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

    protected Map<String, T> uidMap;

    protected Map<String, T> nameMap;

    protected Map<String, T> codeMap;

    //-------------------------------------------------------------------------------------------------------
    // Abstract methods that sub-classes needs to implement
    //-------------------------------------------------------------------------------------------------------

    /**
     * Called every time a new object is to be imported.
     *
     * @param object  Object to import
     * @param options Current import options
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected abstract ImportConflict newObject( T object, ImportOptions options );

    /**
     * Update object from old => new.
     *
     * @param object    Object to import
     * @param oldObject The current version of the object
     * @param options   Current import options
     * @return An ImportConflict instance if there was a conflict, otherwise null
     */
    protected abstract ImportConflict updatedObject( T object, T oldObject, ImportOptions options );

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
    public List<ImportConflict> importCollection( List<T> objects, ImportOptions options )
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
        if ( object == null )
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

    private ImportConflict importObjectLocal( T object, ImportOptions options )
    {
        ImportConflict conflict = validateIdentifiableObject( object, options );

        if ( conflict == null )
        {
            conflict = startImport( object, options );
        }

        return conflict;
    }

    private ImportConflict startImport( T object, ImportOptions options )
    {
        T oldObject = getObject( object, options.getIdScheme() );
        ImportConflict conflict = null;

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
        T uidObject = uidMap.get( object );
        T nameObject = nameMap.get( object );
        T codeObject = codeMap.get( object );

        ImportConflict conflict = null;

        if ( options.getImportStrategy().isNewStrategy() )
        {
            if ( uidObject != null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object fails uniqueness constraint on identifier uid." );
            }

            if ( nameObject != null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object fails uniqueness constraint on identifier name." );
            }

            if ( codeObject != null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object fails uniqueness constraint on identifier code." );
            }
        }
        else if ( options.getImportStrategy().isUpdatesStrategy() )
        {
            if ( options.getIdScheme().isUidScheme() && uidObject == null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, did lookup with identifier uid." );
            }

            if ( options.getIdScheme().isNameScheme() && nameObject == null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, did lookup with identifier name." );
            }

            if ( options.getIdScheme().isCodeScheme() && codeObject == null )
            {
                conflict = new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Object does not exist, did lookup with identifier code." );
            }
        }
        else if ( options.getImportStrategy().isNewAndUpdatesStrategy() )
        {
        }

        return conflict;
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
        if ( object.getUid() == null )
        {
            object.setUid( generateUid() );
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
