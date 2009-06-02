package org.hisp.dhis.customvalue;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;

/**
 * @author Latifov Murodillo Abdusamadovich
 * 
 * @version $Id$
 */
public class DefaultCustomValueService
    implements CustomValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CustomValueStore customValueStore;

    public void setCustomValueStore( CustomValueStore customValueStore )
    {
        this.customValueStore = customValueStore;
    }
    
    // -------------------------------------------------------------------------
    // CustomValueService implementation
    // -------------------------------------------------------------------------

    public int addCustomValue( CustomValue customValue )
    {
        int id = customValueStore.addCustomValue( customValue );

        return id;
    }

    public void deleteCustomValue( CustomValue customValue )
    {
        customValueStore.deleteCustomValue( customValue );
    }

    public Collection<CustomValue> getCustomValues( DataSet dataSet )
    {
        return customValueStore.getCustomValuesByDataSet( dataSet );
    }

    public Collection<CustomValue> getCustomValuesByCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        return customValueStore.getCustomValuesByCategoryCombo( categoryCombo );
    }

    public Collection<CustomValue> getCustomValuesByDataElement( DataElement dataElement )
    {
        return customValueStore.getCustomValuesByDataElement( dataElement );
    }

    public Collection<CustomValue> getCustomValuesByDataSet( DataSet dataSet )
    {
        return customValueStore.getCustomValuesByDataSet( dataSet );
    }

    public CustomValue getCustomValuesById( Integer id )
    {
        return customValueStore.getCustomValuesById( id );
    }

    public Collection<CustomValue> getCustomValues( DataSet dataSet, DataElement dataElement,
        DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        return customValueStore.getCustomValues( dataSet, dataElement, dataElementCategoryOptionCombo );
    }

	public Collection<CustomValue> findCustomValues(String searchValue) 
	{
		return customValueStore.findCustomValues( searchValue );
	}
}
