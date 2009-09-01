// HIEU DONE //

package org.hisp.dhis.vn.dataelement.action;

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
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementCategoryComboNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;

import com.opensymphony.xwork.Action;



/**
 */
public class GetDataElementGroupsAndCateCombosListAction
    implements Action
{
	private static final long serialVersionUID = 1L;
	
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private DataElementService dataElementService;
	
	private DataElementCategoryComboService dataElementCategoryComboService;
	
    /**
	 * 
	 */
	
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setDataElementCategoryComboService ( DataElementCategoryComboService dataElementCategoryComboService )
    {
        this.dataElementCategoryComboService = dataElementCategoryComboService;
    }    

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
	
	private List<DataElementGroup> dataElementGroups;
	
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<DataElementCategoryCombo> categoryCombos;
    
    public List<DataElementCategoryCombo> getcategoryCombos()
    {
        return categoryCombos;
    }
   
	
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // get list of DataElementGroups and list of CategoryCombos
        // ---------------------------------------------------------------------
        
		if ( dataElementGroups == null ) {
			
			dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
			System.err.println("dataElementGroups.size = " + dataElementGroups.size());
		}

		Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );
		
		if ( categoryCombos == null ) {
		
			categoryCombos = new ArrayList<DataElementCategoryCombo> ( dataElementCategoryComboService.getAllDataElementCategoryCombos() );
		}
		
		Collections.sort( categoryCombos, new DataElementCategoryComboNameComparator() );
        
		return SUCCESS;
    }
}

// HIEU DONE //