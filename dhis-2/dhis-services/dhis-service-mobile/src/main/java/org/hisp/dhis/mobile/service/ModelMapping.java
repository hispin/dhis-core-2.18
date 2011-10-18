package org.hisp.dhis.mobile.service;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import java.util.List;
import org.hisp.dhis.mobile.api.model.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.mobile.api.model.Model;
import org.hisp.dhis.mobile.api.model.ModelList;
import org.springframework.beans.factory.annotation.Required;

public class ModelMapping
{
    private DataElementCategoryService categoryService;

    @Required
    public void setCategoryService( org.hisp.dhis.dataelement.DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public DataElement getDataElement( org.hisp.dhis.dataelement.DataElement dataElement )
    {
        DataElement de = new DataElement();
        de.setId( dataElement.getId() );

        // Name defaults to alternative name with fallback to name if empty
        String name = dataElement.getAlternativeName();
        if ( name == null || name.trim().isEmpty() )
        {
            name = dataElement.getName();
        }
        de.setName( name );
        de.setType( dataElement.getType() );

        de.setCategoryOptionCombos( getCategoryOptionCombos( dataElement ) );
        return de;
    }

    public ModelList getCategoryOptionCombos( org.hisp.dhis.dataelement.DataElement dataElement )
    {
        DataElementCategoryCombo categoryCombo = dataElement.getCategoryCombo();
        
        // Set<DataElementCategoryOptionCombo> deCatOptCombs =
        // dataElement.getCategoryCombo().getOptionCombos();

        // if ( deCatOptCombs.size() < 2 )
        // {
        // return null;
        // }

        // Client DataElement
        ModelList deCateOptCombo = new ModelList();
        List<Model> listCateOptCombo = new ArrayList<Model>();
        deCateOptCombo.setModels( listCateOptCombo );

        for ( DataElementCategoryOptionCombo oneCatOptCombo : categoryCombo.getSortedOptionCombos() )
        {
            Model oneCateOptCombo = new Model();
            oneCateOptCombo.setId( oneCatOptCombo.getId() );
            oneCateOptCombo.setName( oneCatOptCombo.getName() );
            listCateOptCombo.add( oneCateOptCombo );
        }
        return deCateOptCombo;
    }

}
