package org.hisp.dhis.dataelement;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.AbstractIdentifiableObject;
import org.hisp.dhis.common.CombinationGenerator;

/**
 * @author Abyot Aselefew
 * @version $Id$
 */
public class DataElementCategoryCombo
    extends AbstractIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1549406078091077760L;

    public static final String DEFAULT_CATEGORY_COMBO_NAME = "default";

    /**
     * The database internal identifier.
     */
    private int id;

    /**
     * The name.
     */
    private String name;

    /**
     * A set with categories.
     */
    private List<DataElementCategory> categories = new ArrayList<DataElementCategory>();

    /**
     * A set of category option combos.
     */
    private Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategoryCombo()
    {
    }

    public DataElementCategoryCombo( String name )
    {
        this.name = name;
    }

    public DataElementCategoryCombo( String name, List<DataElementCategory> categories )
    {
        this.name = name;
        this.categories = categories;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isDefault()
    {
        return name.equals( DEFAULT_CATEGORY_COMBO_NAME );
    }
    
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        final List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();
        
        for ( DataElementCategory category : categories )
        {
            categoryOptions.addAll( category.getCategoryOptions() );            
        }
        
        return categoryOptions;
    }

    public boolean doTotal()
    {
        return optionCombos != null && optionCombos.size() > 1;
    }
    
    public boolean doSubTotals()
    {
        return categories != null && categories.size() > 1;
    }
    
    public DataElementCategoryOption[][] getCategoryOptionsAsArray()
    {
        DataElementCategoryOption[][] arrays = new DataElementCategoryOption[categories.size()][];
        
        int i = 0;
        
        for ( DataElementCategory category : categories )
        {
            arrays[i++] = new ArrayList<DataElementCategoryOption>( 
                category.getCategoryOptions() ).toArray( new DataElementCategoryOption[0] );
        }
        
        return arrays;
    }
    
    public void generateOptionCombos()
    {
        CombinationGenerator<DataElementCategoryOption> generator = 
            new CombinationGenerator<DataElementCategoryOption>( this.getCategoryOptionsAsArray() );
        
        while ( generator.hasNext() )
        {
            DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();
            optionCombo.setCategoryOptions( generator.getNext() );
            optionCombo.setCategoryCombo( this );
            this.optionCombos.add( optionCombo );
        }
    }
    
    //TODO update category option -> category option combo association
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( !(object instanceof DataElementCategoryCombo) )
        {
            return false;
        }

        final DataElementCategoryCombo other = (DataElementCategoryCombo) object;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }   

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( List<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    public Set<DataElementCategoryOptionCombo> getOptionCombos()
    {
        return optionCombos;
    }

    public void setOptionCombos( Set<DataElementCategoryOptionCombo> optionCombos )
    {
        this.optionCombos = optionCombos;
    }
}
