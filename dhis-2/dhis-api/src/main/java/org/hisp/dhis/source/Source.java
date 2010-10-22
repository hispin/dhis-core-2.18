package org.hisp.dhis.source;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dimension.Dimension;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionOptionElement;
import org.hisp.dhis.dimension.DimensionType;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: Source.java 5277 2008-05-27 15:48:42Z larshelg $
 */
public abstract class Source
    extends IdentifiableObject implements DimensionOption
{
    protected Set<DataSet> dataSets = new HashSet<DataSet>();

    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public Set<DataElement> getDataElementsInDataSets()
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();
        
        for ( DataSet dataSet : dataSets )
        {
            dataElements.addAll( dataSet.getDataElements() );
        }
        
        return dataElements;
    }
    
    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public static final Dimension DIMENSION = new SourceDimension();
    
    public static class SourceDimension
        extends Dimension
    {
        private static final String NAME = "Source";
        
        public String getName()
        {
            return NAME;
        }
        
        public List<? extends DimensionOption> getDimensionOptions()
        {
            return null;
        }
        
        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            
            if ( o == null )
            {
                return false;
            }
            
            if ( !( o instanceof SourceDimension ) )
            {
                return false;
            }
            
            final SourceDimension other = (SourceDimension) o;
            
            return NAME.equals( other.getName() );
        }
        
        @Override
        public int hashCode()
        {
            return NAME.hashCode();
        }

        @Override
        public String toString()
        {
            return "[" + NAME + "]";
        }
    }

    public DimensionType getDimensionType()
    {
        return null;
    }
    
    public Set<? extends DimensionOptionElement> getDimensionOptionElements()
    {
        return null;
    }

    public Dimension getDimension()
    {
        return DIMENSION;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    public abstract int hashCode();

    public abstract boolean equals( Object o );
    
    public abstract String toString();

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }
}
