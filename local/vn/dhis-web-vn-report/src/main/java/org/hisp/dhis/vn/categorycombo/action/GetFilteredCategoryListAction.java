
// HIEU DONE //
package org.hisp.dhis.vn.categorycombo.action;

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
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.comparator.DataElementCategoryNameComparator;
import org.hisp.dhis.vn.report.action.ActionSupport;


public class GetFilteredCategoryListAction 
	extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryComboService dataElementCategoryComboService;

    public void setDataElementCategoryComboService( DataElementCategoryComboService dataElementCategoryComboService )
    {
        this.dataElementCategoryComboService = dataElementCategoryComboService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElementCategory> dataElementCategoryComparator;

    public void setDataElementComparator( Comparator<DataElementCategory> dataElementCategoryComparator )
    {
        this.dataElementCategoryComparator = dataElementCategoryComparator;
    }
    
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataElementCategory> dataElementCategories;
    
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private Integer dataElementComboId;

    public void setdataElementComboId( Integer dataElementComboId )
    {
        this.dataElementComboId = dataElementComboId;
    }

    public Integer getdataElementComboId()
    {
        return dataElementComboId;
    }
	
	public List<DataElementCategory> getDataElementCategories()
    {
        return dataElementCategories;
    }


    // -------------------------------------------------------------------------
    // Action implemantation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
    {
        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        if ( (dataElementComboId != null) || (dataElementComboId == -1) ) {
        	
        	dataElementCategories = new ArrayList<DataElementCategory>( dataElementCategoryComboService.getDataElementCategoryCombo(dataElementComboId).getCategories() );
        	Collections.sort( dataElementCategories, new DataElementCategoryNameComparator() );
        }
        else {
        	message = i18n.getString( "Invalid" );
        	return ERROR;
        }

        return SUCCESS;
    }
}
// HIEU DONE //