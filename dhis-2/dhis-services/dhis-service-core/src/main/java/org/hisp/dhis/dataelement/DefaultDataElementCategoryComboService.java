package org.hisp.dhis.dataelement;

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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.common.GenericNameStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultDataElementCategoryComboService
    implements DataElementCategoryComboService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericNameStore<DataElementCategoryCombo> dataElementCategoryComboStore;

    public void setDataElementCategoryComboStore( GenericNameStore<DataElementCategoryCombo> dataElementCategoryComboStore )
    {
        this.dataElementCategoryComboStore = dataElementCategoryComboStore;
    }    

    // -------------------------------------------------------------------------
    // DataElementCategoryCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        return dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.delete( dataElementCategoryCombo );
    }

    public Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos()
    {
        return dataElementCategoryComboStore.getAll();
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( int id )
    {
        return dataElementCategoryComboStore.get( id );
    }
    
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombos( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllDataElementCategoryCombos();
        }
        
        Collection<DataElementCategoryCombo> categoryCombos = new ArrayList<DataElementCategoryCombo>();
        
        for ( Integer id : identifiers )
        {
            categoryCombos.add( getDataElementCategoryCombo( id ) );
        }
        
        return categoryCombos;
    }

    public DataElementCategoryCombo getDataElementCategoryComboByName( String name )
    {
        return dataElementCategoryComboStore.getByName( name );
    }
    
}
