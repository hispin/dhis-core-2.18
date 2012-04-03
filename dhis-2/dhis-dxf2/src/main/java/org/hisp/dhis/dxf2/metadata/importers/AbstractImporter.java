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
    @Autowired
    private IdentifiableObjectManager manager;

    protected int imports;

    protected int updates;

    protected int ignores;

    protected abstract void newObject( T object );

    protected abstract void updatedObject( T object, T oldObject );

    protected abstract String getObjectName();

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
        Map<String, T> map = getIdMap( (Class) object.getClass(), options.getIdScheme() );
        String identifier = getIdentifier( object, options.getIdScheme() );
        T oldObject = map.get( identifier );

        if ( options.getImportStrategy().isNewStrategy() )
        {
            if ( oldObject != null )
            {
                ignores++;
                return new ImportConflict( object.getClass().getName(), "Strategy is new, but identifier '" + identifier + "' already exists." );
            }

            imports++;
            newObject( object );
        }
        else if ( options.getImportStrategy().isUpdatesStrategy() )
        {
            if ( oldObject == null )
            {
                ignores++;
                return new ImportConflict( object.getClass().getName(), "Strategy is updates, but identifier '" + identifier + "' does not exist." );
            }

            updates++;
            updatedObject( object, oldObject );
        }
        else if ( options.getImportStrategy().isNewAndUpdatesStrategy() )
        {
            if ( oldObject != null )
            {
                updates++;
                updatedObject( object, oldObject );
            }
            else
            {
                imports++;
                newObject( object );
            }
        }

        return null;
    }

    @Override
    public ImportCount getImportCount()
    {
        ImportCount importCount = new ImportCount( getObjectName() );

        importCount.setImports( imports );
        importCount.setUpdates( updates );
        importCount.setIgnores( ignores );

        return importCount;
    }

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
}
