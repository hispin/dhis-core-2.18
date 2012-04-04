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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.metadata.IdScheme;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.Importer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractImporter<T extends IdentifiableObject>
    implements Importer<T>
{
    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    //-------------------------------------------------------------------------------------------------------
    // Current import counts
    //-------------------------------------------------------------------------------------------------------

    protected int imports;

    protected int updates;

    protected int ignores;

    //-------------------------------------------------------------------------------------------------------
    // Abstract methods that sub-classes needs to implement
    //-------------------------------------------------------------------------------------------------------

    /**
     * Called every time a new object is to be imported.
     *
     * @param object Object to import
     */
    protected abstract ImportConflict newObject( T object );

    /**
     * Update object from old => new.
     *
     * @param object    Object to import
     * @param oldObject The current version of the object
     */
    protected abstract ImportConflict updatedObject( T object, T oldObject );

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
    public List<ImportConflict> importCollection( Collection<T> objects, ImportOptions options )
    {
        imports = 0;
        updates = 0;
        ignores = 0;

        List<ImportConflict> conflicts = new ArrayList<ImportConflict>();

        for ( T object : objects )
        {
            ImportConflict importConflict = importObject( object, options );

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
        // move this to importCollection
        Map<String, T> map = getIdMap( (Class) object.getClass(), options.getIdScheme() );
        String identifier = getIdentifier( object, options.getIdScheme() );
        T oldObject = map.get( identifier );

        if ( options.getImportStrategy().isNewStrategy() )
        {
            if ( oldObject != null )
            {
                ignores++;
                return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Strategy is new, but identifier '" + identifier + "' already exists." );
            }

            ImportConflict conflict = newObject( object );

            if ( conflict != null )
            {
                return conflict;
            }

            imports++;
        }
        else if ( options.getImportStrategy().isUpdatesStrategy() )
        {
            if ( oldObject == null )
            {
                ignores++;
                return new ImportConflict( getDisplayName( object, options.getIdScheme() ), "Strategy is updates, but identifier '" + identifier + "' does not exist." );
            }

            ImportConflict conflict = updatedObject( object, oldObject );

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
                ImportConflict conflict = updatedObject( object, oldObject );

                if ( conflict != null )
                {
                    return conflict;
                }

                updates++;
            }
            else
            {
                ImportConflict conflict = newObject( object );

                if ( conflict != null )
                {
                    return conflict;
                }

                imports++;
            }
        }

        return null;
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
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    protected Map<String, T> getIdMap( Class<T> clazz, IdScheme scheme )
    {
        if ( scheme.isUidScheme() )
        {
            return manager.getIdMap( clazz, IdentifiableObject.IdentifiableProperty.UID );
        }
        else if ( scheme.isNameScheme() )
        {
            return manager.getIdMap( clazz, IdentifiableObject.IdentifiableProperty.NAME );
        }
        else if ( scheme.isCodeScheme() )
        {
            return manager.getIdMap( clazz, IdentifiableObject.IdentifiableProperty.CODE );
        }

        return null;
    }

    protected String getIdentifier( T object, IdScheme scheme )
    {
        if ( scheme.isUidScheme() )
        {
            return object.getUid();
        }
        else if ( scheme.isNameScheme() )
        {
            return object.getName();
        }
        else if ( scheme.isCodeScheme() )
        {
            return object.getCode();
        }

        return null;
    }

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
}
